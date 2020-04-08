/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.transport.packet.command;

import com.google.common.base.Preconditions;
import com.jdd.global.shardingsphere.workshop.proxy.transport.MySQLPacketPayload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Command packet type loader for MySQL.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MySQLCommandPacketTypeLoader {
    
    /**
     * Get command packet type.
     *
     * @param payload packet payload for MySQL
     * @return command packet type for MySQL
     */
    public static MySQLCommandPacketType getCommandPacketType(final MySQLPacketPayload payload) {
        Preconditions.checkArgument(0 == payload.readInt1(), "Sequence ID of MySQL command packet must be `0`.");
        return MySQLCommandPacketType.valueOf(payload.readInt1());
    }
}
