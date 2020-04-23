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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum ArrayUtils {
    ;

    public static <T> T[] concat(T[] list, T...tailElems) {
        Objects.requireNonNull(list);
        Objects.requireNonNull(tailElems);
        if (tailElems.length == 0) return list;
        List<T> result = new ArrayList<>(Arrays.asList(list));
        result.addAll(Arrays.asList(tailElems));
        return result.toArray(list);
    }
}
