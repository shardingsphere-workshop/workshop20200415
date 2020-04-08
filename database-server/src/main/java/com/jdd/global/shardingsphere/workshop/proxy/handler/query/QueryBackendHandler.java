/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.handler.query;

import com.jdd.global.shardingsphere.workshop.proxy.handler.BackendHandler;
import com.jdd.global.shardingsphere.workshop.proxy.packet.MySQLPacket;

import java.sql.SQLException;
import java.util.List;

/**
 * Text protocol backend handler.
 */
public interface QueryBackendHandler extends BackendHandler {
    
    /**
     * Goto next result value.
     *
     * @return has more result value or not
     * @throws SQLException SQL exception
     */
    boolean next() throws SQLException;
    
    /**
     * Get query data.
     *
     * @return query data
     * @throws SQLException SQL exception
     */
    MySQLPacket getQueryData() throws SQLException;
}
