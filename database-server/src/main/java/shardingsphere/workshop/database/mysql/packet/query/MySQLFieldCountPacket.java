
package shardingsphere.workshop.database.mysql.packet.query;

import shardingsphere.workshop.database.mysql.packet.MySQLPacketPayload;
import shardingsphere.workshop.database.mysql.packet.MySQLPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * COM_QUERY response field count packet for MySQL.
 * 
 * @see <a href="https://dev.mysql.com/doc/internals/en/com-query-response.html">COM_QUERY field count</a>
 */
@RequiredArgsConstructor
@Getter
public final class MySQLFieldCountPacket implements MySQLPacket {
    
    private final int sequenceId;
    
    private final int columnCount;
    
    @Override
    public void write(final MySQLPacketPayload payload) {
        payload.writeIntLenenc(columnCount);
    }
}
