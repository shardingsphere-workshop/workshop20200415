
package shardingsphere.workshop.parser.statement.segment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import shardingsphere.workshop.parser.statement.ASTNode;

/**
 * Identifier segment.
 */
@RequiredArgsConstructor
@Getter
public final class IdentifierSegment implements ASTNode {
    
    private final String value;
}
