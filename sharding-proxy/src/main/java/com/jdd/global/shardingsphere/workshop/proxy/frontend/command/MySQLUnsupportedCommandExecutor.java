/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.frontend.command;

import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.MySQLPacket;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.command.MySQLCommandPacketType;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.error.MySQLServerErrorCode;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.generic.MySQLErrPacket;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;

/**
 * Unsupported command packet executor for MySQL.
 */
@RequiredArgsConstructor
public final class MySQLUnsupportedCommandExecutor implements CommandExecutor {
    
    private final MySQLCommandPacketType type;
    
    @Override
    public Collection<MySQLPacket> execute() {
        return Collections.singletonList(new MySQLErrPacket(1, MySQLServerErrorCode.UNSUPPORTED_COMMAND, type));
    }
}
