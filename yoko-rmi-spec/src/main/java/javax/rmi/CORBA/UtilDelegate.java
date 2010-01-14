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
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public interface UtilDelegate {
    Object copyObject(Object o, ORB orb) throws RemoteException;
    Object[] copyObjects(Object[] objs, ORB orb) throws RemoteException;
    ValueHandler createValueHandler();
    String getCodebase(Class clz);
    Tie getTie(Remote t);
    boolean isLocal(Stub s) throws RemoteException;
    Class loadClass(String name, String codebase, ClassLoader loader) throws ClassNotFoundException;
    RemoteException mapSystemException(SystemException e);
    Object readAny(InputStream is);
    void registerTarget(Tie tie, Remote target);
    void unexportObject(Remote target) throws NoSuchObjectException;
    RemoteException wrapException(Throwable obj);
    void writeAbstractObject(OutputStream os, Object o);
    void writeAny(OutputStream os, Object o);
    void writeRemoteObject(OutputStream os, Object o);
}
