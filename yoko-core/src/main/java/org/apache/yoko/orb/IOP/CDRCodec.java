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

package org.apache.yoko.orb.IOP;
import org.apache.yoko.orb.CORBA.Any;
import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.OB.ORBInstance;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.TypeCode;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecPackage.FormatMismatch;
import org.omg.IOP.CodecPackage.TypeMismatch;

final class CDRCodec extends LocalObject implements Codec {
    private ORBInstance orbInstance_;

    public byte[] encode(org.omg.CORBA.Any data) {
        try (OutputStream out = new OutputStream()) {
            out._OB_ORBInstance(orbInstance_);
            out._OB_writeEndian();
            out.write_any(data);
            return out.copyWrittenBytes();
        }
    }

    public org.omg.CORBA.Any decode(byte[] data) throws FormatMismatch {
        try {
            InputStream in = new InputStream(data);
            in._OB_ORBInstance(orbInstance_);
            in._OB_readEndian();

            return in.read_any();
        } catch (MARSHAL ex) {
            throw (FormatMismatch)new FormatMismatch().initCause(ex);
        }
    }

    public byte[] encode_value(org.omg.CORBA.Any data) {
        try (OutputStream out = new OutputStream()) {
            out._OB_ORBInstance(orbInstance_);
            out._OB_writeEndian();
            data.write_value(out);
            return out.copyWrittenBytes();
        }
    }

    public org.omg.CORBA.Any decode_value(byte[] data, TypeCode tc) throws FormatMismatch, TypeMismatch {
        if (tc == null) throw new TypeMismatch();

        try {
            InputStream in = new InputStream(data);
            in._OB_ORBInstance(orbInstance_);
            in._OB_readEndian();

            Any any = new Any(orbInstance_, tc, null);
            any.read_value(in, tc);

            return any;
        } catch (MARSHAL ex) {
            throw (FormatMismatch)new FormatMismatch().initCause(ex);
        }
    }

    CDRCodec(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }
}
