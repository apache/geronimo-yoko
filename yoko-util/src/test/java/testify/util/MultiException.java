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
package testify.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MultiException extends RuntimeException {
    private static final String SEP = "--------------------------------------------------------------------------------";
    private static final String NULL_COUNT_FORMAT = SEP + "%n%d \u2715 null%n" + SEP + "%n";
    private static final String ENTRY_FORMAT = "%n" + SEP + "%n%d \u2715 %s" + SEP + "%n";
    private final Map<String, Integer> map = new TreeMap<>();
    private int nullCount;

    public boolean isEmpty() {
        return map.isEmpty();
    }

    private Integer add(Throwable t) {
        if (t == null) return nullCount++;
        String desc = getDescription(t);
        Integer count = map.get(desc);
        return count == null ?
                map.put(desc, 1) :
                map.put(desc, ++count);
    }

    private String getDescription(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println(t);
        pw.println(SEP);
        t.printStackTrace(pw);
        pw.flush();
        return sw.getBuffer().toString();
    }

    public <T extends Throwable, F extends Future<T>> MultiException(Iterable<F> results) {
        for (F f : results)
            try {
                add(f.get());
            } catch (InterruptedException | ExecutionException e) {
                add(e);
            }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        s.printf(NULL_COUNT_FORMAT, nullCount);
        for (Map.Entry<String, Integer> e : map.entrySet()) s.printf(ENTRY_FORMAT, e.getValue(), e.getKey());
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        s.printf(NULL_COUNT_FORMAT, nullCount);
        for (Map.Entry<String, Integer> e : map.entrySet()) s.printf(ENTRY_FORMAT, e.getValue(), e.getKey());
    }
}
