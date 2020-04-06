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

package org.apache.shardingsphere.workshop.proxy.frontend.core.spi;

import org.apache.shardingsphere.workshop.proxy.frontend.core.engine.AuthenticationEngine;
import org.apache.shardingsphere.workshop.proxy.frontend.core.engine.CommandExecuteEngine;
import org.apache.shardingsphere.workshop.proxy.transport.core.codec.DatabasePacketCodecEngine;

/**
 * Database protocol frontend engine.
 */
public interface DatabaseProtocolFrontendEngine {
    
    /**
     * Get database packet codec engine.
     * 
     * @return database packet codec engine
     */
    DatabasePacketCodecEngine getCodecEngine();
    
    /**
     * Get authentication engine.
     * 
     * @return authentication engine
     */
    AuthenticationEngine getAuthEngine();
    
    /**
     * Get command execute engine.
     * 
     * @return command execute engine
     */
    CommandExecuteEngine getCommandExecuteEngine();
    
    /**
     * Release resource.
     */
    void release();
    
    /**
     * Get database type.
     *
     * <p>
     *     The value of database type must registered by SPI for {@code org.apache.shardingsphere.spi.database.DatabaseType}.
     * </p>
     *
     * @return database type
     */
    String getDatabaseType();
}
