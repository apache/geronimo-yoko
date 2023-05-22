/*
 * Copyright 2010 IBM Corporation and others.
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
package ORBTest_Basic;

//
// IDL:ORBTest_Basic/Intf:1.0
//
public abstract class IntfPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               IntfOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:ORBTest_Basic/Intf:1.0",
    };

    public Intf
    _this()
    {
        return IntfHelper.narrow(super._this_object());
    }

    public Intf
    _this(org.omg.CORBA.ORB orb)
    {
        return IntfHelper.narrow(super._this_object(orb));
    }

    public String[]
    _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectId)
    {
        return _ob_ids_;
    }

    public org.omg.CORBA.portable.OutputStream
    _invoke(String opName,
            org.omg.CORBA.portable.InputStream in,
            org.omg.CORBA.portable.ResponseHandler handler)
    {
        final String[] _ob_names =
        {
            "_get_attrAny",
            "_get_attrBoolean",
            "_get_attrChar",
            "_get_attrDouble",
            "_get_attrFixedArray",
            "_get_attrFixedArrayBoundSequence",
            "_get_attrFixedArraySequence",
            "_get_attrFixedStruct",
            "_get_attrFixedUnion",
            "_get_attrFloat",
            "_get_attrIntf",
            "_get_attrLong",
            "_get_attrOctet",
            "_get_attrShort",
            "_get_attrString",
            "_get_attrStringSequence",
            "_get_attrTestEnum",
            "_get_attrULong",
            "_get_attrUShort",
            "_get_attrVariableArray",
            "_get_attrVariableArrayBoundSequence",
            "_get_attrVariableArraySequence",
            "_get_attrVariableStruct",
            "_get_attrVariableUnion",
            "_set_attrAny",
            "_set_attrBoolean",
            "_set_attrChar",
            "_set_attrDouble",
            "_set_attrFixedArray",
            "_set_attrFixedArrayBoundSequence",
            "_set_attrFixedArraySequence",
            "_set_attrFixedStruct",
            "_set_attrFixedUnion",
            "_set_attrFloat",
            "_set_attrIntf",
            "_set_attrLong",
            "_set_attrOctet",
            "_set_attrShort",
            "_set_attrString",
            "_set_attrStringSequence",
            "_set_attrTestEnum",
            "_set_attrULong",
            "_set_attrUShort",
            "_set_attrVariableArray",
            "_set_attrVariableArrayBoundSequence",
            "_set_attrVariableArraySequence",
            "_set_attrVariableStruct",
            "_set_attrVariableUnion",
            "opAny",
            "opAnyEx",
            "opBoolean",
            "opBooleanEx",
            "opChar",
            "opCharEx",
            "opDouble",
            "opDoubleEx",
            "opExRecursiveStruct",
            "opFixedArray",
            "opFixedArrayBoundSequence",
            "opFixedArrayBoundSequenceEx",
            "opFixedArrayEx",
            "opFixedArraySequence",
            "opFixedArraySequenceEx",
            "opFixedStruct",
            "opFixedStructEx",
            "opFixedUnion",
            "opFixedUnionEx",
            "opFloat",
            "opFloatEx",
            "opIntf",
            "opIntfEx",
            "opLong",
            "opLongEx",
            "opOctet",
            "opOctetEx",
            "opShort",
            "opShortEx",
            "opString",
            "opStringEx",
            "opStringSequence",
            "opStringSequenceEx",
            "opTestEnum",
            "opTestEnumEx",
            "opULong",
            "opULongEx",
            "opUShort",
            "opUShortEx",
            "opVariableArray",
            "opVariableArrayBoundSequence",
            "opVariableArrayBoundSequenceEx",
            "opVariableArrayEx",
            "opVariableArraySequence",
            "opVariableArraySequenceEx",
            "opVariableStruct",
            "opVariableStructEx",
            "opVariableUnion",
            "opVariableUnionEx",
            "opVoid",
            "opVoidEx"
        };

        int _ob_left = 0;
        int _ob_right = _ob_names.length;
        int _ob_index = -1;

        while(_ob_left < _ob_right)
        {
            int _ob_m = (_ob_left + _ob_right) / 2;
            int _ob_res = _ob_names[_ob_m].compareTo(opName);
            if(_ob_res == 0)
            {
                _ob_index = _ob_m;
                break;
            }
            else if(_ob_res > 0)
                _ob_right = _ob_m;
            else
                _ob_left = _ob_m + 1;
        }

        if(_ob_index == -1 && opName.charAt(0) == '_')
        {
            _ob_left = 0;
            _ob_right = _ob_names.length;
            String _ob_ami_op =
                opName.substring(1);

            while(_ob_left < _ob_right)
            {
                int _ob_m = (_ob_left + _ob_right) / 2;
                int _ob_res = _ob_names[_ob_m].compareTo(_ob_ami_op);
                if(_ob_res == 0)
                {
                    _ob_index = _ob_m;
                    break;
                }
                else if(_ob_res > 0)
                    _ob_right = _ob_m;
                else
                    _ob_left = _ob_m + 1;
            }
        }

        switch(_ob_index)
        {
        case 0: // _get_attrAny
            return _OB_att_get_attrAny(in, handler);

        case 1: // _get_attrBoolean
            return _OB_att_get_attrBoolean(in, handler);

        case 2: // _get_attrChar
            return _OB_att_get_attrChar(in, handler);

        case 3: // _get_attrDouble
            return _OB_att_get_attrDouble(in, handler);

        case 4: // _get_attrFixedArray
            return _OB_att_get_attrFixedArray(in, handler);

        case 5: // _get_attrFixedArrayBoundSequence
            return _OB_att_get_attrFixedArrayBoundSequence(in, handler);

        case 6: // _get_attrFixedArraySequence
            return _OB_att_get_attrFixedArraySequence(in, handler);

        case 7: // _get_attrFixedStruct
            return _OB_att_get_attrFixedStruct(in, handler);

        case 8: // _get_attrFixedUnion
            return _OB_att_get_attrFixedUnion(in, handler);

        case 9: // _get_attrFloat
            return _OB_att_get_attrFloat(in, handler);

        case 10: // _get_attrIntf
            return _OB_att_get_attrIntf(in, handler);

        case 11: // _get_attrLong
            return _OB_att_get_attrLong(in, handler);

        case 12: // _get_attrOctet
            return _OB_att_get_attrOctet(in, handler);

        case 13: // _get_attrShort
            return _OB_att_get_attrShort(in, handler);

        case 14: // _get_attrString
            return _OB_att_get_attrString(in, handler);

        case 15: // _get_attrStringSequence
            return _OB_att_get_attrStringSequence(in, handler);

        case 16: // _get_attrTestEnum
            return _OB_att_get_attrTestEnum(in, handler);

        case 17: // _get_attrULong
            return _OB_att_get_attrULong(in, handler);

        case 18: // _get_attrUShort
            return _OB_att_get_attrUShort(in, handler);

        case 19: // _get_attrVariableArray
            return _OB_att_get_attrVariableArray(in, handler);

        case 20: // _get_attrVariableArrayBoundSequence
            return _OB_att_get_attrVariableArrayBoundSequence(in, handler);

        case 21: // _get_attrVariableArraySequence
            return _OB_att_get_attrVariableArraySequence(in, handler);

        case 22: // _get_attrVariableStruct
            return _OB_att_get_attrVariableStruct(in, handler);

        case 23: // _get_attrVariableUnion
            return _OB_att_get_attrVariableUnion(in, handler);

        case 24: // _set_attrAny
            return _OB_att_set_attrAny(in, handler);

        case 25: // _set_attrBoolean
            return _OB_att_set_attrBoolean(in, handler);

        case 26: // _set_attrChar
            return _OB_att_set_attrChar(in, handler);

        case 27: // _set_attrDouble
            return _OB_att_set_attrDouble(in, handler);

        case 28: // _set_attrFixedArray
            return _OB_att_set_attrFixedArray(in, handler);

        case 29: // _set_attrFixedArrayBoundSequence
            return _OB_att_set_attrFixedArrayBoundSequence(in, handler);

        case 30: // _set_attrFixedArraySequence
            return _OB_att_set_attrFixedArraySequence(in, handler);

        case 31: // _set_attrFixedStruct
            return _OB_att_set_attrFixedStruct(in, handler);

        case 32: // _set_attrFixedUnion
            return _OB_att_set_attrFixedUnion(in, handler);

        case 33: // _set_attrFloat
            return _OB_att_set_attrFloat(in, handler);

        case 34: // _set_attrIntf
            return _OB_att_set_attrIntf(in, handler);

        case 35: // _set_attrLong
            return _OB_att_set_attrLong(in, handler);

        case 36: // _set_attrOctet
            return _OB_att_set_attrOctet(in, handler);

        case 37: // _set_attrShort
            return _OB_att_set_attrShort(in, handler);

        case 38: // _set_attrString
            return _OB_att_set_attrString(in, handler);

        case 39: // _set_attrStringSequence
            return _OB_att_set_attrStringSequence(in, handler);

        case 40: // _set_attrTestEnum
            return _OB_att_set_attrTestEnum(in, handler);

        case 41: // _set_attrULong
            return _OB_att_set_attrULong(in, handler);

        case 42: // _set_attrUShort
            return _OB_att_set_attrUShort(in, handler);

        case 43: // _set_attrVariableArray
            return _OB_att_set_attrVariableArray(in, handler);

        case 44: // _set_attrVariableArrayBoundSequence
            return _OB_att_set_attrVariableArrayBoundSequence(in, handler);

        case 45: // _set_attrVariableArraySequence
            return _OB_att_set_attrVariableArraySequence(in, handler);

        case 46: // _set_attrVariableStruct
            return _OB_att_set_attrVariableStruct(in, handler);

        case 47: // _set_attrVariableUnion
            return _OB_att_set_attrVariableUnion(in, handler);

        case 48: // opAny
            return _OB_op_opAny(in, handler);

        case 49: // opAnyEx
            return _OB_op_opAnyEx(in, handler);

        case 50: // opBoolean
            return _OB_op_opBoolean(in, handler);

        case 51: // opBooleanEx
            return _OB_op_opBooleanEx(in, handler);

        case 52: // opChar
            return _OB_op_opChar(in, handler);

        case 53: // opCharEx
            return _OB_op_opCharEx(in, handler);

        case 54: // opDouble
            return _OB_op_opDouble(in, handler);

        case 55: // opDoubleEx
            return _OB_op_opDoubleEx(in, handler);

        case 56: // opExRecursiveStruct
            return _OB_op_opExRecursiveStruct(in, handler);

        case 57: // opFixedArray
            return _OB_op_opFixedArray(in, handler);

        case 58: // opFixedArrayBoundSequence
            return _OB_op_opFixedArrayBoundSequence(in, handler);

        case 59: // opFixedArrayBoundSequenceEx
            return _OB_op_opFixedArrayBoundSequenceEx(in, handler);

        case 60: // opFixedArrayEx
            return _OB_op_opFixedArrayEx(in, handler);

        case 61: // opFixedArraySequence
            return _OB_op_opFixedArraySequence(in, handler);

        case 62: // opFixedArraySequenceEx
            return _OB_op_opFixedArraySequenceEx(in, handler);

        case 63: // opFixedStruct
            return _OB_op_opFixedStruct(in, handler);

        case 64: // opFixedStructEx
            return _OB_op_opFixedStructEx(in, handler);

        case 65: // opFixedUnion
            return _OB_op_opFixedUnion(in, handler);

        case 66: // opFixedUnionEx
            return _OB_op_opFixedUnionEx(in, handler);

        case 67: // opFloat
            return _OB_op_opFloat(in, handler);

        case 68: // opFloatEx
            return _OB_op_opFloatEx(in, handler);

        case 69: // opIntf
            return _OB_op_opIntf(in, handler);

        case 70: // opIntfEx
            return _OB_op_opIntfEx(in, handler);

        case 71: // opLong
            return _OB_op_opLong(in, handler);

        case 72: // opLongEx
            return _OB_op_opLongEx(in, handler);

        case 73: // opOctet
            return _OB_op_opOctet(in, handler);

        case 74: // opOctetEx
            return _OB_op_opOctetEx(in, handler);

        case 75: // opShort
            return _OB_op_opShort(in, handler);

        case 76: // opShortEx
            return _OB_op_opShortEx(in, handler);

        case 77: // opString
            return _OB_op_opString(in, handler);

        case 78: // opStringEx
            return _OB_op_opStringEx(in, handler);

        case 79: // opStringSequence
            return _OB_op_opStringSequence(in, handler);

        case 80: // opStringSequenceEx
            return _OB_op_opStringSequenceEx(in, handler);

        case 81: // opTestEnum
            return _OB_op_opTestEnum(in, handler);

        case 82: // opTestEnumEx
            return _OB_op_opTestEnumEx(in, handler);

        case 83: // opULong
            return _OB_op_opULong(in, handler);

        case 84: // opULongEx
            return _OB_op_opULongEx(in, handler);

        case 85: // opUShort
            return _OB_op_opUShort(in, handler);

        case 86: // opUShortEx
            return _OB_op_opUShortEx(in, handler);

        case 87: // opVariableArray
            return _OB_op_opVariableArray(in, handler);

        case 88: // opVariableArrayBoundSequence
            return _OB_op_opVariableArrayBoundSequence(in, handler);

        case 89: // opVariableArrayBoundSequenceEx
            return _OB_op_opVariableArrayBoundSequenceEx(in, handler);

        case 90: // opVariableArrayEx
            return _OB_op_opVariableArrayEx(in, handler);

        case 91: // opVariableArraySequence
            return _OB_op_opVariableArraySequence(in, handler);

        case 92: // opVariableArraySequenceEx
            return _OB_op_opVariableArraySequenceEx(in, handler);

        case 93: // opVariableStruct
            return _OB_op_opVariableStruct(in, handler);

        case 94: // opVariableStructEx
            return _OB_op_opVariableStructEx(in, handler);

        case 95: // opVariableUnion
            return _OB_op_opVariableUnion(in, handler);

        case 96: // opVariableUnionEx
            return _OB_op_opVariableUnionEx(in, handler);

        case 97: // opVoid
            return _OB_op_opVoid(in, handler);

        case 98: // opVoidEx
            return _OB_op_opVoidEx(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrAny(org.omg.CORBA.portable.InputStream in,
                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.Any _ob_r = attrAny();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_any(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrBoolean(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        boolean _ob_r = attrBoolean();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_boolean(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrChar(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        char _ob_r = attrChar();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_char(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrDouble(org.omg.CORBA.portable.InputStream in,
                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        double _ob_r = attrDouble();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_double(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrFixedArray(org.omg.CORBA.portable.InputStream in,
                               org.omg.CORBA.portable.ResponseHandler handler)
    {
        short[][][] _ob_r = attrFixedArray();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        FixedArrayHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrFixedArrayBoundSequence(org.omg.CORBA.portable.InputStream in,
                                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        short[][][][] _ob_r = attrFixedArrayBoundSequence();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        FixedArrayBoundSequenceHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrFixedArraySequence(org.omg.CORBA.portable.InputStream in,
                                       org.omg.CORBA.portable.ResponseHandler handler)
    {
        short[][][][] _ob_r = attrFixedArraySequence();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        FixedArraySequenceHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrFixedStruct(org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler handler)
    {
        FixedStruct _ob_r = attrFixedStruct();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        FixedStructHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrFixedUnion(org.omg.CORBA.portable.InputStream in,
                               org.omg.CORBA.portable.ResponseHandler handler)
    {
        FixedUnion _ob_r = attrFixedUnion();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        FixedUnionHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrFloat(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        float _ob_r = attrFloat();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_float(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrIntf(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        Intf _ob_r = attrIntf();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        IntfHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrLong(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        int _ob_r = attrLong();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_long(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrOctet(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        byte _ob_r = attrOctet();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_octet(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrShort(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        short _ob_r = attrShort();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_short(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrString(org.omg.CORBA.portable.InputStream in,
                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        String _ob_r = attrString();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_string(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrStringSequence(org.omg.CORBA.portable.InputStream in,
                                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        String[] _ob_r = attrStringSequence();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        StringSequenceHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrTestEnum(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        TestEnum _ob_r = attrTestEnum();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        TestEnumHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrULong(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        int _ob_r = attrULong();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_ulong(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrUShort(org.omg.CORBA.portable.InputStream in,
                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        short _ob_r = attrUShort();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_ushort(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrVariableArray(org.omg.CORBA.portable.InputStream in,
                                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        String[][] _ob_r = attrVariableArray();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        VariableArrayHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrVariableArrayBoundSequence(org.omg.CORBA.portable.InputStream in,
                                               org.omg.CORBA.portable.ResponseHandler handler)
    {
        String[][][] _ob_r = attrVariableArrayBoundSequence();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        VariableArrayBoundSequenceHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrVariableArraySequence(org.omg.CORBA.portable.InputStream in,
                                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        String[][][] _ob_r = attrVariableArraySequence();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        VariableArraySequenceHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrVariableStruct(org.omg.CORBA.portable.InputStream in,
                                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        VariableStruct _ob_r = attrVariableStruct();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        VariableStructHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrVariableUnion(org.omg.CORBA.portable.InputStream in,
                                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        VariableUnion _ob_r = attrVariableUnion();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        VariableUnionHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrAny(org.omg.CORBA.portable.InputStream in,
                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.Any _ob_a = in.read_any();
        attrAny(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrBoolean(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        boolean _ob_a = in.read_boolean();
        attrBoolean(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrChar(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        char _ob_a = in.read_char();
        attrChar(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrDouble(org.omg.CORBA.portable.InputStream in,
                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        double _ob_a = in.read_double();
        attrDouble(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrFixedArray(org.omg.CORBA.portable.InputStream in,
                               org.omg.CORBA.portable.ResponseHandler handler)
    {
        short[][][] _ob_a = FixedArrayHelper.read(in);
        attrFixedArray(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrFixedArrayBoundSequence(org.omg.CORBA.portable.InputStream in,
                                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        short[][][][] _ob_a = FixedArrayBoundSequenceHelper.read(in);
        attrFixedArrayBoundSequence(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrFixedArraySequence(org.omg.CORBA.portable.InputStream in,
                                       org.omg.CORBA.portable.ResponseHandler handler)
    {
        short[][][][] _ob_a = FixedArraySequenceHelper.read(in);
        attrFixedArraySequence(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrFixedStruct(org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler handler)
    {
        FixedStruct _ob_a = FixedStructHelper.read(in);
        attrFixedStruct(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrFixedUnion(org.omg.CORBA.portable.InputStream in,
                               org.omg.CORBA.portable.ResponseHandler handler)
    {
        FixedUnion _ob_a = FixedUnionHelper.read(in);
        attrFixedUnion(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrFloat(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        float _ob_a = in.read_float();
        attrFloat(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrIntf(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        Intf _ob_a = IntfHelper.read(in);
        attrIntf(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrLong(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        int _ob_a = in.read_long();
        attrLong(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrOctet(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        byte _ob_a = in.read_octet();
        attrOctet(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrShort(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        short _ob_a = in.read_short();
        attrShort(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrString(org.omg.CORBA.portable.InputStream in,
                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        String _ob_a = in.read_string();
        attrString(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrStringSequence(org.omg.CORBA.portable.InputStream in,
                                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        String[] _ob_a = StringSequenceHelper.read(in);
        attrStringSequence(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrTestEnum(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        TestEnum _ob_a = TestEnumHelper.read(in);
        attrTestEnum(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrULong(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        int _ob_a = in.read_ulong();
        attrULong(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrUShort(org.omg.CORBA.portable.InputStream in,
                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        short _ob_a = in.read_ushort();
        attrUShort(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrVariableArray(org.omg.CORBA.portable.InputStream in,
                                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        String[][] _ob_a = VariableArrayHelper.read(in);
        attrVariableArray(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrVariableArrayBoundSequence(org.omg.CORBA.portable.InputStream in,
                                               org.omg.CORBA.portable.ResponseHandler handler)
    {
        String[][][] _ob_a = VariableArrayBoundSequenceHelper.read(in);
        attrVariableArrayBoundSequence(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrVariableArraySequence(org.omg.CORBA.portable.InputStream in,
                                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        String[][][] _ob_a = VariableArraySequenceHelper.read(in);
        attrVariableArraySequence(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrVariableStruct(org.omg.CORBA.portable.InputStream in,
                                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        VariableStruct _ob_a = VariableStructHelper.read(in);
        attrVariableStruct(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrVariableUnion(org.omg.CORBA.portable.InputStream in,
                                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        VariableUnion _ob_a = VariableUnionHelper.read(in);
        attrVariableUnion(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opAny(org.omg.CORBA.portable.InputStream in,
                 org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_a0 = in.read_any();
        org.omg.CORBA.AnyHolder _ob_ah1 = new org.omg.CORBA.AnyHolder();
        _ob_ah1.value = in.read_any();
        org.omg.CORBA.AnyHolder _ob_ah2 = new org.omg.CORBA.AnyHolder();
        org.omg.CORBA.Any _ob_r = opAny(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        out.write_any(_ob_r);
        out.write_any(_ob_ah1.value);
        out.write_any(_ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opAnyEx(org.omg.CORBA.portable.InputStream in,
                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            org.omg.CORBA.Any _ob_a0 = in.read_any();
            org.omg.CORBA.AnyHolder _ob_ah1 = new org.omg.CORBA.AnyHolder();
            _ob_ah1.value = in.read_any();
            org.omg.CORBA.AnyHolder _ob_ah2 = new org.omg.CORBA.AnyHolder();
            org.omg.CORBA.Any _ob_r = opAnyEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            out.write_any(_ob_r);
            out.write_any(_ob_ah1.value);
            out.write_any(_ob_ah2.value);
        }
        catch(ExAny _ob_ex)
        {
            out = handler.createExceptionReply();
            ExAnyHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opBoolean(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        boolean _ob_a0 = in.read_boolean();
        org.omg.CORBA.BooleanHolder _ob_ah1 = new org.omg.CORBA.BooleanHolder();
        _ob_ah1.value = in.read_boolean();
        org.omg.CORBA.BooleanHolder _ob_ah2 = new org.omg.CORBA.BooleanHolder();
        boolean _ob_r = opBoolean(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        out.write_boolean(_ob_r);
        out.write_boolean(_ob_ah1.value);
        out.write_boolean(_ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opBooleanEx(org.omg.CORBA.portable.InputStream in,
                       org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            boolean _ob_a0 = in.read_boolean();
            org.omg.CORBA.BooleanHolder _ob_ah1 = new org.omg.CORBA.BooleanHolder();
            _ob_ah1.value = in.read_boolean();
            org.omg.CORBA.BooleanHolder _ob_ah2 = new org.omg.CORBA.BooleanHolder();
            boolean _ob_r = opBooleanEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            out.write_boolean(_ob_r);
            out.write_boolean(_ob_ah1.value);
            out.write_boolean(_ob_ah2.value);
        }
        catch(ExBoolean _ob_ex)
        {
            out = handler.createExceptionReply();
            ExBooleanHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opChar(org.omg.CORBA.portable.InputStream in,
                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        char _ob_a0 = in.read_char();
        org.omg.CORBA.CharHolder _ob_ah1 = new org.omg.CORBA.CharHolder();
        _ob_ah1.value = in.read_char();
        org.omg.CORBA.CharHolder _ob_ah2 = new org.omg.CORBA.CharHolder();
        char _ob_r = opChar(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        out.write_char(_ob_r);
        out.write_char(_ob_ah1.value);
        out.write_char(_ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opCharEx(org.omg.CORBA.portable.InputStream in,
                    org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            char _ob_a0 = in.read_char();
            org.omg.CORBA.CharHolder _ob_ah1 = new org.omg.CORBA.CharHolder();
            _ob_ah1.value = in.read_char();
            org.omg.CORBA.CharHolder _ob_ah2 = new org.omg.CORBA.CharHolder();
            char _ob_r = opCharEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            out.write_char(_ob_r);
            out.write_char(_ob_ah1.value);
            out.write_char(_ob_ah2.value);
        }
        catch(ExChar _ob_ex)
        {
            out = handler.createExceptionReply();
            ExCharHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opDouble(org.omg.CORBA.portable.InputStream in,
                    org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        double _ob_a0 = in.read_double();
        org.omg.CORBA.DoubleHolder _ob_ah1 = new org.omg.CORBA.DoubleHolder();
        _ob_ah1.value = in.read_double();
        org.omg.CORBA.DoubleHolder _ob_ah2 = new org.omg.CORBA.DoubleHolder();
        double _ob_r = opDouble(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        out.write_double(_ob_r);
        out.write_double(_ob_ah1.value);
        out.write_double(_ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opDoubleEx(org.omg.CORBA.portable.InputStream in,
                      org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            double _ob_a0 = in.read_double();
            org.omg.CORBA.DoubleHolder _ob_ah1 = new org.omg.CORBA.DoubleHolder();
            _ob_ah1.value = in.read_double();
            org.omg.CORBA.DoubleHolder _ob_ah2 = new org.omg.CORBA.DoubleHolder();
            double _ob_r = opDoubleEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            out.write_double(_ob_r);
            out.write_double(_ob_ah1.value);
            out.write_double(_ob_ah2.value);
        }
        catch(ExDouble _ob_ex)
        {
            out = handler.createExceptionReply();
            ExDoubleHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opExRecursiveStruct(org.omg.CORBA.portable.InputStream in,
                               org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            opExRecursiveStruct();
            out = handler.createReply();
        }
        catch(ExRecursiveStruct _ob_ex)
        {
            out = handler.createExceptionReply();
            ExRecursiveStructHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opFixedArray(org.omg.CORBA.portable.InputStream in,
                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        short[][][] _ob_a0 = FixedArrayHelper.read(in);
        FixedArrayHolder _ob_ah1 = new FixedArrayHolder();
        _ob_ah1.value = FixedArrayHelper.read(in);
        FixedArrayHolder _ob_ah2 = new FixedArrayHolder();
        short[][][] _ob_r = opFixedArray(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        FixedArrayHelper.write(out, _ob_r);
        FixedArrayHelper.write(out, _ob_ah1.value);
        FixedArrayHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opFixedArrayBoundSequence(org.omg.CORBA.portable.InputStream in,
                                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        short[][][][] _ob_a0 = FixedArrayBoundSequenceHelper.read(in);
        FixedArrayBoundSequenceHolder _ob_ah1 = new FixedArrayBoundSequenceHolder();
        _ob_ah1.value = FixedArrayBoundSequenceHelper.read(in);
        FixedArrayBoundSequenceHolder _ob_ah2 = new FixedArrayBoundSequenceHolder();
        short[][][][] _ob_r = opFixedArrayBoundSequence(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        FixedArrayBoundSequenceHelper.write(out, _ob_r);
        FixedArrayBoundSequenceHelper.write(out, _ob_ah1.value);
        FixedArrayBoundSequenceHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opFixedArrayBoundSequenceEx(org.omg.CORBA.portable.InputStream in,
                                       org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            short[][][][] _ob_a0 = FixedArrayBoundSequenceHelper.read(in);
            FixedArrayBoundSequenceHolder _ob_ah1 = new FixedArrayBoundSequenceHolder();
            _ob_ah1.value = FixedArrayBoundSequenceHelper.read(in);
            FixedArrayBoundSequenceHolder _ob_ah2 = new FixedArrayBoundSequenceHolder();
            short[][][][] _ob_r = opFixedArrayBoundSequenceEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            FixedArrayBoundSequenceHelper.write(out, _ob_r);
            FixedArrayBoundSequenceHelper.write(out, _ob_ah1.value);
            FixedArrayBoundSequenceHelper.write(out, _ob_ah2.value);
        }
        catch(ExFixedArrayBoundSequence _ob_ex)
        {
            out = handler.createExceptionReply();
            ExFixedArrayBoundSequenceHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opFixedArrayEx(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            short[][][] _ob_a0 = FixedArrayHelper.read(in);
            FixedArrayHolder _ob_ah1 = new FixedArrayHolder();
            _ob_ah1.value = FixedArrayHelper.read(in);
            FixedArrayHolder _ob_ah2 = new FixedArrayHolder();
            short[][][] _ob_r = opFixedArrayEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            FixedArrayHelper.write(out, _ob_r);
            FixedArrayHelper.write(out, _ob_ah1.value);
            FixedArrayHelper.write(out, _ob_ah2.value);
        }
        catch(ExFixedArray _ob_ex)
        {
            out = handler.createExceptionReply();
            ExFixedArrayHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opFixedArraySequence(org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        short[][][][] _ob_a0 = FixedArraySequenceHelper.read(in);
        FixedArraySequenceHolder _ob_ah1 = new FixedArraySequenceHolder();
        _ob_ah1.value = FixedArraySequenceHelper.read(in);
        FixedArraySequenceHolder _ob_ah2 = new FixedArraySequenceHolder();
        short[][][][] _ob_r = opFixedArraySequence(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        FixedArraySequenceHelper.write(out, _ob_r);
        FixedArraySequenceHelper.write(out, _ob_ah1.value);
        FixedArraySequenceHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opFixedArraySequenceEx(org.omg.CORBA.portable.InputStream in,
                                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            short[][][][] _ob_a0 = FixedArraySequenceHelper.read(in);
            FixedArraySequenceHolder _ob_ah1 = new FixedArraySequenceHolder();
            _ob_ah1.value = FixedArraySequenceHelper.read(in);
            FixedArraySequenceHolder _ob_ah2 = new FixedArraySequenceHolder();
            short[][][][] _ob_r = opFixedArraySequenceEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            FixedArraySequenceHelper.write(out, _ob_r);
            FixedArraySequenceHelper.write(out, _ob_ah1.value);
            FixedArraySequenceHelper.write(out, _ob_ah2.value);
        }
        catch(ExFixedArraySequence _ob_ex)
        {
            out = handler.createExceptionReply();
            ExFixedArraySequenceHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opFixedStruct(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        FixedStruct _ob_a0 = FixedStructHelper.read(in);
        FixedStructHolder _ob_ah1 = new FixedStructHolder();
        _ob_ah1.value = FixedStructHelper.read(in);
        FixedStructHolder _ob_ah2 = new FixedStructHolder();
        FixedStruct _ob_r = opFixedStruct(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        FixedStructHelper.write(out, _ob_r);
        FixedStructHelper.write(out, _ob_ah1.value);
        FixedStructHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opFixedStructEx(org.omg.CORBA.portable.InputStream in,
                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            FixedStruct _ob_a0 = FixedStructHelper.read(in);
            FixedStructHolder _ob_ah1 = new FixedStructHolder();
            _ob_ah1.value = FixedStructHelper.read(in);
            FixedStructHolder _ob_ah2 = new FixedStructHolder();
            FixedStruct _ob_r = opFixedStructEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            FixedStructHelper.write(out, _ob_r);
            FixedStructHelper.write(out, _ob_ah1.value);
            FixedStructHelper.write(out, _ob_ah2.value);
        }
        catch(ExFixedStruct _ob_ex)
        {
            out = handler.createExceptionReply();
            ExFixedStructHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opFixedUnion(org.omg.CORBA.portable.InputStream in,
                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        FixedUnion _ob_a0 = FixedUnionHelper.read(in);
        FixedUnionHolder _ob_ah1 = new FixedUnionHolder();
        _ob_ah1.value = FixedUnionHelper.read(in);
        FixedUnionHolder _ob_ah2 = new FixedUnionHolder();
        FixedUnion _ob_r = opFixedUnion(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        FixedUnionHelper.write(out, _ob_r);
        FixedUnionHelper.write(out, _ob_ah1.value);
        FixedUnionHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opFixedUnionEx(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            FixedUnion _ob_a0 = FixedUnionHelper.read(in);
            FixedUnionHolder _ob_ah1 = new FixedUnionHolder();
            _ob_ah1.value = FixedUnionHelper.read(in);
            FixedUnionHolder _ob_ah2 = new FixedUnionHolder();
            FixedUnion _ob_r = opFixedUnionEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            FixedUnionHelper.write(out, _ob_r);
            FixedUnionHelper.write(out, _ob_ah1.value);
            FixedUnionHelper.write(out, _ob_ah2.value);
        }
        catch(ExFixedUnion _ob_ex)
        {
            out = handler.createExceptionReply();
            ExFixedUnionHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opFloat(org.omg.CORBA.portable.InputStream in,
                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        float _ob_a0 = in.read_float();
        org.omg.CORBA.FloatHolder _ob_ah1 = new org.omg.CORBA.FloatHolder();
        _ob_ah1.value = in.read_float();
        org.omg.CORBA.FloatHolder _ob_ah2 = new org.omg.CORBA.FloatHolder();
        float _ob_r = opFloat(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        out.write_float(_ob_r);
        out.write_float(_ob_ah1.value);
        out.write_float(_ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opFloatEx(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            float _ob_a0 = in.read_float();
            org.omg.CORBA.FloatHolder _ob_ah1 = new org.omg.CORBA.FloatHolder();
            _ob_ah1.value = in.read_float();
            org.omg.CORBA.FloatHolder _ob_ah2 = new org.omg.CORBA.FloatHolder();
            float _ob_r = opFloatEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            out.write_float(_ob_r);
            out.write_float(_ob_ah1.value);
            out.write_float(_ob_ah2.value);
        }
        catch(ExFloat _ob_ex)
        {
            out = handler.createExceptionReply();
            ExFloatHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opIntf(org.omg.CORBA.portable.InputStream in,
                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        Intf _ob_a0 = IntfHelper.read(in);
        IntfHolder _ob_ah1 = new IntfHolder();
        _ob_ah1.value = IntfHelper.read(in);
        IntfHolder _ob_ah2 = new IntfHolder();
        Intf _ob_r = opIntf(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        IntfHelper.write(out, _ob_r);
        IntfHelper.write(out, _ob_ah1.value);
        IntfHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opIntfEx(org.omg.CORBA.portable.InputStream in,
                    org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            Intf _ob_a0 = IntfHelper.read(in);
            IntfHolder _ob_ah1 = new IntfHolder();
            _ob_ah1.value = IntfHelper.read(in);
            IntfHolder _ob_ah2 = new IntfHolder();
            Intf _ob_r = opIntfEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            IntfHelper.write(out, _ob_r);
            IntfHelper.write(out, _ob_ah1.value);
            IntfHelper.write(out, _ob_ah2.value);
        }
        catch(ExIntf _ob_ex)
        {
            out = handler.createExceptionReply();
            ExIntfHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opLong(org.omg.CORBA.portable.InputStream in,
                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        int _ob_a0 = in.read_long();
        org.omg.CORBA.IntHolder _ob_ah1 = new org.omg.CORBA.IntHolder();
        _ob_ah1.value = in.read_long();
        org.omg.CORBA.IntHolder _ob_ah2 = new org.omg.CORBA.IntHolder();
        int _ob_r = opLong(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        out.write_long(_ob_r);
        out.write_long(_ob_ah1.value);
        out.write_long(_ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opLongEx(org.omg.CORBA.portable.InputStream in,
                    org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            int _ob_a0 = in.read_long();
            org.omg.CORBA.IntHolder _ob_ah1 = new org.omg.CORBA.IntHolder();
            _ob_ah1.value = in.read_long();
            org.omg.CORBA.IntHolder _ob_ah2 = new org.omg.CORBA.IntHolder();
            int _ob_r = opLongEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            out.write_long(_ob_r);
            out.write_long(_ob_ah1.value);
            out.write_long(_ob_ah2.value);
        }
        catch(ExLong _ob_ex)
        {
            out = handler.createExceptionReply();
            ExLongHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opOctet(org.omg.CORBA.portable.InputStream in,
                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        byte _ob_a0 = in.read_octet();
        org.omg.CORBA.ByteHolder _ob_ah1 = new org.omg.CORBA.ByteHolder();
        _ob_ah1.value = in.read_octet();
        org.omg.CORBA.ByteHolder _ob_ah2 = new org.omg.CORBA.ByteHolder();
        byte _ob_r = opOctet(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        out.write_octet(_ob_r);
        out.write_octet(_ob_ah1.value);
        out.write_octet(_ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opOctetEx(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            byte _ob_a0 = in.read_octet();
            org.omg.CORBA.ByteHolder _ob_ah1 = new org.omg.CORBA.ByteHolder();
            _ob_ah1.value = in.read_octet();
            org.omg.CORBA.ByteHolder _ob_ah2 = new org.omg.CORBA.ByteHolder();
            byte _ob_r = opOctetEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            out.write_octet(_ob_r);
            out.write_octet(_ob_ah1.value);
            out.write_octet(_ob_ah2.value);
        }
        catch(ExOctet _ob_ex)
        {
            out = handler.createExceptionReply();
            ExOctetHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opShort(org.omg.CORBA.portable.InputStream in,
                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        short _ob_a0 = in.read_short();
        org.omg.CORBA.ShortHolder _ob_ah1 = new org.omg.CORBA.ShortHolder();
        _ob_ah1.value = in.read_short();
        org.omg.CORBA.ShortHolder _ob_ah2 = new org.omg.CORBA.ShortHolder();
        short _ob_r = opShort(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        out.write_short(_ob_r);
        out.write_short(_ob_ah1.value);
        out.write_short(_ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opShortEx(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            short _ob_a0 = in.read_short();
            org.omg.CORBA.ShortHolder _ob_ah1 = new org.omg.CORBA.ShortHolder();
            _ob_ah1.value = in.read_short();
            org.omg.CORBA.ShortHolder _ob_ah2 = new org.omg.CORBA.ShortHolder();
            short _ob_r = opShortEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            out.write_short(_ob_r);
            out.write_short(_ob_ah1.value);
            out.write_short(_ob_ah2.value);
        }
        catch(ExShort _ob_ex)
        {
            out = handler.createExceptionReply();
            ExShortHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opString(org.omg.CORBA.portable.InputStream in,
                    org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String _ob_a0 = in.read_string();
        org.omg.CORBA.StringHolder _ob_ah1 = new org.omg.CORBA.StringHolder();
        _ob_ah1.value = in.read_string();
        org.omg.CORBA.StringHolder _ob_ah2 = new org.omg.CORBA.StringHolder();
        String _ob_r = opString(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        out.write_string(_ob_r);
        out.write_string(_ob_ah1.value);
        out.write_string(_ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opStringEx(org.omg.CORBA.portable.InputStream in,
                      org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            String _ob_a0 = in.read_string();
            org.omg.CORBA.StringHolder _ob_ah1 = new org.omg.CORBA.StringHolder();
            _ob_ah1.value = in.read_string();
            org.omg.CORBA.StringHolder _ob_ah2 = new org.omg.CORBA.StringHolder();
            String _ob_r = opStringEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            out.write_string(_ob_r);
            out.write_string(_ob_ah1.value);
            out.write_string(_ob_ah2.value);
        }
        catch(ExString _ob_ex)
        {
            out = handler.createExceptionReply();
            ExStringHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opStringSequence(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String[] _ob_a0 = StringSequenceHelper.read(in);
        StringSequenceHolder _ob_ah1 = new StringSequenceHolder();
        _ob_ah1.value = StringSequenceHelper.read(in);
        StringSequenceHolder _ob_ah2 = new StringSequenceHolder();
        String[] _ob_r = opStringSequence(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        StringSequenceHelper.write(out, _ob_r);
        StringSequenceHelper.write(out, _ob_ah1.value);
        StringSequenceHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opStringSequenceEx(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            String[] _ob_a0 = StringSequenceHelper.read(in);
            StringSequenceHolder _ob_ah1 = new StringSequenceHolder();
            _ob_ah1.value = StringSequenceHelper.read(in);
            StringSequenceHolder _ob_ah2 = new StringSequenceHolder();
            String[] _ob_r = opStringSequenceEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            StringSequenceHelper.write(out, _ob_r);
            StringSequenceHelper.write(out, _ob_ah1.value);
            StringSequenceHelper.write(out, _ob_ah2.value);
        }
        catch(ExStringSequence _ob_ex)
        {
            out = handler.createExceptionReply();
            ExStringSequenceHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opTestEnum(org.omg.CORBA.portable.InputStream in,
                      org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestEnum _ob_a0 = TestEnumHelper.read(in);
        TestEnumHolder _ob_ah1 = new TestEnumHolder();
        _ob_ah1.value = TestEnumHelper.read(in);
        TestEnumHolder _ob_ah2 = new TestEnumHolder();
        TestEnum _ob_r = opTestEnum(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        TestEnumHelper.write(out, _ob_r);
        TestEnumHelper.write(out, _ob_ah1.value);
        TestEnumHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opTestEnumEx(org.omg.CORBA.portable.InputStream in,
                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            TestEnum _ob_a0 = TestEnumHelper.read(in);
            TestEnumHolder _ob_ah1 = new TestEnumHolder();
            _ob_ah1.value = TestEnumHelper.read(in);
            TestEnumHolder _ob_ah2 = new TestEnumHolder();
            TestEnum _ob_r = opTestEnumEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            TestEnumHelper.write(out, _ob_r);
            TestEnumHelper.write(out, _ob_ah1.value);
            TestEnumHelper.write(out, _ob_ah2.value);
        }
        catch(ExTestEnum _ob_ex)
        {
            out = handler.createExceptionReply();
            ExTestEnumHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opULong(org.omg.CORBA.portable.InputStream in,
                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        int _ob_a0 = in.read_ulong();
        org.omg.CORBA.IntHolder _ob_ah1 = new org.omg.CORBA.IntHolder();
        _ob_ah1.value = in.read_ulong();
        org.omg.CORBA.IntHolder _ob_ah2 = new org.omg.CORBA.IntHolder();
        int _ob_r = opULong(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        out.write_ulong(_ob_r);
        out.write_ulong(_ob_ah1.value);
        out.write_ulong(_ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opULongEx(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            int _ob_a0 = in.read_ulong();
            org.omg.CORBA.IntHolder _ob_ah1 = new org.omg.CORBA.IntHolder();
            _ob_ah1.value = in.read_ulong();
            org.omg.CORBA.IntHolder _ob_ah2 = new org.omg.CORBA.IntHolder();
            int _ob_r = opULongEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            out.write_ulong(_ob_r);
            out.write_ulong(_ob_ah1.value);
            out.write_ulong(_ob_ah2.value);
        }
        catch(ExULong _ob_ex)
        {
            out = handler.createExceptionReply();
            ExULongHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opUShort(org.omg.CORBA.portable.InputStream in,
                    org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        short _ob_a0 = in.read_ushort();
        org.omg.CORBA.ShortHolder _ob_ah1 = new org.omg.CORBA.ShortHolder();
        _ob_ah1.value = in.read_ushort();
        org.omg.CORBA.ShortHolder _ob_ah2 = new org.omg.CORBA.ShortHolder();
        short _ob_r = opUShort(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        out.write_ushort(_ob_r);
        out.write_ushort(_ob_ah1.value);
        out.write_ushort(_ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opUShortEx(org.omg.CORBA.portable.InputStream in,
                      org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            short _ob_a0 = in.read_ushort();
            org.omg.CORBA.ShortHolder _ob_ah1 = new org.omg.CORBA.ShortHolder();
            _ob_ah1.value = in.read_ushort();
            org.omg.CORBA.ShortHolder _ob_ah2 = new org.omg.CORBA.ShortHolder();
            short _ob_r = opUShortEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            out.write_ushort(_ob_r);
            out.write_ushort(_ob_ah1.value);
            out.write_ushort(_ob_ah2.value);
        }
        catch(ExUShort _ob_ex)
        {
            out = handler.createExceptionReply();
            ExUShortHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opVariableArray(org.omg.CORBA.portable.InputStream in,
                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String[][] _ob_a0 = VariableArrayHelper.read(in);
        VariableArrayHolder _ob_ah1 = new VariableArrayHolder();
        _ob_ah1.value = VariableArrayHelper.read(in);
        VariableArrayHolder _ob_ah2 = new VariableArrayHolder();
        String[][] _ob_r = opVariableArray(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        VariableArrayHelper.write(out, _ob_r);
        VariableArrayHelper.write(out, _ob_ah1.value);
        VariableArrayHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opVariableArrayBoundSequence(org.omg.CORBA.portable.InputStream in,
                                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String[][][] _ob_a0 = VariableArrayBoundSequenceHelper.read(in);
        VariableArrayBoundSequenceHolder _ob_ah1 = new VariableArrayBoundSequenceHolder();
        _ob_ah1.value = VariableArrayBoundSequenceHelper.read(in);
        VariableArrayBoundSequenceHolder _ob_ah2 = new VariableArrayBoundSequenceHolder();
        String[][][] _ob_r = opVariableArrayBoundSequence(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        VariableArrayBoundSequenceHelper.write(out, _ob_r);
        VariableArrayBoundSequenceHelper.write(out, _ob_ah1.value);
        VariableArrayBoundSequenceHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opVariableArrayBoundSequenceEx(org.omg.CORBA.portable.InputStream in,
                                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            String[][][] _ob_a0 = VariableArrayBoundSequenceHelper.read(in);
            VariableArrayBoundSequenceHolder _ob_ah1 = new VariableArrayBoundSequenceHolder();
            _ob_ah1.value = VariableArrayBoundSequenceHelper.read(in);
            VariableArrayBoundSequenceHolder _ob_ah2 = new VariableArrayBoundSequenceHolder();
            String[][][] _ob_r = opVariableArrayBoundSequenceEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            VariableArrayBoundSequenceHelper.write(out, _ob_r);
            VariableArrayBoundSequenceHelper.write(out, _ob_ah1.value);
            VariableArrayBoundSequenceHelper.write(out, _ob_ah2.value);
        }
        catch(ExVariableArrayBoundSequence _ob_ex)
        {
            out = handler.createExceptionReply();
            ExVariableArrayBoundSequenceHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opVariableArrayEx(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            String[][] _ob_a0 = VariableArrayHelper.read(in);
            VariableArrayHolder _ob_ah1 = new VariableArrayHolder();
            _ob_ah1.value = VariableArrayHelper.read(in);
            VariableArrayHolder _ob_ah2 = new VariableArrayHolder();
            String[][] _ob_r = opVariableArrayEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            VariableArrayHelper.write(out, _ob_r);
            VariableArrayHelper.write(out, _ob_ah1.value);
            VariableArrayHelper.write(out, _ob_ah2.value);
        }
        catch(ExVariableArray _ob_ex)
        {
            out = handler.createExceptionReply();
            ExVariableArrayHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opVariableArraySequence(org.omg.CORBA.portable.InputStream in,
                                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String[][][] _ob_a0 = VariableArraySequenceHelper.read(in);
        VariableArraySequenceHolder _ob_ah1 = new VariableArraySequenceHolder();
        _ob_ah1.value = VariableArraySequenceHelper.read(in);
        VariableArraySequenceHolder _ob_ah2 = new VariableArraySequenceHolder();
        String[][][] _ob_r = opVariableArraySequence(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        VariableArraySequenceHelper.write(out, _ob_r);
        VariableArraySequenceHelper.write(out, _ob_ah1.value);
        VariableArraySequenceHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opVariableArraySequenceEx(org.omg.CORBA.portable.InputStream in,
                                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            String[][][] _ob_a0 = VariableArraySequenceHelper.read(in);
            VariableArraySequenceHolder _ob_ah1 = new VariableArraySequenceHolder();
            _ob_ah1.value = VariableArraySequenceHelper.read(in);
            VariableArraySequenceHolder _ob_ah2 = new VariableArraySequenceHolder();
            String[][][] _ob_r = opVariableArraySequenceEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            VariableArraySequenceHelper.write(out, _ob_r);
            VariableArraySequenceHelper.write(out, _ob_ah1.value);
            VariableArraySequenceHelper.write(out, _ob_ah2.value);
        }
        catch(ExVariableArraySequence _ob_ex)
        {
            out = handler.createExceptionReply();
            ExVariableArraySequenceHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opVariableStruct(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        VariableStruct _ob_a0 = VariableStructHelper.read(in);
        VariableStructHolder _ob_ah1 = new VariableStructHolder();
        _ob_ah1.value = VariableStructHelper.read(in);
        VariableStructHolder _ob_ah2 = new VariableStructHolder();
        VariableStruct _ob_r = opVariableStruct(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        VariableStructHelper.write(out, _ob_r);
        VariableStructHelper.write(out, _ob_ah1.value);
        VariableStructHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opVariableStructEx(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            VariableStruct _ob_a0 = VariableStructHelper.read(in);
            VariableStructHolder _ob_ah1 = new VariableStructHolder();
            _ob_ah1.value = VariableStructHelper.read(in);
            VariableStructHolder _ob_ah2 = new VariableStructHolder();
            VariableStruct _ob_r = opVariableStructEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            VariableStructHelper.write(out, _ob_r);
            VariableStructHelper.write(out, _ob_ah1.value);
            VariableStructHelper.write(out, _ob_ah2.value);
        }
        catch(ExVariableStruct _ob_ex)
        {
            out = handler.createExceptionReply();
            ExVariableStructHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opVariableUnion(org.omg.CORBA.portable.InputStream in,
                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        VariableUnion _ob_a0 = VariableUnionHelper.read(in);
        VariableUnionHolder _ob_ah1 = new VariableUnionHolder();
        _ob_ah1.value = VariableUnionHelper.read(in);
        VariableUnionHolder _ob_ah2 = new VariableUnionHolder();
        VariableUnion _ob_r = opVariableUnion(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        VariableUnionHelper.write(out, _ob_r);
        VariableUnionHelper.write(out, _ob_ah1.value);
        VariableUnionHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opVariableUnionEx(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            VariableUnion _ob_a0 = VariableUnionHelper.read(in);
            VariableUnionHolder _ob_ah1 = new VariableUnionHolder();
            _ob_ah1.value = VariableUnionHelper.read(in);
            VariableUnionHolder _ob_ah2 = new VariableUnionHolder();
            VariableUnion _ob_r = opVariableUnionEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            VariableUnionHelper.write(out, _ob_r);
            VariableUnionHelper.write(out, _ob_ah1.value);
            VariableUnionHelper.write(out, _ob_ah2.value);
        }
        catch(ExVariableUnion _ob_ex)
        {
            out = handler.createExceptionReply();
            ExVariableUnionHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opVoid(org.omg.CORBA.portable.InputStream in,
                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        opVoid();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opVoidEx(org.omg.CORBA.portable.InputStream in,
                    org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            opVoidEx();
            out = handler.createReply();
        }
        catch(ExVoid _ob_ex)
        {
            out = handler.createExceptionReply();
            ExVoidHelper.write(out, _ob_ex);
        }
        return out;
    }
}
