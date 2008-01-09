/**
*
* Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.apache.yoko.rmi.impl;

class CharDescriptor extends SimpleDescriptor {
    CharDescriptor(TypeRepository repository) {
        super(Character.TYPE, repository, "wchar",
                org.omg.CORBA.TCKind.tk_wchar);
    }

    public Object read(org.omg.CORBA.portable.InputStream in) {
        return new Character(in.read_wchar());
    }

    public void write(org.omg.CORBA.portable.OutputStream out, Object val) {
        out.write_wchar(((Character) val).charValue());
    }

}
