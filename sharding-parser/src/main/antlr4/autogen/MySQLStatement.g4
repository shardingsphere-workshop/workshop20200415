
grammar MySQLStatement;

import Symbol, SQLStatement;

execute
    : (use
    | insert
    ) SEMI_?
    ;
