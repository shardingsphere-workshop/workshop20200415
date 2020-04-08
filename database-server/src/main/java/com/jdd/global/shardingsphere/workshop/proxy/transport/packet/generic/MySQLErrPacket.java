/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.transport.packet.generic;

import com.jdd.global.shardingsphere.workshop.proxy.transport.MySQLPacketPayload;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.MySQLPacket;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.error.MySQLServerErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ERR packet protocol for MySQL.
 * 
 * @see <a href="https://dev.mysql.com/doc/internals/en/packet-ERR_Packet.html">ERR Packet</a>
 */
@RequiredArgsConstructor
@Getter
public final class MySQLErrPacket implements MySQLPacket {
    
    /**
     * Header of ERR packet.
     */
    public static final int HEADER = 0xff;
    
    private static final String SQL_STATE_MARKER = "#";
    
    private final int sequenceId;
    
    private final int errorCode;
    
    private final String sqlState;
    
    private final String errorMessage;
    
    public MySQLErrPacket(final int sequenceId, final MySQLServerErrorCode sqlErrorCode, final Object... errorMessageArguments) {
        this(sequenceId, sqlErrorCode.getErrorCode(), sqlErrorCode.getSqlState(), String.format(sqlErrorCode.getErrorMessage(), errorMessageArguments));
    }
    
    @Override
    public void write(final MySQLPacketPayload payload) {
        payload.writeInt1(HEADER);
        payload.writeInt2(errorCode);
        payload.writeStringFix(SQL_STATE_MARKER);
        payload.writeStringFix(sqlState);
        payload.writeStringEOF(errorMessage);
    }
}
