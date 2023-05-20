/*
 * Copyright 2021 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package javax.rmi.CORBA;

import org.apache.yoko.rmispec.util.DelegateType;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.ObjectImpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.rmi.RemoteException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import static java.security.AccessController.doPrivileged;

public abstract class Stub extends ObjectImpl implements Serializable {
    static final long serialVersionUID = 1087775603798577179L;
    private transient StubDelegate delegate = DelegateHelper.createDelegate();
    public void connect(ORB orb) throws RemoteException { delegate.connect(this, orb); }
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        delegate = DelegateHelper.createDelegate();
        delegate.readObject(this, ois);
    }
    private void writeObject(ObjectOutputStream oos) throws IOException { delegate.writeObject(this, oos); }
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) { return delegate.equals(this, o); }
    public int hashCode() { return delegate.hashCode(this); }
    public String toString() { return delegate.toString(this); }

    enum DelegateHelper {
        ;
        private static final Constructor<? extends StubDelegate> DELEGATE_CONSTRUCTOR;

        static {
            try {
                DELEGATE_CONSTRUCTOR = doPrivEx(DelegateType.STUB.getConstructorAction());
            } catch (Exception e) {
                throw (INITIALIZE) new INITIALIZE("Can not create Stub delegate").initCause(e);
            }
        }

        private static StubDelegate createDelegate() {
            try {
                return DELEGATE_CONSTRUCTOR.newInstance();
            } catch (Exception e) {
                throw (INITIALIZE) new INITIALIZE("Can not create Stub delegate").initCause(e);
            }
        }

        private static <T> T doPrivEx(PrivilegedExceptionAction<T> action) throws PrivilegedActionException { return doPrivileged(action); }
    }
}
