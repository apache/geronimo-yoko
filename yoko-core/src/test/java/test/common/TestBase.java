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

package test.common;

public class TestBase {
    public static org.omg.CORBA.TypeCode getOrigType(org.omg.CORBA.TypeCode tc) {
        org.omg.CORBA.TypeCode result = tc;

        try {
            while (result.kind() == org.omg.CORBA.TCKind.tk_alias)
                result = result.content_type();
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            throw new AssertionError(ex);
        }

        return result;
    }
}
