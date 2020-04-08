
package shardingsphere.workshop.parser.exception;

/**
 * Throw exception when SQL parsing error.
 */
public final class SQLParsingException extends RuntimeException {
    
    private static final long serialVersionUID = -6408790652103666096L;
    
    public SQLParsingException(final String message, final Object... args) {
        super(String.format(message, args));
    }
}
