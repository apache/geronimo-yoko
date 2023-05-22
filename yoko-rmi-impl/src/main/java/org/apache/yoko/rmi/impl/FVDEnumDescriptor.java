/*
 * Copyright 2016 IBM Corporation and others.
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
package org.apache.yoko.rmi.impl;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;

class FVDEnumDescriptor extends EnumDescriptor {
    private final FullValueDescription fvd;
    private final String repid;

    FVDEnumDescriptor(FullValueDescription fvd, Class clazz, TypeRepository rep, String repid, ValueDescriptor super_desc) {
        super(clazz, rep);
        this.fvd = fvd;
        this.repid = repid;

        init();

        _super_descriptor = super_desc;
    }

    @Override
    protected String genRepId() {
        return repid;
    }

    @Override
    FullValueDescription getFullValueDescription() {
        return fvd;
    }

    @Override
    protected TypeCode genTypeCode() {
        return fvd.type;
    }

    @Override
    public boolean isCustomMarshalled() {
        return fvd.is_custom;
    }
}
