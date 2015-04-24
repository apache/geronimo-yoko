package org.apache.yoko.util.cmsf;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class CmsfThreadLocal {
    private static final Logger LOGGER = Logger.getLogger(CmsfThreadLocal.class.getName());
    private static final ThreadLocal<Version> value = new ThreadLocal<>();

    private CmsfThreadLocal() {}

    private enum Version {
        CMSFv1(1), CMSFv2(2);

        public final byte value;

        private Version(int value) {
            this.value = (byte)(value & 0xff);
        }

        static Version get(byte value) {
            return (value >= 2) ? CMSFv2 : CMSFv1;
        }
    }

    public static void set(byte cmsfv) {
        final Version version = Version.get(cmsfv);
        if (LOGGER.isLoggable(Level.FINER))
            LOGGER.finer(String.format("CMSF thread local version set: %s", version));
        value.set(version);
    }

    public static byte get() {
        final Version version = value.get();
        if (LOGGER.isLoggable(Level.FINER))
            LOGGER.finer(String.format("CMSF thread local version retrieved: %s", version));
        return version.value;
    }

    public static void reset() {
        if (LOGGER.isLoggable(Level.FINER))
            LOGGER.finer("CMSF thread local reset");
        value.remove();
    }
}
