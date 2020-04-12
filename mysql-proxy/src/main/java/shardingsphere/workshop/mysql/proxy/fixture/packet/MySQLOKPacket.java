
package shardingsphere.workshop.mysql.proxy.fixture.packet;

import shardingsphere.workshop.mysql.proxy.fixture.packet.constant.MySQLStatusFlag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * OK packet protocol for MySQL.
 * 
 * @see <a href="https://dev.mysql.com/doc/internals/en/packet-OK_Packet.html">OK Packet</a>
 */
@RequiredArgsConstructor
@Getter
public final class MySQLOKPacket implements MySQLPacket {
    
    /**
     * Header of OK packet.
     */
    public static final int HEADER = 0x00;
    
    private static final int DEFAULT_STATUS_FLAG = MySQLStatusFlag.SERVER_STATUS_AUTOCOMMIT.getValue();
    
    private final int sequenceId;
    
    private final long affectedRows;
    
    private final long lastInsertId;
    
    private final int statusFlag;
    
    private final int warnings;
    
    private final String info;
    
    public MySQLOKPacket(final int sequenceId) {
        this(sequenceId, 0L, 0L, DEFAULT_STATUS_FLAG, 0, "");
    }
    
    public MySQLOKPacket(final int sequenceId, final long affectedRows, final long lastInsertId) {
        this(sequenceId, affectedRows, lastInsertId, DEFAULT_STATUS_FLAG, 0, "");
    }
    
    @Override
    public void write(final MySQLPacketPayload payload) {
        payload.writeInt1(HEADER);
        payload.writeIntLenenc(affectedRows);
        payload.writeIntLenenc(lastInsertId);
        payload.writeInt2(statusFlag);
        payload.writeInt2(warnings);
        payload.writeStringEOF(info);
    }
}
