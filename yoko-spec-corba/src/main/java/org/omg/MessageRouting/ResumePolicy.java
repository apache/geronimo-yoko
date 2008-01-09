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

package org.omg.MessageRouting;

//
// IDL:omg.org/MessageRouting/ResumePolicy:1.0
//
/***/

public abstract class ResumePolicy implements org.omg.CORBA.portable.StreamableValue,
                                              org.omg.CORBA.PolicyOperations
{
    //
    // IDL:omg.org/MessageRouting/ResumePolicy/resume_seconds:1.0
    //
    /***/

    public int resume_seconds;

    private static String[] _OB_truncatableIds_ =
    {
        ResumePolicyHelper.id()
    };

    public String[]
    _truncatable_ids()
    {
        return _OB_truncatableIds_;
    }

    public void
    _read(org.omg.CORBA.portable.InputStream in)
    {
        resume_seconds = in.read_ulong();
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        out.write_ulong(resume_seconds);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return ResumePolicyHelper.type();
    }
}
