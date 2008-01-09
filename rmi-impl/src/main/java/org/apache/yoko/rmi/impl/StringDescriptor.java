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

public class StringDescriptor extends ValueDescriptor {
    public String getIDLName() {
        return "CORBA_WStringValue";
    }
    
    StringDescriptor(TypeRepository repository) {
        super(String.class, repository);
        // strings have a special type and package name other than the java class name. 
        setTypeName("WStringValue"); 
        setPackageName("CORBA"); 
    }

    public void init() {
        super.init();
    }

    /** Read an instance of this value from a CDR stream */
    public Object read(org.omg.CORBA.portable.InputStream in) {
        return org.omg.CORBA.WStringValueHelper.read(in);
    }

    /** Write an instance of this value to a CDR stream */
    public void write(org.omg.CORBA.portable.OutputStream out, Object value) {
        org.omg.CORBA.WStringValueHelper.write(out, (String) value);
    }

    public void writeValue(org.omg.CORBA.portable.OutputStream out,
            java.io.Serializable value) {
        throw new org.omg.CORBA.MARSHAL("internal error");
    }

    public java.io.Serializable readValue(
            org.omg.CORBA.portable.InputStream in, java.io.Serializable value,
            java.util.Map offsetMap) {
        throw new org.omg.CORBA.MARSHAL("internal error");
    }

    org.omg.CORBA.TypeCode getTypeCode() {
        return org.omg.CORBA.WStringValueHelper.type();
    }

    Object copyObject(Object value, CopyState state) {
        return value;
    }
}
