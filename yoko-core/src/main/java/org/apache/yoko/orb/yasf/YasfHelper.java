/*
 * Copyright 2022 IBM Corporation and others.
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
package org.apache.yoko.orb.yasf;

import org.apache.yoko.util.yasf.Yasf;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.ORB;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TaggedComponent;
import org.omg.PortableInterceptor.ClientRequestInfo;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.InvalidSlot;
import org.omg.PortableInterceptor.ServerRequestInfo;

import static org.apache.yoko.util.MinorCodes.MinorInvalidComponentId;
import static org.apache.yoko.util.MinorCodes.MinorInvalidServiceContextId;

public enum YasfHelper {
    ;

    public static void addTc(IORInfo info) {
        TaggedComponent tc = new TaggedComponent(Yasf.TAG_YOKO_AUXILIARY_STREAM_FORMAT, Yasf.toData());
        info.add_ior_component(tc);
    }

    private static ServiceContext sc() {
        return new ServiceContext(Yasf.YOKO_AUXILIARY_STREAM_FORMAT_SC, Yasf.toData());
    }

    public static void addSc(ClientRequestInfo ri) {
        ServiceContext sc = sc();
        ri.add_request_service_context(sc, false);
    }

    public static void addSc(ServerRequestInfo ri) {
        ServiceContext sc = sc();
        ri.add_reply_service_context(sc, false);
    }

    public static byte[] readData(ClientRequestInfo ri) {
        try {
            TaggedComponent tc = ri.get_effective_component(Yasf.TAG_YOKO_AUXILIARY_STREAM_FORMAT);
            return tc.component_data;
        } catch (BAD_PARAM e) {
            if (e.minor != MinorInvalidComponentId) {
                throw e;
            }
        }
        return null;
    }

    public static byte[] readData(ServerRequestInfo ri) {
        try {
            ServiceContext sc = ri.get_request_service_context(Yasf.YOKO_AUXILIARY_STREAM_FORMAT_SC);
            return sc.context_data;
        } catch (BAD_PARAM e) {
            if (e.minor != MinorInvalidServiceContextId) {
                throw e;
            }
        }
        return null;
    }

    public static void setSlot(int slotId, ServerRequestInfo ri, byte[] data) {
        Any any = ORB.init().create_any();
        any.insert_Value(data);
        try {
            ri.set_slot(slotId, any);
        } catch (InvalidSlot e) {
            throw (INTERNAL)(new INTERNAL(e.getMessage())).initCause(e);
        }
    }

    public static byte[] getSlot(int slotId, ServerRequestInfo ri) {
        try {
            Any any = ri.get_slot(slotId);
            return (byte[])any.extract_Value();
        } catch (InvalidSlot e) {
            throw (INTERNAL)(new INTERNAL(e.getMessage())).initCause(e);
        }
    }
}
