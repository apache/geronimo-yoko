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

import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;

abstract class SimpleDescriptor extends TypeDescriptor {
    private final String idl_name;
    private final TCKind tc;
    SimpleDescriptor(Class type, TypeRepository repository, String idl_name, TCKind tc) {
        super(type, repository);
        this.idl_name = idl_name;
        this.tc = tc;
    }

    @Override
    protected final String genIDLName() {
        return idl_name;
    }

    @Override
    protected final String genPackageName() {
        return "";
    }

    @Override
    protected final String genTypeName() {
        return idl_name;
    }

    @Override
    protected final TypeCode genTypeCode() {
        return ORB.init().get_primitive_tc(tc);
    }

    @Override
    boolean copyInStub() {
        return false;
    }

    @Override
    public boolean copyBetweenStates() {
        return false;
    }

    @Override
    public boolean copyWithinState() {
        return false;
    }
}
