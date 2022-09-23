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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static testify.matchers.ByteArrayMatchers.matchesHex;


class ByteArrayMatchersTest {

    @Test
    void testEmptyByteArray() {
        Matcher<byte[]> emptyByteArray = ByteArrayMatchers.emptyByteArray();
        assertThat(new byte[] {}, emptyByteArray);
    }

    @Test
    void testMatchesHexCreatesBaseMatcherObject() {
        Matcher<byte[]> baseMatcherObject = matchesHex("");
        assertTrue(baseMatcherObject instanceof BaseMatcher);
    }

    @Test
    void testPrettifyHexRegexToRemoveUnwantedChars() {
        Matcher<byte[]> baseMatcherObject = matchesHex("not a hex string");
        Description testDescription = new TestDescription();

        baseMatcherObject.describeTo(testDescription);
        assertThat(testDescription.toString(), is("\n\t\tae"));
    }

    @Test
    void testMatchesHex() {
        Matcher<byte[]> baseMatcherObject = matchesHex("0c0d647F0c");
        assertTrue(baseMatcherObject.matches(new byte[]{0x0c, 0x0d, 0x64, 0x7f, 0xc}));
    }

    @Test
    void testNotMatchesHex() {
        Matcher<byte[]> baseMatcherObject = matchesHex("0c0d647F0c");
        assertFalse(baseMatcherObject.matches(new byte[]{0x0c, 0x0d, 0x64}));
    }

    @Test
    void testDescribeTo() {
        Matcher<byte[]> baseMatcherObject = matchesHex("0c0d647F");

        Description testDescription = new TestDescription();
        baseMatcherObject.describeTo(testDescription);
        assertThat(testDescription.toString(), is("\n\t\t0c0d647F"));
    }

    @Test
    void testMismatchDescription() {
        Matcher<byte[]> baseMatcherObject = matchesHex("0c0d647F");

        Description testDescription = new TestDescription();
        baseMatcherObject.describeMismatch(new byte[]{0x0c, 0x0d, 0x64}, testDescription);
        assertThat(testDescription.toString(), is("actual bytes differed at byte 0x3" +
                                                        "\ncommon prefix:\n\t\t0c0d64\nexpected suffix:" +
                                                        "\n\t\t      7F\nactual suffix:\n\t\t      \n"));
    }

}
