
grammar MySQLStatement;

import Symbol, SQLStatement;

execute
    : (use
    | insert
    | select
    ) SEMI_?
    ;
