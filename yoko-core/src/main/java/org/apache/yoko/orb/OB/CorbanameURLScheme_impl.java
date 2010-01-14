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

import org.apache.yoko.orb.OB.CorbalocURLScheme;
import org.apache.yoko.orb.OB.CorbalocURLSchemeHelper;
import org.apache.yoko.orb.OB.URLRegistry;
import org.apache.yoko.orb.OB.URLScheme;

public class CorbanameURLScheme_impl extends org.omg.CORBA.LocalObject
        implements URLScheme {
    private org.omg.CORBA.ORB orb_;

    private CorbalocURLScheme corbaloc_;

    // ------------------------------------------------------------------
    // CorbanameURLScheme_impl constructor
    // ------------------------------------------------------------------

    public CorbanameURLScheme_impl(org.omg.CORBA.ORB orb, URLRegistry registry) {
        orb_ = orb;
        URLScheme scheme = registry.find_scheme("corbaloc");
        Assert._OB_assert(scheme != null);
        corbaloc_ = CorbalocURLSchemeHelper.narrow(scheme);
        Assert._OB_assert(corbaloc_ != null);
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String name() {
        return "corbaname";
    }

    public org.omg.CORBA.Object parse_url(String url) {
        //
        // Get the object key
        //
        String keyStr;
        int slash = url.indexOf('/');
        int fragmentStart = url.indexOf('#');
        if (slash != -1 && fragmentStart == -1) {
            //
            // e.g., corbaname::localhost:5000/blah
            //
            keyStr = url.substring(slash + 1);
        } else if (slash == -1 || fragmentStart - 1 == slash
                || fragmentStart < slash) {
            //
            // e.g., corbaname::localhost:5000
            // corbaname::localhost:5000/#foo
            // corbaname::localhost:5000#foo/bar
            //
            keyStr = "NameService";
        } else {
            keyStr = url.substring(slash + 1, fragmentStart);
        }

        //
        // Get start and end of protocol address(es)
        //
        int addrStart = 10; // skip "corbaname:"
        int addrEnd;

        if (addrStart == slash)
            throw new org.omg.CORBA.BAD_PARAM(org.apache.yoko.orb.OB.MinorCodes
                    .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorBadAddress)
                    + ": no protocol address", org.apache.yoko.orb.OB.MinorCodes.MinorBadAddress,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        if (slash == -1 && fragmentStart == -1)
            addrEnd = url.length() - 1;
        else if ((slash != -1 && fragmentStart == -1)
                || (slash != -1 && fragmentStart != -1 && slash < fragmentStart))
            addrEnd = slash - 1;
        else
            addrEnd = fragmentStart - 1;

        //
        // Create a corbaloc URL
        //
        String corbaloc = "corbaloc:" + url.substring(addrStart, addrEnd + 1)
                + "/" + keyStr;

        //
        // Create object reference from the naming context
        //
        org.omg.CORBA.Object nc = corbaloc_.parse_url(corbaloc);

        // 
        // If there is no URL fragment "#.....", or the stringified
        // name is empty, then the URL refers to the naming context
        // itself
        // 
        if (fragmentStart == -1 || url.substring(fragmentStart).length() == 0)
            return nc;

        //
        // Make a DII invocation on the Naming Service to resolve the
        // specified context
        //
        try {
            //
            // Create typecodes for Name and NameComponent
            //
            org.omg.CORBA.StructMember[] contents = new org.omg.CORBA.StructMember[2];
            contents[0] = new org.omg.CORBA.StructMember();
            contents[0].name = "id";
            contents[0].type = TypeCodeFactory.createStringTC(0);
            contents[1] = new org.omg.CORBA.StructMember();
            contents[1].name = "kind";
            contents[1].type = TypeCodeFactory.createStringTC(0);
            org.omg.CORBA.TypeCode tcNameComponent = TypeCodeFactory
                    .createStructTC("IDL:omg.org/CosNaming/NameComponent:1.0",
                            "NameComponent", contents);

            org.omg.CORBA.TypeCode tcName = TypeCodeFactory.createSequenceTC(0,
                    tcNameComponent);

            //
            // Parse path (remove URL escapes first) and create
            // NameComponent sequence
            //
            String fragment = URLUtil.unescapeURL(url
                    .substring(fragmentStart + 1));
            CORBANameParser parser = new CORBANameParser(fragment);
            if (!parser.isValid())
                throw new org.omg.CORBA.BAD_PARAM(
                        MinorCodes
                                .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorBadSchemeSpecificPart)
                                + ": invalid stringified name",
                        org.apache.yoko.orb.OB.MinorCodes.MinorBadSchemeSpecificPart,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            String[] content = parser.getContents();
            Assert._OB_assert((content.length % 2) == 0);

            org.omg.CORBA.Object factoryObj = orb_
                    .resolve_initial_references("DynAnyFactory");
            org.omg.DynamicAny.DynAnyFactory dynAnyFactory = org.omg.DynamicAny.DynAnyFactoryHelper
                    .narrow(factoryObj);

            org.omg.CORBA.Any[] as = new org.omg.CORBA.Any[content.length / 2];
            for (int i = 0; i < content.length; i += 2) {
                //
                // Create the DynStruct containing the id and kind fields
                //
                org.omg.DynamicAny.DynAny dynAny = dynAnyFactory
                        .create_dyn_any_from_type_code(tcNameComponent);
                org.omg.DynamicAny.DynStruct name = org.omg.DynamicAny.DynStructHelper
                        .narrow(dynAny);
                name.insert_string(content[i]);
                name.next();
                name.insert_string(content[i + 1]);

                org.omg.CORBA.Any nany = name.to_any();
                name.destroy();

                as[i / 2] = nany;
            }

            //
            // Create the Name
            //
            org.omg.DynamicAny.DynAny dynAny = dynAnyFactory
                    .create_dyn_any_from_type_code(tcName);
            org.omg.DynamicAny.DynSequence seq = org.omg.DynamicAny.DynSequenceHelper
                    .narrow(dynAny);
            seq.set_length(as.length);
            seq.set_elements(as);
            org.omg.CORBA.Any any = seq.to_any();
            seq.destroy();

            //
            // Create the DII request
            //
            org.omg.CORBA.Request request = nc._request("resolve");

            //
            // Copy in the arguments
            //
            org.omg.CORBA.Any arg = request.add_in_arg();
            arg.read_value(any.create_input_stream(), any.type());

            request.set_return_type(TypeCodeFactory
                    .createPrimitiveTC(org.omg.CORBA.TCKind.tk_objref));

            //
            // Invoke the request
            //
            request.invoke();

            //
            // Return the result if there was no exception
            //
            if (request.env().exception() == null)
                return request.return_value().extract_Object();
        } catch (org.omg.CORBA.SystemException ex) {
            // Fall through
        } catch (org.omg.CORBA.UserException ex) {
            // Fall through
        }

        throw new org.omg.CORBA.BAD_PARAM(org.apache.yoko.orb.OB.MinorCodes
                .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorOther)
                + ": corbaname evaluation error", org.apache.yoko.orb.OB.MinorCodes.MinorOther,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    public void destroy() {
        orb_ = null;
    }
}
