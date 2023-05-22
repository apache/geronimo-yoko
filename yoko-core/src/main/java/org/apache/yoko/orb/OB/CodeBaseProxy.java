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
package org.apache.yoko.orb.OB;

import org.apache.yoko.orb.CORBA.InputStream;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Repository;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.IOP.SendingContextRunTime;
import org.omg.IOP.ServiceContext;
import org.omg.SendingContext.CodeBase;
import org.omg.SendingContext.CodeBaseHelper;

import static org.apache.yoko.util.MinorCodes.MinorInvalidContextID;
import static org.apache.yoko.util.MinorCodes.describeBadParam;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

public class CodeBaseProxy extends LocalObject implements CodeBase {
    private final org.omg.CORBA.Object codebaseObj;
    private volatile CodeBase codebase;

    CodeBaseProxy(ORBInstance orb, ServiceContext ctx) {
        if (null == ctx || ctx.context_id != SendingContextRunTime.value) {
            throw new BAD_PARAM(describeBadParam(MinorInvalidContextID), MinorInvalidContextID, COMPLETED_NO);
        }
        final InputStream in = new InputStream(ctx.context_data);
        in._OB_ORBInstance(orb);
        in._OB_readEndian();
        codebaseObj = in.read_Object();
    }

    @Override
    public String implementation(String arg0) {
        return getCodeBase().implementation(arg0);
    }

    @Override
    public String[] implementations(String[] arg0) {
        return getCodeBase().implementations(arg0);
    }

    @Override
    public String[] bases(String arg0) {
        return getCodeBase().bases(arg0);
    }

    @Override
    public Repository get_ir() {
        return getCodeBase().get_ir();
    }

    @Override
    public FullValueDescription meta(String arg0) {
        return getCodeBase().meta(arg0);
    }

    @Override
    public FullValueDescription[] metas(String arg0) {
        return getCodeBase().metas(arg0);
    }
    
    public CodeBase getCodeBase() {
        CodeBase result = codebase;
        if (null != result) { return result; }
        synchronized (codebaseObj) {
            result = codebase;
            if (null != result) return result;
            try {
                codebase = result = CodeBaseHelper.narrow(codebaseObj);
            } catch (BAD_PARAM ignored) {}
        }
        return result;
    }
}
