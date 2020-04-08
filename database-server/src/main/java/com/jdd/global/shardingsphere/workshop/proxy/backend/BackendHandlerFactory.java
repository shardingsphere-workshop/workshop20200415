/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.backend;

import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.sql.parser.SQLParserEngine;
import org.apache.shardingsphere.sql.parser.sql.statement.SQLStatement;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement;

/**
 * Backend handler factory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BackendHandlerFactory {
    
    /**
     * Create new instance of text protocol backend handler.
     *
     * @param sql SQL to be executed
     * @return instance of text protocol backend handler
     */
    public static BackendHandler newInstance(final String sql) {
        if (Strings.isNullOrEmpty(sql)) {
            return new SkipBackendHandler();
        }
        SQLStatement sqlStatement = new SQLParserEngine("MySQL").parse(sql, false);
        if (sqlStatement instanceof SelectStatement) {
            return new CSVQueryBackendHandler((SelectStatement) sqlStatement);
        }
        return new SkipBackendHandler();
    }
}
