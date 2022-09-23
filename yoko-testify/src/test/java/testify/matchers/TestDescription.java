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

import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.joining;

class TestDescription implements Description {
    final StringBuilder buffer = new StringBuilder();

    public Description appendText(String text) {
        buffer.append(text);
        return this;
    }

    public Description appendDescriptionOf(SelfDescribing value) {
        value.describeTo(this);
        return this;
    }

    public Description appendValue(Object value) {
        buffer.append(value);
        return this;
    }

    public <T> Description appendValueList(String start, String separator, String end, T... values) {
        buffer.append(Stream.of(values)
                .map(Objects::toString)
                .collect(joining(separator, start, end)));
        return this;
    }

    public <T> Description appendValueList(String start, String separator, String end, Iterable<T> values) {
        buffer.append(StreamSupport.stream(values.spliterator(), false)
                .map(Objects::toString)
                .collect(joining(separator, start, end)));
        return this;
    }

    public Description appendList(String start, String separator, String end, Iterable<? extends SelfDescribing> values) {
        buffer.append(start);
        boolean separatorNeeded = false;
        for (SelfDescribing value : values) {
            if (separatorNeeded) buffer.append(separator);
            value.describeTo(this);
            separatorNeeded = true;
        }
        buffer.append(end);
        return this;
    }

    public String toString() {
        return buffer.toString();
    }
}
