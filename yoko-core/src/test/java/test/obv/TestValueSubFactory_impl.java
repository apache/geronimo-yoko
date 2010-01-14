/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
*  contributor license agreements.  See the NOTICE file distributed with
*  this work for additional information regarding copyright ownership.
*  The ASF licenses this file to You under the Apache License, Version 2.0
*  (the "License"); you may not use this file except in compliance with
*  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package test.obv;

public class TestValueSubFactory_impl implements TestValueSubValueFactory {
    public java.io.Serializable read_value(
            org.omg.CORBA_2_3.portable.InputStream in) {
        return in.read_value(new TestValueSub_impl());
    }

    public TestValueSub create_sub(int l, String s) {
        TestValueSub result = new TestValueSub_impl();
        result.count = l;
        result.name = s;
        return result;
    }

    public static TestValueSubValueFactory install(org.omg.CORBA.ORB orb) {
        org.omg.CORBA_2_3.ORB orb_2_3 = (org.omg.CORBA_2_3.ORB) orb;
        TestValueSubValueFactory result = new TestValueSubFactory_impl();
        orb_2_3.register_value_factory(TestValueSubHelper.id(), result);
        return result;

    }
}
