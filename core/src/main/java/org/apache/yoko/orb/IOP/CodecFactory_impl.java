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

final public class CodecFactory_impl extends org.omg.CORBA.LocalObject
        implements org.omg.IOP.CodecFactory {
    private org.omg.IOP.Codec cdrCodec_; // Cached CDR Codec

    private org.apache.yoko.orb.OB.ORBInstance orbInstance_; // The
                                                                // ORBInstance

    // ----------------------------------------------------------------------
    // CodecFactory_impl public member implementation
    // ----------------------------------------------------------------------

    public org.omg.IOP.Codec create_codec(org.omg.IOP.Encoding encoding)
            throws org.omg.IOP.CodecFactoryPackage.UnknownEncoding {
        org.apache.yoko.orb.OB.Assert._OB_assert(orbInstance_ != null);

        // TODO: check major/minor version
        if (encoding.format != org.omg.IOP.ENCODING_CDR_ENCAPS.value)
            throw new org.omg.IOP.CodecFactoryPackage.UnknownEncoding();

        synchronized (this) {
            if (cdrCodec_ == null)
                cdrCodec_ = new CDRCodec(orbInstance_);
        }

        return cdrCodec_;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public void _OB_setORBInstance(
            org.apache.yoko.orb.OB.ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }
}
