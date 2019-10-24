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

import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.PortableInterceptor.ArgumentStrategy;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ORB;
import org.omg.IOP.IOR;

public class PIDIIDowncall extends PIDowncall {
    public final NVList args_;
    public final NamedValue result_;
    public final ExceptionList exceptionList_;

    public PIDIIDowncall(ORBInstance orbInstance, Client client,
            ProfileInfo profileInfo,
            RefCountPolicyList policies, String op, boolean resp,
            IOR IOR, IOR origIOR, PIManager piManager,
            NVList args, NamedValue result,
            ExceptionList exceptions) {
        super(orbInstance, client, profileInfo, policies, op, resp, IOR, origIOR, piManager);
        args_ = args;
        result_ = result;
        exceptionList_ = exceptions;
    }

    @Override
    public ArgumentStrategy createArgumentStrategy(ORB orb) {
        return ArgumentStrategy.create(orb, this);
    }
}
