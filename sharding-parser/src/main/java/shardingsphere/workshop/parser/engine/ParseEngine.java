
package shardingsphere.workshop.parser.engine;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import shardingsphere.workshop.parser.engine.parser.SQLParser;
import shardingsphere.workshop.parser.exception.SQLParsingException;
import shardingsphere.workshop.parser.statement.ASTNode;
import shardingsphere.workshop.parser.engine.visitor.SQLVisitor;

/**
 * Parse engine.
 *
 * @author panjuan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParseEngine {
    
    /**
     * Parse SQL.
     *
     * @return AST node
     */
    public static ASTNode parse(final String sql) {
        ParseTree parseTree = createParseTree(sql);
        return new SQLVisitor().visit(parseTree.getChild(0));
    }
    
    private static ParseTree createParseTree(final String sql) {
        SQLParser sqlParser = new SQLParser(sql);
        ParseTree result = sqlParser.parse();
        if (result.getChild(0) instanceof ErrorNode) {
            throw new SQLParsingException(String.format("Unsupported SQL of `%s`", sql));
        }
        return result;
    }
}
