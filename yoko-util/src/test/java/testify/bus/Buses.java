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
package testify.bus;

import testify.streams.BiStream;

public enum Buses {
    ;
    public static String dump(Bus bus) {
        return String.format("Bus[%s]%n%s", bus.user(), dump(bus.biStream()));
    }

    public static String dump(SimpleBus bus) {
        return String.format("%s%n%s", bus, dump(bus.biStream()));
    }

    static String dump(BiStream<String, String> bis) {
        StringBuilder sb = new StringBuilder("{");
        bis.forEach((k, v) -> sb.append("\n\t").append(k).append(" -> ").append(v));
        if (sb.length() == 1) return "{}";
        sb.append("\n}");
        return sb.toString();
    }
}
