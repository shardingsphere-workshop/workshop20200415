
package shardingsphere.workshop.database.netty;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shardingsphere.workshop.database.mysql.MySQLAuthenticationHandler;
import shardingsphere.workshop.database.mysql.packet.MySQLEofPacket;
import shardingsphere.workshop.database.mysql.packet.MySQLErrPacketFactory;
import shardingsphere.workshop.database.mysql.packet.MySQLOKPacket;
import shardingsphere.workshop.database.mysql.packet.MySQLPacket;
import shardingsphere.workshop.database.mysql.packet.MySQLPacketPayload;
import shardingsphere.workshop.database.mysql.packet.constant.MySQLColumnType;
import shardingsphere.workshop.database.mysql.packet.query.MySQLColumnDefinition41Packet;
import shardingsphere.workshop.database.mysql.packet.query.MySQLFieldCountPacket;
import shardingsphere.workshop.database.mysql.packet.query.MySQLTextResultSetRowPacket;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
    
    private void executeCommand(final ChannelHandlerContext context, final MySQLPacketPayload payload) throws SQLException {
        Preconditions.checkArgument(0 == payload.readInt1(), "Sequence ID of MySQL command packet must be `0`.");
        Preconditions.checkState(0x03 == payload.readInt1(), "only support COM_QUERY command type");
        String sql = payload.readStringEOF();
        String skipSQL = "select @@version_comment limit 1";
        if (skipSQL.equals(sql.toLowerCase())) {
            context.writeAndFlush(new MySQLOKPacket(0));
            return;
        }
        // TODO 1. Read SQL from payload, then system.out it
        // TODO 2. Return mock MySQLPacket to client (header: MySQLFieldCountPacket + MySQLColumnDefinition41Packet + MySQLEofPacket, content: MySQLTextResultSetRowPacket
        // TODO 3. Parse SQL, return actual data according to SQLStatement
//        int currentSequenceId = 0;
//        Collection<MySQLPacket> headerPackets = new LinkedList<>();
        context.write(new MySQLFieldCountPacket(0, 1));
        context.write(new MySQLColumnDefinition41Packet(1, 0, "sharding_db", "t_order", "t_order", "order_id", "order_id", 100, MySQLColumnType.MYSQL_TYPE_STRING,0));
        context.write(new MySQLEofPacket(2));
        context.write(new MySQLTextResultSetRowPacket(3, ImmutableList.of(100)));
        context.write(new MySQLEofPacket(4));
        context.flush();
//        context.writeAndFlush(new MySQLEofPacket(++currentSequenceId));
    
//        context.writeAndFlush(new MySQLFieldCountPacket(++currentSequenceId, 0));
//        headerPackets.add(new MySQLFieldCountPacket(++currentSequenceId, 1));
//        headerPackets.add(new MySQLColumnDefinition41Packet(++currentSequenceId, 0, "sharding_db", "t_order", "t_order", "order_id", "order_id", 100, MySQLColumnType.MYSQL_TYPE_LONG,0));
//        headerPackets.add(new MySQLEofPacket(++currentSequenceId));
//        context.writeAndFlush(headerPackets);
//        context.writeAndFlush(new MySQLTextResultSetRowPacket(++currentSequenceId, ImmutableList.of(100)));
//        context.writeAndFlush(new MySQLEofPacket(++currentSequenceId));
    }
}
