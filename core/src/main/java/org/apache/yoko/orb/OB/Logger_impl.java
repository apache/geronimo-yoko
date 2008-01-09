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
import java.util.logging.Logger;

import org.apache.yoko.orb.OB.WLogger;

public class Logger_impl extends org.omg.CORBA.LocalObject implements WLogger {

    // the real logger backing instance.
    static final Logger logger = Logger.getLogger(ORBInstance.class.getName());

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public void info(String msg) {
        logger.info(msg);
    }

    public void info(String msg, Throwable e) {
        logger.log(Level.INFO, msg, e);
    }

    public void winfo(String msg) {
        info(msg);
    }

    public void winfo(String msg, Throwable e) {
        info(msg, e); 
    }

    public void error(String msg) {
        logger.severe(msg);
    }

    public void error(String msg, Throwable e) {
        logger.log(Level.SEVERE, msg, e);
    }

    public void werror(String msg) {
        error(msg);
    }

    public void werror(String msg, Throwable e) {
        error(msg, e);
    }

    public void warning(String msg) {
        logger.warning(msg);
    }

    public void warning(String msg, Throwable e) {
        logger.log(Level.WARNING, msg, e);
    }

    public void wwarning(String msg) {
        warning(msg);
    }

    public void wwarning(String msg, Throwable e) {
        warning(msg, e);
    }

    public void debug(String msg) {
        logger.fine(msg);
    }

    public void debug(String msg, Throwable e) {
        logger.log(Level.FINE, msg, e);
    }
    
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE); 
    }

    public void wdebug(String msg) {
        debug(msg);
    }

    public void wdebug(String msg, Throwable e) {
        debug(msg, e);
    }

    public void trace(String category, String msg) {
        logger.log(Level.FINE, category, msg);
        String s = "[ " + category + ": ";
        int start = 0;
        int next;
        while ((next = msg.indexOf('\n', start)) != -1) {
            s += msg.substring(start, next + 1);
            s += "  ";
            start = next + 1;
        }
        s += msg.substring(start);
        s += " ]";
        logger.log(Level.FINE, msg);
    }

    public void wtrace(String category, String msg) {
        trace(category, msg);
    }

    /**
     * Log a message of the indicated level.
     *
     * @param level  The message level.
     * @param msg    The logged message.
     */
    public void log(Level level, String msg) {
        logger.log(level, msg);
    }

    /**
     * Log a message of the indicated level.
     *
     * @param level  The message level.
     * @param msg    The logged message.
     * @param param  A single parameter object included with the message.
     */
    public void log(Level level, String msg, Object param) {
        logger.log(level, msg, param);
    }

    /**
     * Log a message of the indicated level.
     *
     * @param level  The message level.
     * @param msg    The logged message.
     * @param params An array of parameter objects logged with the message.
     */
    public void log(Level level, String msg, Object[] params) {
        logger.log(level, msg, params);
    }

    /**
     * Log a message of the indicated level.
     *
     * @param level  The message level.
     * @param msg    The logged message.
     * @param thrown An exception object included in the log.
     */
    public void log(Level level, String msg, Throwable thrown) {
        logger.log(level, msg, thrown);
    }
}
