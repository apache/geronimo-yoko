/*
 * Copyright 2010 IBM Corporation and others.
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
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;

final class TestLocationForward_impl extends TestLocationForwardPOA {
    private Test_impl delegate_;

    TestLocationForward_impl(ORB orb) {
        delegate_ = new Test_impl(orb, "", false);
    }

    public void deactivate_servant() {
        byte[] oid = null;
        POA poa = null;

        try {
            oid = delegate_.current_.get_object_id();
            poa = delegate_.current_.get_POA();
        } catch (org.omg.PortableServer.CurrentPackage.NoContext ex) {
            throw new RuntimeException();
        }

        try {
            poa.deactivate_object(oid);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        }
    }

    public void aMethod() {
        delegate_.aMethod();
    }
}
