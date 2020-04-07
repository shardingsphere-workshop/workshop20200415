/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.workshop.proxy.frontend;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Sharding-Proxy.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShardingProxy {
    
    private static final ShardingProxy INSTANCE = new ShardingProxy();
    
    private EventLoopGroup bossGroup;
    
    private EventLoopGroup workerGroup;
    
    private ExecutorService userExecutorService;
    
    /**
     * Get instance of proxy context.
     *
     * @return instance of proxy context.
     */
    public static ShardingProxy getInstance() {
        return INSTANCE;
    }
    
    /**
     * Start Sharding-Proxy.
     *
     * @param port port
     */
    @SneakyThrows
    public void start(final int port) {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            userExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(8 * 1024 * 1024, 16 * 1024 * 1024))
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ServerHandlerInitializer(userExecutorService));
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            userExecutorService.shutdownNow();
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
