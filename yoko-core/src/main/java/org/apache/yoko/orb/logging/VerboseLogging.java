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
package org.apache.yoko.orb.logging;

import org.apache.yoko.util.Factory;

import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.WARNING;

/**
 * Define standard logger objects for the most verbose logging.
 */
public enum VerboseLogging {
    ;

    /*
     * These loggers replace the older mechanism in
     * <code>org.apache.yoko.orb.OB.CoreTraceLevels</code> and
     * <code>org.apache.yoko.orb.OB.Logger</code> and
     * <code>org.apache.yoko.orb.OB.Logger_impl</code>
     */

    public static final Logger CONN_LOG = Logger.getLogger("yoko.verbose.connection");
    public static final Logger CONN_IN_LOG = Logger.getLogger("yoko.verbose.connection.in");
    public static final Logger CONN_OUT_LOG = Logger.getLogger("yoko.verbose.connection.out");
    public static final Logger RETRY_LOG = Logger.getLogger("yoko.verbose.retry");
    public static final Logger REQ_IN_LOG = Logger.getLogger("yoko.verbose.request.in");
    public static final Logger REQ_OUT_LOG = Logger.getLogger("yoko.verbose.request.out");
    public static final Logger MARSHAL_LOG = Logger.getLogger("yoko.verbose.marshal");

    public static <L extends Throwable> L logged(Logger logger, L loggable, String reason) {
        loggable.addSuppressed(new StackTraceRecord(reason));
        if (logger.isLoggable(FINEST)) logger.log(FINEST, reason, loggable); // usually formats stack trace
        else if (logger.isLoggable(FINE)) logger.fine(reason + ": " + loggable); // will only log exception.toString()
        return loggable;
    }

    public static <W extends Throwable> W wrapped(Logger logger, Exception cause, String reason, Factory<W> wrapperFactory) {
        return logged(logger, (W)wrapperFactory.create().initCause(cause), reason);
    }

    public static <L extends Throwable> L warned(Logger logger, L loggable, String reason) {
        logger.log(WARNING, reason, loggable);
        return loggable;
    }

    /**
     * Create one of these to (optionally) log a decision point
     * and to add a record of the stack trace to the causal exception
     */
    private static class StackTraceRecord extends Exception {
        StackTraceRecord(String reason) { super(reason); }
    }
}
