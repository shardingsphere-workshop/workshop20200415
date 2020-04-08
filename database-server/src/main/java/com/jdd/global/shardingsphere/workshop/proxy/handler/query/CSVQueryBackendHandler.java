/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.handler.query;

import au.com.bytecode.opencsv.CSVReader;
import com.jdd.global.shardingsphere.workshop.proxy.packet.MySQLPacket;
import com.jdd.global.shardingsphere.workshop.proxy.packet.query.MySQLColumnDefinition41Packet;
import com.jdd.global.shardingsphere.workshop.proxy.packet.query.MySQLColumnFieldDetailFlag;
import com.jdd.global.shardingsphere.workshop.proxy.packet.query.MySQLFieldCountPacket;
import com.jdd.global.shardingsphere.workshop.proxy.packet.query.MySQLTextResultSetRowPacket;
import com.jdd.global.shardingsphere.workshop.proxy.packet.constant.MySQLColumnType;
import com.jdd.global.shardingsphere.workshop.proxy.packet.MySQLEofPacket;
import com.jdd.global.shardingsphere.workshop.proxy.CSVLogicSchema;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * CSV backend handler.
 */
@RequiredArgsConstructor
public final class CSVQueryBackendHandler implements QueryBackendHandler {
    
    private final CSVLogicSchema logicSchema = CSVLogicSchema.getInstance();
    
    private final SelectStatement sqlStatement;
    
    private Iterator<Object[]> iterator;
    
    private int currentSequenceId;
    
    @Override
    public Collection<MySQLPacket> execute() {
        String tableName = sqlStatement.getSimpleTableSegments().iterator().next().getTableName().getIdentifier().getValue();
        List<QueryHeader> queryHeaders = createQueryHeader(tableName);
        prepareQueryData(tableName);
        return createHeaderPackets(queryHeaders);
    }
    
    private List<QueryHeader> createQueryHeader(String tableName) {
        List<QueryHeader> result = new LinkedList<>();
        logicSchema.getMetadata().get(tableName).getColumns().values()
            .forEach(columnMetaData -> result.add(new QueryHeader(logicSchema.getSchemaName(), tableName, columnMetaData)));
        return result;
    }
    
    @SneakyThrows
    private void prepareQueryData(final String tableName) {
        List<Object[]> data = new LinkedList<>();
        try (CSVReader csvReader = logicSchema.getCSVReader(tableName)) {
            csvReader.readNext();
            data.addAll(csvReader.readAll());
        }
        iterator = data.iterator();
    }
    
    private Collection<MySQLPacket> createHeaderPackets(final List<QueryHeader> queryHeader) {
        Collection<MySQLPacket> result = new LinkedList<>();
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
    public boolean next() {
        return iterator.hasNext();
    }
    
    @Override
    public MySQLPacket getQueryData() {
        return new MySQLTextResultSetRowPacket(++currentSequenceId, Arrays.asList(iterator.next()));
    }
}
