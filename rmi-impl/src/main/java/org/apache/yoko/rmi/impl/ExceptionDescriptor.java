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

public class ExceptionDescriptor extends ValueDescriptor {
    ExceptionDescriptor(Class type, TypeRepository repository) {
        super(type, repository);
    }

    String ex_repid = null;

    public String getExceptionRepositoryID() {
        if (ex_repid == null) {
            init_repid();
        }

        return ex_repid;
    }

    void init_repid() {
        Class type = getJavaClass();
        String name = type.getName();
        String encname = null;

        if (name.endsWith("Exception")) {
            encname = name.substring(0, name.length() - 7);
        } else {
            encname = name + "Ex";
        }

        ex_repid = "IDL:" + encname.replace('.', '/') + ":1.0";
    }

}
