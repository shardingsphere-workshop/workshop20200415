
package shardingsphere.workshop.mysql.proxy.todo.packet;

import shardingsphere.workshop.mysql.proxy.fixture.packet.MySQLPacketPayload;
import shardingsphere.workshop.mysql.proxy.fixture.packet.MySQLPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Text result set row packet for MySQL.
 *
 * @see <a href="https://dev.mysql.com/doc/internals/en/com-query-response.html#packet-ProtocolText::ResultsetRow">ResultsetRow</a>
 */
@RequiredArgsConstructor
@Getter
public final class MySQLTextResultSetRowPacket implements MySQLPacket {
    
    private static final int NULL = 0xfb;
    
    private final int sequenceId;
    
    private final List<Object> data;
    
    @Override
    public void write(final MySQLPacketPayload payload) {
        for (Object each : data) {
            if (null == each) {
                payload.writeInt1(NULL);
            } else {
                if (each instanceof byte[]) {
                    payload.writeBytesLenenc((byte[]) each);
                } else if ((each instanceof Timestamp) && (0 == ((Timestamp) each).getNanos())) {
                    payload.writeStringLenenc(each.toString().split("\\.")[0]);
                } else if (each instanceof BigDecimal) {
                    payload.writeStringLenenc(((BigDecimal) each).toPlainString());
                } else if (each instanceof Boolean) {
                    payload.writeBytesLenenc((Boolean) each ? new byte[]{1} : new byte[]{0});
                } else {
                    payload.writeStringLenenc(each.toString());
                }
            }
        }
    }
}
