/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.frontend.command;

import com.jdd.global.shardingsphere.workshop.proxy.transport.MySQLPacketPayload;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.command.MySQLCommandPacketType;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.command.MySQLCommandPacketTypeLoader;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.command.query.MySQLComQueryPacket;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Command executor factory for MySQL.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class MySQLCommandExecutorFactory {
    
    /**
     * Create new instance of packet executor.
     *
     * @param payload command packet for MySQL
     * @return command executor
     */
    public static CommandExecutor newInstance(final MySQLPacketPayload payload) {
        MySQLCommandPacketType packetType = MySQLCommandPacketTypeLoader.getCommandPacketType(payload);
        switch (packetType) {
            case COM_QUERY:
                return new MySQLComQueryPacketExecutor(new MySQLComQueryPacket(payload));
            default:
                return new MySQLUnsupportedCommandExecutor(packetType);
        }
    }
}
