/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.apache.yoko.orb.OCI;

import org.apache.yoko.orb.OCI.ConFactory;
import org.apache.yoko.orb.OCI.FactoryAlreadyExists;
import org.apache.yoko.orb.OCI.NoSuchFactory;

public final class ConFactoryRegistry_impl extends org.omg.CORBA.LocalObject
        implements org.apache.yoko.orb.OCI.ConFactoryRegistry {
    //
    // All connector factories
    //
    java.util.Vector factories_ = new java.util.Vector();

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public synchronized void add_factory(ConFactory factory)
            throws FactoryAlreadyExists {
        String id = factory.id();

        for (int i = 0; i < factories_.size(); i++)
            if (id.equals(((ConFactory) factories_.elementAt(i)).id()))
                throw new FactoryAlreadyExists(id);

        factories_.addElement(factory);
    }

    public synchronized ConFactory get_factory(String id) throws NoSuchFactory {
        for (int i = 0; i < factories_.size(); i++) {
            ConFactory factory = (ConFactory) factories_.elementAt(i);
            if (id.equals(factory.id()))
                return factory;
        }

        throw new NoSuchFactory(id);
    }

    public synchronized ConFactory[] get_factories() {
        ConFactory[] result = new ConFactory[factories_.size()];
        factories_.copyInto(result);
        return result;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public ConFactoryRegistry_impl() {
    }
}
