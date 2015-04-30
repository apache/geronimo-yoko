package org.apache.yoko.util.cmsf;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class CmsfThreadLocal {
    private static final Logger LOGGER = Logger.getLogger(CmsfThreadLocal.class.getName());
    private static final ThreadLocal<CmsfInfo> cmsfInfo = new ThreadLocal<CmsfInfo>() {
        @Override protected CmsfInfo initialValue() {
            return new CmsfInfo();
        }
    };

    private CmsfThreadLocal() {}

    private static final class CmsfInfo {
        public Frame head = Frame.DEFAULT;
        public boolean override = false;
    }

    private static final class Frame {
        static final Frame DEFAULT = new Frame();
        public final Version version;
        public final Frame prev;

        private Frame() {
            this.version = Version.CMSFv1;
            this.prev = this;
        }

        Frame(Version version, Frame prev) {
            this.version = version;
            this.prev = prev;
        }
    }
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

    public static final class CmsfOverride implements AutoCloseable {
        private final CmsfInfo info;

        CmsfOverride(CmsfInfo info) {
            this.info = info;
            info.override = true;
        }

        @Override
        public void close() {
            info.override = false;
        }
    }

    public static CmsfOverride override() {
        return new CmsfOverride(cmsfInfo.get());
    }

    public static void push(byte cmsfv) {
        final CmsfInfo info = cmsfInfo.get();
        final Version version = Version.get(cmsfv);
        if (LOGGER.isLoggable(Level.FINER))
            LOGGER.finer(String.format("CMSF thread local version pushed onto stack: %s", version));
        info.head = new Frame(version, info.head);
    }

    public static byte get() {
        final CmsfInfo info = cmsfInfo.get();
        final boolean override = info.override;
        final Version version = (override) ? Version.CMSFv1 : info.head.version;
        if (LOGGER.isLoggable(Level.FINER))
            LOGGER.finer(String.format("CMSF thread local version retrieved: %s, override is %b", version, override));
        return version.value;
    }

    public static byte pop() {
        final CmsfInfo info = cmsfInfo.get();
        final Version version = info.head.version;
        if (LOGGER.isLoggable(Level.FINER))
            LOGGER.finer(String.format("CMSF thread local version popped from stack: %s", version));
        info.head = info.head.prev;
        return version.value;
    }

    public static void reset() {
        if (LOGGER.isLoggable(Level.FINER))
            LOGGER.finer("CMSF thread local stack reset");
        cmsfInfo.remove();
    }
}
