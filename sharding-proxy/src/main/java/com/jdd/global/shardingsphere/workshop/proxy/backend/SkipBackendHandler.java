/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.backend;

import com.jdd.global.shardingsphere.workshop.proxy.backend.response.BackendResponse;
import com.jdd.global.shardingsphere.workshop.proxy.backend.response.query.QueryData;
import com.jdd.global.shardingsphere.workshop.proxy.backend.response.update.UpdateResponse;

/**
 * Skip backend handler.
 */
public final class SkipBackendHandler implements BackendHandler {
    
    @Override
    public BackendResponse execute() {
        return new UpdateResponse();
    }
    
    @Override
    public boolean next() {
        return false;
    }
    
    @Override
    public QueryData getQueryData() {
        return null;
    }
}
