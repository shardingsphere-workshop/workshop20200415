
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
    
columnNames
    : columnName (COMMA_ columnName)*
    ;

columnName
    : identifier
    ;
   
tableName
    : identifier
    ;
    
identifier
    : IDENTIFIER_ | STRING_ | NUMBER_
    ;
    