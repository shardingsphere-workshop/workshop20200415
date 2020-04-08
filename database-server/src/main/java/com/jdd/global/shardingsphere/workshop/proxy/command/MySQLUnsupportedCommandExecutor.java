/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.command;

import com.jdd.global.shardingsphere.workshop.proxy.packet.MySQLPacket;
import com.jdd.global.shardingsphere.workshop.proxy.packet.command.MySQLCommandPacketType;
import com.jdd.global.shardingsphere.workshop.proxy.packet.error.MySQLServerErrorCode;
import com.jdd.global.shardingsphere.workshop.proxy.packet.generic.MySQLErrPacket;
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
