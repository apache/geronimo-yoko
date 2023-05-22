/*
 * Copyright 2021 IBM Corporation and others.
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
package test.pi;

import org.apache.yoko.util.Assert;
import org.omg.CORBA.*;
import org.omg.PortableInterceptor.*;

final class MyIORInterceptor_impl extends org.omg.CORBA.LocalObject implements
        IORInterceptor {
    private org.omg.IOP.Codec cdrCodec_;

    MyIORInterceptor_impl(ORBInitInfo info) {
        org.omg.IOP.CodecFactory factory = info.codec_factory();

        org.omg.IOP.Encoding how = new org.omg.IOP.Encoding(
                (byte) org.omg.IOP.ENCODING_CDR_ENCAPS.value, (byte) 0,
                (byte) 0);

        try {
            cdrCodec_ = factory.create_codec(how);
        } catch (org.omg.IOP.CodecFactoryPackage.UnknownEncoding ex) {
            throw new RuntimeException();
        }
        Assert.ensure(cdrCodec_ != null);
    }

    //
    // IDL to Java Mapping
    //

    public String name() {
        return "";
    }

    public void destroy() {
    }

    public void establish_components(IORInfo info) {
        try {
            Policy p = info.get_effective_policy(MY_SERVER_POLICY_ID.value);
            if (p == null) {
                return;
            }
            MyServerPolicy policy = MyServerPolicyHelper.narrow(p);

            MyComponent content = new MyComponent();
            content.val = policy.value();
            Any any = ORB.init().create_any();
            MyComponentHelper.insert(any, content);

            byte[] encoding = null;
            try {
                encoding = cdrCodec_.encode_value(any);
            } catch (org.omg.IOP.CodecPackage.InvalidTypeForEncoding ex) {
                throw new RuntimeException();
            }

            org.omg.IOP.TaggedComponent component = new org.omg.IOP.TaggedComponent();
            component.tag = MY_COMPONENT_ID.value;
            component.component_data = new byte[encoding.length];
            System.arraycopy(encoding, 0, component.component_data, 0,
                    encoding.length);

            info.add_ior_component(component);
        } catch (INV_POLICY ex) {
            return;
        }
    }

    public void components_established(IORInfo info) {
    }

    public void adapter_state_change(ObjectReferenceTemplate[] templates,
            short state) {
    }

    public void adapter_manager_state_change(int id, short state) {
    }
}
