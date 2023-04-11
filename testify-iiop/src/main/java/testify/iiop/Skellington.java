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
package testify.iiop;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;

import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Skellington extends Servant implements Tie, Remote {
    private final String[] ids;

    public Skellington() {
        final ValueHandler vh = Util.createValueHandler();
        this.ids = findRemoteInterfaces(this.getClass())
                .map(vh::getRMIRepositoryID)
                .collect(Collectors.toList())
                .toArray(new String[0]);
    }

    private static Stream<Class<? extends Remote>> findRemoteInterfaces(final Class<?> forClass) {
        Set<Class<? extends Remote>> ifaces = new TreeSet<>((c1, c2) -> c1.getName().compareTo(c2.getName()));
        for (Class<?> c = forClass; c != Object.class; c = c.getSuperclass()) {
            NEXT_CLASS: for (Class<?> iface: c.getInterfaces()) {
                if (Remote.class.isAssignableFrom(iface)) {
                    for (Method m : iface.getMethods()) {
                        if (Arrays.asList(m.getExceptionTypes()).contains(RemoteException.class))
                            continue;
                        continue NEXT_CLASS;
                    }
                    // there were no non-remote methods, so add the interface
                    ifaces.add((Class<? extends Remote>)iface);
                }
            }
        }
        return ifaces.stream();
    }

    @Override
    public String[] _all_interfaces(POA poa, byte[] objectId) {
        return ids.clone();
    }

    @Override
    public org.omg.CORBA.Object thisObject() {
        return _this_object();
    }

    @Override
    public void deactivate() {
        try{
            _poa().deactivate_object(_poa().servant_to_id(this));
        } catch (WrongPolicy |ObjectNotActive |ServantNotActive ignored){}
    }

    @Override
    public ORB orb() {return _orb();}

    @Override
    public void orb(ORB orb) {
        try {
            ((org.omg.CORBA_2_3.ORB)orb).set_delegate(this);
        } catch(ClassCastException e) {
            throw new BAD_PARAM("POA Servant requires an instance of org.omg.CORBA_2_3.ORB");
        }
    }

    @Override
    public void setTarget(Remote target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Remote getTarget() {
        return this;
    }

    @Override
    public OutputStream  _invoke(String method, InputStream _in, ResponseHandler reply) throws SystemException {
        try {
            return dispatch(method, (org.omg.CORBA_2_3.portable.InputStream) _in, reply);
        } catch (SystemException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new UnknownException(ex);
        }
    }

    public String publish(ORB serverORB) throws InvalidName, AdapterInactive, ServantAlreadyActive, WrongPolicy {
        POA rootPOA = POAHelper.narrow(serverORB.resolve_initial_references("RootPOA"));
        rootPOA.the_POAManager().activate();
        rootPOA.activate_object(this);
        return serverORB.object_to_string(thisObject());
    }

    protected abstract OutputStream dispatch(String method, org.omg.CORBA_2_3.portable.InputStream in, ResponseHandler reply) throws RemoteException;
}
