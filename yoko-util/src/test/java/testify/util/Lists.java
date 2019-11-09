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

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public enum Lists {
    ;
    public static <T> List<T> of(T t) { return unmodifiableList(asList(t));}
    public static <T> List<T> of(T t1, T t2) { return unmodifiableList(asList(t1, t2));}
    public static <T> List<T> of(T t1, T t2, T t3) { return unmodifiableList(asList(t1, t2, t3));}
}
