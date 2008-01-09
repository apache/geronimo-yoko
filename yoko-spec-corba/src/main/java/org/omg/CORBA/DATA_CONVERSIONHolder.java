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
package org.omg.CORBA;

final public class DATA_CONVERSIONHolder implements
        org.omg.CORBA.portable.Streamable {
    public DATA_CONVERSION value;

    public DATA_CONVERSIONHolder() {
    }

    public DATA_CONVERSIONHolder(DATA_CONVERSION initial) {
        value = initial;
    }

    public void _read(org.omg.CORBA.portable.InputStream in) {
        value = DATA_CONVERSIONHelper.read(in);
    }

    public void _write(org.omg.CORBA.portable.OutputStream out) {
        DATA_CONVERSIONHelper.write(out, value);
    }

    public TypeCode _type() {
        return DATA_CONVERSIONHelper.type();
    }
}
