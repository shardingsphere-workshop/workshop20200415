/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.handler;

import com.jdd.global.shardingsphere.workshop.proxy.packet.MySQLPacket;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Default backend handler
 *
 */
public class DefaultBackendHandler implements BackendHandler {
    
    @Override
    public Collection<MySQLPacket> execute() {
        return new LinkedList<>();
    }
}
