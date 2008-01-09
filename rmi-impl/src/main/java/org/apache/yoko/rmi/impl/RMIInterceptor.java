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

import java.util.logging.Logger;
import java.util.logging.Level;

import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.IOP.Codec;
import org.omg.IOP.TAG_JAVA_CODEBASE;
import org.omg.IOP.TaggedComponent;
import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.IORInterceptor;

public class RMIInterceptor extends LocalObject implements IORInterceptor {

    static final Logger logger = Logger.getLogger(RMIInterceptor.class
            .getName());

    public RMIInterceptor(Codec codec) {
        this.codec = codec;
    }
    
    private Codec codec;

    static ThreadLocal currentCodeBase = new ThreadLocal();

    static void setCurrent(String codeBase) {
        currentCodeBase.set(codeBase);
    }

    /**
     * @see org.omg.PortableInterceptor.IORInterceptorOperations#establish_components(IORInfo)
     */
    public void establish_components(IORInfo info) {

        String codeBase = (String) currentCodeBase.get();
        if (codeBase != null) {

            logger.finer("registering " + codeBase + " for ORB");

            //
            // Create encapsulation
            //
            
            Any any = ORB.init().create_any();
            
            any.insert_string(codeBase);

            try {
                byte[] data = codec.encode(any);
                TaggedComponent component = new TaggedComponent(
                        TAG_JAVA_CODEBASE.value, data);

                info.add_ior_component(component);
            } catch (InvalidTypeForEncoding e) {
                logger.log(Level.WARNING, "Failed to add java codebase to IOR" + e.getMessage(), e);
            }
        }
    }

    /**
     * @see org.omg.PortableInterceptor.InterceptorOperations#name()
     */
    public String name() {
        return "RMI IOR Interceptor";
    }

    /**
     * @see org.omg.PortableInterceptor.InterceptorOperations#destroy()
     */
    public void destroy() {
    }
}
