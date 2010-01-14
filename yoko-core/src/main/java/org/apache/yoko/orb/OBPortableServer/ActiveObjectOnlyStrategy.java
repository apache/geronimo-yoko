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

package org.apache.yoko.orb.OBPortableServer;

//
// Mapping for ObjectId to a sequence of DirectStubImpl
//
class DirectSeqEntry {
    private java.util.Vector seq_;

    private byte[] oid_; // TODO: tmp

    private void traceoid() {
        org.apache.yoko.orb.OB.Util.printOctets(System.out, oid_, 0,
                oid_.length);
    }

    DirectSeqEntry(byte[] oid) {
        seq_ = new java.util.Vector();
        oid_ = oid;
    }

    protected void finalize() throws Throwable {
        deactivate();
        super.finalize();
    }

    void deactivate() {
        // traceoid();
        // System.out.println("deactivate: ");
        for (int i = 0; i < seq_.size(); i++)
            ((DirectServant) seq_.elementAt(i)).deactivate();
        seq_.removeAllElements();
    }

    void add(DirectServant directServant) {
        // traceoid();
        // System.out.println("add: " + directServant);

        seq_.addElement(directServant);
    }

    boolean remove(DirectServant directServant) {
        // traceoid();
        // System.out.println("remove: " + directServant);

        for (int i = 0; i < seq_.size(); i++) {
            if (seq_.elementAt(i) == directServant) {
                seq_.removeElementAt(i);
                return seq_.isEmpty();
            }
        }
        return false;
    }
}

//
// If USE_ACTIVE_OBJECT_MAP_ONLY this strategy is used
//
class ActiveObjectOnlyStrategy implements ServantLocationStrategy {
    //
    // The AOM
    //
    protected java.util.Hashtable activeObjectTable_;

    //
    // Reverse map from servant to id
    //
    protected java.util.Hashtable servantIdTable_;

    //
    // Mapping for ObjectId's to DirectStubImpl
    //
    private java.util.Hashtable directSeqTable_;

    //
    // The ORBInstance
    //
    private org.apache.yoko.orb.OB.ORBInstance orbInstance_;

    //
    // This method is synchronized on the TableEntry
    //
    protected void completeActivation(
            org.apache.yoko.orb.OB.ObjectIdHasher oid,
            org.omg.PortableServer.Servant servant, TableEntry entry) {
        //
        // If there is a DirectStubImpl that refers to a default servant
        // under this oid then deactivate each
        //
        synchronized (directSeqTable_) {
            DirectSeqEntry table;
            table = (DirectSeqEntry) directSeqTable_.get(oid);
            if (table != null) {
                table.deactivate();
                directSeqTable_.remove(oid);
            }
        }

        //
        // If using UNIQUE_ID add the servant to the servantIdTable
        //
        if (servantIdTable_ != null) {
            org.apache.yoko.orb.OB.Assert._OB_assert(!servantIdTable_
                    .containsKey(servant));
            servantIdTable_.put(servant, oid.getObjectId());
        }

        //
        // Set the servant's delegate
        //
        ((org.omg.CORBA_2_3.ORB) orbInstance_.getORB()).set_delegate(servant);

        //
        // Update the object entry
        //
        entry.setServant(servant);
        entry.setActive();
    }

    protected void completeDeactivate(org.omg.PortableServer.POA poa,
            org.apache.yoko.orb.OB.ObjectIdHasher oid, TableEntry entry) {
        //
        // Mark each DirectServant associated with this oid as
        // deactivated
        //
        synchronized (directSeqTable_) {
            DirectSeqEntry table = (DirectSeqEntry) directSeqTable_.get(oid);
            if (table != null) {
                table.deactivate();
                directSeqTable_.remove(oid);
            }
        }

        //
        // If we're using UNIQUE_ID then remove the servant from the
        // servantIdTable
        //
        if (servantIdTable_ != null) {
            org.omg.PortableServer.Servant servant = entry.getServant();
            org.apache.yoko.orb.OB.Assert._OB_assert(servantIdTable_
                    .containsKey(servant));
            servantIdTable_.remove(servant);
        }

        //
        // Release the servant reference before calling etherealize and
        // mark the entry as deactivated.
        //
        entry.setDeactivated();
        entry.clearServant();
    }

    protected DirectServant completeDirectStubImpl(
            org.omg.PortableServer.POA poa, byte[] rawoid,
            org.omg.PortableServer.Servant servant,
            org.apache.yoko.orb.OB.RefCountPolicyList policies) {
        DirectServant directServant;

        org.apache.yoko.orb.OB.ObjectIdHasher oid = new org.apache.yoko.orb.OB.ObjectIdHasher(
                rawoid);

        //
        // No direct invocations for DSI servants
        //
        if (servant instanceof org.omg.PortableServer.DynamicImplementation)
            return null;

        //
        // We must have direct invocations if the servant has native
        // types. Always use direct invocations, if possible, if there are
        // no interceptors installed.
        //
        // TODO: Check the POA interceptor policy
        //
        // We need this hack in Java because the servant class is
        // standardized, so we can't invoke _OB_haveNativeTypes().
        //
        boolean haveNativeTypes = (servant instanceof org.omg.PortableServer.ServantManagerOperations);
        org.apache.yoko.orb.OB.PIManager piManager = orbInstance_
                .getPIManager();
        if (!haveNativeTypes
                && (policies.locationTransparency == org.apache.yoko.orb.OB.LOCATION_TRANSPARENCY_STRICT.value
                        || piManager.haveClientInterceptors() || piManager
                        .haveServerInterceptors()))
            return null;

        //
        // Create a DirectServant
        //
        directServant = new DirectServant(
                (org.apache.yoko.orb.OBPortableServer.POA_impl) poa, oid
                        .getObjectId(), servant);

        //
        // Add the DirectServant to the table
        //
        synchronized (directSeqTable_) {
            DirectSeqEntry table = (DirectSeqEntry) directSeqTable_.get(oid);
            if (table == null) {
                table = new DirectSeqEntry(oid.getObjectId());
                directSeqTable_.put(oid, table);
            }
            table.add(directServant);
        }

        return directServant;
    }

    ActiveObjectOnlyStrategy(
            org.apache.yoko.orb.OBPortableServer.POAPolicies policies,
            org.apache.yoko.orb.OB.ORBInstance orbInstance) {
        activeObjectTable_ = new java.util.Hashtable(1023);
        directSeqTable_ = new java.util.Hashtable(1023);
        orbInstance_ = orbInstance;

        if (policies.idUniquenessPolicy() == org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID)
            servantIdTable_ = new java.util.Hashtable(1023);
        else
            servantIdTable_ = null;
    }

    public void destroy(org.omg.PortableServer.POA poa, boolean etherealize) {
        synchronized (activeObjectTable_) {
            activeObjectTable_.clear();
        }

        synchronized (directSeqTable_) {
            directSeqTable_.clear();
        }

        if (servantIdTable_ != null)
            servantIdTable_.clear();
    }

    public void etherealize(org.omg.PortableServer.POA poa) {
        // Do nothing
    }

    public void activate(byte[] rawoid, org.omg.PortableServer.Servant servant)
            throws org.omg.PortableServer.POAPackage.ServantAlreadyActive,
            org.omg.PortableServer.POAPackage.WrongPolicy,
            org.omg.PortableServer.POAPackage.ObjectAlreadyActive {
        org.apache.yoko.orb.OB.ObjectIdHasher oid = new org.apache.yoko.orb.OB.ObjectIdHasher(
                rawoid);

        while (true) {
            boolean incarnate = false;
            TableEntry entry;

            //
            // Find out whether a servant is already bound under this id
            // if not add an entry into the AOM
            //
            synchronized (activeObjectTable_) {
                entry = (TableEntry) activeObjectTable_.get(oid);
                if (entry == null) {
                    //
                    // If using UNIQUE_ID, then verify that the
                    // servant isn't already activated.
                    //
                    if (servantIdTable_ != null
                            && servantIdTable_.containsKey(servant)) {
                        throw new org.omg.PortableServer.POAPackage.ServantAlreadyActive();
                    }

                    //
                    // Insert the servant in the active object table
                    // with the provided id.
                    //
                    entry = new TableEntry();
                    activeObjectTable_.put(oid, entry);
                }
            }

            synchronized (entry) {
                switch (entry.state()) {
                case TableEntry.DEACTIVATE_PENDING:
                    entry.waitForStateChange();
                    continue;

                case TableEntry.ACTIVATE_PENDING:
                    incarnate = true;
                    break;

                case TableEntry.ACTIVE:
                    throw new org.omg.PortableServer.POAPackage.ObjectAlreadyActive();

                case TableEntry.DEACTIVATED:
                    break;
                }

                if (incarnate) {
                    completeActivation(oid, servant, entry);
                    return;
                }
            }
        }
    }

    public void deactivate(org.omg.PortableServer.POA poa, byte[] rawoid)
            throws org.omg.PortableServer.POAPackage.ObjectNotActive,
            org.omg.PortableServer.POAPackage.WrongPolicy {
        org.apache.yoko.orb.OB.ObjectIdHasher oid = new org.apache.yoko.orb.OB.ObjectIdHasher(
                rawoid);

        //
        // If no object in the active object table associated with
        // this key then raise an ObjectNotActive exception.
        //
        TableEntry entry;
        synchronized (activeObjectTable_) {
            entry = (TableEntry) activeObjectTable_.get(oid);
            if (entry == null)
                throw new org.omg.PortableServer.POAPackage.ObjectNotActive();
        }

        boolean deactivate = false;
        synchronized (entry) {
            switch (entry.state()) {
            case TableEntry.ACTIVE:
                entry.setDeactivatePending();
                deactivate = entry.getOutstandingRequests() == 0;
                break;

            case TableEntry.DEACTIVATE_PENDING:
                return;

            case TableEntry.ACTIVATE_PENDING:
            case TableEntry.DEACTIVATED:
                throw new org.omg.PortableServer.POAPackage.ObjectNotActive();
            }

            if (deactivate) {
                completeDeactivate(poa, oid, entry);

                //
                // Remove the entry from the active object map
                //
                synchronized (activeObjectTable_) {
                    activeObjectTable_.remove(oid);
                }
            }
        }
    }

    public byte[] servantToId(org.omg.PortableServer.Servant servant,
            org.apache.yoko.orb.PortableServer.Current_impl poaCurrent) {
        byte[] id = null;
        if (servantIdTable_ != null)
            id = (byte[]) servantIdTable_.get(servant);
        return id;
    }

    public org.omg.PortableServer.Servant idToServant(byte[] rawoid,
            boolean useDefaultServant) {
        org.apache.yoko.orb.OB.ObjectIdHasher oid = new org.apache.yoko.orb.OB.ObjectIdHasher(
                rawoid);
        while (true) {
            TableEntry entry;
            synchronized (activeObjectTable_) {
                entry = (TableEntry) activeObjectTable_.get(oid);
                if (entry == null)
                    return null;
            }

            synchronized (entry) {
                switch (entry.state()) {
                case TableEntry.DEACTIVATE_PENDING:
                case TableEntry.ACTIVATE_PENDING:
                    entry.waitForStateChange();
                    continue;

                case TableEntry.ACTIVE:
                    return entry.getServant();

                case TableEntry.DEACTIVATED:
                    return null;
                }
            }
        }
    }

    public org.omg.PortableServer.Servant locate(byte[] rawoid,
            org.omg.PortableServer.POA poa, String op,
            org.omg.PortableServer.ServantLocatorPackage.CookieHolder cookie)
            throws org.apache.yoko.orb.OB.LocationForward {
        org.omg.PortableServer.Servant servant = idToServant(rawoid, false);
        if (servant == null) {
            //
            // If the servant isn't in the table then this is an
            // OBJECT_NOT_EXIST exception
            //
            throw new org.omg.CORBA.OBJECT_NOT_EXIST(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeObjectNotExist(org.apache.yoko.orb.OB.MinorCodes.MinorCannotDispatch),
                    org.apache.yoko.orb.OB.MinorCodes.MinorCannotDispatch,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
        return servant;
    }

    public void preinvoke(byte[] rawoid) {
        org.apache.yoko.orb.OB.ObjectIdHasher oid = new org.apache.yoko.orb.OB.ObjectIdHasher(
                rawoid);

        TableEntry entry;
        synchronized (activeObjectTable_) {
            entry = (TableEntry) activeObjectTable_.get(oid);
            if (entry == null)
                return;
        }

        synchronized (entry) {
            entry.incOutstandingRequest();
        }
    }

    public void postinvoke(byte[] rawoid, org.omg.PortableServer.POA poa,
            String op, java.lang.Object cookie,
            org.omg.PortableServer.Servant servant) {
        org.apache.yoko.orb.OB.ObjectIdHasher oid = new org.apache.yoko.orb.OB.ObjectIdHasher(
                rawoid);

        TableEntry entry;
        synchronized (activeObjectTable_) {
            entry = (TableEntry) activeObjectTable_.get(oid);
            if (entry == null)
                return;
        }

        //
        // If the number of outstanding requests is now 0 and the
        // entry has been deactivated then complete the deactivation
        //
        boolean deactivate = false;
        synchronized (entry) {
            if (entry.decOutstandingRequest() == 0)
                deactivate = entry.state() == TableEntry.DEACTIVATE_PENDING;

            if (deactivate) {
                completeDeactivate(poa, oid, entry);

                //
                // Remove the entry for the active object map
                //
                synchronized (activeObjectTable_) {
                    activeObjectTable_.remove(oid);
                }
            }
        }
    }

    public DirectServant createDirectStubImpl(org.omg.PortableServer.POA poa,
            byte[] oid, org.apache.yoko.orb.OB.RefCountPolicyList policies)
            throws org.apache.yoko.orb.OB.LocationForward {
        try {
            org.omg.PortableServer.ServantLocatorPackage.CookieHolder cookie = null;
            org.omg.PortableServer.Servant servant = locate(oid, poa, "",
                    cookie);
            return completeDirectStubImpl(poa, oid, servant, policies);
        } catch (org.omg.CORBA.SystemException ex) {
        }
        return null;
    }

    public void removeDirectStubImpl(byte[] rawoid, DirectServant directStubImpl) {
        org.apache.yoko.orb.OB.ObjectIdHasher oid = new org.apache.yoko.orb.OB.ObjectIdHasher(
                rawoid);
        synchronized (directSeqTable_) {
            DirectSeqEntry table = (DirectSeqEntry) directSeqTable_.get(oid);
            if (table != null) {
                if (table.remove(directStubImpl))
                    directSeqTable_.remove(oid);
            }
        }
    }

    public ServantManagerStrategy getServantManagerStrategy() {
        return null;
    }

    public DefaultServantHolder getDefaultServantHolder() {
        return null;
    }
}
