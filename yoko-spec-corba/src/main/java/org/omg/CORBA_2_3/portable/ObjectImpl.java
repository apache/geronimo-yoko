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
/***** Copyright (c) 1999 Object Management Group. Unlimited rights to 
       duplicate and use this code are hereby granted provided that this 
       copyright notice is included.
*****/

package org.omg.CORBA_2_3.portable;

public abstract class ObjectImpl extends org.omg.CORBA.portable.ObjectImpl {

    public String _get_codebase() {
        org.omg.CORBA.portable.Delegate delegate = _get_delegate();
        if (delegate instanceof org.omg.CORBA_2_3.portable.Delegate)
            return ((org.omg.CORBA_2_3.portable.Delegate)
                        delegate).get_codebase(this);
        return null;
    }
}
