package shardingsphere.workshop.parser.select;

import lombok.RequiredArgsConstructor;
import shardingsphere.workshop.parser.ASTNode;

/**
 * Where segment.
 */
@RequiredArgsConstructor
public class AndSegment implements ASTNode {
    
    private final IdentifierSegment columName;
    
    private final IdentifierSegment columnValue;
}
