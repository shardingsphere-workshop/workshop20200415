
package shardingsphere.workshop.mysql.proxy.fixture;

import shardingsphere.workshop.mysql.proxy.fixture.packet.MySQLPacket;
import shardingsphere.workshop.mysql.proxy.fixture.packet.MySQLPacketPayload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * MySQL packet codec.
 */
@RequiredArgsConstructor
@Slf4j
public final class MySQLPacketCodec extends ByteToMessageCodec<MySQLPacket> {
    
    @Override
    protected void decode(final ChannelHandlerContext context, final ByteBuf in, final List<Object> out) {
        int readableBytes = in.readableBytes();
        if (!isValidHeader(readableBytes)) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("Read from client {} : \n {}", context.channel().id().asShortText(), ByteBufUtil.prettyHexDump(in));
        }
        doDecode(in, out, readableBytes);
    }
    
    @Override
    protected void encode(final ChannelHandlerContext context, final MySQLPacket message, final ByteBuf out) {
        doEncode(context, message, out);
        if (log.isDebugEnabled()) {
            log.debug("Write to client {} : \n {}", context.channel().id().asShortText(), ByteBufUtil.prettyHexDump(out));
        }
    }
    
    private boolean isValidHeader(final int readableBytes) {
        return readableBytes > MySQLPacket.PAYLOAD_LENGTH + MySQLPacket.SEQUENCE_LENGTH;
    }
    
    private void doDecode(final ByteBuf in, final List<Object> out, final int readableBytes) {
        int payloadLength = in.markReaderIndex().readMediumLE();
        int realPacketLength = payloadLength + MySQLPacket.PAYLOAD_LENGTH + MySQLPacket.SEQUENCE_LENGTH;
        if (readableBytes < realPacketLength) {
            in.resetReaderIndex();
            return;
        }
        in.skipBytes(MySQLPacket.SEQUENCE_LENGTH);
        out.add(in.readRetainedSlice(payloadLength));
    }
    
    private void doEncode(final ChannelHandlerContext context, final MySQLPacket message, final ByteBuf out) {
        try (MySQLPacketPayload payload = new MySQLPacketPayload(context.alloc().buffer())) {
            message.write(payload);
            out.writeMediumLE(payload.getByteBuf().readableBytes());
            out.writeByte(message.getSequenceId());
            out.writeBytes(payload.getByteBuf());
        }
    }
}
