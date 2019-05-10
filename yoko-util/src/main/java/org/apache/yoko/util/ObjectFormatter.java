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
package org.apache.yoko.util;

import org.omg.IOP.ServiceContext;

import static org.apache.yoko.util.HexConverter.octetsToAscii;

/**
 * Format objects as strings for easy readability,
 * especially for classes that are API and not modifiable.
 */
public enum ObjectFormatter {
    ;
    public static String format(ServiceContext sc) {
        return String.format("Service Context [0x%08x] = %s", sc.context_id, hex(sc));
    }

    private static String hex(ServiceContext sc) {
        return sc.context_data == null ? null : octetsToAscii(sc.context_data, sc.context_data.length);
    }
}
