/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.backend;

import au.com.bytecode.opencsv.CSVReader;
import com.jdd.global.shardingsphere.workshop.proxy.backend.response.query.QueryData;
import com.jdd.global.shardingsphere.workshop.proxy.backend.response.query.QueryHeader;
import com.jdd.global.shardingsphere.workshop.proxy.backend.response.query.QueryResponse;
import com.jdd.global.shardingsphere.workshop.proxy.backend.schema.CSVLogicSchema;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * CSV backend handler.
 */
@RequiredArgsConstructor
public final class CSVQueryBackendHandler implements BackendHandler {
    
    private final CSVLogicSchema logicSchema = CSVLogicSchema.getInstance();
    
    private final SelectStatement sqlStatement;
    
    private Iterator<Object[]> iterator;
    
    @Override
    public QueryResponse execute() {
        return createQueryResponse(sqlStatement);
    }
    
    @SneakyThrows
    private QueryResponse createQueryResponse(final SelectStatement sqlStatement) {
        String tableName = sqlStatement.getSimpleTableSegments().iterator().next().getTableName().getIdentifier().getValue();
        QueryResponse result = new QueryResponse(createQueryHeader(tableName));
        try (CSVReader csvReader = logicSchema.getCSVReader(tableName)) {
            csvReader.readNext();
            result.getQueryResults().addAll(csvReader.readAll());
        }
        iterator = result.getQueryResults().iterator();
        return result;
    }
    
    private List<QueryHeader> createQueryHeader(String tableName) {
        List<QueryHeader> result = new LinkedList<>();
        logicSchema.getMetadata().get(tableName).getColumns().values()
            .forEach(columnMetaData -> result.add(new QueryHeader(logicSchema.getSchemaName(), tableName, columnMetaData)));
        return result;
    }
    
    @Override
    public boolean next() {
        return iterator.hasNext();
    }
    
    @Override
    public QueryData getQueryData() {
        return new QueryData(Arrays.asList(iterator.next()));
    }
}
