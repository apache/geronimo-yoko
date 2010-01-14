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

import org.apache.yoko.orb.OB.URLRegistry;
import org.apache.yoko.orb.OB.URLScheme;

public class URLRegistry_impl extends org.omg.CORBA.LocalObject implements
        URLRegistry {
    private java.util.Hashtable schemes_ = new java.util.Hashtable();

    // ------------------------------------------------------------------
    // URLRegistry_impl constructor
    // ------------------------------------------------------------------

    public URLRegistry_impl() {
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public void add_scheme(URLScheme scheme)
            throws org.apache.yoko.orb.OB.URLRegistryPackage.SchemeAlreadyExists {
        String name = scheme.name();
        if (schemes_.containsKey(name))
            throw new org.apache.yoko.orb.OB.URLRegistryPackage.SchemeAlreadyExists();
        schemes_.put(name, scheme);
    }

    public URLScheme find_scheme(String name) {
        return (URLScheme) schemes_.get(name);
    }

    public org.omg.CORBA.Object parse_url(String url) {
        if (url == null)
            throw new org.omg.CORBA.BAD_PARAM();

        int colon = url.indexOf(':');
        if (colon <= 0)
            throw new org.omg.CORBA.BAD_PARAM(org.apache.yoko.orb.OB.MinorCodes
                    .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorBadSchemeName),
                    org.apache.yoko.orb.OB.MinorCodes.MinorBadSchemeName,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        String name = url.substring(0, colon).toLowerCase();
        URLScheme scheme = find_scheme(name);
        if (scheme == null)
            throw new org.omg.CORBA.BAD_PARAM(org.apache.yoko.orb.OB.MinorCodes
                    .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorBadSchemeName),
                    org.apache.yoko.orb.OB.MinorCodes.MinorBadSchemeName,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        String urlCopy = name + url.substring(colon);
        return scheme.parse_url(urlCopy);
    }

    public void destroy() {
        java.util.Enumeration e = schemes_.elements();
        while (e.hasMoreElements()) {
            URLScheme scheme = (URLScheme) e.nextElement();
            scheme.destroy();
        }
        schemes_.clear();
    }
}
