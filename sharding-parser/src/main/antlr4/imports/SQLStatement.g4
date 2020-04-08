
grammar SQLStatement;

import Symbol, Keyword, Literals;

use
    : USE schemaName
    ;
    
schemaName
    : identifier
    ;
    
insert
    : INSERT INTO? tableName columnNames? VALUE assignmentValues
    ;
  
assignmentValues
    : LP_ assignmentValue (COMMA_ assignmentValue)* RP_
    ;

assignmentValue
    : identifier
    ;
    
select 
    : SELECT columnNames fromClause? whereClause?
    ;

columnNames
    : columnName (COMMA_ columnName)*
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
    : WHERE columnName EQ_ columnValue
    ;

columnValue
    : identifier
    ;
    
identifier
    : IDENTIFIER_
    ;