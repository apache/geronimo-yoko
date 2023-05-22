/*
 * Copyright 2022 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.orb.OB;

import org.apache.yoko.orb.CORBA.StringValueFactory;
import org.apache.yoko.orb.CORBA.WStringValueFactory;
import org.apache.yoko.util.Assert;
import org.apache.yoko.util.cmsf.RepIds;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.StringValueHelper;
import org.omg.CORBA.WStringValueHelper;
import org.omg.CORBA.portable.ValueFactory;

import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedActionException;
import java.util.Hashtable;
import java.util.logging.Logger;

import static java.security.AccessController.doPrivileged;
import static org.apache.yoko.util.MinorCodes.MinorORBDestroyed;
import static org.apache.yoko.util.MinorCodes.MinorValueFactoryError;
import static org.apache.yoko.util.MinorCodes.describeBadParam;
import static org.apache.yoko.util.MinorCodes.describeInitialize;
import static org.apache.yoko.util.PrivilegedActions.getNoArgConstructor;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

public final class ValueFactoryManager {
    static final Logger logger = Logger.getLogger(ValueFactoryManager.class.getName());
    //
    // The set of registered valuetype factories
    //
    private Hashtable factories_;

    //
    // Cached set of factories resolved by class (Java only)
    //
    private Hashtable classFactories_;

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
        factories_ = new Hashtable(1023);
        classFactories_ = new Hashtable(1023);

        //
        // Install factories for standard value box types
        //
        registerValueFactory(StringValueHelper.id(), new StringValueFactory());
        registerValueFactory(WStringValueHelper.id(), new WStringValueFactory());
    }

    public synchronized ValueFactory registerValueFactory(String id, ValueFactory factory) {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_)
            throw new INITIALIZE(describeInitialize(MinorORBDestroyed), MinorORBDestroyed, COMPLETED_NO);

        Assert.ensure(id != null && factory != null);

        ValueFactory old = (ValueFactory) factories_.get(id);

        factories_.put(id, factory);

        return old;
    }

    public synchronized void unregisterValueFactory(String id) {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_)
            throw new INITIALIZE(describeInitialize(MinorORBDestroyed), MinorORBDestroyed, COMPLETED_NO);

        Assert.ensure(id != null);

        if (factories_.remove(id) == null)
            throw new BAD_PARAM(describeBadParam(MinorValueFactoryError) + ": " + id, MinorValueFactoryError, COMPLETED_NO);
    }

    public synchronized ValueFactory lookupValueFactory(String id) {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_)
            throw new INITIALIZE(describeInitialize(MinorORBDestroyed), MinorORBDestroyed, COMPLETED_NO);

        Assert.ensure(id != null);

        return (ValueFactory)factories_.get(id);
    }

    // Java-specific method
    public ValueFactory lookupValueFactoryWithClass(String id) {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_)
        {
            throw new INITIALIZE(describeInitialize(MinorORBDestroyed), MinorORBDestroyed, COMPLETED_NO);
        }

        Assert.ensure(id != null);

        ValueFactory result;

        logger.fine("Looking up value factory for class " + id);
        //
        // Check the registered factories
        //
        result = (ValueFactory) factories_.get(id);
        if (result != null) {
            logger.finer("Returning registered value factory " + result.getClass().getName());
            return result;
        }

        //
        // Check the cached factories
        //
        result = (ValueFactory) classFactories_.get(id);
        if (result != null) {
            logger.finer("Returning cached value factory " + result.getClass().getName());
            return result;
        }

        //
        // Try to convert the repository ID into a class name.
        //
        Class<? extends ValueFactory> c = RepIds.query(id).suffix("DefaultFactory").toClass();
        if (c != null) {
            try {
                logger.finer("Attempting to create value factory from class " + c.getName());
                //
                // Instantiate the factory
                //
                result = doPrivileged(getNoArgConstructor(c)).newInstance();

                //
                // Cache the result
                //
                classFactories_.put(id, result);
            } catch (ClassCastException | PrivilegedActionException | InvocationTargetException | InstantiationException | IllegalAccessException ignored) {
            }
        }

        return result;
    }
}
