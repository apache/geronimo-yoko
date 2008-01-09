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

public class DirectServant extends org.omg.CORBA.portable.ServantObject {
    //
    // The POA
    //
    private org.apache.yoko.orb.OBPortableServer.POA_impl poa_;

    // The object ID
    //
    private byte[] oid_;

    //
    // This flag is true if the servant has been deactivated
    //
    private boolean deactivated_;
    
	private Object original_servant;

    public DirectServant(org.apache.yoko.orb.OBPortableServer.POA_impl poa,
            byte[] oid, java.lang.Object servant) {
        poa_ = poa;
        oid_ = oid;
        deactivated_ = false;
        this.original_servant = servant;
        this.servant = servant;
        if (servant instanceof javax.rmi.CORBA.Tie) {
        	javax.rmi.CORBA.Tie tie = (javax.rmi.CORBA.Tie) servant;
        	this.servant = tie.getTarget();
        }
    }

    protected void finalize() throws Throwable {
        //
        // This object *must* have been deactivated already
        //
        org.apache.yoko.orb.OB.Assert._OB_assert(deactivated_);

        super.finalize();
    }

    public void destroy() {
        //
        // An explicit destroy method is needed in Java to force the
        // removal of this object from the POA's table. Otherwise,
        // this object will never be garbage collected.
        //
        if (!deactivated_) {
            poa_._OB_removeDirectServant(oid_, this);
            deactivated_ = true;
        }
    }

    public boolean deactivated() {
        return deactivated_;
    }

    public void deactivate() {
        deactivated_ = true;
    }

    public org.omg.CORBA.portable.ServantObject preinvoke(String op) {
        //
        // Validate POA manager state
        //
        poa_._OB_validateManagerState();

        //
        // Increment the POA's request count
        //
        if (!poa_._OB_incrementRequestCount())
            return null;

        //
        // Preinvoke
        //
        poa_._OB_preinvoke(op, oid_, (org.omg.PortableServer.Servant) original_servant,
                null, null);

        return this;
    }

    public void postinvoke() {
        //
        // Postinvoke
        //
        poa_._OB_postinvoke();

        //
        // Decrement the outstanding request count
        //
        poa_._OB_decrementRequestCount();
    }

    public boolean locate_request() {
        try {
            if (preinvoke("_locate") != null) {
                postinvoke();
                return true;
            }
        } catch (org.omg.CORBA.OBJECT_NOT_EXIST ex) {
            // Fall through
        }

        return false;
    }
}
