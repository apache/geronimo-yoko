package org.apache.yoko.giop;

public enum ReplyStatus {
    NO_EXCEPTION,
    USER_EXCEPTION,
    SYSTEM_EXCEPTION,
    LOCATION_FORWARD,
    LOCATION_FORWARD_PERM,
    NEEDS_ADDRESSING_MODE,
    UNKNOWN;
    static ReplyStatus valueOf(int status) {
        try {
            return values()[status];
        } catch (IndexOutOfBoundsException e) {
            return UNKNOWN;
        }
    }
}
