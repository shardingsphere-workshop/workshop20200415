/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jdd.global.shardingsphere.workshop.proxy.frontend;

import com.jdd.global.shardingsphere.workshop.proxy.frontend.auth.MySQLAuthenticationEngine;
import com.jdd.global.shardingsphere.workshop.proxy.frontend.command.ComQueryCommandExecutor;
import com.jdd.global.shardingsphere.workshop.proxy.frontend.command.CommandExecutor;
import com.jdd.global.shardingsphere.workshop.proxy.frontend.command.MySQLCommandExecutorFactory;
import com.jdd.global.shardingsphere.workshop.proxy.transport.MySQLPacketPayload;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.MySQLPacket;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.error.MySQLErrPacketFactory;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.generic.MySQLEofPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

/**
 * Frontend channel inbound handler.
 */
@RequiredArgsConstructor
@Slf4j
public final class FrontendChannelInboundHandler extends ChannelInboundHandlerAdapter {
    
    private final ExecutorService executorService;
    
    private final MySQLAuthenticationEngine authEngine = new MySQLAuthenticationEngine();
    
    private boolean authorized;
    
    @Override
    public void channelActive(final ChannelHandlerContext context) {
        authEngine.handshake(context);
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext context, final Object message) {
        if (!authorized) {
            authorized = auth(context, (ByteBuf) message);
            return;
        }
        executorService.execute(new CommandExecutorTask(context, message));
    }
    
    private boolean auth(final ChannelHandlerContext context, final ByteBuf message) {
        try (MySQLPacketPayload payload = new MySQLPacketPayload(message)) {
            return authEngine.auth(context, payload);
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            log.error("Exception occur: ", ex);
            context.write(MySQLErrPacketFactory.newInstance(1, ex));
        }
        return false;
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext context) {
        context.fireChannelInactive();
    }
    
    @RequiredArgsConstructor
    private final class CommandExecutorTask implements Runnable {
    
        private final ChannelHandlerContext context;
    
        private final Object message;
    
        @Override
        public void run() {
            try (MySQLPacketPayload payload = new MySQLPacketPayload((ByteBuf) message)) {
                executeCommand(context, payload);
                // CHECKSTYLE:OFF
            } catch (final Exception ex) {
                // CHECKSTYLE:ON
                log.error("Exception occur: ", ex);
                context.writeAndFlush(MySQLErrPacketFactory.newInstance(1, ex));
            }
        }
    
        private void executeCommand(final ChannelHandlerContext context, final MySQLPacketPayload payload) throws SQLException {
            CommandExecutor commandExecutor = MySQLCommandExecutorFactory.newInstance(payload);
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
    }
}
