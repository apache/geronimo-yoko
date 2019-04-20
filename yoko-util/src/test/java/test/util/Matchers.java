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
package test.util;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public enum Matchers {
    ;
    public static <T> Matcher<Iterable<T>> consistsOf(final T...elems) {
        return new BaseMatcher<Iterable<T>>() {
            Iterable<T> iterable;
            List<T> actualElems;
            List<T> expectedElems;
            public boolean matches(Object item) {
                iterable = (Iterable<T>) item;
                actualElems = new ArrayList<>();
                for (T elem: iterable) actualElems.add(elem);
                expectedElems = new ArrayList<>(asList(elems));
                return actualElems.equals(expectedElems);
            }

            public void describeTo(Description description) {
                description.appendText(asList(elems).toString());
            }

            public void describeMismatch(Object item, Description description) {
                if (item != iterable) throw new Error();
                description
                        .appendText("expected: ").appendText(expectedElems.toString()).appendText("\n")
                        .appendText("but was:  ").appendText(actualElems.toString());
            }
        };
    }

    public static Matcher<Iterable<?>> isEmpty() {
        return (Matcher) consistsOf(/*nothing*/);
    }
}
