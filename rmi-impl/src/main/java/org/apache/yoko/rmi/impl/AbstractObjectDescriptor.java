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

public class AbstractObjectDescriptor extends ValueDescriptor {
    protected AbstractObjectDescriptor(Class type, TypeRepository repository) {
        super(type, repository);
    }

    public String getRepositoryID() {
        if (_repid == null)
            _repid = "IDL:" + getJavaClass().getName().replace('.', '/')
                    + ":1.0";

        return _repid;
    }

    /** Read an instance of this value from a CDR stream */
    public Object read(org.omg.CORBA.portable.InputStream in) {
        org.omg.CORBA_2_3.portable.InputStream _in = (org.omg.CORBA_2_3.portable.InputStream) in;

        return _in.read_abstract_interface();
    }

    /** Write an instance of this value to a CDR stream */
    public void write(org.omg.CORBA.portable.OutputStream out, Object value) {
        org.omg.CORBA_2_3.portable.OutputStream _out = (org.omg.CORBA_2_3.portable.OutputStream) out;

        _out.write_abstract_interface(value);
    }

    /*
     * public java.io.Serializable writeReplace (java.io.Serializable val) {
     * return null; }
     * 
     * public void writeValue(org.omg.CORBA.portable.OutputStream out,
     * java.io.Serializable value) { // skip // }
     * 
     * public java.io.Serializable readValue (org.omg.CORBA.portable.InputStream
     * in, java.util.Map offsetMap) { return null; }
     */

    org.omg.CORBA.TypeCode getTypeCode() {
        if (_type_code == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            _type_code = orb.create_abstract_interface_tc(getRepositoryID(),
                    getJavaClass().getName());
        }

        return _type_code;
    }

    public long computeHashCode() {
        return 0L;
    }

    Object copyObject(Object value, CopyState state) {
        throw new IllegalStateException("not serializable " + value.getClass().getName());
    }

    void writeMarshalValue(java.io.PrintWriter pw, String outName,
            String paramName) {
        pw.print("javax.rmi.CORBA.Util.writeAbstractObject(");
        pw.print(outName);
        pw.print(',');
        pw.print(paramName);
        pw.print(')');
    }

    void writeUnmarshalValue(java.io.PrintWriter pw, String inName) {
        pw.print(inName);
        pw.print(".read_abstract_interface()");
    }

}
