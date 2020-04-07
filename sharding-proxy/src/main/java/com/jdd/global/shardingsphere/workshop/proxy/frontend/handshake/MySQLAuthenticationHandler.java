/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.frontend.handshake;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.error.MySQLServerErrorCode;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.handshake.MySQLAuthPluginData;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.handshake.MySQLHandshakeResponse41Packet;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication handler for MySQL.
 */
@Getter
public final class MySQLAuthenticationHandler {
    
    private final MySQLAuthPluginData authPluginData = new MySQLAuthPluginData();
    
    private final Map<String, String> authentication = ImmutableMap.of("root", "root");
    
    /**
     * Login.
     *
     * @param response41 handshake response
     * @return login success or failure
     */
    public Optional<MySQLServerErrorCode> login(final MySQLHandshakeResponse41Packet response41) {
        if (!authentication.containsKey(response41.getUsername()) || !isPasswordRight(authentication.get(response41.getUsername()), response41.getAuthResponse())) {
            return Optional.of(MySQLServerErrorCode.ER_ACCESS_DENIED_ERROR);
        }
        return Optional.empty();
    }
    
    private boolean isPasswordRight(final String password, final byte[] authResponse) {
        return Strings.isNullOrEmpty(password) || Arrays.equals(getAuthCipherBytes(password), authResponse);
    }
    
    private byte[] getAuthCipherBytes(final String password) {
        byte[] sha1Password = DigestUtils.sha1(password);
        byte[] doubleSha1Password = DigestUtils.sha1(sha1Password);
        byte[] concatBytes = new byte[authPluginData.getAuthPluginData().length + doubleSha1Password.length];
        System.arraycopy(authPluginData.getAuthPluginData(), 0, concatBytes, 0, authPluginData.getAuthPluginData().length);
        System.arraycopy(doubleSha1Password, 0, concatBytes, authPluginData.getAuthPluginData().length, doubleSha1Password.length);
        byte[] sha1ConcatBytes = DigestUtils.sha1(concatBytes);
        return xor(sha1Password, sha1ConcatBytes);
    }
    
    private byte[] xor(final byte[] input, final byte[] secret) {
        final byte[] result = new byte[input.length];
        for (int i = 0; i < input.length; ++i) {
            result[i] = (byte) (input[i] ^ secret[i]);
        }
        return result;
    }
}
