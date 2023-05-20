/*
 * Copyright 2021 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.giop;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum ServiceContextTag {
    TRANSACTION_SERVICE(0),
    CODE_SETS(1),
    CHAIN_BYPASS_CHECK(2),
    CHAIN_BYPASS_INFO(3),
    LOGICAL_THREAD_ID(4),
    BI_DIR_IIOP(5),
    SENDING_CONTEXT_RUNTIME(6),
    INVOCATION_POLICIES(7),
    FORWARDED_IDENTITY(8),
    UNKNOWN_EXCEPTION_INFO(9),
    RT_CORBA_PRIORITY(10),
    RT_CORBA_PRIORITY_RANGE(11),
    FT_GROUP_VERSION(12),
    FT_REQUEST(13),
    EXCEPTION_DETAIL_MESSAGE(14),
    SECURITY_ATTRIBUTE_SERVICE(15),
    ACTIVITY_SERVICE(16),
    RMI_CUSTOM_MAX_STREAM_FORMAT(17),
    YOKO_AUXILIARY_STREAM_FORMAT(0xEEEEEEEE),
    UNKNOWN(-1);
    ;
    final int value;
    ServiceContextTag(int value) {
        this.value = value;
    }
    private final static Map<Integer, ServiceContextTag> TAG_BY_ID = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    HashMap::new,
                    (map, tag) -> map.put(tag.value, tag),
                    Map::putAll));
    static ServiceContextTag valueOf(int tag) { return TAG_BY_ID.getOrDefault(tag, UNKNOWN); }
}
