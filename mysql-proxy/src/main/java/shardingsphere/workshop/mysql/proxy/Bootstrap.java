
package shardingsphere.workshop.mysql.proxy;

import com.google.common.primitives.Ints;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import shardingsphere.workshop.mysql.proxy.fixture.DatabaseProxy;

/**
 * Database-proxy Bootstrap.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Bootstrap {
    
    private static final int DEFAULT_PORT = 3307;

    /**
     * Main entrance.
     *
     * @param args startup arguments
     */
    public static void main(final String[] args) {
        DatabaseProxy.getInstance().start(getPort(args));
    }
    
    private static int getPort(final String[] args) {
        if (0 == args.length) {
            return DEFAULT_PORT;
        }
        Integer port = Ints.tryParse(args[0]);
        return port == null ? DEFAULT_PORT : port;
    }
}
