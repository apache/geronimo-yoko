/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
*  contributor license agreements.  See the NOTICE file distributed with
*  this work for additional information regarding copyright ownership.
*  The ASF licenses this file to You under the Apache License, Version 2.0
*  (the "License"); you may not use this file except in compliance with
*  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.yoko.orb.OB;

import java.util.logging.Level;

/**
 *
 * The Yoko message logger interface.
 *
 **/
public interface Logger
{
    /**
     * Log an informational message.
     * 
     * @param msg    The message.
     * @param e      An exception associated with the error.
     */
    void
    info(String msg, Throwable e);

    /**
     *
     * Log an informational message.
     *
     * @param msg The message.
     *
     **/
    void
    info(String msg);

    /**
     *
     * Log an error message.
     *
     * @param msg The error message.
     *
     **/
    void
    error(String msg);
    
    
    /**
     * Log an error message.
     * 
     * @param msg    The error message.
     * @param e      An exception associated with the error.
     */
    void
    error(String msg, Throwable e);

    /**
     *
     * Log a warning message.
     *
     * @param msg The warning message.
     *
     **/
    void
    warning(String msg);
    
    
    /**
     *
     * Log a warning message.
     *
     * @param msg The warning message.
     * @param e      An exception associated with the warning.
     *
     **/
    void
    warning(String msg, Throwable e);

    /**
     *
     * Log a debug message.
     *
     * @param msg The debug message.
     *
     **/
    void
    debug(String msg);
    
    /**
     *
     * Log a debug message.
     *
     * @param msg The debug message.
     * @param e      An exception associated with the warning.
     *
     **/
    void
    debug(String msg, Throwable e);
    
    
    /**
     * Test if debug output is enabled for this logger.
     * 
     * @return True if debug logging is enabled, false if debug 
     *         output is not enabled.
     */
    boolean isDebugEnabled();     

    /**
     *
     * Log a trace message.
     *
     * @param category The trace category.
     *
     * @param msg The trace message.
     *
     **/
    void
    trace(String category,
          String msg);

    /**
     * Log a message of the indicated level.
     *
     * @param level  The message level.
     * @param msg    The logged message.
     */
    void log(Level level, String msg);

    /**
     * Log a message of the indicated level.
     *
     * @param level  The message level.
     * @param msg    The logged message.
     * @param param  A single parameter object included with the message.
     */
    void log(Level level, String msg, Object param);

    /**
     * Log a message of the indicated level.
     *
     * @param level  The message level.
     * @param msg    The logged message.
     * @param params An array of parameter objects logged with the message.
     */
    void log(Level level, String msg, Object[] params);

    /**
     * Log a message of the indicated level.
     *
     * @param level  The message level.
     * @param msg    The logged message.
     * @param thrown An exception object included in the log.
     */
    void log(Level level, String msg, Throwable thrown);
}
