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

public class StubImpl implements javax.rmi.CORBA.StubDelegate {
    public StubImpl() {
    }

    public int hashCode(javax.rmi.CORBA.Stub stub) {
        return stub._get_delegate().hashCode(stub);
    }

    public boolean equals(javax.rmi.CORBA.Stub stub, java.lang.Object obj) {
        if (obj instanceof org.omg.CORBA.Object) {
            return stub._is_equivalent((org.omg.CORBA.Object) obj);
        } else {
            return false;
        }
    }

    public java.lang.String toString(javax.rmi.CORBA.Stub stub) {
        return stub._get_delegate().toString(stub);
    }

    public void connect(javax.rmi.CORBA.Stub stub, org.omg.CORBA.ORB orb)
            throws java.rmi.RemoteException {
        try {
            org.omg.CORBA.portable.Delegate delegate;

            try {
                delegate = stub._get_delegate();
            } catch (org.omg.CORBA.BAD_OPERATION ex) {
                throw new java.rmi.RemoteException("stub has no identity", ex);
            }

            if (delegate.orb(stub) != orb) {
                org.omg.CORBA.portable.OutputStream out = orb
                        .create_output_stream();

                out.write_Object(stub);

                org.omg.CORBA.portable.InputStream in = out
                        .create_input_stream();

                org.omg.CORBA.portable.ObjectImpl impl = (org.omg.CORBA.portable.ObjectImpl) in
                        .read_Object();

                stub._set_delegate(impl._get_delegate());
            }

        } catch (org.omg.CORBA.SystemException ex) {
            throw javax.rmi.CORBA.Util.mapSystemException(ex);
        }
    }

    public void readObject(javax.rmi.CORBA.Stub stub,
            java.io.ObjectInputStream ois) throws java.io.IOException,
            java.lang.ClassNotFoundException {
        org.omg.CORBA.portable.InputStream in = null;

        if (ois instanceof CorbaObjectReader) {
            in = ((CorbaObjectReader) ois).in;
        } else {
            IOR ior = new IOR();
            ior.read(ois);

            org.omg.CORBA.portable.OutputStream out = RMIState.current()
                    .getORB().create_output_stream();

            ior.write(out);

            in = out.create_input_stream();
        }

        org.omg.CORBA.portable.ObjectImpl impl = (org.omg.CORBA.portable.ObjectImpl) in
                .read_Object();

        stub._set_delegate(impl._get_delegate());
    }

    public void writeObject(javax.rmi.CORBA.Stub stub,
            java.io.ObjectOutputStream oos) throws java.io.IOException {
        if (oos instanceof CorbaObjectWriter) {
            ((CorbaObjectWriter) oos).out.write_Object(stub);
        } else {
            org.omg.CORBA.portable.OutputStream out = RMIState.current()
                    .getORB().create_output_stream();

            out.write_Object(stub);

            org.omg.CORBA.portable.InputStream in = out.create_input_stream();

            IOR ior = new IOR();
            ior.read(in);
            ior.write(oos);
        }
    }

}
