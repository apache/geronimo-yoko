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

final public class CodeSetIORInterceptor_impl extends org.omg.CORBA.LocalObject
        implements org.omg.PortableInterceptor.IORInterceptor_3_0 {
    //
    // The native codesets
    //
    private int nativeCs_;

    private int nativeWcs_;

    public CodeSetIORInterceptor_impl(int nativeCs, int nativeWcs) {
        nativeCs_ = nativeCs;
        nativeWcs_ = nativeWcs;
    }

    //
    // IDL to Java Mapping
    //

    public String name() {
        return "";
    }

    public void destroy() {
    }

    public void establish_components(org.omg.PortableInterceptor.IORInfo info) {
        org.omg.CONV_FRAME.CodeSetComponent c = CodeSetUtil
                .createCodeSetComponent(nativeCs_, false);
        org.omg.CONV_FRAME.CodeSetComponent wc = CodeSetUtil
                .createCodeSetComponent(nativeWcs_, true);
        org.omg.CONV_FRAME.CodeSetComponentInfo codeSetInfo = new org.omg.CONV_FRAME.CodeSetComponentInfo(
                c, wc);

        org.omg.IOP.TaggedComponent component = new org.omg.IOP.TaggedComponent();
        component.tag = org.omg.IOP.TAG_CODE_SETS.value;

        //
        // The Codec could be used here -- but that means that the
        // Any insertion/extraction operators would have to be
        // generated unnecessarily
        //
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
        org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                buf);
        out._OB_writeEndian();
        org.omg.CONV_FRAME.CodeSetComponentInfoHelper.write(out, codeSetInfo);

        component.component_data = new byte[out._OB_pos()];
        System.arraycopy(buf.data(), 0, component.component_data, 0, buf
                .length());

        try {
            info.add_ior_component(component);
        } catch (org.omg.CORBA.BAD_PARAM ex) {
            // Ignore - profile may not be supported
        }
    }

    public void components_established(org.omg.PortableInterceptor.IORInfo info) {
    }

    public void adapter_manager_state_changed(String id, short state) {
    }

    public void adapter_state_changed(
            org.omg.PortableInterceptor.ObjectReferenceTemplate[] templates,
            short state) {
    }
}
