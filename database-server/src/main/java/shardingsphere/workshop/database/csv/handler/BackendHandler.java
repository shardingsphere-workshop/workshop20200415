
package shardingsphere.workshop.database.csv.handler;

import shardingsphere.workshop.database.mysql.packet.MySQLPacket;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Command backend handler.
 */
public interface BackendHandler {
    
    /**
     * Execute command.
     *
     * @return backend response
     * @throws SQLException SQL exception
     */
    Collection<MySQLPacket> execute() throws SQLException;
}
