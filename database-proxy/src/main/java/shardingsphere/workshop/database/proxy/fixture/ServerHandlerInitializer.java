
package shardingsphere.workshop.database.proxy.fixture;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;
import shardingsphere.workshop.database.proxy.todo.FrontendChannelInboundHandler;

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
