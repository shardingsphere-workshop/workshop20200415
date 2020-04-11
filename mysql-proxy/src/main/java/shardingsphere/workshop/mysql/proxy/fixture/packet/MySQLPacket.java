
package shardingsphere.workshop.mysql.proxy.fixture.packet;

/**
 * Database packet for MySQL.
 */
public interface MySQLPacket {
    
    int PAYLOAD_LENGTH = 3;
    
    int SEQUENCE_LENGTH = 1;
    
    /**
     * Get sequence ID.
     *
     * @return sequence ID
     */
    int getSequenceId();
    
    /**
     * Write packet to byte buffer.
     *
     * @param payload packet payload to be written
     */
    void write(MySQLPacketPayload payload);
}
