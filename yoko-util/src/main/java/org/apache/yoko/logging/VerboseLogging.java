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
package org.apache.yoko.logging;

import org.apache.yoko.util.Factory;
import org.apache.yoko.util.Wrapper;

import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.WARNING;

/**
 * Define standard logger objects for yoko verbose logging.
 * The intention here is to log according to what is happening rather than which particular class or method it happens in.
 * This logging should be useful to a user of Yoko in understanding the behaviour of their application.
 * <p>
 *     <strong>
 *         N.B. Anything that is specific to debugging a class or method within Yoko may still be traced to that class's Logger.
 *     </strong>
 * </p>
 */
public enum VerboseLogging {
    ;

    /*
     * N.B. These loggers replace the older mechanism in
     * <code>org.apache.yoko.orb.OB.CoreTraceLevels</code> and
     * <code>org.apache.yoko.orb.OB.Logger</code> and
     * <code>org.apache.yoko.orb.OB.Logger_impl</code>
     */

    /** The verbose logging for all of Yoko: <pre>
     * CONFIG: Config / startup only
     * FINE:   Minimal overview of operations
     * FINER:  Detail of operations
     * FINEST: Maximum detail of operations
     </pre>*/
    public static final Logger ROOT_LOG = Logger.getLogger("yoko.verbose");

    /** Connection logging: <pre>
     * CONFIG: timeout settings etc.
     * FINE:   open/accept/close connection to/from endpoint
     * FINER:  code sets, codebase, YASF, service context counting
     * FINEST: requests, request counts, parallel requests etc.
     * </pre>
     * <br/>
     */
    public static final Logger CONN_LOG = Logger.getLogger("yoko.verbose.connection");
    /** @see #CONN_LOG */
    public static final Logger CONN_IN_LOG = Logger.getLogger("yoko.verbose.connection.in");
    /** @see #CONN_LOG */
    public static final Logger CONN_OUT_LOG = Logger.getLogger("yoko.verbose.connection.out");

    /** Log data transmissions */
    public static final Logger DATA_LOG = Logger.getLogger("yoko.verbose.data");
    /** Log data received */
    public static final Logger DATA_IN_LOG = Logger.getLogger("yoko.verbose.data.in");
    /** Log data sent */
    public static final Logger DATA_OUT_LOG = Logger.getLogger("yoko.verbose.data.out");


    public static final Logger RETRY_LOG = Logger.getLogger("yoko.verbose.retry");

    /** IOR logging: <pre>
     * CONFIG: IOR interceptor addition
     * FINE:
     * FINER:
     * FINEST:
     * </pre>
     */
    public static final Logger IOR_LOG = Logger.getLogger("yoko.verbose.ior");

    /** Request logging: <pre>
     * CONFIG: Request interceptor addition
     * FINE:
     * FINER:
     * FINEST:
     * </pre>
     */
    public static final Logger REQ_LOG = Logger.getLogger("yoko.verbose.request");
    /** @see #REQ_LOG */
    public static final Logger REQ_OUT_LOG = Logger.getLogger("yoko.verbose.request.out");
    /** @see #REQ_LOG */
    public static final Logger REQ_IN_LOG = Logger.getLogger("yoko.verbose.request.in");

    /** Marshalling logging: <pre>
     * CONFIG:
     * FINE:
     * FINER:
     * FINEST:
     * </pre>
     */
    public static final Logger MARSHAL_LOG = Logger.getLogger("yoko.verbose.marshal");

    /**
     * Use this as a pass-through method for an exception when it is being processed without a new exception being created.
     * It will help to determine the code location where the processing happens by:&mdash;
     * <ul>
     * <li> Adding a stack trace to the suppressed exceptions on <code>loggable</code>. </li>
     * <li> Logging the provided <code>reason</code> and exception message if {@link java.util.logging.Level#FINE} logging is enabled. </li>
     * <li> Logging the decorated exception (i.e. with added stack trace) if {@link java.util.logging.Level#FINEST} logging is enabled. </li>
     * </ul>
     */
    public static <L extends Throwable> L logged(Logger logger, L loggable, String reason) {
        loggable.addSuppressed(new StackTraceRecord(reason));
        if (logger.isLoggable(FINEST)) logger.log(FINEST, reason, loggable); // usually formats stack trace
        else if (logger.isLoggable(FINE)) logger.fine(reason + ": " + loggable); // will only log exception.toString()
        return loggable;
    }

    /**
     * Use this as a pass-through method to wrap and log an exception.
     * @see #logged(Logger, Throwable, String)
     */
    public static <W extends Throwable> W wrapped(Logger logger, Exception cause, String reason, Factory<W> wrapperFactory) {
        return logged(logger, (W)wrapperFactory.create().initCause(cause), reason);
    }

    /**
     * Use this as a pass-through method to wrap and log an exception.
     * @see #logged(Logger, Throwable, String)
     */
    public static <V extends Throwable, W extends Throwable> W wrapped(Logger logger, V cause, String reason, Wrapper<V,W> wrapperFactory) {
        return logged(logger, wrapperFactory.wrap(cause), reason);
    }

    /**
     * Use this as a pass-through (or not) method to log <code>loggable</code> and <code>reason</code>
     * to <code>logger</code> at the {@link java.util.logging.Level#WARNING} level.
     */
    public static <L extends Throwable> L warned(Logger logger, L loggable, String reason) {
        logger.log(WARNING, reason, loggable);
        return loggable;
    }

    /**
     * Create one of these to (optionally) log a decision point
     * and to add a record of the stack trace to the causal exception
     */
    private static class StackTraceRecord extends Exception {
        private static final long serialVersionUID = 1L;
        StackTraceRecord(String reason) {
            super(reason);
        }
    }
}
