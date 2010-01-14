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
package org.apache.yoko.orb.PortableServer;

final class CurrentState {
    org.omg.PortableServer.POA poa;

    org.omg.PortableServer.Servant servant;

    java.lang.Object cookie;

    byte[] oid;

    String op;

    CurrentState next;
}

final public class Current_impl extends org.omg.CORBA.LocalObject implements
        org.omg.PortableServer.Current {
    private java.util.Hashtable stateKey_ = new java.util.Hashtable();

    public Current_impl() {
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }

    public org.omg.PortableServer.POA get_POA()
            throws org.omg.PortableServer.CurrentPackage.NoContext {
        CurrentState state = (CurrentState) stateKey_.get(Thread
                .currentThread());

        if (state == null || state.poa == null)
            throw new org.omg.PortableServer.CurrentPackage.NoContext();
        return state.poa;
    }

    public byte[] get_object_id()
            throws org.omg.PortableServer.CurrentPackage.NoContext {
        CurrentState state = (CurrentState) stateKey_.get(Thread
                .currentThread());

        if (state == null || state.poa == null)
            throw new org.omg.PortableServer.CurrentPackage.NoContext();

        return state.oid;
    }

    public org.omg.CORBA.Object get_reference()
            throws org.omg.PortableServer.CurrentPackage.NoContext {
        CurrentState state = (CurrentState) stateKey_.get(Thread
                .currentThread());

        if (state == null || state.poa == null)
            throw new org.omg.PortableServer.CurrentPackage.NoContext();
        try {
            return state.poa.servant_to_reference(state.servant);
        } catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.PortableServer.POAPackage.ServantNotActive ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        return null;
    }

    public org.omg.PortableServer.Servant get_servant()
            throws org.omg.PortableServer.CurrentPackage.NoContext {
        CurrentState state = (CurrentState) stateKey_.get(Thread
                .currentThread());

        if (state == null || state.poa == null)
            throw new org.omg.PortableServer.CurrentPackage.NoContext();
        return state.servant;
    }

    public void _OB_preinvoke(
            org.apache.yoko.orb.OBPortableServer.POA_impl poa,
            org.omg.PortableServer.Servant servant, String op, byte[] oid,
            java.lang.Object cookie) {
        Thread t = Thread.currentThread();

        CurrentState state = (CurrentState) stateKey_.get(t);

        CurrentState ns = new CurrentState();
        ns.poa = poa;

        //
        // These should be valid for the duration of the invocation
        //
        ns.oid = oid;
        ns.cookie = cookie;
        ns.servant = servant;
        ns.op = op;

        ns.next = state;
        state = ns;

        stateKey_.put(t, state);
    }

    public void _OB_postinvoke() {
        Thread t = Thread.currentThread();

        CurrentState state = (CurrentState) stateKey_.get(t);

        CurrentState save = state;
        state = state.next;

        //
        // Note: Hashtable cannot hold null keys or values
        //
        if (state == null)
            stateKey_.remove(t);
        else
            stateKey_.put(t, state);
    }

    public boolean _OB_inUpcall() {
        CurrentState state = (CurrentState) stateKey_.get(Thread
                .currentThread());

        return state != null;
    }

    public org.omg.PortableServer.Servant _OB_getServant() {
        CurrentState state = (CurrentState) stateKey_.get(Thread
                .currentThread());

        org.apache.yoko.orb.OB.Assert._OB_assert(state != null);

        return state.servant;
    }

    public java.lang.Object _OB_getCookie() {
        CurrentState state = (CurrentState) stateKey_.get(Thread
                .currentThread());

        org.apache.yoko.orb.OB.Assert._OB_assert(state != null);

        return state.cookie;
    }

    public byte[] _OB_getObjectId() {
        CurrentState state = (CurrentState) stateKey_.get(Thread
                .currentThread());

        org.apache.yoko.orb.OB.Assert._OB_assert(state != null);

        return state.oid;
    }

    public String _OB_getOp() {
        CurrentState state = (CurrentState) stateKey_.get(Thread
                .currentThread());

        org.apache.yoko.orb.OB.Assert._OB_assert(state != null);

        return state.op;
    }
}
