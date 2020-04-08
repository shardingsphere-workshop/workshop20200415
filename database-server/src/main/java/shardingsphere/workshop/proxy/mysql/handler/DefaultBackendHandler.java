/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package shardingsphere.workshop.proxy.mysql.handler;

import shardingsphere.workshop.proxy.mysql.packet.MySQLPacket;

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
