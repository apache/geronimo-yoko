/*
 * Copyright 2015 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.rmi.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class WeakKey<T> extends WeakReference<T> implements Key<T> {
    private final int hash;

    public WeakKey(T r, ReferenceQueue<T> q) {
        super(r, q);
        hash = r.hashCode();
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!!!(o instanceof Key)) return false;
        final Object otherKey = ((Key<?>)o).get();
        if (null == otherKey) return false;
        return otherKey.equals(get());
    }
}
