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

package org.apache.shardingsphere.workshop.proxy.frontend.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.workshop.proxy.frontend.engine.AuthenticationEngine;
import org.apache.shardingsphere.workshop.proxy.frontend.engine.CommandExecuteEngine;
import org.apache.shardingsphere.workshop.proxy.frontend.engine.MySQLAuthenticationEngine;
import org.apache.shardingsphere.workshop.proxy.frontend.engine.MySQLCommandExecuteEngine;
import org.apache.shardingsphere.workshop.proxy.transport.error.MySQLErrPacketFactory;
import org.apache.shardingsphere.workshop.proxy.transport.payload.PacketPayload;
import org.apache.shardingsphere.workshop.proxy.transport.payload.MySQLPacketPayload;

import java.util.concurrent.ExecutorService;

/**
 * Frontend channel inbound handler.
 */
@RequiredArgsConstructor
@Slf4j
public final class FrontendChannelInboundHandler extends ChannelInboundHandlerAdapter {
    
    private final ExecutorService executorService;
    
    private final AuthenticationEngine authEngine = new MySQLAuthenticationEngine();
    
    private final CommandExecuteEngine commandExecuteEngine = new MySQLCommandExecuteEngine();
    
    private boolean authorized;
    
    @Override
    public void channelActive(final ChannelHandlerContext context) {
        authEngine.handshake(context);
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext context, final Object message) {
        if (!authorized) {
            authorized = auth(context, (ByteBuf) message);
            return;
        }
        executorService.execute(new CommandExecutorTask(commandExecuteEngine, context, message));
    }
    
    private boolean auth(final ChannelHandlerContext context, final ByteBuf message) {
        try (PacketPayload payload = new MySQLPacketPayload(message)) {
            return authEngine.auth(context, payload);
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            log.error("Exception occur: ", ex);
            context.write(MySQLErrPacketFactory.newInstance(1, ex));
        }
        return false;
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext context) {
        context.fireChannelInactive();
    }
}
