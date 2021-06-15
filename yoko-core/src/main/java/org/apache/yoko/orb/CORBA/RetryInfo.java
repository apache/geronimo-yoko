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

package org.apache.yoko.orb.CORBA;

import org.apache.yoko.rmi.util.ObjectUtil;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;
import static org.apache.yoko.logging.VerboseLogging.RETRY_LOG;

/**
 * RetryInfo holds counters which track the number of retries and location forward replies
 */

public final class RetryInfo {
    private final String label = ObjectUtil.getNextObjectLabel(RetryInfo.class);
    private int retry; // retry count
    private int hop; // forward hop count

    public RetryInfo() {
        if (RETRY_LOG.isLoggable(FINER)) RETRY_LOG.finer(label + ": created");
    }

    public int getRetry() {
        if (RETRY_LOG.isLoggable(FINER)) RETRY_LOG.finer(label + ": retry count is " + retry);
        return retry;
    }

    public int getHop() {
        if (RETRY_LOG.isLoggable(FINER)) RETRY_LOG.finer(label + ": hop count is " + hop);
        return hop;
    }

    public void incrementRetryCount() {
        retry++;
        if (RETRY_LOG.isLoggable(FINE)) RETRY_LOG.fine(label + ": retry count incremented to " + retry);
    }

    public void incrementHopCount() {
        hop++;
        retry = 0;
        if (RETRY_LOG.isLoggable(FINE)) RETRY_LOG.fine(label + ": hop count incremented to " + hop);
    }

    @Override
    public String toString() { return label + "{retry=" + retry + ", hop=" + hop + '}'; }
}
