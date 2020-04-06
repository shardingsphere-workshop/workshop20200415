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

package org.apache.shardingsphere.workshop.proxy.frontend.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.workshop.proxy.frontend.engine.CommandExecuteEngine;
import org.apache.shardingsphere.workshop.proxy.frontend.command.CommandExecutor;
import org.apache.shardingsphere.workshop.proxy.frontend.command.ComQueryCommandExecutor;
import org.apache.shardingsphere.workshop.proxy.transport.error.MySQLErrPacketFactory;
import org.apache.shardingsphere.workshop.proxy.transport.packet.CommandPacket;
import org.apache.shardingsphere.workshop.proxy.transport.packet.CommandPacketType;
import org.apache.shardingsphere.workshop.proxy.transport.packet.DatabasePacket;
import org.apache.shardingsphere.workshop.proxy.transport.payload.PacketPayload;
import org.apache.shardingsphere.workshop.proxy.transport.payload.MySQLPacketPayload;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Command executor task.
 */
@RequiredArgsConstructor
@Slf4j
public final class CommandExecutorTask implements Runnable {
    
    private final CommandExecuteEngine commandExecuteEngine;
    
    private final ChannelHandlerContext context;
    
    private final Object message;
    
    @Override
    public void run() {
        try (PacketPayload payload = new MySQLPacketPayload((ByteBuf) message)) {
            executeCommand(context, payload);
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            log.error("Exception occur: ", ex);
            context.writeAndFlush(MySQLErrPacketFactory.newInstance(1, ex));
        }
    }
    
    private void executeCommand(final ChannelHandlerContext context, final PacketPayload payload) throws SQLException {
        CommandPacketType type = commandExecuteEngine.getCommandPacketType(payload);
        CommandPacket commandPacket = commandExecuteEngine.getCommandPacket(payload, type);
        CommandExecutor commandExecutor = commandExecuteEngine.getCommandExecutor(type, commandPacket);
        Collection<DatabasePacket> responsePackets = commandExecutor.execute();
        for (DatabasePacket each : responsePackets) {
            context.writeAndFlush(each);
        }
        if (commandExecutor instanceof ComQueryCommandExecutor) {
            commandExecuteEngine.writeQueryData(context, (ComQueryCommandExecutor) commandExecutor, responsePackets.size());
        }
    }
}
