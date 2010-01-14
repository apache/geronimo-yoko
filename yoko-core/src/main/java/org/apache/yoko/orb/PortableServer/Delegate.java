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

// This class must be public
public final class Delegate implements org.omg.PortableServer.portable.Delegate {
    private org.omg.CORBA.ORB orb_;

    private org.omg.CORBA.Object resolve(String name) {
        try {
            return orb_.resolve_initial_references(name);
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            throw new org.omg.CORBA.INITIALIZE("Initial reference not found: "
                    + name);
        }
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public org.omg.CORBA.ORB orb(org.omg.PortableServer.Servant self) {
        return orb_;
    }

    public org.omg.CORBA.Object this_object(org.omg.PortableServer.Servant self) {
        //
        // Similar to PortableServant::ServantBase::_OB_createReference()
        //

        org.omg.CORBA.Object obj = null;

        org.omg.PortableServer.POA thePOA = self._default_POA();
        org.apache.yoko.orb.OBPortableServer.POA_impl poaImpl = (org.apache.yoko.orb.OBPortableServer.POA_impl) thePOA;
        Current_impl current = poaImpl._OB_POACurrent();

        // now that we have the current context, we need to use the current POA 
        // for validating.  

        try {
            poaImpl = (org.apache.yoko.orb.OBPortableServer.POA_impl)current.get_POA(); 
        } catch (org.omg.PortableServer.CurrentPackage.NoContext ex) {
            // we ignore the exception, and use the rootPOA for the activation, if possible.
        }

        //
        // If we're in an upcall to this servant, then obtain the object
        // reference for the target CORBA Object it is incarnating for
        // this request.
        //
        if (current._OB_inUpcall() && current._OB_getServant() == self) {
            try {
                byte[] oid = current.get_object_id();
                String[] all = self._all_interfaces(thePOA, oid);
                obj = poaImpl.create_reference_with_id(oid, all[0]);
            } catch (org.omg.PortableServer.CurrentPackage.NoContext ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            }
        } else {
            try {
                obj = thePOA.servant_to_reference(self);
            } catch (org.omg.PortableServer.POAPackage.ServantNotActive ex) {
                throw new org.omg.CORBA.OBJ_ADAPTER("Servant is not active");
            } catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
                throw new org.omg.CORBA.OBJ_ADAPTER("Wrong policy");
            }
        }

        return obj;
    }

    public org.omg.PortableServer.POA poa(org.omg.PortableServer.Servant self) {
        org.omg.PortableServer.Current current = org.omg.PortableServer.CurrentHelper
                .narrow(resolve("POACurrent"));
        try {
            return current.get_POA();
        } catch (org.omg.PortableServer.CurrentPackage.NoContext ex) {
            throw new org.omg.CORBA.OBJ_ADAPTER("No current context");
        }
    }

    public byte[] object_id(org.omg.PortableServer.Servant self) {
        org.omg.PortableServer.Current current = org.omg.PortableServer.CurrentHelper
                .narrow(resolve("POACurrent"));
        try {
            return current.get_object_id();
        } catch (org.omg.PortableServer.CurrentPackage.NoContext ex) {
            throw new org.omg.CORBA.OBJ_ADAPTER("No current context");
        }
    }

    public org.omg.PortableServer.POA default_POA(
            org.omg.PortableServer.Servant self) {
        return org.omg.PortableServer.POAHelper.narrow(resolve("RootPOA"));
    }

    public boolean is_a(org.omg.PortableServer.Servant self,
            String repository_id) {
        if (repository_id.equals("IDL:omg.org/CORBA/Object:1.0"))
            return true;
        else {
            org.omg.PortableServer.POA poa = poa(self);
            byte[] objectId = object_id(self);

            String[] ids = self._all_interfaces(poa, objectId);
            for (int i = 0; i < ids.length; i++)
                if (repository_id.equals(ids[i]))
                    return true;
        }

        return false;
    }

    public boolean non_existent(org.omg.PortableServer.Servant self) {
        return false;
    }

    public org.omg.CORBA.InterfaceDef get_interface(
            org.omg.PortableServer.Servant self) {
        org.omg.PortableServer.POA thePOA = self._default_POA();
        org.apache.yoko.orb.OBPortableServer.POA_impl poaImpl = (org.apache.yoko.orb.OBPortableServer.POA_impl) thePOA;

        org.omg.CORBA.Object obj = null;

        try {
            org.apache.yoko.orb.OB.ORBInstance orbInstance = poaImpl
                    ._OB_ORBInstance();

            org.apache.yoko.orb.OB.InitialServiceManager initialServiceManager = orbInstance
                    .getInitialServiceManager();

            obj = initialServiceManager
                    .resolveInitialReferences("InterfaceRepository");
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            throw new org.omg.CORBA.INTF_REPOS(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeIntfRepos(org.apache.yoko.orb.OB.MinorCodes.MinorNoIntfRepos),
                    org.apache.yoko.orb.OB.MinorCodes.MinorNoIntfRepos,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        if (obj == null)
            throw new org.omg.CORBA.INTF_REPOS(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeIntfRepos(org.apache.yoko.orb.OB.MinorCodes.MinorNoIntfRepos),
                    org.apache.yoko.orb.OB.MinorCodes.MinorNoIntfRepos,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        org.omg.CORBA.Repository repository = null;

        try {
            repository = org.omg.CORBA.RepositoryHelper.narrow(obj);
        } catch (org.omg.CORBA.BAD_PARAM ex) // narrow failed
        {
            throw new org.omg.CORBA.INTF_REPOS(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeIntfRepos(org.apache.yoko.orb.OB.MinorCodes.MinorNoIntfRepos),
                    org.apache.yoko.orb.OB.MinorCodes.MinorNoIntfRepos,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        byte[] objectId = object_id(self);
        String[] ids = self._all_interfaces(thePOA, objectId);
        org.omg.CORBA.Contained contained = repository.lookup_id(ids[0]);

        if (contained == null)
            return null;

        org.omg.CORBA.InterfaceDef result = null;
        try {
            result = org.omg.CORBA.InterfaceDefHelper.narrow(contained);
        } catch (org.omg.CORBA.BAD_PARAM ex) // narrow failed
        {
            // ignore
        }

        return result;
    }

    public org.omg.CORBA.Object get_interface_def(
            org.omg.PortableServer.Servant self) {
        return get_interface(self);
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public Delegate(org.omg.CORBA.ORB orb) {
        orb_ = orb;
    }

    public org.omg.CORBA.portable.InputStream _OB_preUnmarshal(
            org.omg.PortableServer.Servant self,
            org.apache.yoko.orb.OB.Upcall up)
            throws org.apache.yoko.orb.OB.LocationForward {
        return up.preUnmarshal();
    }

    public void _OB_unmarshalEx(org.omg.PortableServer.Servant self,
            org.apache.yoko.orb.OB.Upcall up, org.omg.CORBA.SystemException ex)
            throws org.apache.yoko.orb.OB.LocationForward {
        up.unmarshalEx(ex);
    }

    public void _OB_postUnmarshal(org.omg.PortableServer.Servant self,
            org.apache.yoko.orb.OB.Upcall up)
            throws org.apache.yoko.orb.OB.LocationForward {
        up.postUnmarshal();
    }

    public void _OB_postinvoke(org.omg.PortableServer.Servant self,
            org.apache.yoko.orb.OB.Upcall up)
            throws org.apache.yoko.orb.OB.LocationForward {
        up.postinvoke();
    }

    public org.omg.CORBA.portable.OutputStream _OB_preMarshal(
            org.omg.PortableServer.Servant self,
            org.apache.yoko.orb.OB.Upcall up)
            throws org.apache.yoko.orb.OB.LocationForward {
        return up.preMarshal();
    }

    public void _OB_marshalEx(org.omg.PortableServer.Servant self,
            org.apache.yoko.orb.OB.Upcall up, org.omg.CORBA.SystemException ex)
            throws org.apache.yoko.orb.OB.LocationForward {
        up.unmarshalEx(ex);
    }

    public void _OB_postMarshal(org.omg.PortableServer.Servant self,
            org.apache.yoko.orb.OB.Upcall up)
            throws org.apache.yoko.orb.OB.LocationForward {
        up.postMarshal();
    }

    public void _OB_setArgDesc(org.omg.PortableServer.Servant self,
            org.apache.yoko.orb.OB.Upcall up,
            org.apache.yoko.orb.OB.ParameterDesc[] argDesc,
            org.apache.yoko.orb.OB.ParameterDesc retDesc,
            org.omg.CORBA.TypeCode[] exceptionTC) {
        if (up instanceof org.apache.yoko.orb.OB.PIUpcall) {
            org.apache.yoko.orb.OB.PIUpcall piup = (org.apache.yoko.orb.OB.PIUpcall) up;
            piup.setArgDesc(argDesc, retDesc, exceptionTC);
        }
    }

    public org.omg.CORBA.portable.OutputStream _OB_beginUserException(
            org.omg.PortableServer.Servant self,
            org.apache.yoko.orb.OB.Upcall up, org.omg.CORBA.UserException ex) {
        return up.beginUserException(ex);
    }
}
