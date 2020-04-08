/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy;

import com.google.common.base.Preconditions;
import com.jdd.global.shardingsphere.workshop.proxy.command.ComQueryCommandExecutor;
import com.jdd.global.shardingsphere.workshop.proxy.command.CommandExecutor;
import com.jdd.global.shardingsphere.workshop.proxy.command.MySQLComQueryPacketExecutor;
import com.jdd.global.shardingsphere.workshop.proxy.handler.MySQLAuthenticationHandler;
import com.jdd.global.shardingsphere.workshop.proxy.command.MySQLUnsupportedCommandExecutor;
import com.jdd.global.shardingsphere.workshop.proxy.packet.MySQLPacketPayload;
import com.jdd.global.shardingsphere.workshop.proxy.packet.MySQLPacket;
import com.jdd.global.shardingsphere.workshop.proxy.packet.command.MySQLCommandPacketType;
import com.jdd.global.shardingsphere.workshop.proxy.packet.command.query.MySQLComQueryPacket;
import com.jdd.global.shardingsphere.workshop.proxy.packet.error.MySQLErrPacketFactory;
import com.jdd.global.shardingsphere.workshop.proxy.packet.generic.MySQLEofPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Frontend channel inbound handler.
 */
@RequiredArgsConstructor
@Slf4j
public final class FrontendChannelInboundHandler extends ChannelInboundHandlerAdapter {
    
    private final MySQLAuthenticationHandler authHandler = new MySQLAuthenticationHandler();
    
    private boolean authorized;
    
    @Override
    public void channelActive(final ChannelHandlerContext context) {
        authHandler.handshake(context);
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext context, final Object message) {
        if (!authorized) {
            authorized = auth(context, (ByteBuf) message);
            return;
        }
        try (MySQLPacketPayload payload = new MySQLPacketPayload((ByteBuf) message)) {
            executeCommand(context, payload);
        } catch (final Exception ex) {
            log.error("Exception occur: ", ex);
            context.writeAndFlush(MySQLErrPacketFactory.newInstance(1, ex));
        }
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext context) {
        context.fireChannelInactive();
    }
    
    private boolean auth(final ChannelHandlerContext context, final ByteBuf message) {
        try (MySQLPacketPayload payload = new MySQLPacketPayload(message)) {
            return authHandler.auth(context, payload);
        } catch (final Exception ex) {
            log.error("Exception occur: ", ex);
            context.write(MySQLErrPacketFactory.newInstance(1, ex));
        }
        return false;
    }
    
    private void executeCommand(final ChannelHandlerContext context, final MySQLPacketPayload payload) throws SQLException {
        CommandExecutor commandExecutor = createCommandExecutor(payload);
        Collection<MySQLPacket> responsePackets = commandExecutor.execute();
        for (MySQLPacket each : responsePackets) {
            context.writeAndFlush(each);
        }
        if (commandExecutor instanceof ComQueryCommandExecutor && ((ComQueryCommandExecutor) commandExecutor).isQuery()) {
            writeQueryData(context, (ComQueryCommandExecutor) commandExecutor, responsePackets.size());
        }
    }
    
    private void writeQueryData(final ChannelHandlerContext context, final ComQueryCommandExecutor queryCommandExecutor, final int headerPackagesCount) throws SQLException {
        int currentSequenceId = 0;
        while (queryCommandExecutor.next()) {
            context.writeAndFlush(queryCommandExecutor.getQueryData());
            currentSequenceId++;
        }
        context.writeAndFlush(new MySQLEofPacket(++currentSequenceId + headerPackagesCount));
    }
    
    private CommandExecutor createCommandExecutor(final MySQLPacketPayload payload) {
        Preconditions.checkArgument(0 == payload.readInt1(), "Sequence ID of MySQL command packet must be `0`.");
        MySQLCommandPacketType packetType = MySQLCommandPacketType.valueOf(payload.readInt1());
        switch (packetType) {
            case COM_QUERY:
                return new MySQLComQueryPacketExecutor(new MySQLComQueryPacket(payload));
            default:
                return new MySQLUnsupportedCommandExecutor(packetType);
        }
    }
}
