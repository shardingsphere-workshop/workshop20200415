/*
 * Copyright (c) 2020 global.jdd.com.
 * All rights reserved.
 */

package com.jdd.global.shardingsphere.workshop.proxy.frontend;

import com.jdd.global.shardingsphere.workshop.proxy.transport.MySQLPacketCodec;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ExecutorService;

/**
 * Channel initializer.
 */
@RequiredArgsConstructor
public final class ServerHandlerInitializer extends ChannelInitializer<SocketChannel> {
    
    @Override
    protected void initChannel(final SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new MySQLPacketCodec());
        pipeline.addLast(new FrontendChannelInboundHandler());
    }
}
