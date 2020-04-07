/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.frontend.command;

import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.MySQLPacket;

import java.sql.SQLException;

/**
 * Query command executor.
 */
public interface ComQueryCommandExecutor extends CommandExecutor {
    
    /**
     * Judge is error response.
     *
     * @return is error response or not
     */
    boolean isErrorResponse();
    
    /**
     * Judge is query SQL or not.
     *
     * @return is query SQL or not
     */
    boolean isQuery();
    
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
     * @return MySQL packet of query data
     * @throws SQLException SQL exception
     */
    MySQLPacket getQueryData() throws SQLException;
}
