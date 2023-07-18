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
package testify.bus.key;

import testify.bus.Bus;
import testify.bus.Key;
import testify.io.Stringifiable;
import testify.io.Stringifier;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * This interface defines how to convert the parametetric type <code>T</code> to a string and back.
 * It provides a default implementation that can convert a {@link Stringifiable} or {@link Serializable} class.
 * <p>
 *     Serializable objects are encoded as bytes, then base-64 encoded.
 *     To avoid the opacity of this result, implementers of this interface are encouraged
 *     to ensure a human-readable format by using one of the following:-
 *     <ol>
 *         <li>Subclass this interface to provide a human readable conversion.</li>
 *         <li>Use one of the derived interfaces that handles certain known types.</li>
 *         <li>Implement {@link Stringifiable} from the payload class and provided an unstringify constructor.</li>
 *     </ol>
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
public interface TypeSpec<T> extends Key<T> {
    /**
     * Implementers are encouraged to override this method to return a human-readable string.
     * @param t the element to convert to a string
     * @return a string representing <code>t</code> 
     */
    default String stringify(T t) { return Stringifier.stringify(t); }
    /**
     * This method must match the implementation of {@link #stringify(T)}.
     * There is no need to override this method if overriding {@link #unstringify(String, Supplier<Bus>)}.
     * @param s the string to convert
     * @return  an instance of <code>T</code> matching the provided string
     */
    default T unstringify(String s) { return (T) Stringifier.unstringify(s); }

    /**
     * This method exists to allow use of a bus context when converting from a
     * string to an object.
     * There is no need to override this method if overriding {@link #unstringify(String)}.
     * @param s the string to convert
     * @param busSupplier the context bus to use (ignored by the default implementation)
     * @return  an instance of <code>T</code> matching the provided string
     */
    default T unstringify(String s, Supplier<Bus> busSupplier) { return unstringify(s); }
}
