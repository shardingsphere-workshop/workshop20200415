/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.packet.command;

import com.jdd.global.shardingsphere.workshop.proxy.packet.MySQLPacketPayload;
import com.jdd.global.shardingsphere.workshop.proxy.packet.MySQLPacket;
import lombok.RequiredArgsConstructor;

/**
 * Command packet for MySQL.
 */
@RequiredArgsConstructor
public abstract class MySQLCommandPacket implements MySQLPacket {
    
    private final MySQLCommandPacketType type;
    
    @Override
    public final void write(final MySQLPacketPayload payload) {
        payload.writeInt1(type.getValue());
        doWrite(payload);
    }
    
    protected void doWrite(final MySQLPacketPayload payload) {
    }
    
    @Override
    public final int getSequenceId() {
        return 0;
    }
}
