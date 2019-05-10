/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.omg.SendingContext;

import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.RepositoryHelper;
import org.omg.CORBA.StringSeqHelper;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.SendingContext.CodeBasePackage.URLHelper;
import org.omg.SendingContext.CodeBasePackage.URLSeqHelper;
import org.omg.SendingContext.CodeBasePackage.ValueDescSeqHelper;

public abstract class CodeBasePOA extends Servant implements InvokeHandler, CodeBaseOperations {
    private final String[] ids = {
            "IDL:omg.org/SendingContext/CodeBase:1.0",
            "IDL:omg.org/SendingContext/RunTime:1.0",
            "IDL:omg.org/CORBA/Object:1.0" };

    public CodeBase _this() {
        return CodeBaseHelper.narrow(_this_object());
    }

    public CodeBase _this(ORB orb) {
        return CodeBaseHelper.narrow(_this_object(orb));
    }

    public OutputStream _invoke(String method, InputStream input, ResponseHandler handler) throws SystemException {
        // do something
        final OutputStream out = handler.createReply();
        switch (method) {
        case "get_ir":
            RepositoryHelper.write(out, get_ir());
            return out;
        case "bases":
            StringSeqHelper.write(out, bases(input.read_string()));
            return out;
        case "meta":
            FullValueDescriptionHelper.write(out, meta(input.read_string()));
            return out;
        case "implementations":
            URLSeqHelper.write(out, implementations(StringSeqHelper.read(input)));
            return out;
        case "metas":
            ValueDescSeqHelper.write(out, metas(input.read_string()));
            return out;
        case "implementation":
            URLHelper.write(out, implementation(input.read_string()));
            return out;
        default:
            throw new BAD_OPERATION(method + " not found");
        }
    }

    public String[] _all_interfaces(POA poa, byte[] obj_id) {
        return ids;
    }
}
