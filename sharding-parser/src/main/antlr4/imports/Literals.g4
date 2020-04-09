
lexer grammar Literals;

import Alphabet, Symbol;

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

HEX_DIGIT_
    : '0x' HEX_+ | 'X' SQ_ HEX_+ SQ_
    ;

BIT_NUM_
    : '0b' ('0' | '1')+ | B SQ_ ('0' | '1')+ SQ_
    ;
    
INT_
    : [0-9]+
    ;

HEX_
    : [0-9a-fA-F]
    ;
