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

public abstract class SimpleDescriptor extends TypeDescriptor {
    SimpleDescriptor(Class type, TypeRepository repository, String idl_name,
            org.omg.CORBA.TCKind tc) {
        super(type, repository);
        setJavaName(type.getName());
        setIDLName(idl_name);
        // the simple type name is the same as the IDL name.  The 
        // package name is null 
        setTypeName(idl_name); 
        setPackageName("");

        org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
        _type_code = orb.get_primitive_tc(tc);
    }

    org.omg.CORBA.TypeCode getTypeCode() {
        return _type_code;
    }

    boolean copyInStub() {
        return false;
    }

    public boolean copyBetweenStates() {
        return false;
    }

    public boolean copyWithinState() {
        return false;
    }

}
