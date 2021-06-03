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

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.util.HexConverter;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.MARSHAL;
import org.omg.IOP.IOR;
import org.omg.IOP.IORHelper;

import static org.apache.yoko.util.MinorCodes.MinorBadSchemeSpecificPart;
import static org.apache.yoko.util.MinorCodes.describeBadParam;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

public class IORURLScheme_impl extends LocalObject implements URLScheme {
    private ORBInstance orbInstance_;

    public IORURLScheme_impl(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }

    public String name() {
        return "ior";
    }

    public org.omg.CORBA.Object parse_url(String url) {
        int len = url.length() - 4; // skip "IOR:"

        if ((len % 2) != 0)
            throw new BAD_PARAM(describeBadParam(MinorBadSchemeSpecificPart) + ": invalid length", MinorBadSchemeSpecificPart, COMPLETED_NO);

        byte[] data = HexConverter.asciiToOctets(url, 4);

        try {
            //
            // Error in conversion
            //
            if (data == null)
                throw new MARSHAL();

            InputStream in = new InputStream(data);
            in._OB_readEndian();
            IOR ior = IORHelper.read(in);
            ObjectFactory objectFactory = orbInstance_.getObjectFactory();
            return objectFactory.createObject(ior);
        } catch (MARSHAL ex) {
            //
            // In this case, a marshal error is really a bad "IOR:..." string
            // 
            throw new BAD_PARAM(describeBadParam(MinorBadSchemeSpecificPart) + ": invalid IOR \"" + url + "\"", MinorBadSchemeSpecificPart, COMPLETED_NO);
        }
    }

    public void destroy() {
        orbInstance_ = null;
    }
}
