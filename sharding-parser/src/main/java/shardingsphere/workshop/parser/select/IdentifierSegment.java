package shardingsphere.workshop.parser.select;

import lombok.RequiredArgsConstructor;
import shardingsphere.workshop.parser.ASTNode;

/**
 * Identifier segment.
 */
@RequiredArgsConstructor
public class IdentifierSegment implements ASTNode {
    
    private final String columnName;
}
