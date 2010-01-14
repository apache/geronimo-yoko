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

package org.apache.yoko.orb.DynamicAny;

import org.apache.yoko.orb.CORBA.Any;
import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.CORBA.TypeCode;

abstract class DynSeqBase_impl extends DynAny_impl {
    protected org.omg.CORBA.TypeCode contentType_;

    protected org.omg.CORBA.TCKind contentKind_;

    protected org.omg.DynamicAny.DynAny[] components_;

    protected int index_ = 0;

    protected int length_ = 0;

    protected int max_ = 0;

    protected java.lang.Object buf_;

    protected boolean primitive_;

    protected boolean ignoreChild_;

    org.apache.yoko.orb.DynamicAny.DynValueReader dynValueReader_;

    DynSeqBase_impl(org.omg.DynamicAny.DynAnyFactory factory,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.TypeCode type) {
        this(factory, orbInstance, type, null);
    }

    DynSeqBase_impl(org.omg.DynamicAny.DynAnyFactory factory,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.TypeCode type,
            org.apache.yoko.orb.DynamicAny.DynValueReader dynValueReader) {
        super(factory, orbInstance, type);

        dynValueReader_ = dynValueReader;

        components_ = new org.omg.DynamicAny.DynAny[0];

        try {
            contentType_ = origType_.content_type();

            org.omg.CORBA.TypeCode origContent = TypeCode
                    ._OB_getOrigType(contentType_);
            contentKind_ = origContent.kind();
            length_ = 0;
            index_ = -1;
            buf_ = null;
            ignoreChild_ = false;

            switch (contentKind_.value()) {
            case org.omg.CORBA.TCKind._tk_short:
            case org.omg.CORBA.TCKind._tk_long:
            case org.omg.CORBA.TCKind._tk_ushort:
            case org.omg.CORBA.TCKind._tk_ulong:
            case org.omg.CORBA.TCKind._tk_float:
            case org.omg.CORBA.TCKind._tk_double:
            case org.omg.CORBA.TCKind._tk_boolean:
            case org.omg.CORBA.TCKind._tk_char:
            case org.omg.CORBA.TCKind._tk_octet:
            case org.omg.CORBA.TCKind._tk_string:
            case org.omg.CORBA.TCKind._tk_longlong:
            case org.omg.CORBA.TCKind._tk_ulonglong:
            case org.omg.CORBA.TCKind._tk_wchar:
            case org.omg.CORBA.TCKind._tk_wstring:
                primitive_ = true;
                break;

            default:
                primitive_ = false;
                break;
            }

            //
            // An array must be initialized to its proper length
            //
            max_ = origType_.length();
            if (origType_.kind() == org.omg.CORBA.TCKind.tk_array) {
                resize(max_, true);
                index_ = 0;
            }
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }
    }

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    protected void childModified(org.omg.DynamicAny.DynAny p) {
        if (ignoreChild_) {
            ignoreChild_ = false;
            return;
        }

        //
        // If this object holds a sequence of primitive types, we need
        // to keep the optimized array and the components in sync
        //
        if (primitive_) {
            int i;
            for (i = 0; i < length_; i++)
                if (components_[i] == p)
                    break;
            org.apache.yoko.orb.OB.Assert._OB_assert(i < length_);

            try {
                setValue(i, p);
            } catch (org.omg.DynamicAny.DynAnyPackage.TypeMismatch ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            } catch (org.omg.DynamicAny.DynAnyPackage.InvalidValue ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            }

            notifyParent();
        }
    }

    protected void validate(org.omg.CORBA.TCKind kind)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (kind != contentKind_)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        if (index_ < 0)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        org.apache.yoko.orb.OB.Assert._OB_assert(length_ > 0);
    }

    protected void getValue(int index, org.omg.CORBA.Any a) {
        Any any = (Any) a;

        //
        // Get the value from the primitive array at the given index
        // and insert it into the any
        //

        org.apache.yoko.orb.OB.Assert._OB_assert(index < length_ && primitive_);

        switch (contentKind_.value()) {
        case org.omg.CORBA.TCKind._tk_short: {
            short[] buf = (short[]) buf_;
            any.replace(contentType_, new Integer(buf[index]));
            break;
        }

        case org.omg.CORBA.TCKind._tk_long: {
            int[] buf = (int[]) buf_;
            any.replace(contentType_, new Integer(buf[index]));
            break;
        }

        case org.omg.CORBA.TCKind._tk_ushort: {
            short[] buf = (short[]) buf_;
            any.replace(contentType_, new Integer(buf[index]));
            break;
        }

        case org.omg.CORBA.TCKind._tk_ulong: {
            int[] buf = (int[]) buf_;
            any.replace(contentType_, new Integer(buf[index]));
            break;
        }

        case org.omg.CORBA.TCKind._tk_float: {
            float[] buf = (float[]) buf_;
            any.replace(contentType_, new Float(buf[index]));
            break;
        }

        case org.omg.CORBA.TCKind._tk_double: {
            double[] buf = (double[]) buf_;
            any.replace(contentType_, new Double(buf[index]));
            break;
        }

        case org.omg.CORBA.TCKind._tk_boolean: {
            boolean[] buf = (boolean[]) buf_;
            any.replace(contentType_, Boolean.valueOf(buf[index]));
            break;
        }

        case org.omg.CORBA.TCKind._tk_char: {
            char[] buf = (char[]) buf_;
            any.replace(contentType_, new Character(buf[index]));
            break;
        }

        case org.omg.CORBA.TCKind._tk_octet: {
            byte[] buf = (byte[]) buf_;
            any.replace(contentType_, new Byte(buf[index]));
            break;
        }

        case org.omg.CORBA.TCKind._tk_string: {
            String[] buf = (String[]) buf_;
            any.replace(contentType_, new String(buf[index]));
            break;
        }

        case org.omg.CORBA.TCKind._tk_longlong: {
            long[] buf = (long[]) buf_;
            any.replace(contentType_, new Long(buf[index]));
            break;
        }

        case org.omg.CORBA.TCKind._tk_ulonglong: {
            long[] buf = (long[]) buf_;
            any.replace(contentType_, new Long(buf[index]));
            break;
        }

        case org.omg.CORBA.TCKind._tk_wchar: {
            char[] buf = (char[]) buf_;
            any.replace(contentType_, new Character(buf[index]));
            break;
        }

        case org.omg.CORBA.TCKind._tk_wstring: {
            String[] buf = (String[]) buf_;
            any.replace(contentType_, new String(buf[index]));
            break;
        }

        default:
            org.apache.yoko.orb.OB.Assert._OB_assert("Unsupported sequence type");
        }
    }

    protected void setValue(int index, org.omg.CORBA.Any any)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        //
        // Set the value of the primitive array and/or component from
        // the contents of the any
        //

        org.apache.yoko.orb.OB.Assert._OB_assert(index < length_);

        if (components_[index] != null)
            components_[index].from_any(any);
        else {
            switch (contentKind_.value()) {
            case org.omg.CORBA.TCKind._tk_short: {
                short[] buf = (short[]) buf_;
                buf[index] = any.extract_short();
                break;
            }

            case org.omg.CORBA.TCKind._tk_long: {
                int[] buf = (int[]) buf_;
                buf[index] = any.extract_long();
                break;
            }

            case org.omg.CORBA.TCKind._tk_ushort: {
                short[] buf = (short[]) buf_;
                buf[index] = any.extract_ushort();
                break;
            }

            case org.omg.CORBA.TCKind._tk_ulong: {
                int[] buf = (int[]) buf_;
                buf[index] = any.extract_ulong();
                break;
            }

            case org.omg.CORBA.TCKind._tk_float: {
                float[] buf = (float[]) buf_;
                buf[index] = any.extract_float();
                break;
            }

            case org.omg.CORBA.TCKind._tk_double: {
                double[] buf = (double[]) buf_;
                buf[index] = any.extract_double();
                break;
            }

            case org.omg.CORBA.TCKind._tk_boolean: {
                boolean[] buf = (boolean[]) buf_;
                buf[index] = any.extract_boolean();
                break;
            }

            case org.omg.CORBA.TCKind._tk_char: {
                char[] buf = (char[]) buf_;
                buf[index] = any.extract_char();
                break;
            }

            case org.omg.CORBA.TCKind._tk_octet: {
                byte[] buf = (byte[]) buf_;
                buf[index] = any.extract_octet();
                break;
            }

            case org.omg.CORBA.TCKind._tk_string: {
                String[] buf = (String[]) buf_;
                buf[index] = any.extract_string();
                break;
            }

            case org.omg.CORBA.TCKind._tk_longlong: {
                long[] buf = (long[]) buf_;
                buf[index] = any.extract_longlong();
                break;
            }

            case org.omg.CORBA.TCKind._tk_ulonglong: {
                long[] buf = (long[]) buf_;
                buf[index] = any.extract_ulonglong();
                break;
            }

            case org.omg.CORBA.TCKind._tk_wchar: {
                char[] buf = (char[]) buf_;
                buf[index] = any.extract_wchar();
                break;
            }

            case org.omg.CORBA.TCKind._tk_wstring: {
                String[] buf = (String[]) buf_;
                buf[index] = any.extract_wstring();
                break;
            }

            default:
                org.apache.yoko.orb.OB.Assert._OB_assert("Unsupported sequence type");
            }
        }
    }

    protected void setValue(int index, org.omg.DynamicAny.DynAny p)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        //
        // Set value from a DynAny object. We update our primitive
        // array and our component.
        //

        if (primitive_) {
            switch (contentKind_.value()) {
            case org.omg.CORBA.TCKind._tk_short: {
                short[] buf = (short[]) buf_;
                buf[index] = p.get_short();
                break;
            }

            case org.omg.CORBA.TCKind._tk_long: {
                int[] buf = (int[]) buf_;
                buf[index] = p.get_long();
                break;
            }

            case org.omg.CORBA.TCKind._tk_ushort: {
                short[] buf = (short[]) buf_;
                buf[index] = p.get_ushort();
                break;
            }

            case org.omg.CORBA.TCKind._tk_ulong: {
                int[] buf = (int[]) buf_;
                buf[index] = p.get_ulong();
                break;
            }

            case org.omg.CORBA.TCKind._tk_float: {
                float[] buf = (float[]) buf_;
                buf[index] = p.get_float();
                break;
            }

            case org.omg.CORBA.TCKind._tk_double: {
                double[] buf = (double[]) buf_;
                buf[index] = p.get_double();
                break;
            }

            case org.omg.CORBA.TCKind._tk_boolean: {
                boolean[] buf = (boolean[]) buf_;
                buf[index] = p.get_boolean();
                break;
            }

            case org.omg.CORBA.TCKind._tk_char: {
                char[] buf = (char[]) buf_;
                buf[index] = p.get_char();
                break;
            }

            case org.omg.CORBA.TCKind._tk_octet: {
                byte[] buf = (byte[]) buf_;
                buf[index] = p.get_octet();
                break;
            }

            case org.omg.CORBA.TCKind._tk_string: {
                String[] buf = (String[]) buf_;
                buf[index] = p.get_string();
                break;
            }

            case org.omg.CORBA.TCKind._tk_longlong: {
                long[] buf = (long[]) buf_;
                buf[index] = p.get_longlong();
                break;
            }

            case org.omg.CORBA.TCKind._tk_ulonglong: {
                long[] buf = (long[]) buf_;
                buf[index] = p.get_ulonglong();
                break;
            }

            case org.omg.CORBA.TCKind._tk_wchar: {
                char[] buf = (char[]) buf_;
                buf[index] = p.get_wchar();
                break;
            }

            case org.omg.CORBA.TCKind._tk_wstring: {
                String[] buf = (String[]) buf_;
                buf[index] = p.get_wstring();
                break;
            }

            default:
                org.apache.yoko.orb.OB.Assert._OB_assert("Unsupported sequence type");
            }
        }

        if (components_[index] != null && components_[index] != p) {
            ignoreChild_ = true;
            components_[index].assign(p);
        }
    }

    private void updateComponent(int index, boolean createComp) {
        //
        // Update the component with the value from the primitive array.
        // If desired, the component will be created first.
        //

        if (components_[index] == null) {
            if (createComp)
                components_[index] = create(contentType_, true);
            else
                return;
        }

        Any any = new Any(orbInstance_);
        getValue(index, any);
        ignoreChild_ = true;

        try {
            components_[index].from_any(any);
        } catch (org.omg.DynamicAny.DynAnyPackage.TypeMismatch ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.DynamicAny.DynAnyPackage.InvalidValue ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }
    }

    protected void resize(int len, boolean init) {
        if (primitive_ && len > length_) {
            switch (contentKind_.value()) {
            case org.omg.CORBA.TCKind._tk_short:
            case org.omg.CORBA.TCKind._tk_ushort: {
                short[] oldBuf = (short[]) buf_;
                if (len > 0) {
                    short[] buf = new short[len];
                    if (init && oldBuf != null)
                        System.arraycopy(oldBuf, 0, buf, 0, length_);
                    buf_ = buf;
                } else
                    buf_ = null;
                break;
            }

            case org.omg.CORBA.TCKind._tk_long:
            case org.omg.CORBA.TCKind._tk_ulong: {
                int[] oldBuf = (int[]) buf_;
                if (len > 0) {
                    int[] buf = new int[len];
                    if (init && oldBuf != null)
                        System.arraycopy(oldBuf, 0, buf, 0, length_);
                    buf_ = buf;
                } else
                    buf_ = null;
                break;
            }

            case org.omg.CORBA.TCKind._tk_float: {
                float[] oldBuf = (float[]) buf_;
                if (len > 0) {
                    float[] buf = new float[len];
                    if (init && oldBuf != null)
                        System.arraycopy(oldBuf, 0, buf, 0, length_);
                    buf_ = buf;
                } else
                    buf_ = null;
                break;
            }

            case org.omg.CORBA.TCKind._tk_double: {
                double[] oldBuf = (double[]) buf_;
                if (len > 0) {
                    double[] buf = new double[len];
                    if (init && oldBuf != null)
                        System.arraycopy(oldBuf, 0, buf, 0, length_);
                    buf_ = buf;
                } else
                    buf_ = null;
                break;
            }

            case org.omg.CORBA.TCKind._tk_boolean: {
                boolean[] oldBuf = (boolean[]) buf_;
                if (len > 0) {
                    boolean[] buf = new boolean[len];
                    if (init && oldBuf != null)
                        System.arraycopy(oldBuf, 0, buf, 0, length_);
                    buf_ = buf;
                } else
                    buf_ = null;
                break;
            }

            case org.omg.CORBA.TCKind._tk_char:
            case org.omg.CORBA.TCKind._tk_wchar: {
                char[] oldBuf = (char[]) buf_;
                if (len > 0) {
                    char[] buf = new char[len];
                    if (init && oldBuf != null)
                        System.arraycopy(oldBuf, 0, buf, 0, length_);
                    buf_ = buf;
                } else
                    buf_ = null;
                break;
            }

            case org.omg.CORBA.TCKind._tk_octet: {
                byte[] oldBuf = (byte[]) buf_;
                if (len > 0) {
                    byte[] buf = new byte[len];
                    if (init && oldBuf != null)
                        System.arraycopy(oldBuf, 0, buf, 0, length_);
                    buf_ = buf;
                } else
                    buf_ = null;
                break;
            }

            case org.omg.CORBA.TCKind._tk_string:
            case org.omg.CORBA.TCKind._tk_wstring: {
                String[] oldBuf = (String[]) buf_;
                if (len > 0) {
                    String[] buf = new String[len];
                    if (init) {
                        if (oldBuf != null) {
                            System.arraycopy(oldBuf, 0, buf, 0, length_);
                            for (int i = length_; i < len; i++)
                                buf[i] = "";
                        } else {
                            for (int i = 0; i < len; i++)
                                buf[i] = "";
                        }
                    }
                    buf_ = buf;
                } else
                    buf_ = null;
                break;
            }

            case org.omg.CORBA.TCKind._tk_longlong:
            case org.omg.CORBA.TCKind._tk_ulonglong: {
                long[] oldBuf = (long[]) buf_;
                if (len > 0) {
                    long[] buf = new long[len];
                    if (init && oldBuf != null)
                        System.arraycopy(oldBuf, 0, buf, 0, length_);
                    buf_ = buf;
                } else
                    buf_ = null;
                break;
            }

            default:
                org.apache.yoko.orb.OB.Assert._OB_assert("Unsupported sequence type");
            }
        }

        //
        // Update the components_ sequence. Although we use a separate
        // array for primitive types, we still keep the length of the
        // components_ sequence consistent. New elements are initialized
        // to nil for primitive types.
        //

        if (len > length_) // increase length
        {
            org.omg.DynamicAny.DynAny[] components = new org.omg.DynamicAny.DynAny[len];
            System.arraycopy(components_, 0, components, 0, length_);

            if ((contentKind_.value() == org.omg.CORBA.TCKind._tk_value)
                    && (dynValueReader_ != null)) {
                for (int i = length_; i < len; i++)
                    components[i] = null;
            } else {
                for (int i = length_; i < len; i++) {
                    if (!primitive_)
                        components[i] = prepare(contentType_, dynValueReader_,
                                true);
                    else
                        components[i] = null;
                }
            }

            components_ = components;
        } else if (len < length_) // decrease length
        {
            org.omg.DynamicAny.DynAny[] components = new org.omg.DynamicAny.DynAny[len];
            System.arraycopy(components_, 0, components, 0, len);
            components_ = components;
        }

        //
        // Update the current position, if necessary
        //

        if (len > length_) // length was increased
        {
            if (index_ == -1)
                index_ = length_; // first newly-added element
        } else if (len < length_) // length was decreased
        {
            if (len == 0 || index_ >= len)
                index_ = -1;
        }

        length_ = len;
    }

    protected org.omg.CORBA.Any[] getElements() {
        org.omg.CORBA.Any[] result = new org.omg.CORBA.Any[length_];
        for (int i = 0; i < length_; i++) {
            if (primitive_) {
                result[i] = new Any(orbInstance_);
                getValue(i, result[i]);
            } else {
                result[i] = components_[i].to_any();
            }
        }

        return result;
    }

    protected org.omg.DynamicAny.DynAny[] getElementsAsDynAny() {
        if (primitive_) {
            for (int i = 0; i < length_; i++) {
                if (components_[i] == null)
                    updateComponent(i, true);
            }
        }

        org.omg.DynamicAny.DynAny[] result = new org.omg.DynamicAny.DynAny[length_];
        System.arraycopy(components_, 0, result, 0, length_);
        return result;
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public synchronized void assign(org.omg.DynamicAny.DynAny dyn_any)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        if (this == dyn_any)
            return;

        if (!dyn_any.type().equivalent(type_))
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        DynAny_impl impl = (DynAny_impl) dyn_any;
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
        OutputStream out = new OutputStream(buf);
        out._OB_ORBInstance(orbInstance_);
        impl._OB_marshal(out);
        org.omg.CORBA.portable.InputStream in = out.create_input_stream();
        _OB_unmarshal((InputStream) in);

        notifyParent();
    }

    public synchronized void from_any(org.omg.CORBA.Any value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        //
        // Convert value to an ORBacus Any - the JDK implementation
        // of TypeCode.equivalent() raises NO_IMPLEMENT
        //
        Any val = null;
        try {
            val = (Any) value;
        } catch (ClassCastException ex) {
            try {
                val = new Any(value);
            } catch (NullPointerException e) {
                throw (org.omg.DynamicAny.DynAnyPackage.InvalidValue)new 
                    org.omg.DynamicAny.DynAnyPackage.InvalidValue().initCause(e);
            }
        }

        if (!val._OB_type().equivalent(type_))
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        if (val.value() == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        org.omg.CORBA.portable.InputStream in = val.create_input_stream();
        _OB_unmarshal((InputStream) in);

        index_ = 0;

        notifyParent();
    }

    public synchronized org.omg.CORBA.Any to_any() {
        return to_any(null);
    }

    public synchronized org.omg.CORBA.Any to_any(DynValueWriter dynValueWriter) {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
        OutputStream out = new OutputStream(buf);
        out._OB_ORBInstance(orbInstance_);

        if (dynValueWriter != null)
            _OB_marshal(out, dynValueWriter);
        else
            _OB_marshal(out);

        org.omg.CORBA.portable.InputStream in = out.create_input_stream();
        Any result = new Any(orbInstance_, type_, in);
        return result;
    }

    public synchronized boolean equal(org.omg.DynamicAny.DynAny dyn_any) {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        if (this == dyn_any)
            return true;

        if (!dyn_any.type().equivalent(type_))
            return false;

        if (length_ != dyn_any.component_count())
            return false;

        if (length_ == 0)
            return true;

        DynSeqBase_impl seq = (DynSeqBase_impl) dyn_any;

        if (primitive_) {
            switch (contentKind_.value()) {
            case org.omg.CORBA.TCKind._tk_short:
            case org.omg.CORBA.TCKind._tk_ushort: {
                short[] buf1 = (short[]) buf_;
                short[] buf2 = (short[]) seq.buf_;
                for (int i = 0; i < length_; i++)
                    if (buf1[i] != buf2[i])
                        return false;
                break;
            }

            case org.omg.CORBA.TCKind._tk_long:
            case org.omg.CORBA.TCKind._tk_ulong: {
                int[] buf1 = (int[]) buf_;
                int[] buf2 = (int[]) seq.buf_;
                for (int i = 0; i < length_; i++)
                    if (buf1[i] != buf2[i])
                        return false;
                break;
            }

            case org.omg.CORBA.TCKind._tk_float: {
                float[] buf1 = (float[]) buf_;
                float[] buf2 = (float[]) seq.buf_;
                for (int i = 0; i < length_; i++)
                    if (buf1[i] != buf2[i])
                        return false;
                break;
            }

            case org.omg.CORBA.TCKind._tk_double: {
                double[] buf1 = (double[]) buf_;
                double[] buf2 = (double[]) seq.buf_;
                for (int i = 0; i < length_; i++)
                    if (buf1[i] != buf2[i])
                        return false;
                break;
            }

            case org.omg.CORBA.TCKind._tk_boolean: {
                boolean[] buf1 = (boolean[]) buf_;
                boolean[] buf2 = (boolean[]) seq.buf_;
                for (int i = 0; i < length_; i++)
                    if (buf1[i] != buf2[i])
                        return false;
                break;
            }

            case org.omg.CORBA.TCKind._tk_char:
            case org.omg.CORBA.TCKind._tk_wchar: {
                char[] buf1 = (char[]) buf_;
                char[] buf2 = (char[]) seq.buf_;
                for (int i = 0; i < length_; i++)
                    if (buf1[i] != buf2[i])
                        return false;
                break;
            }

            case org.omg.CORBA.TCKind._tk_octet: {
                byte[] buf1 = (byte[]) buf_;
                byte[] buf2 = (byte[]) seq.buf_;
                for (int i = 0; i < length_; i++)
                    if (buf1[i] != buf2[i])
                        return false;
                break;
            }

            case org.omg.CORBA.TCKind._tk_string:
            case org.omg.CORBA.TCKind._tk_wstring: {
                String[] buf1 = (String[]) buf_;
                String[] buf2 = (String[]) seq.buf_;
                for (int i = 0; i < length_; i++)
                    if (!buf1[i].equals(buf2[i]))
                        return false;
                break;
            }

            case org.omg.CORBA.TCKind._tk_longlong:
            case org.omg.CORBA.TCKind._tk_ulonglong: {
                long[] buf1 = (long[]) buf_;
                long[] buf2 = (long[]) seq.buf_;
                for (int i = 0; i < length_; i++)
                    if (buf1[i] != buf2[i])
                        return false;
                break;
            }

            default:
                org.apache.yoko.orb.OB.Assert._OB_assert("Unsupported sequence type");
            }
        } else {
            for (int i = 0; i < length_; i++)
                if (!components_[i].equal(seq.components_[i]))
                    return false;
        }

        return true;
    }

    public synchronized org.omg.DynamicAny.DynAny copy() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        DynValueWriter dynValueWriter = new DynValueWriter(orbInstance_,
                factory_);

        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
        OutputStream out = new OutputStream(buf);
        out._OB_ORBInstance(orbInstance_);
        _OB_marshal(out, dynValueWriter);

        dynValueReader_ = dynValueWriter.getReader();

        org.omg.DynamicAny.DynAny result = prepare(type_, dynValueReader_,
                false);

        DynSeqBase_impl seq = (DynSeqBase_impl) result;

        org.omg.CORBA.portable.InputStream in = out.create_input_stream();
        seq._OB_unmarshal((InputStream) in);

        return result;
    }

    public synchronized boolean seek(int index) {
        if (index >= length_ || index < 0) {
            index_ = -1;
            return false;
        }

        index_ = index;
        return true;
    }

    public synchronized void rewind() {
        seek(0);
    }

    public synchronized boolean next() {
        index_++;

        if (index_ == length_) {
            index_ = -1;
            return false;
        }

        return true;
    }

    public synchronized int component_count() {
        return length_;
    }

    public synchronized org.omg.DynamicAny.DynAny current_component()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        if (index_ < 0)
            return null;

        //
        // Create component if necessary
        //
        if (components_[index_] == null)
            updateComponent(index_, true);

        return components_[index_];
    }

    final public synchronized void insert_boolean(boolean value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_boolean);

        boolean[] buf = (boolean[]) buf_;
        buf[index_] = value;
        updateComponent(index_, false);
        notifyParent();
    }

    final public synchronized void insert_octet(byte value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_octet);

        byte[] buf = (byte[]) buf_;
        buf[index_] = value;
        updateComponent(index_, false);
        notifyParent();
    }

    final public synchronized void insert_char(char value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_char);

        char[] buf = (char[]) buf_;
        buf[index_] = value;
        updateComponent(index_, false);
        notifyParent();
    }

    final public synchronized void insert_short(short value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_short);

        short[] buf = (short[]) buf_;
        buf[index_] = value;
        updateComponent(index_, false);
        notifyParent();
    }

    final public synchronized void insert_ushort(short value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_ushort);

        short[] buf = (short[]) buf_;
        buf[index_] = value;
        updateComponent(index_, false);
        notifyParent();
    }

    final public synchronized void insert_long(int value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_long);

        int[] buf = (int[]) buf_;
        buf[index_] = value;
        updateComponent(index_, false);
        notifyParent();
    }

    final public synchronized void insert_ulong(int value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_ulong);

        int[] buf = (int[]) buf_;
        buf[index_] = value;
        updateComponent(index_, false);
        notifyParent();
    }

    final public synchronized void insert_float(float value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_float);

        float[] buf = (float[]) buf_;
        buf[index_] = value;
        updateComponent(index_, false);
        notifyParent();
    }

    final public synchronized void insert_double(double value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_double);

        double[] buf = (double[]) buf_;
        buf[index_] = value;
        updateComponent(index_, false);
        notifyParent();
    }

    final public synchronized void insert_string(String value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (value == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        validate(org.omg.CORBA.TCKind.tk_string);

        org.omg.CORBA.TypeCode origContent = TypeCode
                ._OB_getOrigType(contentType_);

        //
        // Check for bounded string
        //
        try {
            int len = origContent.length();
            if (len > 0 && value.length() > len)
                throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        String[] buf = (String[]) buf_;
        buf[index_] = value;
        updateComponent(index_, false);
        notifyParent();
    }

    final public synchronized void insert_reference(org.omg.CORBA.Object value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_objref);

        //
        // Delegate to component
        //
        components_[index_].insert_reference(value);

        notifyParent();
    }

    final public synchronized void insert_typecode(org.omg.CORBA.TypeCode value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_TypeCode);

        //
        // Delegate to component
        //
        components_[index_].insert_typecode(value);

        notifyParent();
    }

    final public synchronized void insert_longlong(long value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_longlong);

        long[] buf = (long[]) buf_;
        buf[index_] = value;
        updateComponent(index_, false);
        notifyParent();
    }

    final public synchronized void insert_ulonglong(long value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_ulonglong);

        long[] buf = (long[]) buf_;
        buf[index_] = value;
        updateComponent(index_, false);
        notifyParent();
    }

    final public synchronized void insert_wchar(char value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_wchar);

        char[] buf = (char[]) buf_;
        buf[index_] = value;
        updateComponent(index_, false);
        notifyParent();
    }

    final public synchronized void insert_wstring(String value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (value == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        validate(org.omg.CORBA.TCKind.tk_wstring);

        org.omg.CORBA.TypeCode origContent = TypeCode
                ._OB_getOrigType(contentType_);

        //
        // Check for bounded wstring
        //
        try {
            int len = origContent.length();
            if (len > 0 && value.length() > len)
                throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        String[] buf = (String[]) buf_;
        buf[index_] = value;
        updateComponent(index_, false);
        notifyParent();
    }

    final public synchronized void insert_any(org.omg.CORBA.Any value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_any);

        //
        // Delegate to component
        //
        components_[index_].insert_any(value);

        notifyParent();
    }

    final public synchronized void insert_dyn_any(
            org.omg.DynamicAny.DynAny value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (value == null)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        validate(org.omg.CORBA.TCKind.tk_any);

        //
        // Delegate to component
        //
        components_[index_].insert_dyn_any(value);

        notifyParent();
    }

    final public synchronized void insert_val(java.io.Serializable value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (contentKind_ != org.omg.CORBA.TCKind.tk_value
                && contentKind_ != org.omg.CORBA.TCKind.tk_value_box)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        if (index_ < 0)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        //
        // Delegate to component
        //
        components_[index_].insert_val(value);

        notifyParent();
    }

    final public synchronized boolean get_boolean()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_boolean);

        boolean[] buf = (boolean[]) buf_;
        return buf[index_];
    }

    final public synchronized byte get_octet()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_octet);

        byte[] buf = (byte[]) buf_;
        return buf[index_];
    }

    final public synchronized char get_char()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_char);

        char[] buf = (char[]) buf_;
        return buf[index_];
    }

    final public synchronized short get_short()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_short);

        short[] buf = (short[]) buf_;
        return buf[index_];
    }

    final public synchronized short get_ushort()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_ushort);

        short[] buf = (short[]) buf_;
        return buf[index_];
    }

    final public synchronized int get_long()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_long);

        int[] buf = (int[]) buf_;
        return buf[index_];
    }

    final public synchronized int get_ulong()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_ulong);

        int[] buf = (int[]) buf_;
        return buf[index_];
    }

    final public synchronized float get_float()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_float);

        float[] buf = (float[]) buf_;
        return buf[index_];
    }

    final public synchronized double get_double()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_double);

        double[] buf = (double[]) buf_;
        return buf[index_];
    }

    final public synchronized String get_string()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_string);

        String[] buf = (String[]) buf_;
        return buf[index_];
    }

    final public synchronized org.omg.CORBA.Object get_reference()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_objref);

        //
        // Delegate to component
        //
        return components_[index_].get_reference();
    }

    final public synchronized org.omg.CORBA.TypeCode get_typecode()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_TypeCode);

        //
        // Delegate to component
        //
        return components_[index_].get_typecode();
    }

    final public synchronized long get_longlong()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_longlong);

        long[] buf = (long[]) buf_;
        return buf[index_];
    }

    final public synchronized long get_ulonglong()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_ulonglong);

        long[] buf = (long[]) buf_;
        return buf[index_];
    }

    final public synchronized char get_wchar()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_wchar);

        char[] buf = (char[]) buf_;
        return buf[index_];
    }

    final public synchronized String get_wstring()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_wstring);

        String[] buf = (String[]) buf_;
        return buf[index_];
    }

    final public synchronized org.omg.CORBA.Any get_any()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_any);

        //
        // Delegate to component
        //
        return components_[index_].get_any();
    }

    final public synchronized org.omg.DynamicAny.DynAny get_dyn_any()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        validate(org.omg.CORBA.TCKind.tk_any);

        //
        // Delegate to component
        //
        return components_[index_].get_dyn_any();
    }

    final public synchronized java.io.Serializable get_val()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (contentKind_ != org.omg.CORBA.TCKind.tk_value
                && contentKind_ != org.omg.CORBA.TCKind.tk_value_box)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        if (index_ < 0)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        //
        // Delegate to component
        //
        return components_[index_].get_val();
    }

    // ------------------------------------------------------------------
    // Internal member implementations
    // ------------------------------------------------------------------

    synchronized void _OB_marshal(OutputStream out) {
        _OB_marshal(out, new DynValueWriter(orbInstance_, factory_));
    }

    synchronized void _OB_marshal(OutputStream out,
            DynValueWriter dynValueWriter) {
        if (origType_.kind() == org.omg.CORBA.TCKind.tk_sequence) {
            out.write_ulong(length_);

            if (length_ == 0)
                return;
        }

        switch (contentKind_.value()) {
        case org.omg.CORBA.TCKind._tk_short:
            out.write_short_array((short[]) buf_, 0, length_);
            break;

        case org.omg.CORBA.TCKind._tk_long:
            out.write_long_array((int[]) buf_, 0, length_);
            break;

        case org.omg.CORBA.TCKind._tk_ushort:
            out.write_ushort_array((short[]) buf_, 0, length_);
            break;

        case org.omg.CORBA.TCKind._tk_ulong:
            out.write_ulong_array((int[]) buf_, 0, length_);
            break;

        case org.omg.CORBA.TCKind._tk_float:
            out.write_float_array((float[]) buf_, 0, length_);
            break;

        case org.omg.CORBA.TCKind._tk_double:
            out.write_double_array((double[]) buf_, 0, length_);
            break;

        case org.omg.CORBA.TCKind._tk_boolean:
            out.write_boolean_array((boolean[]) buf_, 0, length_);
            break;

        case org.omg.CORBA.TCKind._tk_char:
            out.write_char_array((char[]) buf_, 0, length_);
            break;

        case org.omg.CORBA.TCKind._tk_octet:
            out.write_octet_array((byte[]) buf_, 0, length_);
            break;

        case org.omg.CORBA.TCKind._tk_string: {
            String[] buf = (String[]) buf_;
            for (int i = 0; i < length_; i++)
                out.write_string(buf[i]);
            break;
        }

        case org.omg.CORBA.TCKind._tk_longlong:
            out.write_longlong_array((long[]) buf_, 0, length_);
            break;

        case org.omg.CORBA.TCKind._tk_ulonglong:
            out.write_ulonglong_array((long[]) buf_, 0, length_);
            break;

        case org.omg.CORBA.TCKind._tk_wchar:
            out.write_wchar_array((char[]) buf_, 0, length_);
            break;

        case org.omg.CORBA.TCKind._tk_wstring: {
            String[] buf = (String[]) buf_;
            for (int i = 0; i < length_; i++)
                out.write_wstring(buf[i]);
            break;
        }

        default: {
            for (int i = 0; i < length_; i++) {
                DynAny_impl impl = (DynAny_impl) components_[i];
                impl._OB_marshal(out, dynValueWriter);
            }
        }
        }
    }

    synchronized void _OB_unmarshal(InputStream in) {
        int len;
        if (origType_.kind() == org.omg.CORBA.TCKind.tk_array)
            len = length_;
        else {
            len = in.read_ulong();
            resize(len, false);

            if (len == 0)
                return;
        }

        switch (contentKind_.value()) {
        case org.omg.CORBA.TCKind._tk_short: {
            short[] buf = (short[]) buf_;
            in.read_short_array(buf, 0, len);
            break;
        }

        case org.omg.CORBA.TCKind._tk_long: {
            int[] buf = (int[]) buf_;
            in.read_long_array(buf, 0, len);
            break;
        }

        case org.omg.CORBA.TCKind._tk_ushort: {
            short[] buf = (short[]) buf_;
            in.read_ushort_array(buf, 0, len);
            break;
        }

        case org.omg.CORBA.TCKind._tk_ulong: {
            int[] buf = (int[]) buf_;
            in.read_ulong_array(buf, 0, len);
            break;
        }

        case org.omg.CORBA.TCKind._tk_float: {
            float[] buf = (float[]) buf_;
            in.read_float_array(buf, 0, len);
            break;
        }

        case org.omg.CORBA.TCKind._tk_double: {
            double[] buf = (double[]) buf_;
            in.read_double_array(buf, 0, len);
            break;
        }

        case org.omg.CORBA.TCKind._tk_boolean: {
            boolean[] buf = (boolean[]) buf_;
            in.read_boolean_array(buf, 0, len);
            break;
        }

        case org.omg.CORBA.TCKind._tk_char: {
            char[] buf = (char[]) buf_;
            in.read_char_array(buf, 0, len);
            break;
        }

        case org.omg.CORBA.TCKind._tk_octet: {
            byte[] buf = (byte[]) buf_;
            in.read_octet_array(buf, 0, len);
            break;
        }

        case org.omg.CORBA.TCKind._tk_string: {
            String[] buf = (String[]) buf_;
            for (int i = 0; i < len; i++)
                buf[i] = in.read_string();
            break;
        }

        case org.omg.CORBA.TCKind._tk_longlong: {
            long[] buf = (long[]) buf_;
            in.read_longlong_array(buf, 0, len);
            break;
        }

        case org.omg.CORBA.TCKind._tk_ulonglong: {
            long[] buf = (long[]) buf_;
            in.read_ulonglong_array(buf, 0, len);
            break;
        }

        case org.omg.CORBA.TCKind._tk_wchar: {
            char[] buf = (char[]) buf_;
            in.read_wchar_array(buf, 0, len);
            break;
        }

        case org.omg.CORBA.TCKind._tk_wstring: {
            String[] buf = (String[]) buf_;
            for (int i = 0; i < len; i++)
                buf[i] = in.read_wstring();
            break;
        }

        case org.omg.CORBA.TCKind._tk_value: {
            if (dynValueReader_ != null) {
                for (int i = 0; i < len; i++) {
                    org.apache.yoko.orb.OB.Assert
                            ._OB_assert(components_[i] == null);

                    try {
                        components_[i] = dynValueReader_.readValue(in,
                                contentType_);
                    } catch (org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode ex) {
                        org.apache.yoko.orb.OB.Assert._OB_assert(ex);
                        return;
                    }

                    adoptChild(components_[i]);
                }
            } else {
                for (int i = 0; i < len; i++) {
                    org.apache.yoko.orb.OB.Assert
                            ._OB_assert(components_[i] != null);
                    DynAny_impl impl = (DynAny_impl) components_[i];
                    impl._OB_unmarshal(in);
                }
            }

            break;
        }

        default: {
            for (int i = 0; i < len; i++) {
                DynAny_impl impl = (DynAny_impl) components_[i];
                impl._OB_unmarshal(in);
            }
        }
        }

        notifyParent();
    }

    synchronized Any _OB_currentAny() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        if (index_ >= 0 && index_ < length_ && !primitive_) {
            DynAny_impl impl = (DynAny_impl) components_[index_];
            return impl._OB_currentAnyValue();
        } else
            return null;
    }

    synchronized Any _OB_currentAnyValue() {
        return null;
    }
}
