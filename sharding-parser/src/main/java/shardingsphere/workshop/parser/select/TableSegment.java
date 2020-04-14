package shardingsphere.workshop.parser.select;

import lombok.RequiredArgsConstructor;
import shardingsphere.workshop.parser.ASTNode;

/**
 * Table segment.
 */
@RequiredArgsConstructor
public class TableSegment implements ASTNode {
    
    private final IdentifierSegment identifier;
}
