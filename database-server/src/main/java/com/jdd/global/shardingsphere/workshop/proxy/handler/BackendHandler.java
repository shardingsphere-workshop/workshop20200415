/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.handler;

import com.jdd.global.shardingsphere.workshop.proxy.response.BackendResponse;
import com.jdd.global.shardingsphere.workshop.proxy.response.query.QueryData;

import java.sql.SQLException;

/**
 * Text protocol backend handler.
 */
public interface BackendHandler {
    
    /**
     * Execute command.
     *
     * @return backend response
     * @throws SQLException SQL exception
     */
    BackendResponse execute() throws SQLException;
    
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
    QueryData getQueryData() throws SQLException;
}
