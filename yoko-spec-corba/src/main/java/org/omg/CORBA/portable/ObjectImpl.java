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

package org.omg.CORBA.portable;

abstract public class ObjectImpl implements org.omg.CORBA.Object {
    private transient Delegate delegate_;

    public Delegate _get_delegate() {
        if (delegate_ == null)
            throw new org.omg.CORBA.BAD_OPERATION();

        return delegate_;
    }

    public void _set_delegate(Delegate delegate) {
        delegate_ = delegate;
    }

    public abstract String[] _ids();

    /**
     * @deprecated Deprecated by CORBA 2.3.
     */
    public org.omg.CORBA.InterfaceDef _get_interface() {
        return _get_delegate().get_interface(this);
    }

    public org.omg.CORBA.Object _get_interface_def() {
        return _get_delegate().get_interface_def(this);
    }

    public org.omg.CORBA.Object _duplicate() {
        return _get_delegate().duplicate(this);
    }

    public void _release() {
        _get_delegate().release(this);
    }

    public boolean _is_a(String repository_id) {
        return _get_delegate().is_a(this, repository_id);
    }

    public boolean _is_equivalent(org.omg.CORBA.Object rhs) {
        return _get_delegate().is_equivalent(this, rhs);
    }

    public boolean _non_existent() {
        return _get_delegate().non_existent(this);
    }

    public int _hash(int maximum) {
        return _get_delegate().hash(this, maximum);
    }

    public org.omg.CORBA.Request _request(String operation) {
        return _get_delegate().request(this, operation);
    }

    public org.omg.CORBA.portable.OutputStream _request(String operation,
            boolean responseExpected) {
        return _get_delegate().request(this, operation, responseExpected);
    }

    public org.omg.CORBA.portable.InputStream _invoke(
            org.omg.CORBA.portable.OutputStream out)
            throws ApplicationException, RemarshalException {
        return _get_delegate().invoke(this, out);
    }

    public void _releaseReply(org.omg.CORBA.portable.InputStream in) {
        _get_delegate().releaseReply(this, in);
    }

    public org.omg.CORBA.Request _create_request(org.omg.CORBA.Context ctx,
            String operation, org.omg.CORBA.NVList arg_list,
            org.omg.CORBA.NamedValue result) {
        return _get_delegate().create_request(this, ctx, operation, arg_list,
                result);
    }

    public org.omg.CORBA.Request _create_request(org.omg.CORBA.Context ctx,
            String operation, org.omg.CORBA.NVList arg_list,
            org.omg.CORBA.NamedValue result,
            org.omg.CORBA.ExceptionList exclist,
            org.omg.CORBA.ContextList ctxlist) {
        return _get_delegate().create_request(this, ctx, operation, arg_list,
                result, exclist, ctxlist);
    }

    public org.omg.CORBA.Policy _get_policy(int policy_type) {
        return _get_delegate().get_policy(this, policy_type);
    }

    public org.omg.CORBA.DomainManager[] _get_domain_managers() {
        return _get_delegate().get_domain_managers(this);
    }

    public org.omg.CORBA.Object _set_policy_override(
            org.omg.CORBA.Policy[] policies,
            org.omg.CORBA.SetOverrideType set_add) {
        return _get_delegate().set_policy_override(this, policies, set_add);
    }

    public org.omg.CORBA.ORB _orb() {
        return _get_delegate().orb(this);
    }

    public boolean _is_local() {
        return _get_delegate().is_local(this);
    }

    public ServantObject _servant_preinvoke(String operation, Class expectedType) {
        return _get_delegate().servant_preinvoke(this, operation, expectedType);
    }

    public void _servant_postinvoke(ServantObject servant) {
        _get_delegate().servant_postinvoke(this, servant);
    }

    public String toString() {
        if (delegate_ != null)
            return delegate_.toString(this);
        else
            return getClass().getName() + ": no delegate set";
    }

    public int hashCode() {
        if (delegate_ != null)
            return delegate_.hashCode(this);
        else
            return System.identityHashCode(this);
    }

    public boolean equals(java.lang.Object obj) {
        if (delegate_ != null)
            return delegate_.equals(this, obj);
        else
            return (this == obj);
    }
}
