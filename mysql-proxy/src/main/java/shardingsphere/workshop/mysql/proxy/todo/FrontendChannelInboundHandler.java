
package shardingsphere.workshop.mysql.proxy.todo;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shardingsphere.workshop.mysql.proxy.fixture.MySQLAuthenticationHandler;
import shardingsphere.workshop.mysql.proxy.fixture.packet.MySQLErrPacketFactory;
import shardingsphere.workshop.mysql.proxy.fixture.packet.MySQLPacketPayload;
import shardingsphere.workshop.mysql.proxy.fixture.packet.constant.MySQLColumnType;
import shardingsphere.workshop.mysql.proxy.todo.packet.MySQLEofPacket;
import shardingsphere.workshop.mysql.proxy.todo.packet.MySQLColumnDefinition41Packet;
import shardingsphere.workshop.mysql.proxy.todo.packet.MySQLFieldCountPacket;
import shardingsphere.workshop.mysql.proxy.todo.packet.MySQLTextResultSetRowPacket;

/**
 * Frontend channel inbound handler.
 */
@RequiredArgsConstructor
@Slf4j
public final class FrontendChannelInboundHandler extends ChannelInboundHandlerAdapter {
    
    private final MySQLAuthenticationHandler authHandler = new MySQLAuthenticationHandler();
    
    private boolean authorized;
    
    @Override
    public void channelActive(final ChannelHandlerContext context) {
        authHandler.handshake(context);
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext context, final Object message) {
        if (!authorized) {
            authorized = auth(context, (ByteBuf) message);
            return;
        }
        try (MySQLPacketPayload payload = new MySQLPacketPayload((ByteBuf) message)) {
            executeCommand(context, payload);
        } catch (final Exception ex) {
            log.error("Exception occur: ", ex);
            context.writeAndFlush(MySQLErrPacketFactory.newInstance(1, ex));
        }
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext context) {
        context.fireChannelInactive();
    }
    
    private boolean auth(final ChannelHandlerContext context, final ByteBuf message) {
        try (MySQLPacketPayload payload = new MySQLPacketPayload(message)) {
            return authHandler.auth(context, payload);
        } catch (final Exception ex) {
            log.error("Exception occur: ", ex);
            context.write(MySQLErrPacketFactory.newInstance(1, ex));
        }
        return false;
    }
    
    private void executeCommand(final ChannelHandlerContext context, final MySQLPacketPayload payload) {
        Preconditions.checkState(0x03 == payload.readInt1(), "only support COM_QUERY command type");
        // TODO 1. Read SQL from payload, then system.out it
        // TODO 2. Return mock MySQLPacket to client (header: MySQLFieldCountPacket + MySQLColumnDefinition41Packet + MySQLEofPacket, content: MySQLTextResultSetRowPacket
        // TODO 3. Parse SQL, return actual data according to SQLStatement
        context.write(new MySQLFieldCountPacket(1, 1));
        context.write(new MySQLColumnDefinition41Packet(2, 0, "sharding_db", "t_order", "t_order", "order_id", "order_id", 100, MySQLColumnType.MYSQL_TYPE_STRING,0));
        context.write(new MySQLEofPacket(3));
        context.write(new MySQLTextResultSetRowPacket(4, ImmutableList.of(100)));
        context.write(new MySQLEofPacket(5));
        context.flush();
    }
}
