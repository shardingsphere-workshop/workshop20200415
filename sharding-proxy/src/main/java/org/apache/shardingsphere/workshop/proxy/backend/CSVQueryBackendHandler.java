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

package org.apache.shardingsphere.workshop.proxy.backend;

import au.com.bytecode.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement;
import org.apache.shardingsphere.workshop.proxy.backend.schema.CSVLogicSchema;
import org.apache.shardingsphere.workshop.proxy.backend.response.query.QueryData;
import org.apache.shardingsphere.workshop.proxy.backend.response.query.QueryHeader;
import org.apache.shardingsphere.workshop.proxy.backend.response.query.QueryResponse;

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
