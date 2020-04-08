/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package shardingsphere.workshop.proxy.mysql.handler;

import shardingsphere.workshop.proxy.mysql.packet.MySQLPacket;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Command backend handler.
 */
public interface BackendHandler {
    
    /**
     * Execute command.
     *
     * @return backend response
     * @throws SQLException SQL exception
     */
    Collection<MySQLPacket> execute() throws SQLException;
}
