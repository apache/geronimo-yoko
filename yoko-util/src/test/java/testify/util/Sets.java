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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

public enum Sets {
    ;
    @SafeVarargs
    public static <T> Set<T> of(T... elems) { return new HashSet<>(Arrays.asList(elems)); }
    @SafeVarargs
    public static <T> Set<T> unmodifiableOf(T... elems) { return unmodifiableSet(new HashSet<>(Arrays.asList(elems))); }
}
