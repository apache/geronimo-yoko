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

abstract public class ObjectImpl extends org.omg.CORBA_2_3.portable.ObjectImpl
        implements org.omg.CORBA_2_4.Object {
    public org.omg.CORBA.Policy[] _get_policy_overrides(int[] policy_types) {
        Delegate d = (Delegate) _get_delegate();
        return d.get_policy_overrides(this, policy_types);
    }

    public org.omg.CORBA.Policy _get_client_policy(int policy_type) {
        Delegate d = (Delegate) _get_delegate();
        return d.get_client_policy(this, policy_type);
    }

    public boolean _validate_connection(org.omg.CORBA.PolicyListHolder policies) {
        Delegate d = (Delegate) _get_delegate();
        return d.validate_connection(this, policies);
    }
}
