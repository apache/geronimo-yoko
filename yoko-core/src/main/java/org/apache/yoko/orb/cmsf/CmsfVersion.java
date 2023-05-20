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
package org.apache.yoko.orb.cmsf;

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.IOP.RMICustomMaxStreamFormat;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TAG_RMI_CUSTOM_MAX_STREAM_FORMAT;
import org.omg.IOP.TaggedComponent;

public enum CmsfVersion {
    CMSFv1(1), CMSFv2(2);
    public static final boolean ENABLED = true;
    private final byte value;
    private final TaggedComponent tc;
    private final ServiceContext sc;
    private final Any any;
    
    private CmsfVersion(int value) {
        this((byte)(value & 0xff));
    }
    
    private CmsfVersion(byte value) {
        this.value = value;
        final byte[] data = genData(value);
        this.tc = new TaggedComponent(TAG_RMI_CUSTOM_MAX_STREAM_FORMAT.value, data.clone());
        this.sc = new ServiceContext(RMICustomMaxStreamFormat.value, data);
        this.any = ORB.init().create_any();
        this.any.insert_octet((byte)value);
    }
    
    byte getValue() {
        return value;
    }
    
    TaggedComponent getTc() {
        return tc;
    }
    
    ServiceContext getSc() {
        return sc;
    }
    
    Any getAny() {
        return any;
    }

    static CmsfVersion readAny(Any any) {
        if (any == null) return CMSFv1;
        return (any.extract_octet() >= 2) ? CMSFv2 : CMSFv1;
    }
    
    static CmsfVersion readData(byte[] data) {
        if (data == null) return CMSFv1;
        int cmsf = 1;
        try (InputStream in = new InputStream(data)) {
            in._OB_readEndian();
            cmsf = in.read_octet();
        } catch (Exception e) {
            throw (MARSHAL)(new MARSHAL(e.getMessage())).initCause(e);
        }
        return (cmsf >= 2) ? CMSFv2 : CMSFv1;
    }
    
    private static byte[] genData(byte value) {
        try (OutputStream out = new OutputStream(2)) {
            out._OB_writeEndian();
            out.write_octet(value);
            return out.copyWrittenBytes();
        }
    }
}
