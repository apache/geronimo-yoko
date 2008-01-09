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

import java.lang.reflect.InvocationTargetException;

import org.omg.PortableServer.portable.Delegate;

abstract public class Servant {
    private transient org.omg.PortableServer.portable.Delegate delegate_ = null;

    final public org.omg.CORBA.Object _this_object() {
        return _get_delegate().this_object(this);
    }

    final public org.omg.CORBA.Object _this_object(org.omg.CORBA.ORB orb) {
        try {
            ((org.omg.CORBA_2_3.ORB) orb).set_delegate(this);
        } catch (ClassCastException ex) {
            throw (org.omg.CORBA.BAD_PARAM)new org.omg.CORBA.BAD_PARAM(
                    "POA servant requires an instance of org.omg.CORBA_2_3.ORB").initCause(ex);
        }

        return _this_object();
    }

    final public org.omg.CORBA.ORB _orb() {
        return _get_delegate().orb(this);
    }

    final public org.omg.PortableServer.POA _poa() {
        return _get_delegate().poa(this);
    }

    final public byte[] _object_id() {
        return _get_delegate().object_id(this);
    }

    public org.omg.PortableServer.POA _default_POA() {
        return _get_delegate().default_POA(this);
    }

    public boolean _is_a(String repository_id) {
        return _get_delegate().is_a(this, repository_id);
    }

    public boolean _non_existent() {
        return _get_delegate().non_existent(this);
    }

    static java.lang.reflect.Method get_interface_method;
    static {
        Class delegate_class = org.omg.PortableServer.portable.Delegate.class;
        Class servant_class = org.omg.PortableServer.Servant.class;
        try {
            get_interface_method = delegate_class.getMethod("get_interface",
                    new Class[] { servant_class });
        } catch (SecurityException e) {
            // TODO should we just ignore this?
        } catch (NoSuchMethodException e) {
            // TODO should we just ignore this?
        }
    }

    public org.omg.CORBA.InterfaceDef _get_interface() {
        if (get_interface_method == null) {
            throw new org.omg.CORBA.NO_IMPLEMENT();
        }
        org.omg.PortableServer.portable.Delegate delegate = _get_delegate();
        Object result;
        try {
            result = get_interface_method.invoke(delegate,
                    new Object[] { this });
        } catch (InvocationTargetException e) {
            Throwable ee = e.getTargetException();
            if (ee instanceof RuntimeException) {
                throw (RuntimeException) ee;
            }
            org.omg.CORBA.INTERNAL iex = new org.omg.CORBA.INTERNAL();
            iex.initCause(ee);
            throw iex;
        } catch (Exception e) {
            org.omg.CORBA.INTERNAL iex = new org.omg.CORBA.INTERNAL();
            iex.initCause(e);
            throw iex;
        }

        return (org.omg.CORBA.InterfaceDef) result;
    }

    public org.omg.CORBA.Object _get_interface_def() {
        return _get_delegate().get_interface_def(this);
    }

    abstract public String[] _all_interfaces(org.omg.PortableServer.POA poa,
            byte[] object_id);

    final public org.omg.PortableServer.portable.Delegate _get_delegate() {
        if (delegate_ == null)
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    "The servant has not been associated with an ORB instance");

        return delegate_;
    }

    final public void _set_delegate(
            org.omg.PortableServer.portable.Delegate delegate) {
        delegate_ = delegate;
    }
}
