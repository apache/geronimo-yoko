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

package org.apache.yoko.orb.PortableInterceptor;

final public class IMRIORInterceptor_impl extends org.omg.CORBA.LocalObject
        implements org.omg.PortableInterceptor.IORInterceptor_3_0 {
    private java.util.Hashtable poas_;

    private org.apache.yoko.orb.OB.Logger logger_;

    private org.apache.yoko.orb.IMR.ActiveState as_;

    private String serverInstance_;

    private boolean running_;

    // ------------------------------------------------------------------
    // Private Member Functions
    // ------------------------------------------------------------------

    private org.apache.yoko.orb.IMR.POAStatus convertState(short state) {
        org.apache.yoko.orb.IMR.POAStatus status = org.apache.yoko.orb.IMR.POAStatus.NON_EXISTENT;
        switch (state) {
        case org.omg.PortableInterceptor.INACTIVE.value:
            status = org.apache.yoko.orb.IMR.POAStatus.INACTIVE;
            break;
        case org.omg.PortableInterceptor.ACTIVE.value:
            status = org.apache.yoko.orb.IMR.POAStatus.ACTIVE;
            break;
        case org.omg.PortableInterceptor.HOLDING.value:
            status = org.apache.yoko.orb.IMR.POAStatus.HOLDING;
            break;
        case org.omg.PortableInterceptor.DISCARDING.value:
            status = org.apache.yoko.orb.IMR.POAStatus.DISCARDING;
            break;
        }
        return status;
    }

    // ------------------------------------------------------------------
    // Public Member Functions
    // ------------------------------------------------------------------

    public IMRIORInterceptor_impl(org.apache.yoko.orb.OB.Logger logger,
            org.apache.yoko.orb.IMR.ActiveState as, String serverInstance) {
        logger_ = logger;
        as_ = as;
        serverInstance_ = serverInstance;
        running_ = false;

        poas_ = new java.util.Hashtable();
    }

    // ------------------------------------------------------------------
    // IDL to Java Mapping
    // ------------------------------------------------------------------

    public String name() {
        return new String("IMRInterceptor");
    }

    public void destroy() {
    }

    public void establish_components(org.omg.PortableInterceptor.IORInfo info) {
    }

    public void components_established(org.omg.PortableInterceptor.IORInfo info) {
        //
        // This method does nothing if this is not a persistent POA
        //
        try {
            org.omg.CORBA.Policy p = info
                    .get_effective_policy(org.omg.PortableServer.LIFESPAN_POLICY_ID.value);
            org.omg.PortableServer.LifespanPolicy policy = org.omg.PortableServer.LifespanPolicyHelper
                    .narrow(p);
            if (policy.value() != org.omg.PortableServer.LifespanPolicyValue.PERSISTENT)
                return;
        } catch (org.omg.CORBA.INV_POLICY e) {
            // 
            // Default Lifespan policy is TRANSIENT
            // 
            return;
        }

        //
        // Get the primary object-reference template
        //
        org.omg.PortableInterceptor.ObjectReferenceTemplate primary = info
                .adapter_template();

        try {
            short state = info.state();
            org.apache.yoko.orb.IMR.POAStatus status = convertState(state);

            org.omg.PortableInterceptor.ObjectReferenceTemplate secondary = as_
                    .poa_create(status, primary);

            info.current_factory(secondary);
        } catch (org.apache.yoko.orb.IMR._NoSuchPOA e) {
            String msg = "IMR: POA not registered: ";
            for (int i = 0; i < e.poa.length; i++) {
                msg += e.poa[i];
                if (i != e.poa.length - 1)
                    msg += "/";
            }
            logger_.error(msg, e);
            throw new org.omg.CORBA.INITIALIZE();
        } catch (org.omg.CORBA.SystemException ex) {
            String msg = "IMR: Cannot contact: " + ex.getMessage();
            logger_.error(msg, ex);
            throw new org.omg.CORBA.INITIALIZE(); // TODO: - some exception
        }

        //
        // Update the poa hash table
        //
        String id = info.manager_id();
        String[] name = primary.adapter_name();

        java.util.Vector poas = (java.util.Vector) poas_.get(new Integer(id));
        if (poas != null) {
            //
            // Add poa to exiting entry
            //
            poas.addElement(name);

            // XXX Do I have to reput
        } else {
            //
            // Add a new entry for this adapter manager
            //
            poas = new java.util.Vector();
            poas.addElement(name);
            poas_.put(new Integer(id), poas);
        }
    }

    public void adapter_state_changed(
            org.omg.PortableInterceptor.ObjectReferenceTemplate[] templates,
            short state) {
        //
        // Only update the IMR from this point if the POAs have
        // been destroyed.
        //
        if (state != org.omg.PortableInterceptor.NON_EXISTENT.value)
            return;

        java.util.Vector poanames = new java.util.Vector();
        for (int i = 0; i < templates.length; ++i) {
            try {
                org.apache.yoko.orb.OBPortableInterceptor.PersistentORT_impl persistentORT = (org.apache.yoko.orb.OBPortableInterceptor.PersistentORT_impl) templates[i];
            } catch (ClassCastException ex) {
                //
                // If not a Persistent ORT continue
                //
                continue;
            }

            String[] adpaterName = templates[i].adapter_name();

            //
            // Add the POA to the list of POAs to send to the
            // IMR for the status update
            //
            poanames.addElement(adpaterName);

            //
            // Find the POA in the POAManager -> POAs map and
            // remove it.
            //
            java.util.Enumeration e = poas_.elements();
            while (e.hasMoreElements()) {
                java.util.Vector poas = (java.util.Vector) e.nextElement();

                //
                // Find the poa being deleted
                //
                int j;
                for (j = 0; j < poas.size(); ++j) {
                    String[] current = (String[]) poas.elementAt(j);
                    if (current.length != adpaterName.length)
                        continue;

                    boolean found = true;
                    for (int k = 0; k < adpaterName.length; ++k) {
                        if (!current[k].equals(adpaterName[k])) {
                            found = false;
                            break;
                        }
                    }
                    if (found)
                        break;
                }

                //
                // Shift back the remaining poas if match found
                //
                if (j != poas.size()) {
                    poas.removeElementAt(j);
                    break;
                }
            }
        }

        if (poanames.size() != 0) {
            try {
                String[][] poaArray = new String[poanames.size()][];
                poanames.copyInto(poaArray);
                as_.poa_status_update(poaArray,
                        org.apache.yoko.orb.IMR.POAStatus.NON_EXISTENT);
            } catch (org.omg.CORBA.SystemException ex) {
                String msg = "IMR: poa_destroy: " + ex.getMessage();
                logger_.warning(msg, ex);
            }
        }
    }

    //
    // Update POA states for this adapter
    //
    public void adapter_manager_state_changed(String id, short state) {
        if (!running_) {
            //
            // Inform the IMR the server is now running if this
            // is the first call.
            //
            try {
                as_.set_status(serverInstance_,
                        org.apache.yoko.orb.IMR.ServerStatus.RUNNING);
            } catch (org.omg.CORBA.OBJECT_NOT_EXIST ex) {
                logger_.error("IMR: Not registered", ex);
                throw new org.omg.CORBA.INITIALIZE();
            } catch (org.omg.CORBA.SystemException ex) {
                logger_.error("IMR: Cannot contact", ex);
                throw new org.omg.CORBA.INITIALIZE();
            }

            running_ = true;
        }

        //
        // Inform the IMR of the POA status update
        //
        java.util.Vector poas = (java.util.Vector) poas_.get(new Integer(id));
        if (poas != null && poas.size() != 0) {
            try {
                String[][] poaArray = new String[poas.size()][];
                poas.copyInto(poaArray);
                org.apache.yoko.orb.IMR.POAStatus status = convertState(state);
                as_.poa_status_update(poaArray, status);
            } catch (org.omg.CORBA.SystemException ex) {
                //
                // XXX ????
                //
            }
        }
    }
}
