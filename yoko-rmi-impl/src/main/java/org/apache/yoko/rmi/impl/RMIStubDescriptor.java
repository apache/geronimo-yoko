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

public class RMIStubDescriptor extends ValueDescriptor {
    RMIStubDescriptor(Class type, TypeRepository repository) {
        super(type, repository);
    }

    String stub_repid = null;

    public String getRepositoryID() {
        if (stub_repid == null) {
            init_repid();
        }

        return stub_repid;
    }

    void init_repid() {
        Class type = getJavaClass();

        Class[] ifaces = type.getInterfaces();

        if (ifaces.length != 2 || ifaces[1] != org.apache.yoko.rmi.util.stub.Stub.class) {
            throw new RuntimeException("Unexpected RMIStub structure");
        }

        String ifname = ifaces[0].getName();
        String stubClassName = null;

        int idx = ifname.lastIndexOf('.');
        if (idx == -1) {
            stubClassName = "_" + ifname + "_Stub";
        } else {
            stubClassName = ifname.substring(0, idx + 1) + "_"
                    + ifname.substring(idx + 1) + "_Stub";
        }

        stub_repid = "RMI:" + stubClassName + ":0";
    }

    //
    // Override writeValue/readvalue, such that only the superclass'
    // state is written. This ensures that fields in the proxy are
    // not included on the wire.
    //
    protected void writeValue(ObjectWriter writer, java.io.Serializable val)
            throws java.io.IOException {
        _super_descriptor.writeValue(writer, val);
    }

    protected void readValue(ObjectReader reader, java.io.Serializable value)
            throws java.io.IOException {
        _super_descriptor.readValue(reader, value);
    }

}
