
package shardingsphere.workshop.mysql.proxy.fixture;

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

/**
 * Database-proxy.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatabaseProxy {
    
    private static final DatabaseProxy INSTANCE = new DatabaseProxy();
    
    private EventLoopGroup bossGroup;
    
    private EventLoopGroup workerGroup;
    
    /**
     * Get instance of proxy context.
     *
     * @return instance of proxy context.
     */
    public static DatabaseProxy getInstance() {
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
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
