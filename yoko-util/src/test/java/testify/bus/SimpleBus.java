/*
 * =============================================================================
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package testify.bus;

import testify.streams.BiStream;

import java.util.function.Consumer;

public interface SimpleBus {
    String GLOBAL_USER = "global";
    default Bus global() { return forUser(GLOBAL_USER); }
    Bus forUser(String user);
    SimpleBus put(String key, String value);
    boolean hasKey(String key);
    String peek(String key);
    String get(String key);
    SimpleBus onMsg(String key, Consumer<String> action);
    BiStream<String, String> biStream();
}
