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

import org.apache.yoko.orb.OB.URLScheme;

public class IORURLScheme_impl extends org.omg.CORBA.LocalObject implements
        URLScheme {
    private ORBInstance orbInstance_;

    // ------------------------------------------------------------------
    // IORURLScheme_impl constructor
    // ------------------------------------------------------------------

    public IORURLScheme_impl(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String name() {
        return "ior";
    }

    public org.omg.CORBA.Object parse_url(String url) {
        int len = url.length() - 4; // skip "IOR:"

        if ((len % 2) != 0)
            throw new org.omg.CORBA.BAD_PARAM(org.apache.yoko.orb.OB.MinorCodes
                    .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorBadSchemeSpecificPart)
                    + ": invalid length",
                    org.apache.yoko.orb.OB.MinorCodes.MinorBadSchemeSpecificPart,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        byte[] data = HexConverter.asciiToOctets(url, 4);

        try {
            //
            // Error in conversion
            //
            if (data == null)
                throw new org.omg.CORBA.MARSHAL();

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    data, data.length);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf, 0, false);
            in._OB_readEndian();
            org.omg.IOP.IOR ior = org.omg.IOP.IORHelper.read(in);
            ObjectFactory objectFactory = orbInstance_.getObjectFactory();
            return objectFactory.createObject(ior);
        } catch (org.omg.CORBA.MARSHAL ex) {
            //
            // In this case, a marshal error is really a bad "IOR:..." string
            // 
            throw new org.omg.CORBA.BAD_PARAM(org.apache.yoko.orb.OB.MinorCodes
                    .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorBadSchemeSpecificPart)
                    + ": invalid IOR", org.apache.yoko.orb.OB.MinorCodes.MinorBadSchemeSpecificPart,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
    }

    public void destroy() {
        orbInstance_ = null;
    }
}
