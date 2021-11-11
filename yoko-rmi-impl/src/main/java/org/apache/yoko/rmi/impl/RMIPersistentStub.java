/*
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

import javax.rmi.CORBA.Stub;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.rmi.CORBA.Util.createValueHandler;
import static javax.rmi.PortableRemoteObject.narrow;

/**
 * RMIStub's (org.apache.yoko.rmi.impl) implement writeReplace by returning an
 * instance of this class; and this class then implements readResolve to narrow
 * itself to the correct type. This way, object references to RMI exported
 * objects are transferred without loss of the runtime type.
 * 
 * @author Kresten Krab Thorup (krab@eos.dk)
 */
public class RMIPersistentStub extends Stub {
    static final Logger logger = Logger.getLogger(RMIPersistentStub.class.getName());
    
    /** the class-type to which this object should be narrow'ed */
    private final Class<?> type;

    public RMIPersistentStub(Stub stub, Class<?> type) {
        _set_delegate(stub._get_delegate());
        this.type = type;
    }

    public Object readResolve() throws ClassNotFoundException {
        try {
            return narrow(this, type);
        } catch (RuntimeException ex) {
            logger.log(Level.WARNING, "Error narrowing object", ex); 
            throw ex;
        }
    }

    public String[] _ids() {
        return new String[] { createValueHandler().getRMIRepositoryID(type) };
    }

}
