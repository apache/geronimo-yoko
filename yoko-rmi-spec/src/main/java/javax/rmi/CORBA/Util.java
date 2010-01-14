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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.security.AccessController;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

import org.apache.yoko.rmispec.util.GetSystemPropertyAction;
import org.apache.yoko.rmispec.util.UtilLoader;

public class Util {
    private static UtilDelegate delegate = null;
    private static final String defaultDelegate = "org.apache.yoko.rmi.impl.UtilImpl";

    // To hide the default constructor we should implement empty private constructor
    private Util() {}

    static {
        // Initialize delegate
        String delegateName = (String)AccessController.doPrivileged(new GetSystemPropertyAction("javax.rmi.CORBA.UtilClass", defaultDelegate));
        try {

            // this is a little bit recursive, but this will use the full default search order for locating
            // this.
            delegate = (UtilDelegate)Util.loadClass(delegateName, null, null).newInstance();
        } catch (Throwable e) {
            org.omg.CORBA.INITIALIZE ex = new org.omg.CORBA.INITIALIZE("Can not create Util delegate: "+delegateName);
            ex.initCause(e); 
            throw ex; 
        }
    }

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
        if (delegate != null) {
            return delegate.loadClass(name, codebase, loader);
        }

        // Things get a little recursive here.  We still need to use the full defined search order for loading
        // classes even when attempting to load the UtilDelegate itself.  So, we're going to use the default
        // implementation for the bootstrapping.
        return UtilLoader.loadClass(name, codebase, loader);
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
