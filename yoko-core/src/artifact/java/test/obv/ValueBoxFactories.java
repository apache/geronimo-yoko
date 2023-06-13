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
package test.obv;

public class ValueBoxFactories {
    // Valuebox factories are not automatically generated, as in C++

    public static void install(org.omg.CORBA.ORB orb) {
        org.omg.CORBA_2_3.ORB orb_2_3 = (org.omg.CORBA_2_3.ORB) orb;
        //
        // Install valuebox factories
        //
        orb_2_3.register_value_factory(TestStringBoxHelper.id(), TestStringBoxHelper::read);
        orb_2_3.register_value_factory(TestULongBoxHelper.id(), TestULongBoxHelper::read);
        orb_2_3.register_value_factory(TestFixStructBoxHelper.id(), TestFixStructBoxHelper::read);
        orb_2_3.register_value_factory(TestVarStructBoxHelper.id(), TestVarStructBoxHelper::read);
        orb_2_3.register_value_factory(TestFixUnionBoxHelper.id(), TestFixUnionBoxHelper::read);
        orb_2_3.register_value_factory(TestVarUnionBoxHelper.id(), TestVarUnionBoxHelper::read);
        orb_2_3.register_value_factory(TestAnonSeqBoxHelper.id(), TestAnonSeqBoxHelper::read);
        orb_2_3.register_value_factory(TestStringSeqBoxHelper.id(), TestStringSeqBoxHelper::read);
    }
}
