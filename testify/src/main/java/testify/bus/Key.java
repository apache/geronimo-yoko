/*
 * Copyright 2023 IBM Corporation and others.
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
package testify.bus;

import testify.bus.key.TypeKey;

import java.util.function.Supplier;

public interface Key<T> {
    /**
     * Convert an element to a string.
     * If possible, this string should be human-readable.
     * @param t the element to convert to a string
     * @return a string representing <code>t</code>
     */
    String stringify(T t);

    /**
     * Convert a string to an element.
     * @param s the string to convert
     * @param sb the supplier of a bus to use for context, if required
     * @return  an instance of <code>T</code> matching the provided string
     */
    T unstringify(String s, Supplier<Bus> sb);

    /** Do not override this method! Instead, make sure the implementing class is an enum. */
    Class<? extends Enum> getDeclaringClass();

    /** @see #getDeclaringClass() */
    int ordinal();

    /** @see #getDeclaringClass() */
    String name();

    /**
     * Do not override this default method.
     * @return the name including the enum class and the member name of this instance.
     */
    default String fullName() { return getDeclaringClass().getTypeName() + '.' + name(); }

    /**
     * Implementers should not override this method!
     * It is here to allow an easy method reference that can be a bus consumer.
     */
    default <K extends Enum<K>& TypeKey<T>> void announce(Bus b) { b.put((K)this); } // assumes 'this' is an enum
    /**
     * Implementers should not override this method!
     * It is here to allow an easy method reference that can be a bus function.
     */
    default <K extends Enum<K>& TypeKey<T>> T await(Bus b) { return b.get((K)this); } // assumes 'this' is an enum
}
