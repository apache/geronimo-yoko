/*
 * Copyright 2019 IBM Corporation and others.
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

import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.PortableInterceptor.ArgumentStrategy;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.IOP.IOR;

public class PIArgsDowncall extends PIDowncall {
    public final ParameterDesc[] argDesc_;
    public final ParameterDesc retDesc_;
    public final TypeCode[] exceptionTC_;

    public PIArgsDowncall(ORBInstance orbInstance, Client client,
            ProfileInfo profileInfo,
            RefCountPolicyList policies, String op, boolean resp,
            IOR IOR, IOR origIOR,
            PIManager piManager, ParameterDesc[] argDesc,
            ParameterDesc retDesc, TypeCode[] exceptionTC) {
        super(orbInstance, client, profileInfo, policies, op, resp, IOR, origIOR, piManager);
        argDesc_ = argDesc;
        retDesc_ = retDesc;
        exceptionTC_ = exceptionTC;
    }

    @Override
    public ArgumentStrategy createArgumentStrategy(ORB orb) {
        return ArgumentStrategy.create(orb, this);
    }
}
