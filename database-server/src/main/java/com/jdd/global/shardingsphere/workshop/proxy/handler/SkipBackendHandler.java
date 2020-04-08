/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.handler;

import com.jdd.global.shardingsphere.workshop.proxy.response.BackendResponse;
import com.jdd.global.shardingsphere.workshop.proxy.response.query.QueryData;
import com.jdd.global.shardingsphere.workshop.proxy.response.update.UpdateResponse;

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
