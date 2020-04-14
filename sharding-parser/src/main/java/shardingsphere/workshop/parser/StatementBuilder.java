package shardingsphere.workshop.parser;

import autogen.SQLBaseVisitor;
import autogen.SQLParser;
import org.antlr.v4.runtime.ParserRuleContext;
import shardingsphere.workshop.parser.select.AndSegment;
import shardingsphere.workshop.parser.select.IdentifierSegment;
import shardingsphere.workshop.parser.select.ProjectSegment;
import shardingsphere.workshop.parser.select.SelectStatement;
import shardingsphere.workshop.parser.select.ShorthandProjectSegment;
import shardingsphere.workshop.parser.select.TableSegment;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Statement builder.
 */
public class StatementBuilder extends SQLBaseVisitor<ASTNode> {
    
    @Override
    public ASTNode visitSelect(SQLParser.SelectContext ctx) {
        SelectStatement result = new SelectStatement();
        if (null != ctx.projections()) {
            if (null != ctx.projections().unqualifiedShorthand()) {
                result.getProjections().add(new ShorthandProjectSegment());
            } else {
                result.getProjections().addAll(visit(ctx.projections().columnName(), ProjectSegment.class));
            }
        }
        if (null != ctx.fromClause()) {
            result.getTables().add((TableSegment) visit(ctx.fromClause()));
        }
        if (null != ctx.whereClause()) {
            result.getConditions().addAll(visit(ctx.whereClause().andPredicate(), AndSegment.class));
        }
        return result;
    }
    
    @Override
    public ASTNode visitIdentifier(SQLParser.IdentifierContext ctx) {
        String value;
        if (null != ctx.IDENTIFIER_()) {
            value = ctx.IDENTIFIER_().getText();
        } else if (null != ctx.NUMBER_()) {
            value = ctx.NUMBER_().getText();
        } else {
            value = ctx.STRING_().getText();
        }
        return new IdentifierSegment(value);
    }
    
    @Override
    public ASTNode visitFromClause(SQLParser.FromClauseContext ctx) {
        return new TableSegment((IdentifierSegment) visit(ctx.tableName().identifier()));
    }
    
    @Override
    public ASTNode visitWhereClause(SQLParser.WhereClauseContext ctx) {
        return visitChildren(ctx);
    }
    
    @Override
    public ASTNode visitAndPredicate(SQLParser.AndPredicateContext ctx) {
        return new AndSegment((IdentifierSegment) visit(ctx.columnName().identifier()), (IdentifierSegment) visit(ctx.columnValue().identifier()));
    }
    
    
    private <T> List<T> visit(List<? extends ParserRuleContext> contexts, Class<T> clazz) {
        return contexts.stream()
            .map(this::visit)
            .map(clazz::cast)
            .collect(toList());
    }
}
