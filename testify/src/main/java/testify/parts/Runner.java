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

import testify.bus.InterProcessBus;

import java.util.concurrent.TimeUnit;

/**
 * The main idea of the Testify framework — allow a piece of work to be executed in another context, such as:
 * <ul>
 *     <li>another thread</li>
 *     <li>another process</li>
 * </ul>
 *
 * This interface abstracts that idea to allow that work to be started, awaited, or terminated.
 *
 * @param <J> the runtime type representing the context, such as a {@link Thread} or {@link Process}
 */
interface Runner<J> {
    J fork(InterProcessBus centralBus, NamedPart part);

    boolean join(J job, long timeout, TimeUnit unit) throws InterruptedException;

    boolean stop(J job, long timeout, TimeUnit unit) throws InterruptedException;
}
