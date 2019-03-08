package org.apache.yoko.util;

public class Timeout {
    public static final Timeout NEVER = new Timeout(0) {
        public boolean isExpired() {
            return false;
        }
    };

    private final long expiry;

    public static Timeout in(int millis) {
        return millis > 0 ?  new Timeout(millis) : NEVER;
    }

    private Timeout(int millis) {
        this.expiry = System.currentTimeMillis() + millis;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiry;
    }
}