
package shardingsphere.workshop.parser.statement.statement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import shardingsphere.workshop.parser.statement.segment.SchemeNameSegment;
import shardingsphere.workshop.parser.statement.ASTNode;

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
