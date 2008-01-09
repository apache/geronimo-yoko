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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.security.AccessController;

import org.apache.yoko.rmispec.util.GetSystemPropertyAction;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.ObjectImpl;

public abstract class Stub extends ObjectImpl implements Serializable {
    static final long serialVersionUID = 1087775603798577179L;

    private transient StubDelegate delegate = null;
    private static final String defaultDelegate = "org.apache.yoko.rmi.impl.StubImpl";
    // the class we use to create delegates.  This is loaded once,
    private static Class delegateClass = null;

    static {
        // Initialize delegate
        String delegateName = (String)AccessController.doPrivileged(new GetSystemPropertyAction("javax.rmi.CORBA.StubClass", defaultDelegate));
        try {
            delegateClass = Util.loadClass(delegateName, null, null);
        } catch (Exception e) {
            org.omg.CORBA.INITIALIZE ex = new org.omg.CORBA.INITIALIZE("Can not create Stub delegate: " + delegateName);
            ex.initCause(e); 
            throw ex; 
        }
    }

    public Stub() {
        super();
        initializeDelegate();
    }

    public void connect(ORB orb) throws RemoteException {
        initializeDelegate();
        delegate.connect(this, orb);
    }

    public boolean equals(Object o) {
        initializeDelegate();
        return delegate.equals(this, o);
    }

    public int hashCode() {
        initializeDelegate();
        return delegate.hashCode(this);
    }

    public String toString() {
        initializeDelegate();
        return delegate.toString(this);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        initializeDelegate();
        delegate.readObject(this, ois);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        initializeDelegate();
        delegate.writeObject(this, oos);
    }

    /**
     * Lazy initialization routine for the Stub delegate.  Normally, you'd do this
     * in the constructor.  Unfortunately, Java serialization will not call the
     * constructor for Serializable classes, so we need to ensure we have one
     * regardless of how/when we are called.
     */
    private void initializeDelegate() {
        if (delegate == null) {
            try {
                delegate = (StubDelegate)delegateClass.newInstance();
            } catch (Exception e) {
                throw (org.omg.CORBA.INITIALIZE)new org.omg.CORBA.INITIALIZE(
                    "Can not create Stub delegate: " + delegateClass.getName()).initCause(e);
            }
        }
    }
}
