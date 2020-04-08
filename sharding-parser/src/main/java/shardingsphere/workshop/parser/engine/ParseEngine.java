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

package shardingsphere.workshop.parser.engine;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import shardingsphere.workshop.parser.engine.parser.SQLParser;
import shardingsphere.workshop.parser.exception.SQLParsingException;
import shardingsphere.workshop.parser.statement.ASTNode;
import shardingsphere.workshop.parser.engine.visitor.SQLVisitor;

/**
 * Parse engine.
 *
 * @author panjuan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParseEngine {
    
    /**
     * Parse SQL.
     *
     * @return AST node
     */
    public static ASTNode parse(final String sql) {
        ParseTree parseTree = createParseTree(sql);
        return new SQLVisitor().visit(parseTree.getChild(0));
    }
    
    private static ParseTree createParseTree(final String sql) {
        SQLParser sqlParser = new SQLParser(sql);
        ParseTree result = sqlParser.parse();
        if (result.getChild(0) instanceof ErrorNode) {
            throw new SQLParsingException(String.format("Unsupported SQL of `%s`", sql));
        }
        return result;
    }
}
