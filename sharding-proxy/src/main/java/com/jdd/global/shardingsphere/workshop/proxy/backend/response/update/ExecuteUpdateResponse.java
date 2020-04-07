/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.backend.response.update;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Execute update response.
 */
@RequiredArgsConstructor
@Getter
public final class ExecuteUpdateResponse implements ExecuteResponse {
    
    private final int updateCount;
    
    private final long lastInsertId;
}
