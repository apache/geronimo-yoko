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

public class ModelElement {
    protected String idl_name;        // fully resolved package name
    protected String type_name = "";       // the simple type name (minus package, if any)
    protected String package_name = "";    // the package name qualifier (if any)
    protected String java_name;       // the java name of the type 

    protected void setIDLName(String name) {
        idl_name = name;
    }
    
    public String getIDLName() {
        return idl_name;
    }

    protected void setTypeName(String name) {
        type_name = name;
    }
    
    public String getTypeName() {
        return type_name;
    }

    protected void setPackageName(String name) {
        package_name = name;
    }
    
    public String getPackageName() {
        return package_name;
    }

    protected void setJavaName(String name) {
        java_name = name;
    }

    public String getJavaName() {
        return java_name;
    }

    protected TypeRepository repository;

    protected void setTypeRepository(TypeRepository repository) {
        this.repository = repository;
    }

    public TypeRepository getTypeRepository() {
        return repository;
    }
}
