
package shardingsphere.workshop.mysql.proxy.fixture.packet.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Sharding-Proxy's information for MySQL.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MySQLServerInfo {
    
    /**
     * Protocol version is always 0x0A.
     */
    public static final int PROTOCOL_VERSION = 0x0A;
    
    /**
     * Server version.
     */
    public static final String SERVER_VERSION = "shardingsphere-workshop-database 1.0.0-SNAPSHOT";
    
    /**
     * Charset code 0x21 is utf8_general_ci.
     */
    public static final int CHARSET = 0x21;
}
