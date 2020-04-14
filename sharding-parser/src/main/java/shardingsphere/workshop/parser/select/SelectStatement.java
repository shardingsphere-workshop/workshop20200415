package shardingsphere.workshop.parser.select;

import lombok.Getter;
import shardingsphere.workshop.parser.ASTNode;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Select statement.
 */
@Getter
public class SelectStatement implements ASTNode {
    
    private final Collection<ProjectSegment> projections = new LinkedList<>();
    
    private final Collection<TableSegment> tables = new LinkedList<>();
    
    private final Collection<AndSegment> conditions = new LinkedList<>();
    
}
