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

package org.omg.CORBA_2_4.portable;

public abstract class Delegate extends org.omg.CORBA_2_3.portable.Delegate {
    public org.omg.CORBA.Policy[] get_policy_overrides(
            org.omg.CORBA.Object self, int[] policy_types) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.Policy get_client_policy(org.omg.CORBA.Object self,
            int policy_type) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public boolean validate_connection(org.omg.CORBA.Object self,
            org.omg.CORBA.PolicyListHolder policies) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
}
