
lexer grammar Keyword;

import Alphabet;

WS
    : [ \t\r\n] + ->skip
    ;
USE
    : U S E
    ;
    
INSERT
    : I N S E R T
    ;
    
SELECT
    : S E L E C T
    ;

TABLE
    : T A B L E
    ;

COLUMN
    : C O L U M N
    ;

INTO
    : I N T O
    ;

VALUES
    : V A L U E S
    ;
    
VALUE
    : V A L U E
    ;
    
FROM
    : F R O M
    ;

WHERE
    : W H E R E
    ;

AND
    : A N D
    ;

OR
    : O R
    ;

NOT
    : N O T
    ;

BETWEEN
    : B E T W E E N
    ;

IN
    : I N
    ;
