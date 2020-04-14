package shardingsphere.workshop.parser.select;

import lombok.RequiredArgsConstructor;

/**
 * Column project segment.
 */
@RequiredArgsConstructor
public class ColumnProjectSegment implements ProjectSegment {
    
    private final IdentifierSegment identifier;
}
