/*
 * Copyright 2022 IBM Corporation and others.
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
package org.omg.CORBA;

import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.IDLEntity;

@SuppressWarnings("unused")
public abstract class TypeCode implements IDLEntity {
    public abstract boolean equal(TypeCode tc);
    public abstract boolean equivalent(TypeCode tc);
    public abstract TypeCode get_compact_typecode();
    public abstract TCKind kind();
    public abstract String id() throws BadKind;
    public abstract String name() throws BadKind;
    public abstract int member_count() throws BadKind;
    public abstract String member_name(int index) throws BadKind, Bounds;
    public abstract TypeCode member_type(int index) throws BadKind, Bounds;
    public abstract Any member_label(int index) throws BadKind, Bounds;
    public abstract TypeCode discriminator_type() throws BadKind;
    public abstract int default_index() throws BadKind;
    public abstract int length() throws BadKind;
    public abstract TypeCode content_type() throws BadKind;

    public short fixed_digits() throws BadKind {
        throw new NO_IMPLEMENT();
    }

    public short fixed_scale() throws BadKind {
        throw new NO_IMPLEMENT();
    }

    public short member_visibility(int index) throws BadKind, Bounds {
        throw new NO_IMPLEMENT();
    }

    public short type_modifier() throws BadKind {
        throw new NO_IMPLEMENT();
    }

    public TypeCode concrete_base_type()  throws BadKind {
        throw new NO_IMPLEMENT();
    }
}
