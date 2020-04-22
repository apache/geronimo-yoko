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
package testify.util;

import java.util.function.Predicate;

public enum Predicates {
    ;

    public static <T> Predicate<T> or(Predicate<T> p1, Predicate<T> p2) {return p1.or(p2);}
}
