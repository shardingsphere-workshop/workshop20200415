/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package shardingsphere.workshop.proxy.mysql.handler.query;

import shardingsphere.workshop.proxy.mysql.handler.BackendHandler;
import shardingsphere.workshop.proxy.mysql.packet.MySQLPacket;

import java.sql.SQLException;

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
