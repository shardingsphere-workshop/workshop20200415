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

package org.apache.shardingsphere.workshop.proxy.frontend;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.workshop.proxy.frontend.core.api.CommandExecutor;
import org.apache.shardingsphere.workshop.proxy.frontend.core.api.QueryCommandExecutor;
import org.apache.shardingsphere.workshop.proxy.frontend.core.engine.CommandExecuteEngine;
import org.apache.shardingsphere.workshop.proxy.frontend.core.spi.DatabaseProtocolFrontendEngine;
import org.apache.shardingsphere.workshop.proxy.transport.core.packet.CommandPacket;
import org.apache.shardingsphere.workshop.proxy.transport.core.packet.CommandPacketType;
import org.apache.shardingsphere.workshop.proxy.transport.core.packet.DatabasePacket;
import org.apache.shardingsphere.workshop.proxy.transport.core.payload.PacketPayload;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

/**
 * Command executor task.
 */
@RequiredArgsConstructor
@Slf4j
public final class CommandExecutorTask implements Runnable {
    
    private final DatabaseProtocolFrontendEngine databaseProtocolFrontendEngine;
    
    
    private final ChannelHandlerContext context;
    
    private final Object message;
    
    @Override
    public void run() {
        try (PacketPayload payload = databaseProtocolFrontendEngine.getCodecEngine().createPacketPayload((ByteBuf) message)) {
            executeCommand(context, payload);
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            log.error("Exception occur: ", ex);
            context.writeAndFlush(databaseProtocolFrontendEngine.getCommandExecuteEngine().getErrorPacket(ex));
            Optional<DatabasePacket> databasePacket = databaseProtocolFrontendEngine.getCommandExecuteEngine().getOtherPacket();
            databasePacket.ifPresent(context::writeAndFlush);
        }
    }
    
    private void executeCommand(final ChannelHandlerContext context, final PacketPayload payload) throws SQLException {
        CommandExecuteEngine commandExecuteEngine = databaseProtocolFrontendEngine.getCommandExecuteEngine();
        CommandPacketType type = commandExecuteEngine.getCommandPacketType(payload);
        CommandPacket commandPacket = commandExecuteEngine.getCommandPacket(payload, type);
        CommandExecutor commandExecutor = commandExecuteEngine.getCommandExecutor(type, commandPacket);
        Collection<DatabasePacket> responsePackets = commandExecutor.execute();
        for (DatabasePacket each : responsePackets) {
            context.writeAndFlush(each);
        }
        if (commandExecutor instanceof QueryCommandExecutor) {
            commandExecuteEngine.writeQueryData(context, (QueryCommandExecutor) commandExecutor, responsePackets.size());
        }
    }
}
