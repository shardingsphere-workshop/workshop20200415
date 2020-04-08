/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.packet.handshake;

import com.google.common.primitives.Bytes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Auth plugin data for MySQL.
 * 
 * <p>
 *     The auth-plugin-data is the concatenation of strings auth-plugin-data-part-1 and auth-plugin-data-part-2.
 *     The auth-plugin-data-part-1's length is 8; The auth-plugin-data-part-2's length is 12.
 * </p>
 */
@RequiredArgsConstructor
@Getter
public final class MySQLAuthPluginData {
    
    private final byte[] authPluginDataPart1;
    
    private final byte[] authPluginDataPart2;
    
    public MySQLAuthPluginData() {
        this(MySQLRandomGenerator.getInstance().generateRandomBytes(8), MySQLRandomGenerator.getInstance().generateRandomBytes(12));
    }
    
    /**
     * Get auth plugin data.
     * 
     * @return auth plugin data
     */
    public byte[] getAuthPluginData() {
        return Bytes.concat(authPluginDataPart1, authPluginDataPart2);
    }
}
