/*
 * Copyright 2019 IBM Corporation and others.
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

import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Repository;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.SendingContext.CodeBasePOA;

import java.util.ArrayList;
import java.util.List;

class RunTimeCodeBaseImpl extends CodeBasePOA {
    private final ValueHandlerImpl valueHandler;

    RunTimeCodeBaseImpl(ValueHandlerImpl handler) { valueHandler = handler; }
    public String implementation(String id) { return valueHandler.getImplementation(id); }
    public String[] implementations(String[] ids) { return valueHandler.getImplementations(ids); }
    public String[] bases(String id) { return valueHandler.getBases(id); }
    public Repository get_ir() { throw new NO_IMPLEMENT(); }
    public FullValueDescription meta(String id) { return valueHandler.meta(id); }

    public FullValueDescription[] metas(String id) {
        final String[] bases = bases(id);
        List<FullValueDescription> result = new ArrayList<>(bases.length);
        for (String base: bases) result.add(meta(base));
        return result.toArray(new FullValueDescription[0]);
    }
}
