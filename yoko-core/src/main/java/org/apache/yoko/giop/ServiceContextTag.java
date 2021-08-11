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
