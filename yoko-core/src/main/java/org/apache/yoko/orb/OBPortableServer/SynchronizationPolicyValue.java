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

package org.apache.yoko.orb.OBPortableServer;

//
// IDL:orb.yoko.apache.org/OBPortableServer/SynchronizationPolicyValue:1.0
//
/**
 *
 * This enumeration details the synchronization strategies for method
 * dispatch.
 *
 * @member NO_SYNCHRONIZATION No direct synchronization is applied
 * @member SYNCHRONIZE_ON_POA Method calls are synchronized on a POA
 * @member SYNCHRONIZE_ON_ORB Method calls are synchronized on the ORB
 *
 * @see SynchronizationPolicy
 *
 **/

public class SynchronizationPolicyValue implements org.omg.CORBA.portable.IDLEntity
{
    private static SynchronizationPolicyValue [] values_ = new SynchronizationPolicyValue[3];
    private int value_;

    public final static int _NO_SYNCHRONIZATION = 0;
    public final static SynchronizationPolicyValue NO_SYNCHRONIZATION = new SynchronizationPolicyValue(_NO_SYNCHRONIZATION);
    public final static int _SYNCHRONIZE_ON_POA = 1;
    public final static SynchronizationPolicyValue SYNCHRONIZE_ON_POA = new SynchronizationPolicyValue(_SYNCHRONIZE_ON_POA);
    public final static int _SYNCHRONIZE_ON_ORB = 2;
    public final static SynchronizationPolicyValue SYNCHRONIZE_ON_ORB = new SynchronizationPolicyValue(_SYNCHRONIZE_ON_ORB);

    protected
    SynchronizationPolicyValue(int value)
    {
        values_[value] = this;
        value_ = value;
    }

    public int
    value()
    {
        return value_;
    }

    public static SynchronizationPolicyValue
    from_int(int value)
    {
        if(value < values_.length)
            return values_[value];
        else
            throw new org.omg.CORBA.BAD_PARAM("Value (" + value  + ") out of range", 25, org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    private java.lang.Object
    readResolve()
        throws java.io.ObjectStreamException
    {
        return from_int(value());
    }
}
