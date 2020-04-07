/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.transport.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * MySQL client/server protocol Authentication Method.
 *
 * @see <a href="https://dev.mysql.com/doc/internals/en/authentication-method.html">Authentication Method</a>
 */
@RequiredArgsConstructor
@Getter
public enum MySQLAuthenticationMethod {
    
    OLD_PASSWORD_AUTHENTICATION("mysql_old_password"),
    
    SECURE_PASSWORD_AUTHENTICATION("mysql_native_password"),
    
    CLEAR_TEXT_AUTHENTICATION("mysql_clear_password"),
    
    WINDOWS_NATIVE_AUTHENTICATION("authentication_windows_client"),
    
    SHA256("sha256_password");
    
    private final String methodName;
}
