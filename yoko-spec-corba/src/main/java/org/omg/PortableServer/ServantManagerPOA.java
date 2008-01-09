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

package org.omg.PortableServer;

//
// This class is provided only for backward compatibility.
//
public abstract class ServantManagerPOA extends org.omg.PortableServer.Servant
        implements ServantManager {
    public ServantManager _this() {
        return this;
    }

    public ServantManager _this(org.omg.CORBA.ORB orb) {
        return this;
    }

    public String[] _all_interfaces(org.omg.PortableServer.POA poa,
            byte[] objectId) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    //
    // Implementation of org.omg.CORBA.Object methods
    //

    public boolean _is_a(String repository_id) {
        throw new org.omg.CORBA.NO_IMPLEMENT(
                "operation not supported by local object", 0x4f4d0000 | 3, // MinorNotSupportedByLocalObject
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    public boolean _is_equivalent(org.omg.CORBA.Object rhs) {
        return equals(rhs);
    }

    public boolean _non_existent() {
        return false;
    }

    public int _hash(int maximum) {
        //
        // Calculate a local hash value
        //
        return hashCode() % (maximum + 1);
    }

    public org.omg.CORBA.Object _duplicate() {
        throw new org.omg.CORBA.NO_IMPLEMENT(
                "operation not supported by local object", 0x4f4d0000 | 3, // MinorNotSupportedByLocalObject
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    public void _release() {
        throw new org.omg.CORBA.NO_IMPLEMENT(
                "operation not supported by local object", 0x4f4d0000 | 3, // MinorNotSupportedByLocalObject
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    /**
     * @deprecated Deprecated by CORBA 2.3.
     */
    public org.omg.CORBA.InterfaceDef _get_interface() {
        throw new org.omg.CORBA.NO_IMPLEMENT(
                "operation not supported by local object", 0x4f4d0000 | 3, // MinorNotSupportedByLocalObject
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    public org.omg.CORBA.Object _get_interface_def() {
        throw new org.omg.CORBA.NO_IMPLEMENT(
                "operation not supported by local object", 0x4f4d0000 | 3, // MinorNotSupportedByLocalObject
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    public org.omg.CORBA.Request _request(String operation) {
        throw new org.omg.CORBA.NO_IMPLEMENT(
                "DII operation not supported by local object", 0x4f4d0000 | 4, // MinorDIINotSupportedByLocalObject
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    public org.omg.CORBA.Request _create_request(org.omg.CORBA.Context ctx,
            String operation, org.omg.CORBA.NVList arg_list,
            org.omg.CORBA.NamedValue result) {
        throw new org.omg.CORBA.NO_IMPLEMENT(
                "DII operation not supported by local object", 0x4f4d0000 | 4, // MinorDIINotSupportedByLocalObject
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    public org.omg.CORBA.Request _create_request(org.omg.CORBA.Context ctx,
            String operation, org.omg.CORBA.NVList arg_list,
            org.omg.CORBA.NamedValue result,
            org.omg.CORBA.ExceptionList excepts,
            org.omg.CORBA.ContextList contexts) {
        throw new org.omg.CORBA.NO_IMPLEMENT(
                "DII operation not supported by local object", 0x4f4d0000 | 4, // MinorDIINotSupportedByLocalObject
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    public org.omg.CORBA.Policy _get_policy(int policy_type) {
        throw new org.omg.CORBA.NO_IMPLEMENT(
                "operation not supported by local object", 0x4f4d0000 | 3, // MinorNotSupportedByLocalObject
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    public org.omg.CORBA.Object _set_policy_override(
            org.omg.CORBA.Policy[] policies,
            org.omg.CORBA.SetOverrideType set_add) {
        throw new org.omg.CORBA.NO_IMPLEMENT(
                "operation not supported by local object", 0x4f4d0000 | 3, // MinorNotSupportedByLocalObject
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    public org.omg.CORBA.DomainManager[] _get_domain_managers() {
        throw new org.omg.CORBA.NO_IMPLEMENT(
                "operation not supported by local object", 0x4f4d0000 | 3, // MinorNotSupportedByLocalObject
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }
}
