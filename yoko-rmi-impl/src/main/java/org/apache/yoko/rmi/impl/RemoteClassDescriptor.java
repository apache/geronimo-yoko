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

final class RemoteClassDescriptor extends RemoteDescriptor {

    @Override
    protected String genRepId() {
        return String.format("IDL:%s:1.0", type.getName().replace('.', '/'));
    }

    RemoteClassDescriptor(Class type, TypeRepository repository) {
        super(type, repository);
    }

    @Override
    protected RemoteInterfaceDescriptor genRemoteInterface() {
        Class[] remotes = collect_remote_interfaces(type);
        if (remotes.length == 0) {
            throw new RuntimeException(type.getName()
                    + " has no remote interfaces");
        }
        Class most_specific_interface = remotes[0];

        return repo.getDescriptor(most_specific_interface).getRemoteInterface();
    }
}
