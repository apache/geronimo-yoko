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

//
// IDL:orb.yoko.apache.org/OB/WLogger:1.0
//
/**
 *
 * The Yoko message logger interface with wide string support.
 *
 **/

public interface WLoggerOperations extends LoggerOperations
{
    //
    // IDL:orb.yoko.apache.org/OB/WLogger/winfo:1.0
    //
    /**
     *
     * Log a wide string informational message.
     *
     * @param msg The message.
     *
     **/

    void
    winfo(String msg);
    
    /**
     * Log a wide string informational message.
     * 
     * @param msg    The message.
     * @param e      An exception associated with the error.
     */
    void
    winfo(String msg, Throwable e);

    //
    // IDL:orb.yoko.apache.org/OB/WLogger/werror:1.0
    //
    /**
     *
     * Log an wide string error message.
     *
     * @param msg The error message.
     *
     **/

    void
    werror(String msg);
    
    
    /**
     * Log a wide string error message.
     * 
     * @param msg    The error message.
     * @param e      An exception associated with the error.
     */
    void
    werror(String msg, Throwable e);

    //
    // IDL:orb.yoko.apache.org/OB/WLogger/wwarning:1.0
    //
    /**
     *
     * Log a wide string warning message.
     *
     * @param msg The warning message.
     *
     **/

    void
    wwarning(String msg);
    
    
    /**
     *
     * Log a wide string warning message.
     *
     * @param msg The warning message.
     * @param e      An exception associated with the warning.
     *
     **/

    void
    wwarning(String msg, Throwable e);

    //
    // IDL:orb.yoko.apache.org/OB/WLogger/debug:1.0
    //
    /**
     *
     * Log a debug message.
     *
     * @param msg The debug message.
     *
     **/

    void
    wdebug(String msg);
    
    /**
     *
     * Log a wide-string debug message.
     *
     * @param msg The debug message.
     * @param e      An exception associated with the warning.
     *
     **/

    void
    wdebug(String msg, Throwable e);

    //
    // IDL:orb.yoko.apache.org/OB/WLogger/wtrace:1.0
    //
    /**
     *
     * Log a wide string trace message.
     *
     * @param category The trace category.
     *
     * @param msg The trace message.
     *
     **/

    void
    wtrace(String category,
           String msg);
}
