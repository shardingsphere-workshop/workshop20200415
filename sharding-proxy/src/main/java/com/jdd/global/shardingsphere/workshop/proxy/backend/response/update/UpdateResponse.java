/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.backend.response.update;

import com.jdd.global.shardingsphere.workshop.proxy.backend.response.BackendResponse;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Update response.
 */
public final class UpdateResponse implements BackendResponse {
    
    private final List<Integer> updateCounts = new LinkedList<>();
    
    @Getter
    private final long lastInsertId;
    
    @Getter
    private long updateCount;
    
    public UpdateResponse() {
        this(Collections.emptyList());
    }
    
    public UpdateResponse(final Collection<ExecuteResponse> responseUnits) {
        for (ExecuteResponse each : responseUnits) {
            updateCount = ((ExecuteUpdateResponse) each).getUpdateCount();
            updateCounts.add(((ExecuteUpdateResponse) each).getUpdateCount());
        }
        lastInsertId = getLastInsertId(responseUnits);
    }
    
    private long getLastInsertId(final Collection<ExecuteResponse> responseUnits) {
        long result = 0;
        for (ExecuteResponse each : responseUnits) {
            result = Math.max(result, ((ExecuteUpdateResponse) each).getLastInsertId());
        }
        return result;
    }
    
    /**
     * Merge updated counts.
     */
    public void mergeUpdateCount() {
        updateCount = 0;
        for (int each : updateCounts) {
            updateCount += each;
        }
    }
}
