/*
 * =============================================================================
 * Copyright (c) 2020 IBM Corporation and others.
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

public enum Buses {
    ;
    public static String dump(Bus bus) {
        return String.format("Bus[%s]%n%s", bus.user(), dump(bus.biStream()));
    }

    public static String dump(SimpleBus bus) {
        return String.format("%s%n%s", bus, dump(bus.biStream()));
    }

    static String dump(BiStream<String, String> bis) {
        StringBuilder sb = new StringBuilder("{");
        bis.forEach((k, v) -> sb.append("\n\t").append(k).append(" -> ").append(v));
        if (sb.length() == 1) return "{}";
        sb.append("\n}");
        return sb.toString();
    }
}
