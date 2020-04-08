/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.packet.command.query;

import com.jdd.global.shardingsphere.workshop.proxy.packet.MySQLPacketPayload;
import com.jdd.global.shardingsphere.workshop.proxy.packet.command.MySQLCommandPacket;
import com.jdd.global.shardingsphere.workshop.proxy.packet.command.MySQLCommandPacketType;
import lombok.Getter;
import lombok.ToString;

/**
 * COM_QUERY command packet for MySQL.
 *
 * @see <a href="https://dev.mysql.com/doc/internals/en/com-query.html">COM_QUERY</a>
 */
@Getter
@ToString
public final class MySQLComQueryPacket extends MySQLCommandPacket {
    
    private final String sql;
    
    public MySQLComQueryPacket(final MySQLPacketPayload payload) {
        super(MySQLCommandPacketType.COM_QUERY);
        sql = payload.readStringEOF();
    }
    
    @Override
    public void doWrite(final MySQLPacketPayload payload) {
        payload.writeStringEOF(sql);
    }
}
