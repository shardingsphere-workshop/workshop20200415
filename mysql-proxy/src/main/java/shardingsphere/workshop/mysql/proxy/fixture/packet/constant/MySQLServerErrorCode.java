
package shardingsphere.workshop.mysql.proxy.fixture.packet.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Common error code.
 */
@RequiredArgsConstructor
@Getter
public enum MySQLServerErrorCode {
    
    CIRCUIT_BREAK_MODE(10000, "C10000", "Circuit break mode is ON."),
    
    UNSUPPORTED_COMMAND(10001, "C10001", "Unsupported command: [%s]"),
    
    UNKNOWN_EXCEPTION(10002, "C10002", "Unknown exception: [%s]"),
    
    ER_DBACCESS_DENIED_ERROR(1044, "42000", "Access denied for user '%s'@'%s' to database '%s'"),
    
    ER_ACCESS_DENIED_ERROR(1045, "28000", "Access denied for user '%s'@'%s' (using password: %s)"),
    
    ER_BAD_DB_ERROR(1049, "42000", "Unknown database '%s'"),
    
    ER_INTERNAL_ERROR(1815, "HY000", "Internal error: %s");
    
    private final int errorCode;
    
    private final String sqlState;
    
    private final String errorMessage;
}
