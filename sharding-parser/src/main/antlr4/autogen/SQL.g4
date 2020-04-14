
grammar SQL;

select
    : SELECT projections fromClause? whereClause?
    ;

projections
    : (unqualifiedShorthand | columnName) (COMMA_ columnName)*
    ;

unqualifiedShorthand
    : ASTERISK_
    ;

columnName
    : identifier
    ;

fromClause
    : FROM tableName
    ;
    
tableName
    : identifier
    ;
    
whereClause
    : WHERE (andPredicate) (COMMA_ andPredicate)*
    ;

andPredicate
    : columnName EQ_ columnValue
    ;

columnValue
    : identifier
    ;
    
identifier
    : IDENTIFIER_ | STRING_ | NUMBER_
    ;

WS
    : [ \t\r\n] + ->skip
    ;

SELECT
    : S E L E C T
    ;

FROM
    : F R O M
    ;

WHERE
    : W H E R E
    ;

IDENTIFIER_
    : [A-Za-z_$0-9]*?[A-Za-z_$]+?[A-Za-z_$0-9]*
    |  BQ_ ~'`'+ BQ_
    | (DQ_ ( '\\'. | '""' | ~('"'| '\\') )* DQ_)
    ;

STRING_
    : (DQ_ ( '\\'. | '""' | ~('"'| '\\') )* DQ_)
    | (SQ_ ('\\'. | '\'\'' | ~('\'' | '\\'))* SQ_)
    ;

NUMBER_
    : INT_? DOT_? INT_ (E (PLUS_ | MINUS_)? INT_)?
    ;

INT_
    : [0-9]+
    ;

PLUS_:               '+';
MINUS_:              '-';
DQ_:                 '"';
SQ_ :                '\'';
DOT_:                '.';
BQ_:                 '`';
COMMA_:              ',';
EQ_:                 '=';
ASTERISK_:           '*';

fragment A:   [Aa];
fragment B:   [Bb];
fragment C:   [Cc];
fragment D:   [Dd];
fragment E:   [Ee];
fragment F:   [Ff];
fragment G:   [Gg];
fragment H:   [Hh];
fragment I:   [Ii];
fragment J:   [Jj];
fragment K:   [Kk];
fragment L:   [Ll];
fragment M:   [Mm];
fragment N:   [Nn];
fragment O:   [Oo];
fragment P:   [Pp];
fragment Q:   [Qq];
fragment R:   [Rr];
fragment S:   [Ss];
fragment T:   [Tt];
fragment U:   [Uu];
fragment V:   [Vv];
fragment W:   [Ww];
fragment X:   [Xx];
fragment Y:   [Yy];
fragment Z:   [Zz];
fragment UL_: '_';