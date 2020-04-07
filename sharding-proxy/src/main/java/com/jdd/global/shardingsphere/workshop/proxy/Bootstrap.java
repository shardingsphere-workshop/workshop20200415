/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy;

import com.google.common.primitives.Ints;
import com.jdd.global.shardingsphere.workshop.proxy.backend.schema.CSVLogicSchema;
import com.jdd.global.shardingsphere.workshop.proxy.frontend.ShardingProxy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * Sharding-Proxy Bootstrap.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Bootstrap {
    
    private static final int DEFAULT_PORT = 3307;

    /**
     * Main entrance.
     *
     * @param args startup arguments
     */
    public static void main(final String[] args) throws IOException {
        CSVLogicSchema.getInstance().init();
        ShardingProxy.getInstance().start(getPort(args));
    }
    
    private static int getPort(final String[] args) {
        if (0 == args.length) {
            return DEFAULT_PORT;
        }
        Integer paredPort = Ints.tryParse(args[0]);
        return paredPort == null ? DEFAULT_PORT : paredPort;
    }
}
