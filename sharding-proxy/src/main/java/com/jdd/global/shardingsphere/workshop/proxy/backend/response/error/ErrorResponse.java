/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.backend.response.error;

import com.jdd.global.shardingsphere.workshop.proxy.backend.response.BackendResponse;
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
