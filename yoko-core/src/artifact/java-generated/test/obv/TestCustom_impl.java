/*
 * Copyright 2015 IBM Corporation and others.
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
package test.obv;

import static org.junit.Assert.assertTrue;

public class TestCustom_impl extends TestCustom {
    public void marshal(org.omg.CORBA.DataOutputStream os) {
        //
        // We can write anything we want here, we just have to make
        // sure we are consistent when unmarshalling.
        //
        os.write_string("Extra String");
        os.write_string(stringVal);
        os.write_double(doubleVal);
        os.write_long(longVal);
        os.write_short(shortVal);
    }

    public void unmarshal(org.omg.CORBA.DataInputStream is) {
        String str = is.read_string();
        assertTrue(str.equals("Extra String"));

        stringVal = is.read_string();
        doubleVal = is.read_double();
        longVal = is.read_long();
        shortVal = is.read_short();
    }

    public void ping1() {
        // do nothing
    }
}
