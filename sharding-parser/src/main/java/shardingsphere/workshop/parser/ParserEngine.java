package shardingsphere.workshop.parser;

import autogen.SQLLexer;
import autogen.SQLParser;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Parser engine.
 */
@RequiredArgsConstructor
public class ParserEngine {
    
    public ASTNode parse(final String sql) {
        Lexer lexer = new SQLLexer(CharStreams.fromString(sql));
        SQLParser parser = new SQLParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.select();
        StatementBuilder visitor = new StatementBuilder();
        return visitor.visit(tree);
    }
}
