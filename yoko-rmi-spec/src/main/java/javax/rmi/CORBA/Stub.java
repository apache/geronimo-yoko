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

/**
 * This class is deliberately not serializable.
 * Its fields are never serialized.
 * When a serializable child class is constructed or deserialized,
 * this class's no-args constructor will be called.
 * This ensures the following:
 * <ul>
 *     <li>every child instance always has a delegate</li>
 *     <li>the delegate is never serialized</li>
 *     <li>the delegate is initialized even on deserialization.</li>
 * </ul>
 */
abstract class DelegateHolder extends ObjectImpl {
    private static final Constructor<? extends StubDelegate> DELEGATE_CONSTRUCTOR;

    static {
        try {
            DELEGATE_CONSTRUCTOR = doPrivEx(DelegateType.STUB.getConstructorAction());
        } catch (Exception e) {
            throw (INITIALIZE) new INITIALIZE("Can not create Stub delegate").initCause(e);
        }
    }

    protected final StubDelegate delegate;

    /** This constructor must be public so that child classes can be deserialized successfully */
    public DelegateHolder() {
        try {
            delegate = DELEGATE_CONSTRUCTOR.newInstance();
        } catch (Exception e) {
            throw (INITIALIZE) new INITIALIZE("Can not create Stub delegate").initCause(e);
        }
    }

    private static <T> T doPrivEx(PrivilegedExceptionAction<T> action) throws PrivilegedActionException { return doPrivileged(action); }
}

public abstract class Stub extends DelegateHolder implements Serializable {
    static final long serialVersionUID = 1087775603798577179L;
    public void connect(ORB orb) throws RemoteException { delegate.connect(this, orb); }
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException { delegate.readObject(this, ois); }
    private void writeObject(ObjectOutputStream oos) throws IOException { delegate.writeObject(this, oos); }
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) { return delegate.equals(this, o); }
    public int hashCode() { return delegate.hashCode(this); }
    public String toString() { return delegate.toString(this); }
}
