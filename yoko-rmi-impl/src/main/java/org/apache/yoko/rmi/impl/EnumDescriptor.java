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

import org.apache.yoko.util.yasf.Yasf;

import java.io.IOException;
import java.io.Serializable;

class EnumDescriptor extends ValueDescriptor {
    public EnumDescriptor(Class<?> type, TypeRepository repo) {
        super(type, repo);
    }

    @Override
    protected final long getSerialVersionUID() {
        return 0L;
    }

    @Override
    protected final boolean isEnum() {
        return true;
    }

    private FieldDescriptor nameField = null;
    private FieldDescriptor ordinalField = null;

    @Override
    public final void init() {
        super.init();
        // Avoid doing anything that would cause the calculated classHash to change
        for (FieldDescriptor f: _fields) {
            if (f.java_name.equals("name")) {
                nameField = f;
            } else if (f.java_name.equals("ordinal")) {
                ordinalField = f;
            }
        }
    }

    @Override
    protected void defaultWriteValue(ObjectWriter writer, Serializable val) throws IOException {
        if ((writer.yasfSet != null) && !!!writer.yasfSet.contains(Yasf.ENUM_FIXED)) {
            // talking to an old yoko that expects an ordinal field to be written;
            ordinalField.write(writer, val);
        }
        nameField.write(writer, val);
    }
}
