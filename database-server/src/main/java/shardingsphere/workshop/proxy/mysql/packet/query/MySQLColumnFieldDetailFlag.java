/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package shardingsphere.workshop.proxy.mysql.packet.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * MySQL Column Field Detail Flag.
 * 
 * @see <a href="https://mariadb.com/kb/en/library/resultset/#field-detail-flag">Field detail flag</a>
 */
@RequiredArgsConstructor
@Getter
public enum MySQLColumnFieldDetailFlag {

    NOT_NULL(1),

    PRIMARY_KEY(2),

    UNIQUE_KEY(4),

    MULTIPLE_KEY(8),

    BLOB(16),

    UNSIGNED(32),

    ZEROFILL_FLAG(64),

    BINARY_COLLATION(128),

    ENUM(256),

    AUTO_INCREMENT(512),

    TIMESTAMP(1024),

    SET(2048),

    NO_DEFAULT_VALUE_FLAG(4096),

    ON_UPDATE_NOW_FLAG(8192),

    NUM_FLAG(32768);
    
    private final int value;
}
