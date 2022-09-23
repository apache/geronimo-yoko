/*==============================================================================
 * Copyright 2022 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *=============================================================================*/

package testify.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static testify.matchers.Matchers.consistsOf;

class MatchersTest{

    @Test
    void testConsistsOfCratesBaseMatcherObject() {
        Matcher<Iterable<Integer>> object = consistsOf(1, 2, 3, 10);
        assertTrue(object instanceof BaseMatcher);
    }

    @Test
    void testMatchingIntegers() {
        Matcher<Iterable<Integer>> object = consistsOf(10, 2, 3, 1);
        assertTrue(object.matches(asList(10, 2, 3, 1)));
    }

    @Test
    void testNonMatchingIntegers() {
        Matcher<Iterable<Integer>> object = consistsOf(10, 2, 3, 1);
        assertFalse(object.matches(asList(1, 2, 3, 10)));
    }

    @Test
    void testMatchingStrings() {
        Matcher<Iterable<String>> object = consistsOf("one", "three", "four");
        assertTrue(object.matches(asList("one", "three", "four")));
    }

    @Test
    void testNonMatchingStrings() {
        Matcher<Iterable<String>> object = consistsOf("one", "four", "three");
        assertFalse(object.matches(asList("one", "three", "four")));
    }

    @Test
    void testMatchingBooleans() {
        Matcher<Iterable<Boolean>> object = consistsOf(true, false, false, true);
        assertTrue(object.matches(asList(true, false, false, true)));
    }

    @Test
    void testNonMatchingBooleans() {
        Matcher<Iterable<Boolean>> object = consistsOf(false, true, false, true);
        assertFalse(object.matches(asList(true, false, false, true)));
    }

    @Test
    void testDescribeTo() {
        Matcher<Iterable<Boolean>> object = consistsOf(false, true, false, true);
        Description mockDescription = Mockito.mock(Description.class);
        object.describeTo(mockDescription);
        verify(mockDescription).appendText("[false, true, false, true]");
    }

    @Test
    void testDescribeMismatchWithNonIterableObject() {
        Matcher<Iterable<Boolean>> object = consistsOf(false, true, false, true);
        Description mockDescription = Mockito.mock(Description.class);
        assertThrows(Error.class, () -> object.describeMismatch("nonIterableObject", mockDescription));
    }

    @Test
    void testDescribeMismatchObject() {
        Matcher<Iterable<Boolean>> object = consistsOf(false, true, false, true);
        List<Boolean> list = asList(true, true, false, true);

        Description testDescription = new TestDescription();
        object.matches(list);
        object.describeMismatch(list, testDescription);
        assertThat(testDescription.toString(), is("expected: [false, true, false, true]\nbut was:  [true, true, false, true]"));
    }

    @Test
    void testFailure() {
        Matcher<Iterable<Boolean>> object = consistsOf(false, true, false, true);
        assertFalse(object.matches(asList(true, false, true)));
    }

    @Test
    void isEmpty() {
        assertTrue(Matchers.isEmpty() instanceof BaseMatcher);
    }

}
