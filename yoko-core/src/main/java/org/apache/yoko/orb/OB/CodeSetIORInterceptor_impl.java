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
import org.omg.CONV_FRAME.CodeSetComponent;
import org.omg.CONV_FRAME.CodeSetComponentInfo;
import org.omg.CONV_FRAME.CodeSetComponentInfoHelper;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.LocalObject;
import org.omg.IOP.TAG_CODE_SETS;
import org.omg.IOP.TaggedComponent;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.IORInterceptor_3_0;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

public final class CodeSetIORInterceptor_impl extends LocalObject implements IORInterceptor_3_0 {
    //
    // The native codesets
    //
    private final int nativeCs_;

    private final int nativeWcs_;

    public CodeSetIORInterceptor_impl(int nativeCs, int nativeWcs) {
        nativeCs_ = nativeCs;
        nativeWcs_ = nativeWcs;
    }

    public String name() {
        return "";
    }

    public void destroy() {}

    public void establish_components(IORInfo info) {
        CodeSetComponent c = CodeSetUtil.createCodeSetComponent(nativeCs_, false);
        CodeSetComponent wc = CodeSetUtil.createCodeSetComponent(nativeWcs_, true);
        CodeSetComponentInfo codeSetInfo = new CodeSetComponentInfo(c, wc);

        TaggedComponent component = new TaggedComponent();
        component.tag = TAG_CODE_SETS.value;

        //
        // The Codec could be used here -- but that means that the
        // Any insertion/extraction operators would have to be
        // generated unnecessarily
        //
        OutputStream out = new OutputStream();
        out._OB_writeEndian();
        CodeSetComponentInfoHelper.write(out, codeSetInfo);

        component.component_data = out.copyWrittenBytes();

        try {
            info.add_ior_component(component);
        } catch (BAD_PARAM ex) {
            // Ignore - profile may not be supported
        }
    }

    public void components_established(IORInfo info) {}

    public void adapter_manager_state_changed(String id, short state) {}

    public void adapter_state_changed(ObjectReferenceTemplate[] templates, short state) {}
}
