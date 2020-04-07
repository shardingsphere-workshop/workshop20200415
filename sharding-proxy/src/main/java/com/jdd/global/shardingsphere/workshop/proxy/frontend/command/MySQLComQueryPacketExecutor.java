/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jdd.global.shardingsphere.workshop.proxy.frontend.command;

import com.jdd.global.shardingsphere.workshop.proxy.backend.BackendHandler;
import com.jdd.global.shardingsphere.workshop.proxy.backend.BackendHandlerFactory;
import com.jdd.global.shardingsphere.workshop.proxy.backend.response.BackendResponse;
import com.jdd.global.shardingsphere.workshop.proxy.backend.response.error.ErrorResponse;
import com.jdd.global.shardingsphere.workshop.proxy.backend.response.query.QueryHeader;
import com.jdd.global.shardingsphere.workshop.proxy.backend.response.query.QueryResponse;
import com.jdd.global.shardingsphere.workshop.proxy.backend.response.update.UpdateResponse;
import com.jdd.global.shardingsphere.workshop.proxy.transport.constant.MySQLColumnType;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.MySQLPacket;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.command.query.MySQLColumnDefinition41Packet;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.command.query.MySQLColumnFieldDetailFlag;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.command.query.MySQLComQueryPacket;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.command.query.MySQLFieldCountPacket;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.command.query.MySQLTextResultSetRowPacket;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.error.MySQLErrPacketFactory;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.generic.MySQLEofPacket;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.generic.MySQLErrPacket;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.generic.MySQLOKPacket;

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
