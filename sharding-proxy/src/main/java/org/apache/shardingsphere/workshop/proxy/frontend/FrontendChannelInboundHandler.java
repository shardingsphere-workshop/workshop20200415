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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.workshop.proxy.frontend.core.spi.DatabaseProtocolFrontendEngine;
import org.apache.shardingsphere.workshop.proxy.transport.core.payload.PacketPayload;

/**
 * Frontend channel inbound handler.
 */
@RequiredArgsConstructor
@Slf4j
public final class FrontendChannelInboundHandler extends ChannelInboundHandlerAdapter {
    
    private final DatabaseProtocolFrontendEngine databaseProtocolFrontendEngine;
    
    private volatile boolean authorized;
    
    @Override
    public void channelActive(final ChannelHandlerContext context) {
        databaseProtocolFrontendEngine.getAuthEngine().handshake(context);
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext context, final Object message) {
        if (!authorized) {
            authorized = auth(context, (ByteBuf) message);
            return;
        }
        UserExecutorGroup.getInstance().getExecutorService().execute(new CommandExecutorTask(databaseProtocolFrontendEngine, context, message));
    }
    
    private boolean auth(final ChannelHandlerContext context, final ByteBuf message) {
        try (PacketPayload payload = databaseProtocolFrontendEngine.getCodecEngine().createPacketPayload(message)) {
            return databaseProtocolFrontendEngine.getAuthEngine().auth(context, payload);
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            log.error("Exception occur: ", ex);
            context.write(databaseProtocolFrontendEngine.getCommandExecuteEngine().getErrorPacket(ex));
        }
        return false;
    }
    
    @Override
    @SneakyThrows
    public void channelInactive(final ChannelHandlerContext context) {
        context.fireChannelInactive();
    }
    
    @Override
    public void channelWritabilityChanged(final ChannelHandlerContext context) {
    }
}
