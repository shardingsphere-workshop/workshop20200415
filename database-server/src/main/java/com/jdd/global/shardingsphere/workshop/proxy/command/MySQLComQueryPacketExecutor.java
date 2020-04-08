/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.command;

import com.jdd.global.shardingsphere.workshop.proxy.handler.BackendHandler;
import com.jdd.global.shardingsphere.workshop.proxy.handler.BackendHandlerFactory;
import com.jdd.global.shardingsphere.workshop.proxy.response.BackendResponse;
import com.jdd.global.shardingsphere.workshop.proxy.response.error.ErrorResponse;
import com.jdd.global.shardingsphere.workshop.proxy.response.query.QueryHeader;
import com.jdd.global.shardingsphere.workshop.proxy.response.query.QueryResponse;
import com.jdd.global.shardingsphere.workshop.proxy.response.update.UpdateResponse;
import com.jdd.global.shardingsphere.workshop.proxy.packet.constant.MySQLColumnType;
import com.jdd.global.shardingsphere.workshop.proxy.packet.MySQLPacket;
import com.jdd.global.shardingsphere.workshop.proxy.packet.command.query.MySQLColumnDefinition41Packet;
import com.jdd.global.shardingsphere.workshop.proxy.packet.command.query.MySQLColumnFieldDetailFlag;
import com.jdd.global.shardingsphere.workshop.proxy.packet.command.query.MySQLComQueryPacket;
import com.jdd.global.shardingsphere.workshop.proxy.packet.command.query.MySQLFieldCountPacket;
import com.jdd.global.shardingsphere.workshop.proxy.packet.command.query.MySQLTextResultSetRowPacket;
import com.jdd.global.shardingsphere.workshop.proxy.packet.error.MySQLErrPacketFactory;
import com.jdd.global.shardingsphere.workshop.proxy.packet.generic.MySQLEofPacket;
import com.jdd.global.shardingsphere.workshop.proxy.packet.generic.MySQLErrPacket;
import com.jdd.global.shardingsphere.workshop.proxy.packet.generic.MySQLOKPacket;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * COM_QUERY command packet executor for MySQL.
 */
public final class MySQLComQueryPacketExecutor implements ComQueryCommandExecutor {
    
    private final BackendHandler backendHandler;
    
    private volatile boolean isQuery;
    
    private volatile boolean isErrorResponse;
    
    private int currentSequenceId;
    
    public MySQLComQueryPacketExecutor(final MySQLComQueryPacket comQueryPacket) {
        backendHandler = BackendHandlerFactory.newInstance(comQueryPacket.getSql());
    }
    
    @Override
    public Collection<MySQLPacket> execute() throws SQLException {
        BackendResponse backendResponse = backendHandler.execute();
        if (backendResponse instanceof ErrorResponse) {
            isErrorResponse = true;
            return Collections.singletonList(createErrorPacket(((ErrorResponse) backendResponse).getCause()));
        }
        if (backendResponse instanceof UpdateResponse) {
            return Collections.singletonList(createUpdatePacket((UpdateResponse) backendResponse));
        }
        isQuery = true;
        return createQueryPackets((QueryResponse) backendResponse);
    }
    
    private MySQLErrPacket createErrorPacket(final Exception cause) {
        return MySQLErrPacketFactory.newInstance(1, cause);
    }
    
    private MySQLOKPacket createUpdatePacket(final UpdateResponse updateResponse) {
        return new MySQLOKPacket(1, updateResponse.getUpdateCount(), updateResponse.getLastInsertId());
    }
    
    private Collection<MySQLPacket> createQueryPackets(final QueryResponse backendResponse) {
        Collection<MySQLPacket> result = new LinkedList<>();
        List<QueryHeader> queryHeader = backendResponse.getQueryHeaders();
        result.add(new MySQLFieldCountPacket(++currentSequenceId, queryHeader.size()));
        for (QueryHeader each : queryHeader) {
            result.add(new MySQLColumnDefinition41Packet(++currentSequenceId, getColumnFieldDetailFlag(each), each.getSchema(), each.getTable(), each.getTable(),
                    each.getColumnLabel(), each.getColumnName(), each.getColumnLength(), MySQLColumnType.valueOfJDBCType(each.getColumnType()), each.getDecimals()));
        }
        result.add(new MySQLEofPacket(++currentSequenceId));
        return result;
    }

    private int getColumnFieldDetailFlag(final QueryHeader header) {
        int result = 0;
        if (header.isPrimaryKey()) {
            result += MySQLColumnFieldDetailFlag.PRIMARY_KEY.getValue();
        }
        if (header.isNotNull()) {
            result += MySQLColumnFieldDetailFlag.NOT_NULL.getValue();
        }
        if (!header.isSigned()) {
            result += MySQLColumnFieldDetailFlag.UNSIGNED.getValue();
        }
        if (header.isAutoIncrement()) {
            result += MySQLColumnFieldDetailFlag.AUTO_INCREMENT.getValue();
        }
        return result;
    }
    
    @Override
    public boolean isQuery() {
        return isQuery;
    }
    
    @Override
    public boolean isErrorResponse() {
        return isErrorResponse;
    }
    
    @Override
    public boolean next() throws SQLException {
        return backendHandler.next();
    }
    
    @Override
    public MySQLPacket getQueryData() throws SQLException {
        return new MySQLTextResultSetRowPacket(++currentSequenceId, backendHandler.getQueryData().getData());
    }
}
