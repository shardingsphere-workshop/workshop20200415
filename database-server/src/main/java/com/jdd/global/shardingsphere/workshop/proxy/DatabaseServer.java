/*
 * Copyright (c) 2020 ShardingSphere.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy;

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
 * Database-server.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatabaseServer {
    
    private static final DatabaseServer INSTANCE = new DatabaseServer();
    
    private EventLoopGroup bossGroup;
    
    private EventLoopGroup workerGroup;
    
    private ExecutorService userExecutorService;
    
    /**
     * Get instance of proxy context.
     *
     * @return instance of proxy context.
     */
    public static DatabaseServer getInstance() {
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
                .childHandler(new ServerHandlerInitializer());
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            userExecutorService.shutdownNow();
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
