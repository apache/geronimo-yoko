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

package javax.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.security.AccessController;
import javax.rmi.CORBA.PortableRemoteObjectDelegate;
import javax.rmi.CORBA.Util;

import org.apache.yoko.rmispec.util.GetSystemPropertyAction;

public class PortableRemoteObject {
    private static PortableRemoteObjectDelegate delegate = null;
    private static final String defaultDelegate = "org.apache.yoko.rmi.impl.PortableRemoteObjectImpl";

    static {
        // Initialize delegate
        String delegateName = (String)AccessController.doPrivileged(new GetSystemPropertyAction("javax.rmi.CORBA.PortableRemoteObjectClass", defaultDelegate));
        try {
            delegate = (PortableRemoteObjectDelegate)Util.loadClass(delegateName, null, null).newInstance();
        } catch (Throwable e) {
           org.omg.CORBA.INITIALIZE ex = new org.omg.CORBA.INITIALIZE("Can not create PortableRemoteObject delegate: "+delegateName);
           ex.initCause(e); 
           throw ex; 
        }
    }

    protected PortableRemoteObject() throws RemoteException {
        // Register object
        exportObject((Remote)this);
    }

    public static void connect(Remote target, Remote source) throws RemoteException {
        delegate.connect(target, source);
    }

    public static void exportObject(Remote o) throws RemoteException {
        delegate.exportObject(o);
    }

    public static Object narrow(Object from, Class to) throws ClassCastException {
        return delegate.narrow(from, to);
    }

    public static Remote toStub(Remote o) throws NoSuchObjectException {
        return delegate.toStub(o);
    }

    public static void unexportObject(Remote o) throws NoSuchObjectException {
        delegate.unexportObject(o);
    }
}

