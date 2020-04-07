/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.backend.response.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Query data.
 */
@RequiredArgsConstructor
@Getter
public final class QueryData {
    
    private final List<Object> data;
}
