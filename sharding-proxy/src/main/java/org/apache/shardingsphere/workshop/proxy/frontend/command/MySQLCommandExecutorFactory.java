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

package org.apache.shardingsphere.workshop.proxy.frontend.command;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.workshop.proxy.frontend.command.admin.ping.MySQLComPingExecutor;
import org.apache.shardingsphere.workshop.proxy.frontend.command.admin.quit.MySQLComQuitExecutor;
import org.apache.shardingsphere.workshop.proxy.frontend.command.generic.MySQLUnsupportedCommandExecutor;
import org.apache.shardingsphere.workshop.proxy.frontend.command.comquery.MySQLComQueryPacketExecutor;
import org.apache.shardingsphere.workshop.proxy.transport.packet.CommandPacket;
import org.apache.shardingsphere.workshop.proxy.transport.packet.command.MySQLCommandPacketType;
import org.apache.shardingsphere.workshop.proxy.transport.packet.command.query.text.query.MySQLComQueryPacket;

/**
 * Command executor factory for MySQL.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class MySQLCommandExecutorFactory {
    
    /**
     * Create new instance of packet executor.
     *
     * @param commandPacketType command packet type for MySQL
     * @param commandPacket command packet for MySQL
     * @return command executor
     */
    public static CommandExecutor newInstance(final MySQLCommandPacketType commandPacketType, final CommandPacket commandPacket) {
        log.debug("Execute packet type: {}, value: {}", commandPacketType, commandPacket);
        switch (commandPacketType) {
            case COM_QUIT:
                return new MySQLComQuitExecutor();
            case COM_QUERY:
                return new MySQLComQueryPacketExecutor((MySQLComQueryPacket) commandPacket);
            case COM_PING:
                return new MySQLComPingExecutor();
            default:
                return new MySQLUnsupportedCommandExecutor(commandPacketType);
        }
    }
}
