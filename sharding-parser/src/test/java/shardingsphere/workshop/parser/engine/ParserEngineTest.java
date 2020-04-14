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

import org.junit.Test;
import shardingsphere.workshop.parser.ParserEngine;
import shardingsphere.workshop.parser.select.SelectStatement;

public final class ParserEngineTest {
    
    private final ParserEngine engine = new ParserEngine();
    
    @Test
    public void testParse() {
        SelectStatement selectStatement = (SelectStatement) engine.parse("select * from t_order where order_id=1");
        System.out.println(selectStatement);
    }
}
