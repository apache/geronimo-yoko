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

package test.types.DynAnyTypes;

//
// IDL:test/types/DynAnyTypes/TestException:1.0
//
/***/

final public class TestException extends org.omg.CORBA.UserException
{
    private static final String _ob_id = "IDL:test/types/DynAnyTypes/TestException:1.0";

    public
    TestException()
    {
        super(_ob_id);
    }

    public
    TestException(String reason,
                  int code)
    {
        super(_ob_id);
        this.reason = reason;
        this.code = code;
    }

    public
    TestException(String _reason,
                  String reason,
                  int code)
    {
        super(_ob_id + " " + _reason);
        this.reason = reason;
        this.code = code;
    }

    public String reason;
    public int code;
}
