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

abstract class ModelElement {
    final TypeRepository repo;
    final String java_name;    // the java name of the type
    private String idl_name;   // fully resolved package name
    private volatile boolean notInit = true;

    protected ModelElement(TypeRepository repo, String java_name) {
        this.repo = repo;
        this.java_name = java_name;
    }

    protected void init() {
        idl_name = genIDLName();
        notInit = false;
    }

    protected abstract String genIDLName();

    final void checkInit() {
        if (notInit) throw new IllegalStateException("Not initialized");
    }
    public String getIDLName() {
        checkInit();
        return idl_name;
    }
}
