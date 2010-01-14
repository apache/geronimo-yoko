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
// Strategy for RETAIN and USE_SERVANT_MANAGER or USE_DEFAULT_SERVANT
//
class RetainStrategy extends ActiveObjectOnlyStrategy {
    private ServantActivatorStrategy servantManager_;

    private DefaultServantHolder defaultServant_;

    private void etherealize(org.apache.yoko.orb.OB.ObjectIdHasher oid,
            org.omg.PortableServer.POA poa,
            org.omg.PortableServer.Servant servant, boolean cleanup) {
        //
        // If we have an ActivationStrategy then we have to
        // etherealize the object
        //
        if (servantManager_ != null) {
            boolean remaining = false;

            //
            // Synchronize on the servant activator now to calculate the
            // remaing activation flag. If we don't synchronize here
            // another thread might deactivate another object associated
            // to the same servant and we might compute an incosistent
            // value for the remaining activatation flag.
            //
            synchronized (servantManager_) {
                if (servantIdTable_ == null) {
                    //
                    // TODO: optimize! If servant is still in the
                    // active object map, then we still have remaining
                    // activations.
                    //
                    java.util.Enumeration keys = activeObjectTable_.keys();
                    while (keys.hasMoreElements()) {
                        TableEntry entry;
                        synchronized (activeObjectTable_) {
                            entry = (TableEntry) activeObjectTable_.get(keys
                                    .nextElement());
                        }

                        if (entry == null)
                            continue;

                        synchronized (entry) {
                            if (entry.state() != TableEntry.DEACTIVATED
                                    && entry.state() != TableEntry.ACTIVATE_PENDING) {
                                if (entry.getServant() == servant) {
                                    remaining = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            servantManager_.etherealize(oid.getObjectId(), poa, servant,
                    cleanup, remaining);
        }
    }

    protected void cleanupEntry(org.apache.yoko.orb.OB.ObjectIdHasher oid,
            TableEntry entry) {
        //
        // Cleanup the active object map and mark the
        // entry as deactivated.
        //
        synchronized (activeObjectTable_) {
            activeObjectTable_.remove(oid);
        }

        synchronized (entry) {
            entry.setDeactivated();
        }
    }

    protected void completeDeactivate(org.omg.PortableServer.POA poa,
            org.apache.yoko.orb.OB.ObjectIdHasher oid, TableEntry entry) {
        org.omg.PortableServer.Servant servant = entry.getServant();

        super.completeDeactivate(poa, oid, entry);

        etherealize(oid, poa, servant, false);
    }

    RetainStrategy(org.apache.yoko.orb.OBPortableServer.POAPolicies policies,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            ServantActivatorStrategy servantManager,
            DefaultServantHolder defaultServant) {
        super(policies, orbInstance);
        servantManager_ = servantManager;
        defaultServant_ = defaultServant;
    }

    public void destroy(org.omg.PortableServer.POA poa, boolean e) {
        if (servantManager_ != null && e) {
            java.util.Enumeration keys = activeObjectTable_.keys();
            while (keys.hasMoreElements()) {
                org.omg.PortableServer.Servant servant = null;
                TableEntry entry = null;
                org.apache.yoko.orb.OB.ObjectIdHasher key = (org.apache.yoko.orb.OB.ObjectIdHasher) keys
                        .nextElement();

                while (true) {
                    synchronized (activeObjectTable_) {
                        entry = (TableEntry) activeObjectTable_.get(key);
                    }

                    if (entry == null)
                        break;

                    synchronized (entry) {
                        switch (entry.state()) {
                        case TableEntry.ACTIVE:
                            entry.setDeactivatePending();
                            servant = entry.getServant();

                            if (servant != null) {
                                super.completeDeactivate(poa, key, entry);

                                //
                                // Etherealize the servant
                                //
                                etherealize(key, poa, servant, true);

                                //
                                // Remove the entry from the active object map
                                //
                                synchronized (activeObjectTable_) {
                                    activeObjectTable_.remove(key);
                                }
                            }
                            break;

                        case TableEntry.DEACTIVATE_PENDING:
                        case TableEntry.ACTIVATE_PENDING:
                            entry.waitForStateChange();
                            continue;

                        case TableEntry.DEACTIVATED:
                            // Nothing to do
                            break;
                        }
                    }

                    break;
                }
            }
        }

        super.destroy(poa, e);

        if (servantManager_ != null)
            servantManager_.destroy();
        if (defaultServant_ != null)
            defaultServant_.destroy();
    }

    public void etherealize(org.omg.PortableServer.POA poa) {
        destroy(poa, true);
    }

    public byte[] servantToId(org.omg.PortableServer.Servant servant,
            org.apache.yoko.orb.PortableServer.Current_impl poaCurrent) {
        byte[] oid = super.servantToId(servant, poaCurrent);
        if (oid == null && defaultServant_ != null)
            return defaultServant_.servantToId(servant, poaCurrent);
        return oid;
    }

    public org.omg.PortableServer.Servant idToServant(byte[] oid,
            boolean useDefaultServant) {
        org.omg.PortableServer.Servant servant = super.idToServant(oid,
                useDefaultServant);
        if (servant == null && useDefaultServant && defaultServant_ != null)
            servant = defaultServant_.getDefaultServant();
        return servant;
    }

    public org.omg.PortableServer.Servant locate(byte[] rawoid,
            org.omg.PortableServer.POA poa, String op,
            org.omg.PortableServer.ServantLocatorPackage.CookieHolder cookie)
            throws org.apache.yoko.orb.OB.LocationForward {
        org.apache.yoko.orb.OB.ObjectIdHasher oid = new org.apache.yoko.orb.OB.ObjectIdHasher(
                rawoid);
        while (true) {
            boolean incarnate = false;
            TableEntry entry;

            synchronized (activeObjectTable_) {
                entry = (TableEntry) activeObjectTable_.get(oid);
                if (entry == null) {
                    if (defaultServant_ != null) {
                        org.omg.PortableServer.Servant servant = defaultServant_
                                .getDefaultServant();

                        if (servant == null) {
                            throw new org.omg.CORBA.OBJ_ADAPTER(
                                    org.apache.yoko.orb.OB.MinorCodes
                                            .describeObjAdapter(org.apache.yoko.orb.OB.MinorCodes.MinorNoDefaultServant),
                                    org.apache.yoko.orb.OB.MinorCodes.MinorNoDefaultServant,
                                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
                        }
                        return servant;
                    }

                    //
                    // Insert the servant entry in the active object table
                    // with the provided id.
                    //
                    entry = new TableEntry();
                    activeObjectTable_.put(oid, entry);
                    incarnate = true;
                }
            }

            org.omg.PortableServer.Servant servant = null;
            if (incarnate) {
                try {
                    servant = servantManager_.incarnate(oid.getObjectId(), poa);

                    //
                    // 11-25:
                    //
                    // "If the incarnate operation returns a servant
                    // that is already active for a different Object
                    // Id and if the POA also has the UNIQUE_ID
                    // policy, the incarnate has violated the POA
                    // policy and is considered to be in error. The
                    // POA will raise an OBJ_ADAPTER system exception
                    // for the request."
                    //
                    // Note: The activator should be allowed to
                    // explicitly activate the servant for the given
                    // ObjectId.
                    //
                    if (servantIdTable_ != null && // TODO: initialize anyway?
                            servantIdTable_.containsKey(servant)) {
                        byte[] oid2 = (byte[]) servantIdTable_.get(servant);
                        if (!org.apache.yoko.orb.OB.ObjectIdHasher.comp(rawoid,
                                oid2))
                            throw new org.omg.CORBA.OBJ_ADAPTER(
                                    "ServantActivator returned a servant that "
                                            + "is already active for a different object ID");
                    }
                } catch (org.apache.yoko.orb.OB.LocationForward l) {
                    cleanupEntry(oid, entry);
                    throw l;
                } catch (org.omg.CORBA.SystemException e) {
                    cleanupEntry(oid, entry);
                    throw e;
                }
            }

            synchronized (entry) {
                switch (entry.state()) {
                case TableEntry.ACTIVATE_PENDING:
                    if (incarnate) {
                        completeActivation(oid, servant, entry);
                        return servant;
                    } else {
                        entry.waitForStateChange();
                        continue;
                    }

                case TableEntry.DEACTIVATE_PENDING: {
                    entry.waitForStateChange();
                    continue;
                }

                case TableEntry.ACTIVE: {
                    //
                    // The incarnate() call might have explicitely
                    // register the servant, check that the registered
                    // servant is the same as the incarnated servant.
                    //
                    org.omg.PortableServer.Servant s = entry.getServant();
                    if (incarnate) {
                        if (s != servant) {
                            throw new org.omg.CORBA.OBJ_ADAPTER(
                                    "ServantActivator returned a servant that "
                                            + "does not match the active object map");
                        }
                        return servant;
                    }
                    return s;
                }

                case TableEntry.DEACTIVATED:
                    break;
                }
            }

        }
    }

    public ServantManagerStrategy getServantManagerStrategy() {
        return servantManager_;
    }

    public DefaultServantHolder getDefaultServantHolder() {
        return defaultServant_;
    }
}
