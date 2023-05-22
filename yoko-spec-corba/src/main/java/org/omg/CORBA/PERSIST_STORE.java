/*
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
package org.omg.CORBA;

final public class PERSIST_STORE extends org.omg.CORBA.SystemException {
    public PERSIST_STORE() {
        super("", 0, CompletionStatus.COMPLETED_NO);
    }

    public PERSIST_STORE(int minor, CompletionStatus completed) {
        super("", minor, completed);
    }

    public PERSIST_STORE(String reason) {
        super(reason, 0, CompletionStatus.COMPLETED_NO);
    }

    public PERSIST_STORE(String reason, int minor, CompletionStatus completed) {
        super(reason, minor, completed);
    }
}
