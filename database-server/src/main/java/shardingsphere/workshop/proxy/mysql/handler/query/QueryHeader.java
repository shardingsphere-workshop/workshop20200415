/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package shardingsphere.workshop.proxy.mysql.handler.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.shardingsphere.sql.parser.binder.metadata.column.ColumnMetaData;

/**
 * Query header.
 */
@AllArgsConstructor
@Getter
public final class QueryHeader {
    
    private final String schema;
    
    private final String table;
    
    private final String columnLabel;
    
    private final String columnName;
    
    private final int columnLength;
    
    private final Integer columnType;
    
    private final int decimals;

    private final boolean signed;

    private final boolean primaryKey;

    private final boolean notNull;

    private final boolean autoIncrement;
    
    public QueryHeader(final String schemaName, final String tableName, final ColumnMetaData columnMetaData) {
        this.columnName = columnMetaData.getName();
        schema = schemaName;
        columnLabel = columnMetaData.getName();
        columnLength = 100;
        columnType = columnMetaData.getDataType();
        decimals = 0;
        signed = false;
        notNull = true;
        autoIncrement = columnMetaData.isGenerated();
        table = tableName;
        primaryKey = columnMetaData.isPrimaryKey();
    }
}
