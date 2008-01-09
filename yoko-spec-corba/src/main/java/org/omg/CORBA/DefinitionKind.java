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

//
// IDL:omg.org/CORBA/DefinitionKind:1.0
//
/***/

public class DefinitionKind implements org.omg.CORBA.portable.IDLEntity
{
    private static DefinitionKind [] values_ = new DefinitionKind[26];
    private int value_;

    public final static int _dk_none = 0;
    public final static DefinitionKind dk_none = new DefinitionKind(_dk_none);
    public final static int _dk_all = 1;
    public final static DefinitionKind dk_all = new DefinitionKind(_dk_all);
    public final static int _dk_Attribute = 2;
    public final static DefinitionKind dk_Attribute = new DefinitionKind(_dk_Attribute);
    public final static int _dk_Constant = 3;
    public final static DefinitionKind dk_Constant = new DefinitionKind(_dk_Constant);
    public final static int _dk_Exception = 4;
    public final static DefinitionKind dk_Exception = new DefinitionKind(_dk_Exception);
    public final static int _dk_Interface = 5;
    public final static DefinitionKind dk_Interface = new DefinitionKind(_dk_Interface);
    public final static int _dk_Module = 6;
    public final static DefinitionKind dk_Module = new DefinitionKind(_dk_Module);
    public final static int _dk_Operation = 7;
    public final static DefinitionKind dk_Operation = new DefinitionKind(_dk_Operation);
    public final static int _dk_Typedef = 8;
    public final static DefinitionKind dk_Typedef = new DefinitionKind(_dk_Typedef);
    public final static int _dk_Alias = 9;
    public final static DefinitionKind dk_Alias = new DefinitionKind(_dk_Alias);
    public final static int _dk_Struct = 10;
    public final static DefinitionKind dk_Struct = new DefinitionKind(_dk_Struct);
    public final static int _dk_Union = 11;
    public final static DefinitionKind dk_Union = new DefinitionKind(_dk_Union);
    public final static int _dk_Enum = 12;
    public final static DefinitionKind dk_Enum = new DefinitionKind(_dk_Enum);
    public final static int _dk_Primitive = 13;
    public final static DefinitionKind dk_Primitive = new DefinitionKind(_dk_Primitive);
    public final static int _dk_String = 14;
    public final static DefinitionKind dk_String = new DefinitionKind(_dk_String);
    public final static int _dk_Sequence = 15;
    public final static DefinitionKind dk_Sequence = new DefinitionKind(_dk_Sequence);
    public final static int _dk_Array = 16;
    public final static DefinitionKind dk_Array = new DefinitionKind(_dk_Array);
    public final static int _dk_Repository = 17;
    public final static DefinitionKind dk_Repository = new DefinitionKind(_dk_Repository);
    public final static int _dk_Wstring = 18;
    public final static DefinitionKind dk_Wstring = new DefinitionKind(_dk_Wstring);
    public final static int _dk_Fixed = 19;
    public final static DefinitionKind dk_Fixed = new DefinitionKind(_dk_Fixed);
    public final static int _dk_Value = 20;
    public final static DefinitionKind dk_Value = new DefinitionKind(_dk_Value);
    public final static int _dk_ValueBox = 21;
    public final static DefinitionKind dk_ValueBox = new DefinitionKind(_dk_ValueBox);
    public final static int _dk_ValueMember = 22;
    public final static DefinitionKind dk_ValueMember = new DefinitionKind(_dk_ValueMember);
    public final static int _dk_Native = 23;
    public final static DefinitionKind dk_Native = new DefinitionKind(_dk_Native);
    public final static int _dk_AbstractInterface = 24;
    public final static DefinitionKind dk_AbstractInterface = new DefinitionKind(_dk_AbstractInterface);
    public final static int _dk_LocalInterface = 25;
    public final static DefinitionKind dk_LocalInterface = new DefinitionKind(_dk_LocalInterface);

    protected
    DefinitionKind(int value)
    {
        values_[value] = this;
        value_ = value;
    }

    public int
    value()
    {
        return value_;
    }

    public static DefinitionKind
    from_int(int value)
    {
        if(value < values_.length)
            return values_[value];
        else
            throw new org.omg.CORBA.BAD_PARAM("Value (" + value  + ") out of range", 25, org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    private java.lang.Object
    readResolve()
        throws java.io.ObjectStreamException
    {
        return from_int(value());
    }
}
