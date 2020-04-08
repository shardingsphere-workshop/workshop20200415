/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.transport.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Sharding-Proxy's information for MySQL.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MySQLServerInfo {
    
    /**
     * Protocol version is always 0x0A.
     */
    public static final int PROTOCOL_VERSION = 0x0A;
    
    /**
     * Server version.
     */
    public static final String SERVER_VERSION = "5.6.4-Sharding-Proxy 5.0.0-RC1";
    
    /**
     * Charset code 0x21 is utf8_general_ci.
     */
    public static final int CHARSET = 0x21;
}
