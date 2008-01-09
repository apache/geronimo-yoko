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

/**
 * @deprecated Deprecated by the Portable Object Adapter.
 */
public class DynamicImplementation extends
        org.omg.CORBA.portable.ObjectImpl {
    /**
     * @deprecated Deprecated by Portable Object Adapter
     */
    public String[] _ids() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
        
    /**
     * @deprecated Deprecated by Portable Object Adapter
     */
    public void invoke(ServerRequest request) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
}
