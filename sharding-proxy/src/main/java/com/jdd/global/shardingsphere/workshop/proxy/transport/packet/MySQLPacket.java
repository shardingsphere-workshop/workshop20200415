/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.transport.packet;

import com.jdd.global.shardingsphere.workshop.proxy.transport.MySQLPacketPayload;

/**
 * Database packet for MySQL.
 */
public interface MySQLPacket {
    
    int PAYLOAD_LENGTH = 3;
    
    int SEQUENCE_LENGTH = 1;
    
    /**
     * Get sequence ID.
     *
     * @return sequence ID
     */
    int getSequenceId();
    
    /**
     * Write packet to byte buffer.
     *
     * @param payload packet payload to be written
     */
    void write(MySQLPacketPayload payload);
}
