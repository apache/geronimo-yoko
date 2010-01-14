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

package org.apache.yoko.orb.OB;

public final class PolicyFactoryManager {
    private boolean destroy_; // True if destroy() was called

    //
    // These are PolicyFactory objects that are registered from the PIManager
    //
    java.util.Hashtable policyFactoryTableExternal_ = new java.util.Hashtable(
            63);

    //
    // These are PolicyFactory objects that are registered internally
    // (for ORB policies, etc)
    //
    java.util.Hashtable policyFactoryTableInternal_ = new java.util.Hashtable(
            63);

    // ----------------------------------------------------------------------
    // PolicyFactoryManager private and protected member implementations
    // ----------------------------------------------------------------------

    void destroy() {
        Assert._OB_assert(!destroy_);
        destroy_ = true;

        policyFactoryTableInternal_.clear();
        policyFactoryTableExternal_.clear();
    }

    // ----------------------------------------------------------------------
    // PolicyFactoryManager public member implementations
    // ----------------------------------------------------------------------

    public void registerPolicyFactory(int type,
            org.omg.PortableInterceptor.PolicyFactory factory, boolean internal) {
        //
        // TODO: some sensible error
        //
        java.util.Hashtable table = (internal) ? policyFactoryTableInternal_
                : policyFactoryTableExternal_;

        Integer itype = new Integer(type);
        if (table.containsKey(itype))
            throw new org.omg.CORBA.BAD_PARAM();
        table.put(itype, factory);
    }

    public org.omg.CORBA.Policy createPolicy(int type, org.omg.CORBA.Any any)
            throws org.omg.CORBA.PolicyError {
        java.lang.Object factory;
        Integer itype = new Integer(type);

        if ((factory = policyFactoryTableInternal_.get(itype)) != null)
            return ((org.omg.PortableInterceptor.PolicyFactory) factory)
                    .create_policy(type, any);
        if ((factory = policyFactoryTableExternal_.get(itype)) != null)
            return ((org.omg.PortableInterceptor.PolicyFactory) factory)
                    .create_policy(type, any);

        throw new org.omg.CORBA.PolicyError(org.omg.CORBA.BAD_POLICY.value);
    }

    public void filterPolicyList(org.omg.CORBA.PolicyListHolder in,
            org.omg.CORBA.PolicyListHolder out) {
        java.util.Vector inVec = new java.util.Vector();
        for (int i = 0; i < in.value.length; i++)
            inVec.addElement(in.value[i]);

        java.util.Vector outVec = new java.util.Vector();
        for (int i = 0; i < out.value.length; i++)
            outVec.addElement(out.value[i]);

        for (int policy = 0; policy < inVec.size(); policy++) {
            org.omg.CORBA.Policy p = (org.omg.CORBA.Policy) inVec
                    .elementAt(policy);
            int type = p.policy_type();

            java.util.Enumeration e = policyFactoryTableExternal_.keys();
            while (e.hasMoreElements()) {
                Integer key = (Integer) e.nextElement();
                if (key.intValue() == type) {
                    outVec.addElement(p);
                    inVec.removeElementAt(policy);
                    --policy;
                    break;
                }
            }
        }

        if (inVec.size() != in.value.length) {
            in.value = new org.omg.CORBA.Policy[inVec.size()];
            inVec.copyInto(in.value);
        }

        if (outVec.size() != out.value.length) {
            out.value = new org.omg.CORBA.Policy[outVec.size()];
            outVec.copyInto(out.value);
        }
    }

    /**
     * Test if a policy type is valid for the current
     * context.
     *
     * @param type   The policy type number.
     *
     * @return true if the policy is a registered type, false for
     *         unknown types.
     */
    public boolean isPolicyRegistered(int type) {
        Integer itype = new Integer(type);

        return policyFactoryTableInternal_.containsKey(itype) || policyFactoryTableExternal_.containsKey(itype);
    }
}
