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
package test.poa;

import org.omg.CORBA.*;

public final class TestServer_impl extends TestServerPOA {
    private ORB orb_;

    private TestInfo[] info_;

    public TestServer_impl(ORB orb, TestInfo[] info) {
        orb_ = orb;
        info_ = info;
    }

    public TestInfo[] get_info() {
        return info_;
    }

    public void deactivate() {
        if (orb_ != null) {
            orb_.shutdown(false);
            orb_ = null;
        }
    }
}
