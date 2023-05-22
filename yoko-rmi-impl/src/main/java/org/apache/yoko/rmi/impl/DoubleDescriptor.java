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
package org.apache.yoko.rmi.impl;

final class DoubleDescriptor extends SimpleDescriptor {
    DoubleDescriptor(TypeRepository repository) {
        super(Double.TYPE, repository, "double", org.omg.CORBA.TCKind.tk_double);
    }

    public Object read(org.omg.CORBA.portable.InputStream in) {
        return Double.valueOf(in.read_double());
    }

    public void write(org.omg.CORBA.portable.OutputStream out, Object val) {
        out.write_double(((Double) val).doubleValue());
    }

}
