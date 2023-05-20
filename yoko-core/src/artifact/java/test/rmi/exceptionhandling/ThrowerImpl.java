/*
 * Copyright 2022 IBM Corporation and others.
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
package test.rmi.exceptionhandling;

import java.rmi.RemoteException;

import org.omg.CORBA.ORB;

public class ThrowerImpl implements Thrower {
    public static MyRuntimeException myRuntimeException;
    public static MyAppException myAppException;
    final ORB orb;

    public ThrowerImpl(ORB orb) {
        this.orb = orb;
    }

    @Override
    public void throwAppException() throws RemoteException, MyAppException {
        throw myAppException;
    }

    @Override
    public void throwRuntimeException() throws RemoteException {
        throw myRuntimeException;
    }
}
