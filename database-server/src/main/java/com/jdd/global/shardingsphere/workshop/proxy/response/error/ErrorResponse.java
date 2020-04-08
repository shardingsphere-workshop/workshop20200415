/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.response.error;

import com.jdd.global.shardingsphere.workshop.proxy.response.BackendResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Error response.
 */
@RequiredArgsConstructor
@Getter
public final class ErrorResponse implements BackendResponse {
    
    private final Exception cause;
}
