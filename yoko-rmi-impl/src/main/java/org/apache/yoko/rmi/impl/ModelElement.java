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
    protected ModelElement(TypeRepository repo, String java_name) {
        this.repo = repo;
        this.java_name = java_name;
    }

    private volatile boolean initComplete = false;
    /** It is the caller's responsibility to ensure this method is called from only one thread at a time. */
    final boolean doInitOnce() {
        if (initComplete) return false;
        init();
        return initComplete = true;
    }

    protected void init() { }

    private volatile String idlName = null;   // fully resolved package name
    protected abstract String genIDLName();
    public final String getIDLName() {
        if (null == idlName) idlName = genIDLName();
        return idlName;
    }
}
