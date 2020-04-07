/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.frontend.handshake;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Connection ID generator.
 */
@NoArgsConstructor(access = AccessLevel.NONE)
public final class ConnectionIdGenerator {
    
    private static final ConnectionIdGenerator INSTANCE = new ConnectionIdGenerator();
    
    private int currentId;
    
    /**
     * Get instance.
     * 
     * @return instance
     */
    public static ConnectionIdGenerator getInstance() {
        return INSTANCE;
    } 
    
    /**
     * Get next connection ID.
     * 
     * @return next connection ID
     */
    public synchronized int nextId() {
        if (currentId >= Integer.MAX_VALUE) {
            currentId = 0;
        }
        return ++currentId;
    }
}
