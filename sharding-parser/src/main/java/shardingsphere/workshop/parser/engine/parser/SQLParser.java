
package shardingsphere.workshop.parser.engine.parser;

import autogen.MySQLStatementLexer;
import autogen.MySQLStatementParser;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * SQL parser.
 *
 * @author panjuan
 */
public final class SQLParser extends MySQLStatementParser {
    
    public SQLParser(final String sql) {
        super(new CommonTokenStream(new MySQLStatementLexer(CharStreams.fromString(sql))));
    }
    
    /**
     * Parse.
     *
     * @return root node
     */
    public ParseTree parse() {
        return twoPhaseParse();
    }
    
    private ParseTree twoPhaseParse() {
        try {
            setErrorHandler(new BailErrorStrategy());
            getInterpreter().setPredictionMode(PredictionMode.SLL);
            return execute();
        } catch (final ParseCancellationException ex) {
            reset();
            setErrorHandler(new DefaultErrorStrategy());
            getInterpreter().setPredictionMode(PredictionMode.LL);
            return execute();
        }
    }
}
