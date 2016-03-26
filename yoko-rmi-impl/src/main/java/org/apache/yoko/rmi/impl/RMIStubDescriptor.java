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

import java.io.IOException;

class RMIStubDescriptor extends ValueDescriptor {
    RMIStubDescriptor(Class type, TypeRepository repository) {
        super(type, repository);
    }

    @Override
    protected String genRepId() {
        final Class[] ifaces = type.getInterfaces();
        if (ifaces.length != 2 || ifaces[1] != org.apache.yoko.rmi.util.stub.Stub.class) {
            throw new RuntimeException("Unexpected RMIStub structure");
        }

        final String ifname = ifaces[0].getName();
        final int idx = ifname.lastIndexOf('.');
        return ((idx < 0) ? String.format("RMI:_%s_Stub:0", ifname) :
                String.format("RMI:%s_%s_Stub:0", ifname.substring(0, idx + 1), ifname.substring(idx + 1)));
    }

    //
    // Override writeValue/readvalue, such that only the superclass'
    // state is written. This ensures that fields in the proxy are
    // not included on the wire.
    //
    @Override
    protected void writeValue(ObjectWriter writer, java.io.Serializable val)
            throws IOException {
        _super_descriptor.writeValue(writer, val);
    }

    @Override
    protected void readValue(ObjectReader reader, java.io.Serializable value)
            throws IOException {
        _super_descriptor.readValue(reader, value);
    }
}
