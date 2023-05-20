/*
 * Copyright 2021 IBM Corporation and others.
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
package org.apache.yoko.util;

final public class AssertionFailed extends RuntimeException {
    public AssertionFailed() {
        super("Yoko encountered an internal error");
    }
    public AssertionFailed(String reason) {
        super("Yoko encountered an internal error " + reason);
    }
    
    public AssertionFailed(Throwable ex) {
        super("Yoko encountered an internal error", ex);
    }
    
    public AssertionFailed(String reason, Throwable ex) {
        super("Yoko encountered an internal error " + reason, ex);
    }
}
