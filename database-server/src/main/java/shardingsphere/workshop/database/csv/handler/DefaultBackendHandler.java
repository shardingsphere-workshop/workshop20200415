
package shardingsphere.workshop.database.csv.handler;

import shardingsphere.workshop.database.mysql.packet.MySQLPacket;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Default backend handler
 *
 */
public class DefaultBackendHandler implements BackendHandler {
    
    @Override
    public Collection<MySQLPacket> execute() {
        return new LinkedList<>();
    }
}
