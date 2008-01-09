/**
*
* Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.apache.yoko.rmi.impl;

import org.apache.yoko.rmi.api.PortableRemoteObjectExt;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ServantObject;


public abstract class RMIStub extends javax.rmi.CORBA.Stub {
    protected final transient RemoteDescriptor _descriptor;

    protected RMIServant _servant;

    protected ServantObject _so;

    protected Delegate _delegate;

    public RMIStub() {
        super();

        Class remote_interface = getClass().getInterfaces()[0];

        RMIState state = (RMIState) PortableRemoteObjectExt.getState();
        Object o = state.getTypeRepository().getRemoteDescriptor(
                remote_interface);

        _descriptor = (RemoteDescriptor) o;
    }

    public String[] _ids() {
        return _descriptor.all_interfaces();
    }

    // replace stub with RMIPersistentStub
    protected abstract Object writeReplace();

    private void writeObject(java.io.ObjectOutputStream oo)
            throws java.io.IOException {
        //
        // This should never happen, because writeReplace ought to
        // have been called.
        //
        throw new java.io.IOException("should not happen");
    }

    // apparently this implementation is expected!
    public String toString() {
        return _orb().object_to_string(this);
    }

    /**
     * @see org.omg.CORBA.portable.ObjectImpl#_get_delegate()
     */
    public Delegate _get_delegate() {
        if (_delegate != null) {
            return _delegate;
        } else if (_servant != null) {
            return (_delegate = _servant.getDelegate());
        } else {
            return super._get_delegate();
        }
    }

    public void _set_delegate(Delegate del) {
        _delegate = del;
    }

    /**
     * @see org.omg.CORBA.portable.ObjectImpl#_is_local()
     */
    public boolean _is_local() {
        if (_servant != null) {
            return true;
        } else {
            return super._is_local();
        }
    }

    /**
     * @see org.omg.CORBA.portable.ObjectImpl#_servant_postinvoke(ServantObject)
     */
    public void _servant_postinvoke(ServantObject servant) {
        if (_servant == null) {
            super._servant_postinvoke(servant);
        } else {
            // do nothing //
        }
    }

    /**
     * @see org.omg.CORBA.portable.ObjectImpl#_servant_preinvoke(String, Class)
     */
    public ServantObject _servant_preinvoke(String operation, Class expectedType) {
        if (_servant == null) {
            return super._servant_preinvoke(operation, expectedType);
        } else {
            if (_so == null) {
                ServantObject so = new ServantObject();
                so.servant = _servant;
                _so = so;
            }
            return _so;
        }
    }

}
