
package shardingsphere.workshop.parser.statement.statement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import shardingsphere.workshop.parser.statement.ASTNode;
import shardingsphere.workshop.parser.statement.segment.SchemeNameSegment;

/**
 * Use statement.
 *
 * @author panjuan
 */
@RequiredArgsConstructor
@Getter
public final class UseStatement implements ASTNode {
    
    private final SchemeNameSegment schemeName;
}
