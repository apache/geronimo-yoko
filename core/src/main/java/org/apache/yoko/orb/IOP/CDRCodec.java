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
import org.omg.IOP.CodecPackage.FormatMismatch;

final class CDRCodec extends org.omg.CORBA.LocalObject implements
        org.omg.IOP.Codec {
    private org.apache.yoko.orb.OB.ORBInstance orbInstance_;

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public byte[] encode(org.omg.CORBA.Any data) {
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
        org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                buf);
        out._OB_ORBInstance(orbInstance_);
        out._OB_writeEndian();
        out.write_any(data);

        byte[] result = new byte[buf.length()];
        System.arraycopy(buf.data(), 0, result, 0, buf.length());
        return result;
    }

    public org.omg.CORBA.Any decode(byte[] data)
            throws org.omg.IOP.CodecPackage.FormatMismatch {
        try {
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    data, data.length);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf, 0, false);
            in._OB_ORBInstance(orbInstance_);
            in._OB_readEndian();

            return in.read_any();
        } catch (org.omg.CORBA.MARSHAL ex) {
            throw (org.omg.IOP.CodecPackage.FormatMismatch)new 
                org.omg.IOP.CodecPackage.FormatMismatch().initCause(ex); 
        }
    }

    public byte[] encode_value(org.omg.CORBA.Any data) {
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
        org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                buf);
        out._OB_ORBInstance(orbInstance_);
        out._OB_writeEndian();
        data.write_value(out);

        byte[] result = new byte[buf.length()];
        System.arraycopy(buf.data(), 0, result, 0, buf.length());
        return result;
    }

    public org.omg.CORBA.Any decode_value(byte[] data, org.omg.CORBA.TypeCode tc)
            throws org.omg.IOP.CodecPackage.FormatMismatch,
            org.omg.IOP.CodecPackage.TypeMismatch {
        if (tc == null)
            throw new org.omg.IOP.CodecPackage.TypeMismatch();

        try {
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    data, data.length);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf, 0, false);
            in._OB_ORBInstance(orbInstance_);
            in._OB_readEndian();

            org.apache.yoko.orb.CORBA.Any any = new org.apache.yoko.orb.CORBA.Any(
                    orbInstance_, tc, null);
            any.read_value(in, tc);

            return any;
        } catch (org.omg.CORBA.MARSHAL ex) {
            throw (org.omg.IOP.CodecPackage.FormatMismatch)new 
                org.omg.IOP.CodecPackage.FormatMismatch().initCause(ex); 
        }
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    CDRCodec(org.apache.yoko.orb.OB.ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }
}
