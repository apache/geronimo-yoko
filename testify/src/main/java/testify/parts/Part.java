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
package testify.parts;

import testify.bus.Bus;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * A piece of work to be performed in another context, such as a separate thread or a new process.
 * <br>
 * Logically, this is like a {@link Consumer} of a {@link Bus},
 * but it enforces an additional requirement on the compiler:
 * any supplied lambda or method reference will implement {@link Serializable}.
 * This matters when sending the part to a remote process.
 * <br>
 * <em>N.B. for a lambda or method reference to be serialized,
 * all its explicit and implicit references must be serializable.</em>
 */
@FunctionalInterface
public interface Part extends Serializable {
    void run(Bus bus) throws Throwable;

    default Part andThen(Part that) {
        if (that == NO_OP) return this;
        if (this == NO_OP) return that;
        return bus -> {
            this.run(bus);
            that.run(bus);
        };
    }

    default Part butFirst(Part that) {
        return that.andThen(this);
    }

    Part NO_OP = bus -> {};
}
