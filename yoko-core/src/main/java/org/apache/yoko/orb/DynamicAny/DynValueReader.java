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
import org.apache.yoko.orb.OB.Assert;
import org.apache.yoko.orb.OB.ORBInstance;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;

import java.util.HashMap;
import java.util.Map;

import static org.apache.yoko.orb.OB.MinorCodes.MinorReadInvalidIndirection;
import static org.apache.yoko.orb.OB.MinorCodes.describeMarshal;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

final public class DynValueReader {

    private final DynAnyFactory factory_;

    private final Map<Integer, DynAny> instanceTable_;

    public boolean mustTruncate;

    public DynValueReader(ORBInstance orbInstance, DynAnyFactory factory, boolean truncateOK) {
        factory_ = factory;
        mustTruncate = false;
        instanceTable_ = new HashMap<>(131);
    }

    public DynAny readValue(InputStream in, TypeCode tc) throws InconsistentTypeCode {
        // See if we already have a DynValue for this position
        DynAny result = getValue(in, tc);
        if (result != null) return result;

        // Read the tag and attempt to process an indirection.
        int tag = in.read_long();
        final int save = in.getPosition() - 4;

        try {
            if (tag == -1) return readIndirection(in);
        } catch (MARSHAL ex) {
            throw Assert.fail(ex);
        }

        // Prepare a new DynValue and reference it as a possible
        // target for indirection.
        DynAnyFactory_impl factory_impl = (DynAnyFactory_impl) factory_;
        result = factory_impl.prepare_dyn_any_from_type_code(tc, this);

        // Skip null valueType
        if (tag != 0) {
            indexValue(save, result);
        }

        // Restore the position of the input stream and populate the
        // DynValue (unmarshal)
        in.setPosition(save);
        DynAny_impl impl = (DynAny_impl) result;
        impl._OB_unmarshal(in);

        return result;
    }

    protected void indexValue(int startPos, DynAny dv) {
        instanceTable_.put(startPos, dv);
    }

    private DynAny readIndirection(InputStream in) throws MARSHAL {
        int offs = in.read_long();
        int startPos = in.getPosition() - 4 + offs;

        DynAny result = instanceTable_.get(startPos);

        if (result == null) throw new MARSHAL(describeMarshal(MinorReadInvalidIndirection), MinorReadInvalidIndirection, COMPLETED_NO);
        return result;
    }

    private DynAny getValue(InputStream in, TypeCode tc) {
        // See if we already have a reference for the DynValue marshalled
        // at the current position of the stream (the record would have
        // been created earlier by DynValueWriter).
        int startPos = in.getPosition();

        DynAny orig = (DynAny) instanceTable_.get(startPos);

        if (orig == null) return null;

        // We found an existing DynValue. Now we have to advance the
        // Input Stream by unmarshalling a temporary copy of the DynValue.
        DynAnyFactory_impl factory_impl = (DynAnyFactory_impl) factory_;
        DynAny copy = null;

        try {
            copy = factory_impl.prepare_dyn_any_from_type_code(tc, this);
        } catch (InconsistentTypeCode ex) {
            throw Assert.fail(ex);
        }

        DynAny_impl impl = (DynAny_impl) copy;
        impl._OB_unmarshal(in);

        // Return the original value (not the copy)
        return orig;
    }
}
