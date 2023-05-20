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
package org.omg.SendingContext;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Repository;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;

public class CodeBasePOATie extends CodeBasePOA {
    private CodeBaseOperations _delegate;
    public CodeBasePOATie(CodeBaseOperations delegate) { _delegate = delegate; }
    public CodeBase _this() { return CodeBaseHelper.narrow(_this_object()); }
    public CodeBase _this(ORB orb) { return CodeBaseHelper.narrow(_this_object(orb)); }
    public CodeBaseOperations _delegate() { return _delegate; }
    public void _delegate(CodeBaseOperations delegate) { _delegate = delegate; }
    public Repository get_ir() { return _delegate.get_ir(); }
    public String[] bases(String id) { return _delegate.bases(id); }
    public FullValueDescription meta(String id) { return _delegate.meta(id); }
    public String[] implementations(String[] ids) { return _delegate.implementations(ids); }
    public FullValueDescription[] metas(String id) { return _delegate.metas(id); }
    public String implementation(String id) { return _delegate.implementation(id); }
}
