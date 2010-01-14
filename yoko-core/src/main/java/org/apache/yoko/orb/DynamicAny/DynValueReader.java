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

package org.apache.yoko.orb.DynamicAny;

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.TypeCode;

final public class DynValueReader {
    private org.apache.yoko.orb.OB.ORBInstance orbInstance_;

    private org.omg.DynamicAny.DynAnyFactory factory_;

    private java.util.Hashtable instanceTable_;

    private boolean truncateOK_;

    public boolean mustTruncate;

    public DynValueReader(org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.DynamicAny.DynAnyFactory factory, boolean truncateOK) {
        orbInstance_ = orbInstance;
        factory_ = factory;
        truncateOK_ = truncateOK;
        mustTruncate = false;
        instanceTable_ = new java.util.Hashtable(131);
    }

    public org.omg.DynamicAny.DynAny readValue(InputStream in,
            org.omg.CORBA.TypeCode tc)
            throws org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode {
        //
        // See if we already have a DynValue for this position
        //
        org.omg.DynamicAny.DynAny result = getValue(in, tc);
        if (result != null)
            return result;

        //
        // Read the tag and attempt to process an indirection.
        //
        org.apache.yoko.orb.OCI.Buffer buf = in._OB_buffer();
        int save = buf.pos_;
        int tag = in.read_long();
        int curPos = save; // buf.cur_ - 4;

        try {
            if (tag == -1)
                return readIndirection(in);
        } catch (org.omg.CORBA.MARSHAL ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            return null;
        }

        //
        // Prepare a new DynValue and reference it as a possible
        // target for indirection.
        //
        DynAnyFactory_impl factory_impl = (DynAnyFactory_impl) factory_;
        result = factory_impl.prepare_dyn_any_from_type_code(tc, this);

        //
        // Skip null valueType
        //
        if (tag != 0) {
            int startPos = curPos;// - buf.data_;
            indexValue(startPos, result);
        }

        //
        // Restore the position of the input stream and populate the
        // DynValue (unmarshal)
        //
        in._OB_pos(save);
        DynAny_impl impl = (DynAny_impl) result;
        impl._OB_unmarshal(in);

        return result;
    }

    protected void indexValue(int startPos, org.omg.DynamicAny.DynAny dv) {
        instanceTable_.put(new Integer(startPos), dv);
    }

    private org.omg.DynamicAny.DynAny readIndirection(InputStream in)
            throws org.omg.CORBA.MARSHAL {
        org.apache.yoko.orb.OCI.Buffer buf = in._OB_buffer();
        int offs = in.read_long();
        int startPos = buf.pos_ - 4 + offs;

        org.omg.DynamicAny.DynAny result = (org.omg.DynamicAny.DynAny) instanceTable_
                .get(new Integer(startPos));

        if (result == null) {
            throw new org.omg.CORBA.MARSHAL(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadInvalidIndirection), 
                org.apache.yoko.orb.OB.MinorCodes.MinorReadInvalidIndirection, 
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        return result;
    }

    private org.omg.DynamicAny.DynAny getValue(InputStream in,
            org.omg.CORBA.TypeCode tc) {
        //
        // See if we already have a reference for the DynValue marshalled
        // at the current position of the stream (the record would have
        // been created earlier by DynValueWriter).
        //
        org.apache.yoko.orb.OCI.Buffer buf = in._OB_buffer();
        int startPos = buf.pos_;

        org.omg.DynamicAny.DynAny orig = (org.omg.DynamicAny.DynAny) instanceTable_
                .get(new Integer(startPos));

        if (orig == null)
            return null;

        //
        // We found an existing DynValue. Now we have to advance the
        // Input Stream by unmarshalling a temporary copy of the DynValue.
        //
        DynAnyFactory_impl factory_impl = (DynAnyFactory_impl) factory_;
        org.omg.DynamicAny.DynAny copy = null;

        try {
            copy = factory_impl.prepare_dyn_any_from_type_code(tc, this);
        } catch (org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        DynAny_impl impl = (DynAny_impl) copy;
        impl._OB_unmarshal(in);

        //
        // Return the original value (not the copy)
        //
        return orig;
    }
}
