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
    //
    // Valuebox factories are not automatically generated, as in C++
    //

    static class TestStringBoxFactory_impl implements
            org.omg.CORBA.portable.ValueFactory {
        public java.io.Serializable read_value(
                org.omg.CORBA_2_3.portable.InputStream in) {
            return TestStringBoxHelper.read(in);
        }
    }

    static class TestULongBoxFactory_impl implements
            org.omg.CORBA.portable.ValueFactory {
        public java.io.Serializable read_value(
                org.omg.CORBA_2_3.portable.InputStream in) {
            return TestULongBoxHelper.read(in);
        }
    }

    static class TestFixStructBoxFactory_impl implements
            org.omg.CORBA.portable.ValueFactory {
        public java.io.Serializable read_value(
                org.omg.CORBA_2_3.portable.InputStream in) {
            return TestFixStructBoxHelper.read(in);
        }
    }

    static class TestVarStructBoxFactory_impl implements
            org.omg.CORBA.portable.ValueFactory {
        public java.io.Serializable read_value(
                org.omg.CORBA_2_3.portable.InputStream in) {
            return TestVarStructBoxHelper.read(in);
        }
    }

    static class TestFixUnionBoxFactory_impl implements
            org.omg.CORBA.portable.ValueFactory {
        public java.io.Serializable read_value(
                org.omg.CORBA_2_3.portable.InputStream in) {
            return TestFixUnionBoxHelper.read(in);
        }
    }

    static class TestVarUnionBoxFactory_impl implements
            org.omg.CORBA.portable.ValueFactory {
        public java.io.Serializable read_value(
                org.omg.CORBA_2_3.portable.InputStream in) {
            return TestVarUnionBoxHelper.read(in);
        }
    }

    static class TestAnonSeqBoxFactory_impl implements
            org.omg.CORBA.portable.ValueFactory {
        public java.io.Serializable read_value(
                org.omg.CORBA_2_3.portable.InputStream in) {
            return TestAnonSeqBoxHelper.read(in);
        }
    }

    static class TestStringSeqBoxFactory_impl implements
            org.omg.CORBA.portable.ValueFactory {
        public java.io.Serializable read_value(
                org.omg.CORBA_2_3.portable.InputStream in) {
            return TestStringSeqBoxHelper.read(in);
        }
    }

    static void install(org.omg.CORBA.ORB orb) {
        org.omg.CORBA_2_3.ORB orb_2_3 = (org.omg.CORBA_2_3.ORB) orb;

        //
        // Install valuebox factories
        //
        orb_2_3.register_value_factory(TestStringBoxHelper.id(),
                new TestStringBoxFactory_impl());
        orb_2_3.register_value_factory(TestULongBoxHelper.id(),
                new TestULongBoxFactory_impl());
        orb_2_3.register_value_factory(TestFixStructBoxHelper.id(),
                new TestFixStructBoxFactory_impl());
        orb_2_3.register_value_factory(TestVarStructBoxHelper.id(),
                new TestVarStructBoxFactory_impl());
        orb_2_3.register_value_factory(TestFixUnionBoxHelper.id(),
                new TestFixUnionBoxFactory_impl());
        orb_2_3.register_value_factory(TestVarUnionBoxHelper.id(),
                new TestVarUnionBoxFactory_impl());
        orb_2_3.register_value_factory(TestAnonSeqBoxHelper.id(),
                new TestAnonSeqBoxFactory_impl());
        orb_2_3.register_value_factory(TestStringSeqBoxHelper.id(),
                new TestStringSeqBoxFactory_impl());
    }
}
