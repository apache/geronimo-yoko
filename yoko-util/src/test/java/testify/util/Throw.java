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

public enum Throw {
    ;

    /**
     * Throw absolutely any throwable as if it were a RuntimeException
     * @param t the throwable to be rethrown
     * @throws <code>t</code>
     * @return declares a return type so that callers can use <code>throw Throw.andThrowAgain(t)</code>
     */
    public static RuntimeException andThrowAgain(Throwable t) throws RuntimeException {
        throw Throw.<RuntimeException>useTypeErasureMadnessToThrowAnyCheckedException(t);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T useTypeErasureMadnessToThrowAnyCheckedException(Throwable t) throws T {
        throw (T)t;
    }
}
