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

final class TestUtil {
    static POA GetRootPOA(ORB orb) {
        org.omg.CORBA.Object obj = null;

        try {
            obj = orb.resolve_initial_references("RootPOA");
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            System.err.println("Error: can't resolve `RootPOA'");
            System.exit(1);
        }

        if (obj == null) {
            System.err.println("Error: `RootPOA' is a nil object reference");
            System.exit(1);
        }

        POA root = null;
        try {
            root = POAHelper.narrow(obj);
        } catch (BAD_PARAM ex) {
            System.err
                    .println("Error: `RootPOA' is not a POA object reference");
            System.exit(1);
        }

        return root;
    }
}
