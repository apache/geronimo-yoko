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

package org.apache.yoko.rmi.util.corba;

import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public class POAObject extends org.omg.CORBA_2_3.portable.ObjectImpl {
    private Servant servant;

    protected POA poa;

    // non-null if object is exported
    protected byte[] id;

    protected POAObject(Servant servant, POA poa) {
        this.servant = servant;
        this.poa = poa;

        if (servant == null || poa == null) {
            throw new IllegalArgumentException();
        }
    }

    public String[] _ids() {
        return servant._all_interfaces(poa, null);
    }

    public org.omg.CORBA.portable.Delegate _get_delegate() {
        try {
            return super._get_delegate();
        } catch (org.omg.CORBA.BAD_OPERATION ex) {
            //
        }

        synchronized (this) {

            try {
                return super._get_delegate();
            } catch (org.omg.CORBA.BAD_OPERATION ex) {
                //
            }

            org.omg.CORBA.portable.ObjectImpl ref = export();

            org.omg.CORBA.portable.Delegate delegate = ref._get_delegate();
            this._set_delegate(delegate);

            return delegate;
        }
    }

    private org.omg.CORBA.portable.ObjectImpl export() {
        try {

            if (id != null) {
                throw new Error("Internal consistency error!");
            }

            id = poa.activate_object(servant);

            return (org.omg.CORBA.portable.ObjectImpl) poa.id_to_reference(id);

        } catch (org.omg.PortableServer.POAPackage.ServantAlreadyActive ex) {
            throw new Error(ex.getMessage(), ex);

        } catch (org.omg.PortableServer.POAPackage.ObjectNotActive ex) {
            throw new Error(ex.getMessage(), ex);

        } catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
            throw new Error(ex.getMessage(), ex);
        }
    }

    public synchronized void unexport() {
        if (id == null) {
            return;
        }

        try {
            poa.deactivate_object(id);
            id = null;

        } catch (org.omg.PortableServer.POAPackage.ObjectNotActive ex) {
            throw (org.omg.CORBA.INTERNAL)new 
                org.omg.CORBA.INTERNAL("ObjectNotActive::" + ex.getMessage()).initCause(ex);
        } catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
            throw (org.omg.CORBA.INTERNAL)new 
                org.omg.CORBA.INTERNAL("WrongPolicy::" + ex.getMessage()).initCause(ex);
        }
    }
}
