/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.backend.response.query;

import com.jdd.global.shardingsphere.workshop.proxy.backend.response.BackendResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * Query response.
 */
@RequiredArgsConstructor
@Getter
public final class QueryResponse implements BackendResponse {
    
    private final List<QueryHeader> queryHeaders;
    
    private final List<Object[]> queryResults = new LinkedList<>();
}
