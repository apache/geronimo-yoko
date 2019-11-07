/*
 * =============================================================================
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package testify.util;

import java.util.HashMap;
import java.util.Map;

public enum Maps {
    ;
    private static <K, V> Map<K, V> put(Map<K, V> map, K k, V v) { map.put(k, v); return map; }
    public static <K, V> Map<K, V> of(K k, V v) { return put(new HashMap<>(), k, v);}
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) { return put(of(k1, v1), k2, v2); }
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) { return put(of(k1, v1, k2, v2), k3, v3); }
}
