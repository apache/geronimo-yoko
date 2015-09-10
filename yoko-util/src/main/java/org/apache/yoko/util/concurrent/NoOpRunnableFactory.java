package org.apache.yoko.util.concurrent;

import org.apache.yoko.util.Factory;
import org.apache.yoko.util.KeyedFactory;

public enum NoOpRunnableFactory implements Runnable, KeyedFactory<Object, Runnable> {
    INSTANCE;
    public Runnable create(Object key) {return this;}
    public void run() {}
}
