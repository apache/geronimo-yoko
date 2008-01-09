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

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.OCI.Buffer;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Repository;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.IOP.ServiceContext;
import org.omg.SendingContext.CodeBase;

public class CodeBaseProxy extends LocalObject implements CodeBase {

    final ORBInstance orbInstance_;
    ServiceContext ctx;
    CodeBase codebase;

    CodeBaseProxy(ORBInstance orb, ServiceContext ctx) {

        if (ctx.context_id != org.omg.IOP.SendingContextRunTime.value) {
            throw new org.omg.CORBA.BAD_PARAM(org.apache.yoko.orb.OB.MinorCodes
                    .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidContextID),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidContextID,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        this.orbInstance_ = orb;
        this.ctx = ctx;
    }

    /* (non-Javadoc)
     * @see org.omg.SendingContext.CodeBaseOperations#implementation(java.lang.String)
     */
    public String implementation(String arg0) {
        return getCodeBase(orbInstance_).implementation(arg0);
    }

    /* (non-Javadoc)
     * @see org.omg.SendingContext.CodeBaseOperations#implementations(java.lang.String[])
     */
    public String[] implementations(String[] arg0) {
        return getCodeBase(orbInstance_).implementations(arg0);
    }

    /* (non-Javadoc)
     * @see org.omg.SendingContext.CodeBaseOperations#bases(java.lang.String)
     */
    public String[] bases(String arg0) {
        return getCodeBase(orbInstance_).bases(arg0);
    }

    /* (non-Javadoc)
     * @see org.omg.SendingContext.CodeBaseOperations#get_ir()
     */
    public Repository get_ir() {
        return getCodeBase(orbInstance_).get_ir();
    }

    /* (non-Javadoc)
     * @see org.omg.SendingContext.CodeBaseOperations#meta(java.lang.String)
     */
    public FullValueDescription meta(String arg0) {
        return getCodeBase(orbInstance_).meta(arg0);
    }

    /* (non-Javadoc)
     * @see org.omg.SendingContext.CodeBaseOperations#metas(java.lang.String)
     */
    public FullValueDescription[] metas(String arg0) {
        return getCodeBase(orbInstance_).metas(arg0);
    }
    
    public CodeBase getCodeBase() {
    	return getCodeBase(orbInstance_);
    }

    
    private CodeBase getCodeBase(ORBInstance orb) {
        
        if (codebase == null || getorb(codebase) != orb.getORB()) {
        		
            byte[] coct = ctx.context_data;
            Buffer buf = new Buffer(coct, coct.length);
            InputStream in = new InputStream(buf);
            in._OB_ORBInstance(orb);
            in._OB_readEndian();
            org.omg.CORBA.Object obj = in.read_Object();
            try {
                codebase = org.omg.SendingContext.CodeBaseHelper.narrow(obj);
            } catch (BAD_PARAM ex) {
                codebase = null;
            }

            ctx = null;
        }

        // TODO: add minor code //
        
        
        return codebase;
    }

    /**
     * @param codebase
     * @return
     */
    private org.omg.CORBA.ORB getorb(org.omg.CORBA.Object codebase) {
        if (codebase instanceof ObjectImpl) {
            return ((ObjectImpl)codebase)._orb();   
        } else {
            return null;   
        }
    }

}
