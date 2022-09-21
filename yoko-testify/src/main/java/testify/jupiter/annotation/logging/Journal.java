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
package testify.jupiter.annotation.logging;

import java.util.LinkedList;
import java.util.logging.LogRecord;

/**
 * A linked list of LogRecords with chronological ordering of first element.
 */
final class Journal extends LinkedList<LogRecord> implements Comparable<Journal> {
    @Override
    public int compareTo(Journal that) {
        final LogRecord thisRec = this.peek();
        final LogRecord thatRec = that.peek();
        if (null == thisRec) return (null == thatRec) ? 0 : 1;
        if (null == thatRec) return -1;
        return Long.signum(thisRec.getMillis() - thatRec.getMillis());
    }
}
