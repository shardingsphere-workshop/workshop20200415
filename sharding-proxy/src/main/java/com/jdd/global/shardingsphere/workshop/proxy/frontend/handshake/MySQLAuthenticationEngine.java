/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.frontend.handshake;

import com.jdd.global.shardingsphere.workshop.proxy.transport.MySQLPacketPayload;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.error.MySQLServerErrorCode;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.generic.MySQLErrPacket;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.generic.MySQLOKPacket;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.handshake.MySQLHandshakePacket;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.handshake.MySQLHandshakeResponse41Packet;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Optional;

/**
 * Authentication engine for MySQL.
 */
public final class MySQLAuthenticationEngine {
    
    private final MySQLAuthenticationHandler authenticationHandler = new MySQLAuthenticationHandler();
    
    /**
     * Handshake.
     *
     * @param context channel handler context
     */
    public void handshake(final ChannelHandlerContext context) {
        int connectionId = ConnectionIdGenerator.getInstance().nextId();
        context.writeAndFlush(new MySQLHandshakePacket(connectionId, authenticationHandler.getAuthPluginData()));
    }
    
    /**
     * Authentication.
     *
     * @param context channel handler context
     * @param payload packet payload
     * @return auth finish or not
     */
    public boolean auth(final ChannelHandlerContext context, final MySQLPacketPayload payload) {
        MySQLHandshakeResponse41Packet response41 = new MySQLHandshakeResponse41Packet(payload);
        Optional<MySQLServerErrorCode> errorCode = authenticationHandler.login(response41);
        if (errorCode.isPresent()) {
            context.writeAndFlush(getMySQLErrPacket(errorCode.get(), context, response41));
        } else {
            context.writeAndFlush(new MySQLOKPacket(response41.getSequenceId() + 1));
        }
        return true;
    }
    
    private MySQLErrPacket getMySQLErrPacket(final MySQLServerErrorCode errorCode, final ChannelHandlerContext context, final MySQLHandshakeResponse41Packet response41) {
        if (MySQLServerErrorCode.ER_DBACCESS_DENIED_ERROR == errorCode) {
            return new MySQLErrPacket(response41.getSequenceId() + 1, MySQLServerErrorCode.ER_DBACCESS_DENIED_ERROR, response41.getUsername(), getHostAddress(context), response41.getDatabase());
        } else {
            return new MySQLErrPacket(response41.getSequenceId() + 1, MySQLServerErrorCode.ER_ACCESS_DENIED_ERROR, response41.getUsername(), getHostAddress(context),
                    0 == response41.getAuthResponse().length ? "NO" : "YES");
        }
    }
    
    private String getHostAddress(final ChannelHandlerContext context) {
        SocketAddress socketAddress = context.channel().remoteAddress();
        return socketAddress instanceof InetSocketAddress ? ((InetSocketAddress) socketAddress).getAddress().getHostAddress() : socketAddress.toString();
    }
}
