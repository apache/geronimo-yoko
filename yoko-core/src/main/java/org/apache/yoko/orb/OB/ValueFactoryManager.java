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

package org.apache.yoko.orb.OB;

import java.util.logging.Logger;

import org.apache.yoko.util.Assert;
import org.apache.yoko.util.MinorCodes;
import org.apache.yoko.util.cmsf.RepIds;

public final class ValueFactoryManager {
    static final Logger logger = Logger.getLogger(ValueFactoryManager.class.getName());
    //
    // The set of registered valuetype factories
    //
    private java.util.Hashtable factories_;

    //
    // Cached set of factories resolved by class (Java only)
    //
    private java.util.Hashtable classFactories_;

    private boolean destroy_; // True if destroy() was called

    // ----------------------------------------------------------------------
    // ValueFactoryManager private and protected member implementations
    // ----------------------------------------------------------------------

    protected void finalize() throws Throwable {
        Assert.ensure(destroy_);

        super.finalize();
    }

    // ----------------------------------------------------------------------
    // ValueFactoryManager package member implementations
    // ----------------------------------------------------------------------

    synchronized void destroy() {
        Assert.ensure(!destroy_); // May only be destroyed once

        //
        // Destroy the hashtable
        //
        factories_ = null;
    }

    // ----------------------------------------------------------------------
    // ValueFactoryManager public member implementations
    // ----------------------------------------------------------------------

    public ValueFactoryManager() {
        //
        // Create the hashtables
        //
        factories_ = new java.util.Hashtable(1023);
        classFactories_ = new java.util.Hashtable(1023);

        //
        // Install factories for standard value box types
        //
        registerValueFactory(org.omg.CORBA.StringValueHelper.id(),
                new org.apache.yoko.orb.CORBA.StringValueFactory());
        registerValueFactory(org.omg.CORBA.WStringValueHelper.id(),
                new org.apache.yoko.orb.CORBA.WStringValueFactory());
    }

    public synchronized org.omg.CORBA.portable.ValueFactory registerValueFactory(
            String id, org.omg.CORBA.portable.ValueFactory factory) {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_)
            throw new org.omg.CORBA.INITIALIZE(MinorCodes
                    .describeInitialize(MinorCodes.MinorORBDestroyed),
                    MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        Assert.ensure(id != null && factory != null);

        org.omg.CORBA.portable.ValueFactory old = (org.omg.CORBA.portable.ValueFactory) factories_
                .get(id);

        factories_.put(id, factory);

        return old;
    }

    public synchronized void unregisterValueFactory(String id) {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_)
            throw new org.omg.CORBA.INITIALIZE(MinorCodes
                    .describeInitialize(MinorCodes.MinorORBDestroyed),
                    MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        Assert.ensure(id != null);

        if (factories_.remove(id) == null)
            throw new org.omg.CORBA.BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorValueFactoryError)
                    + ": " + id, MinorCodes.MinorValueFactoryError,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    public synchronized org.omg.CORBA.portable.ValueFactory lookupValueFactory(
            String id) {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_)
            throw new org.omg.CORBA.INITIALIZE(MinorCodes
                    .describeInitialize(MinorCodes.MinorORBDestroyed),
                    MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        Assert.ensure(id != null);

        return (org.omg.CORBA.portable.ValueFactory) factories_.get(id);
    }

    // Java-specific method
    public org.omg.CORBA.portable.ValueFactory lookupValueFactoryWithClass(String id) {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_)
        {
            throw new org.omg.CORBA.INITIALIZE(MinorCodes
                                               .describeInitialize(MinorCodes.MinorORBDestroyed),
                                               MinorCodes.MinorORBDestroyed,
                                               org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        Assert.ensure(id != null);

        org.omg.CORBA.portable.ValueFactory result;

        logger.fine("Looking up value factory for class " + id);
        //
        // Check the registered factories
        //
        result = (org.omg.CORBA.portable.ValueFactory) factories_.get(id);
        if (result != null) {
            logger.finer("Returning registered value factory " + result.getClass().getName());
            return result;
        }

        //
        // Check the cached factories
        //
        result = (org.omg.CORBA.portable.ValueFactory) classFactories_.get(id);
        if (result != null) {
            logger.finer("Returning cached value factory " + result.getClass().getName());
            return result;
        }

        //
        // Try to convert the repository ID into a class name.
        //
        Class c = RepIds.query(id).suffix("DefaultFactory").toClass();
        if (c != null) {
            try {
                logger.finer("Attempting to create value factory from class " + c.getName());
                //
                // Instantiate the factory
                //
                result = (org.omg.CORBA.portable.ValueFactory) c.newInstance();

                //
                // Cache the result
                //
                classFactories_.put(id, result);
            } catch (ClassCastException ex) {
                // ignore
            } catch (InstantiationException ex) {
                // ignore
            } catch (IllegalAccessException ex) {
                // ignore
            }
        }

        return result;
    }
}
