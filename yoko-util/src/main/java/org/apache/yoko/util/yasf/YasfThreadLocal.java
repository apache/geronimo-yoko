package org.apache.yoko.util.yasf;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class YasfThreadLocal {
    private static final Logger LOGGER = Logger.getLogger(YasfThreadLocal.class.getName());
    private static final ThreadLocal<YasfInfo> yasfInfo = new ThreadLocal<YasfInfo>() {
        @Override protected YasfInfo initialValue() {
            return new YasfInfo();
        }
    };

    private YasfThreadLocal() {}

    private static final class YasfInfo {
        public Frame head = Frame.DEFAULT;
        public boolean override = false;
    }

    private static final class Frame {
        static final Frame DEFAULT = new Frame();
        public final Set<Yasf> value;
        public final Frame prev;

        private Frame() {
            this.value = null;
            this.prev = this;
        }

        Frame(Set<Yasf> value, Frame prev) {
            this.value = value;
            this.prev = prev;
        }
    }

    public static final class YasfOverride implements AutoCloseable {
        private final YasfInfo info;

        YasfOverride(YasfInfo info) {
            this.info = info;
            info.override = true;
        }

        @Override
        public void close() {
            info.override = false;
        }
    }

    public static YasfOverride override() {
        return new YasfOverride(yasfInfo.get());
    }

    public static void push(Set<Yasf> items) {
        final YasfInfo info = yasfInfo.get();
        if (LOGGER.isLoggable(Level.FINER))
            LOGGER.finer(String.format("YASF thread local version pushed onto stack: %s", items));
        info.head = new Frame(items, info.head);
    }

    public static Set<Yasf> get() {
        final YasfInfo info = yasfInfo.get();
        final boolean override = info.override;
        final Set<Yasf> items = (override) ? null : info.head.value;
        if (LOGGER.isLoggable(Level.FINER))
            LOGGER.finer(String.format("YASF thread local version retrieved: %s, override is %b", items, override));
        return items;
    }

    public static Set<Yasf> pop() {
        final YasfInfo info = yasfInfo.get();
        final Set<Yasf> items = info.head.value;
        if (LOGGER.isLoggable(Level.FINER))
            LOGGER.finer(String.format("YASF thread local version popped from stack: %s", items));
        info.head = info.head.prev;
        return items;
    }

    public static void reset() {
        if (LOGGER.isLoggable(Level.FINER))
            LOGGER.finer("YASF thread local stack reset");
        yasfInfo.remove();
    }
}
