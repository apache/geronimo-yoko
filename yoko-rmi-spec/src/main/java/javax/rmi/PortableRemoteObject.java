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

import org.apache.yoko.rmispec.util.DelegateType;
import org.omg.CORBA.INITIALIZE;

import javax.rmi.CORBA.PortableRemoteObjectDelegate;
import java.lang.reflect.Constructor;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import static java.security.AccessController.doPrivileged;

public class PortableRemoteObject {
    private static final PortableRemoteObjectDelegate DELEGATE;

    static {
        try {
            Constructor<? extends PortableRemoteObjectDelegate> constructor = doPrivEx(DelegateType.PRO.getConstructorAction());
            DELEGATE = constructor.newInstance();
        } catch (Throwable e) {
            throw (INITIALIZE) new INITIALIZE("Can not create PortableRemoteObject delegate").initCause(e);
        }
    }

    protected PortableRemoteObject() throws RemoteException {
        // Register object
        exportObject((Remote)this);
    }

    public static void connect(Remote target, Remote source) throws RemoteException {
        DELEGATE.connect(target, source);
    }

    public static void exportObject(Remote o) throws RemoteException {
        DELEGATE.exportObject(o);
    }

    public static Object narrow(Object from, Class to) throws ClassCastException {
        return DELEGATE.narrow(from, to);
    }

    public static Remote toStub(Remote o) throws NoSuchObjectException {
        return DELEGATE.toStub(o);
    }

    public static void unexportObject(Remote o) throws NoSuchObjectException {
        DELEGATE.unexportObject(o);
    }

    private static <T> T doPrivEx(PrivilegedExceptionAction<T> action) throws PrivilegedActionException { return doPrivileged(action); }
}

