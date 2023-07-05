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

import testify.bus.key.CollectionSpec;
import testify.bus.key.EnumSpec;
import testify.bus.key.FieldSpec;
import testify.bus.key.IntSpec;
import testify.bus.key.ListSpec;
import testify.bus.key.MemberSpec;
import testify.bus.key.MethodSpec;
import testify.bus.key.StringListSpec;
import testify.bus.key.StringSpec;
import testify.bus.key.VoidSpec;
import testify.util.SerialUtil;

/**
 * This interface defines how to convert the parametetric type <code>T</code> to a string and back.
 * It provides a default implementation that uses serialization to convert the supplied instance
 * into an opaque string representation.
 * <p>
 *     Implementers are encouraged to provide a human-readable string conversion, or to use one of the
 *     derived interfaces that does this for certain known types.
 * </p>
 * <p>
 *     Each implementing class must be an <code>enum</code>.
 *     This allows the instances to be used as keys when sending a message on a {@link Bus}.
 *     The generic declarations of these methods allow only the correct type to be sent as a value.
 * </p>
 *
 * @param <T> the type to be converted to and from a string representation
 *
 * @see IntSpec ,
 * @see StringSpec
 * @see EnumSpec
 * @see StringListSpec
 * @see ListSpec
 * @see CollectionSpec
 * @see FieldSpec
 * @see MemberSpec
 * @see MethodSpec
 * @see VoidSpec
 */
@SuppressWarnings("unchecked")
public interface TypeSpec<T> {
    /**
     * Implementers should not override this method!
     * It is here to provide a hint that the implementing class should be an enum.
     * The implementation is provided by the {@link Enum} class.
     */
    Class<? extends Enum> getDeclaringClass();
    /**
     * Implementers should not override this method!
     * It is here to provide a hint that the implementing class should be an enum.
     * The implementation is provided by the {@link Enum} class.
     */
    int ordinal();
    /**
     * Implementers should not override this method!
     * It is here to provide a hint that the implementing class should be an enum.
     * The implementation is provided by the {@link Enum} class.
     */
    String name();
    /**
     * Implementers should not override this method!
     * @return the name including the enum class and the member name of this instance.
     */
    default String fullName() { return getDeclaringClass().getTypeName() + '.' + name(); }

    /**
     * Implementers are encouraged to override this method to return a human-readable string.
     * @param t the element to convert to a string
     * @return a string representing <code>t</code> 
     */
    default String stringify(T t) { return SerialUtil.stringify(t); }
    /**
     * This method must match the implementation of {@link #stringify(T)}.
     * @param s the string to convert
     * @return  an instance of <code>T</code> matching the provided string
     */
    default T unstringify(String s) { return (T) SerialUtil.unstringify(s); }

    /**
     * Implementers should not override this method!
     * It is here to allow an easy method reference that is a bus consumer.
     */
    default <K extends Enum<K>&TypeSpec<T>> void send(Bus b) { b.put((K)this); } // assumes 'this' is an enum
}
