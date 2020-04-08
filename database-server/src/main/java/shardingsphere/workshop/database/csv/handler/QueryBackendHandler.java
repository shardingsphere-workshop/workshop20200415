
package shardingsphere.workshop.database.csv.handler;

import shardingsphere.workshop.database.mysql.packet.MySQLPacket;

import java.sql.SQLException;

/**
 * Text protocol backend handler.
 */
public interface QueryBackendHandler extends BackendHandler {
    
    /**
     * Goto next result value.
     *
     * @return has more result value or not
     * @throws SQLException SQL exception
     */
    boolean next() throws SQLException;
    
    /**
     * Get query data.
     *
     * @return query data
     * @throws SQLException SQL exception
     */
    MySQLPacket getQueryData() throws SQLException;
}
