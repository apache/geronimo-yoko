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

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

import org.omg.CORBA.ORB;

/**
 * This class is the interface for instances of POAStub. When a client
 * calls a remote method, this is translated to a call to the invoke() method in
 * this class.
 */
public interface StubHandler {
    /**
     * Invocation method for an method call.  This 
     * method catches the calls from the generated 
     * stub method and handles the appropriate 
     * argument and return value marshalling.
     * 
     * @param stub   The stub object used to catch the call.
     * @param method The descriptor for the method being invoked.
     * @param args   The arguments passed to the method.
     * 
     * @return The method return value (if any).
     * @exception Throwable
     */
    public Object invoke(RMIStub stub, MethodDescriptor method, Object[] args) throws Throwable;
    
    /**
     * Handle a writeReplace operation on a Stub. 
     * 
     * @param stub   The source RMIStub.
     * 
     * @return The replacement object for serialization.
     */
    public Object stubWriteReplace(RMIStub stub);
}

