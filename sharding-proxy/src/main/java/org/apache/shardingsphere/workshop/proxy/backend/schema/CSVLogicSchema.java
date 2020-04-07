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

package org.apache.shardingsphere.workshop.proxy.backend.schema;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.shardingsphere.sql.parser.binder.metadata.column.ColumnMetaData;
import org.apache.shardingsphere.sql.parser.binder.metadata.table.TableMetaData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Logic schema.
 */
@Getter
public class CSVLogicSchema {
    
    private final static CSVLogicSchema instance = new CSVLogicSchema();
    
    private final String schemaName = "sharding-db";
    
    private final Map<String, TableMetaData> metadata = new HashMap<>();
    
    private final Map<String, File> repository = new HashMap<>();
    
    /**
     * Get instance.
     *
     * @return CSV logic schema
     */
    public static CSVLogicSchema getInstance() {
        return instance;
    }
    
    /**
     * init.
     *
     * @throws IOException IO exception
     */
    public void init() throws IOException {
        File csvDirectory = new File(CSVLogicSchema.class.getResource("/data").getFile());
        File[] files = csvDirectory.listFiles((file) -> file.getName().endsWith(".csv"));
        if (null == files) {
            return;
        }
        for (File each : files) {
            String tableName = each.getName().replace(".csv", "");
            try (CSVReader csvReader = getCSVReader(each)) {
                String[] header = csvReader.readNext();
                loadMetaData(tableName, header);
            }
            repository.put(tableName, each);
        }
    }
    
    private CSVReader getCSVReader(final File file) throws FileNotFoundException {
        return new CSVReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    }
    
    private void loadMetaData(final String tableName, final String[] header) {
        Collection<ColumnMetaData> columnMetaDataList = new LinkedList<>();
        for (String each : header) {
            String[] column = each.split(":");
            Preconditions.checkState(column.length == 2, "csv file header should be column:type pattern.");
            columnMetaDataList.add(new ColumnMetaData(column[0], 1, column[1], false, false, true));
        }
        metadata.put(tableName, new TableMetaData(columnMetaDataList, new LinkedList<>()));
    }
    
    /**
     * Get CSV reader.
     *
     * @param name file name
     * @return CSV reader
     * @throws FileNotFoundException File not found exception
     */
    public CSVReader getCSVReader(final String name) throws FileNotFoundException {
        return new CSVReader(new InputStreamReader(new FileInputStream(repository.get(name)), StandardCharsets.UTF_8));
    }
}
