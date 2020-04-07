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

package com.jdd.global.shardingsphere.workshop.proxy.transport.packet.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Common error code.
 */
@RequiredArgsConstructor
@Getter
public enum MySQLServerErrorCode {
    
    CIRCUIT_BREAK_MODE(10000, "C10000", "Circuit break mode is ON."),
    
    UNSUPPORTED_COMMAND(10001, "C10001", "Unsupported command: [%s]"),
    
    UNKNOWN_EXCEPTION(10002, "C10002", "Unknown exception: [%s]"),
    
    ER_DBACCESS_DENIED_ERROR(1044, "42000", "Access denied for user '%s'@'%s' to database '%s'"),
    
    ER_ACCESS_DENIED_ERROR(1045, "28000", "Access denied for user '%s'@'%s' (using password: %s)"),
    
    ER_BAD_DB_ERROR(1049, "42000", "Unknown database '%s'"),
    
    ER_INTERNAL_ERROR(1815, "HY000", "Internal error: %s");
    
    private final int errorCode;
    
    private final String sqlState;
    
    private final String errorMessage;
}
