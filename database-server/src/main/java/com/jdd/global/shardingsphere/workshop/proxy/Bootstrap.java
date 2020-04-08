/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy;

import com.google.common.primitives.Ints;
import com.jdd.global.shardingsphere.workshop.proxy.schema.CSVLogicSchema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * Database-server Bootstrap.
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
        DatabaseServer.getInstance().start(getPort(args));
    }
    
    private static int getPort(final String[] args) {
        if (0 == args.length) {
            return DEFAULT_PORT;
        }
        Integer paredPort = Ints.tryParse(args[0]);
        return paredPort == null ? DEFAULT_PORT : paredPort;
    }
}
