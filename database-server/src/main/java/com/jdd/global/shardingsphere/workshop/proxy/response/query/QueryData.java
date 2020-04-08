/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.response.query;

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
