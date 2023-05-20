/*
 * Copyright 2023 IBM Corporation and others.
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
import org.omg.IOP.SendingContextRunTime;
import org.omg.IOP.ServiceContext;
import org.omg.SendingContext.CodeBase;
import org.omg.SendingContext.CodeBaseHelper;

import static javax.rmi.CORBA.Util.createValueHandler;

public enum SendingContextRuntimes {
    ;
    public static final CodeBase LOCAL_CODE_BASE = (CodeBase) createValueHandler().getRunTimeCodeBase();
    // TODO we should really generate a new object ref for each connection
    public static final ServiceContext SENDING_CONTEXT_RUNTIME;
    static {
        CodeBase codeBase = LOCAL_CODE_BASE;
        try (OutputStream outCBC = new OutputStream()) {
            outCBC._OB_writeEndian();
            CodeBaseHelper.write(outCBC, codeBase);
            SENDING_CONTEXT_RUNTIME = new ServiceContext();
            SENDING_CONTEXT_RUNTIME.context_id = SendingContextRunTime.value;
            SENDING_CONTEXT_RUNTIME.context_data = outCBC.copyWrittenBytes();
        }
    }
}
