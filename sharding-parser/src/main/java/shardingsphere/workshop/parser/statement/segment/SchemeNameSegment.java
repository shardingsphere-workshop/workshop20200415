
package shardingsphere.workshop.parser.statement.segment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import shardingsphere.workshop.parser.statement.ASTNode;

/**
 * Scheme name segment.
 *
 * @author panjuan
 */
@RequiredArgsConstructor
@Getter
public final class SchemeNameSegment implements ASTNode {
    
    private final IdentifierSegment identifier;
}
