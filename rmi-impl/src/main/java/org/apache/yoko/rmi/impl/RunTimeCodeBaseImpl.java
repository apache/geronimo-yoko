/**
*
* Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.apache.yoko.rmi.impl;

import org.omg.CORBA.ValueDefPackage.FullValueDescription;

class RunTimeCodeBaseImpl extends org.omg.SendingContext.CodeBasePOA {
    ValueHandlerImpl valueHandler;

    RunTimeCodeBaseImpl(ValueHandlerImpl handler) {
        valueHandler = handler;
    }

    public String implementation(String id) {
        return valueHandler.getImplementation(id);
    }

    public String[] implementations(String[] ids) {
        return valueHandler.getImplementations(ids);
    }

    public String[] bases(String id) {
        return valueHandler.getBases(id);
    }

    public org.omg.CORBA.Repository get_ir() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.ValueDefPackage.FullValueDescription meta(String id) {
        return valueHandler.meta(id);
    }

    public org.omg.CORBA.ValueDefPackage.FullValueDescription[] metas(String id) {
        String[] bases = bases(id);
        org.omg.CORBA.ValueDefPackage.FullValueDescription[] descriptors = new org.omg.CORBA.ValueDefPackage.FullValueDescription[bases.length];

        for (int i = bases.length - 1; i >= 0; i--) {
            descriptors[i] = meta(bases[i]);
        }

        return descriptors;
    }

    public String implementationx(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public FullValueDescription[] metas(String[] arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
