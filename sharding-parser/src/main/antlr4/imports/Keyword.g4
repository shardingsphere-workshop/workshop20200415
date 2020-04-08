/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
