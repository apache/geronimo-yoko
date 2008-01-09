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

public abstract class TypeCode implements org.omg.CORBA.portable.IDLEntity {
    public abstract boolean equal(TypeCode tc);

    public abstract boolean equivalent(TypeCode tc);

    public abstract TypeCode get_compact_typecode();

    public abstract TCKind kind();

    public abstract String id() throws org.omg.CORBA.TypeCodePackage.BadKind;

    public abstract String name() throws org.omg.CORBA.TypeCodePackage.BadKind;

    public abstract int member_count()
            throws org.omg.CORBA.TypeCodePackage.BadKind;

    public abstract String member_name(int index)
            throws org.omg.CORBA.TypeCodePackage.BadKind,
            org.omg.CORBA.TypeCodePackage.Bounds;

    public abstract TypeCode member_type(int index)
            throws org.omg.CORBA.TypeCodePackage.BadKind,
            org.omg.CORBA.TypeCodePackage.Bounds;

    public abstract Any member_label(int index)
            throws org.omg.CORBA.TypeCodePackage.BadKind,
            org.omg.CORBA.TypeCodePackage.Bounds;

    public abstract TypeCode discriminator_type()
            throws org.omg.CORBA.TypeCodePackage.BadKind;

    public abstract int default_index()
            throws org.omg.CORBA.TypeCodePackage.BadKind;

    public abstract int length() throws org.omg.CORBA.TypeCodePackage.BadKind;

    public abstract TypeCode content_type()
            throws org.omg.CORBA.TypeCodePackage.BadKind;

    public short fixed_digits() throws org.omg.CORBA.TypeCodePackage.BadKind {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public short fixed_scale() throws org.omg.CORBA.TypeCodePackage.BadKind {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public short member_visibility(int index)
            throws org.omg.CORBA.TypeCodePackage.BadKind,
            org.omg.CORBA.TypeCodePackage.Bounds {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public short type_modifier() throws org.omg.CORBA.TypeCodePackage.BadKind {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public TypeCode concrete_base_type()
            throws org.omg.CORBA.TypeCodePackage.BadKind {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
}
