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

package javax.rmi.CORBA;

import org.apache.yoko.rmispec.util.UtilLoader;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PrivilegedAction;

import static java.security.AccessController.doPrivileged;
import static org.apache.yoko.rmispec.util.UtilLoader.loadServiceClass;

public class Util {
    private static final String defaultDelegate = "org.apache.yoko.rmi.impl.UtilImpl";
    private static final String DELEGATE_KEY = "javax.rmi.CORBA.UtilClass";
    private static final UtilDelegate delegate = getDelegate();

    private static UtilDelegate getDelegate() {
        String delegateName = doPrivileged((PrivilegedAction<String>)() -> System.getProperty(DELEGATE_KEY, defaultDelegate));
        try {
            // this is a bit recursive, but this will use the full default search order for locating this.
            Class<? extends UtilDelegate> delegateClass = loadServiceClass(delegateName, DELEGATE_KEY);
            return delegateClass.newInstance();
        } catch (Throwable e) {
            throw (INITIALIZE) new INITIALIZE("Can not create Util delegate: "+delegateName).initCause(e);
        }
    }

    // To hide the default constructor we should implement empty private constructor
    private Util() {}

    public static Object copyObject(Object o, ORB orb) throws RemoteException {
        return delegate.copyObject(o, orb);
    }

    public static Object[] copyObjects(Object[] objs, ORB orb) throws RemoteException {
        return delegate.copyObjects(objs, orb);
    }

    public static ValueHandler createValueHandler() {
        return delegate.createValueHandler();
    }

    public static String getCodebase(Class clz) {
        return delegate.getCodebase(clz);
    }

    public static Tie getTie(Remote t) {
        return delegate.getTie(t);
    }

    public static boolean isLocal(Stub s) throws RemoteException {
        return delegate.isLocal(s);
    }

    public static Class loadClass(String name, String codebase, ClassLoader loader) throws ClassNotFoundException {
        return null == delegate ?
                // If there is no delegate yet, use the default implementation search order
                UtilLoader.loadClass(name, loader) :
                delegate.loadClass(name, codebase, loader);

    }

    public static RemoteException mapSystemException(SystemException e) {
        return delegate.mapSystemException(e);
    }

    public static Object readAny(InputStream is) {
        return delegate.readAny(is);
    }

    public static void registerTarget(Tie tie, Remote target) {
        delegate.registerTarget(tie, target);
    }

    public static void unexportObject(Remote t) throws NoSuchObjectException {
        delegate.unexportObject(t);
    }

    public static RemoteException wrapException(Throwable e) {
        return delegate.wrapException(e);
    }

    public static void writeAbstractObject(OutputStream os, Object o) {
        delegate.writeAbstractObject(os, o);
    }

    public static void writeAny(OutputStream os, Object o) {
        delegate.writeAny(os, o);
    }

    public static void writeRemoteObject(OutputStream os, Object o) {
        delegate.writeRemoteObject(os, o);
    }
}
