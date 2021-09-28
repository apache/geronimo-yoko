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

import org.apache.yoko.rmispec.util.UtilLoader;
import org.omg.CORBA.INITIALIZE;

import javax.rmi.CORBA.PortableRemoteObjectDelegate;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PrivilegedAction;

import static java.security.AccessController.doPrivileged;

public class PortableRemoteObject {
    // Initialize delegate
    private static final String defaultDelegate = "org.apache.yoko.rmi.impl.PortableRemoteObjectImpl";
    private static final String DELEGATE_KEY = "javax.rmi.CORBA.PortableRemoteObjectClass";
    private static final PortableRemoteObjectDelegate delegate = getDelegate();

    private static PortableRemoteObjectDelegate getDelegate() {
        PortableRemoteObjectDelegate d;
        String delegateName = doPrivileged((PrivilegedAction<String>)() -> System.getProperty(DELEGATE_KEY, defaultDelegate));
        try {
            Class<? extends PortableRemoteObjectDelegate> delegateClass = UtilLoader.loadServiceClass(delegateName, DELEGATE_KEY);
            d = delegateClass.newInstance();
        } catch (Throwable e) {
            throw (INITIALIZE) new INITIALIZE("Can not create PortableRemoteObject delegate: " + delegateName).initCause(e);
        }
        return d;
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

