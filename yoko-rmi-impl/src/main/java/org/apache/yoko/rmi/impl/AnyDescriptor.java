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

import java.io.PrintWriter;

import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

class AnyDescriptor extends TypeDescriptor {
    AnyDescriptor(Class type, TypeRepository rep) {
        super(type, rep);
    }

    @Override
    protected String genRepId() {
        return String.format("IDL:%s:1.0", type.getName().replace('.', '/'));
    }

    /** Read an instance of this value from a CDR stream */
    @Override
    public Object read(InputStream in) {
        return javax.rmi.CORBA.Util.readAny(in);
    }

    /** Write an instance of this value to a CDR stream */
    @Override
    public void write(OutputStream out, Object val) {
        javax.rmi.CORBA.Util.writeAny(out, val);
    }

    @Override
    protected TypeCode genTypeCode() {
        ORB orb = ORB.init();
        return orb.get_primitive_tc(TCKind.tk_any);
    }

    @Override
    Object copyObject(Object value, CopyState state) {
        throw new InternalError("cannot copy org.omg.CORBA.Any");
    }

    @Override
    void writeMarshalValue(PrintWriter pw, String outName, String paramName) {
        pw.print("javax.rmi.CORBA.Util.writeAny(");
        pw.print(outName);
        pw.print(',');
        pw.print(paramName);
        pw.print(')');
    }

    @Override
    void writeUnmarshalValue(PrintWriter pw, String inName) {
        pw.print("javax.rmi.CORBA.Util.readAny(");
        pw.print(inName);
        pw.print(")");
    }
}
