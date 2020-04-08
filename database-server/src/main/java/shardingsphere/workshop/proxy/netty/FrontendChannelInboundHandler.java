/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package shardingsphere.workshop.proxy.netty;

import com.google.common.base.Preconditions;
import shardingsphere.workshop.proxy.mysql.handler.BackendHandler;
import shardingsphere.workshop.proxy.mysql.handler.query.CSVQueryBackendHandler;
import shardingsphere.workshop.proxy.mysql.handler.MySQLAuthenticationHandler;
import shardingsphere.workshop.proxy.mysql.handler.query.QueryBackendHandler;
import shardingsphere.workshop.proxy.mysql.handler.DefaultBackendHandler;
import shardingsphere.workshop.proxy.mysql.packet.MySQLPacket;
import shardingsphere.workshop.proxy.mysql.packet.MySQLPacketPayload;
import shardingsphere.workshop.proxy.mysql.packet.MySQLErrPacketFactory;
import shardingsphere.workshop.proxy.mysql.packet.MySQLEofPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.sql.parser.SQLParserEngine;
import org.apache.shardingsphere.sql.parser.sql.statement.SQLStatement;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement;

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
        Preconditions.checkArgument(0 == payload.readInt1(), "Sequence ID of MySQL command packet must be `0`.");
        Preconditions.checkState(0x03 == payload.readInt1(), "only support COM_QUERY command type");
        SQLStatement sqlStatement = new SQLParserEngine("MySQL").parse(payload.readStringEOF(), false);
        BackendHandler backendHandler = sqlStatement instanceof SelectStatement ? new CSVQueryBackendHandler((SelectStatement) sqlStatement) : new DefaultBackendHandler();
        Collection<MySQLPacket> responsePackets = backendHandler.execute();
        for (MySQLPacket each : responsePackets) {
            context.writeAndFlush(each);
        }
        if (backendHandler instanceof QueryBackendHandler) {
            writeQueryData(context, (QueryBackendHandler) backendHandler, responsePackets.size());
        }
    }
    
    private void writeQueryData(final ChannelHandlerContext context, final QueryBackendHandler queryBackendHandler, final int headerPackagesCount) throws SQLException {
        int currentSequenceId = 0;
        while (queryBackendHandler.next()) {
            context.writeAndFlush(queryBackendHandler.getQueryData());
            currentSequenceId++;
        }
        context.writeAndFlush(new MySQLEofPacket(++currentSequenceId + headerPackagesCount));
    }
}
