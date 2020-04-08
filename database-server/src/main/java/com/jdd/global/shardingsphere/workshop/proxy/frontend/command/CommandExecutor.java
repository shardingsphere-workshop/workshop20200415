/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.frontend.command;

import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.MySQLPacket;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Command executor.
 */
public interface CommandExecutor {
    
    /**
     * Execute command.
     *
     * @return database packets to be sent
     * @throws SQLException SQL exception
     */
    Collection<MySQLPacket> execute() throws SQLException;
}
