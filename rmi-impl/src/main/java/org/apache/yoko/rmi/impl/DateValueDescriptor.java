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

import java.util.Date;

/**
 * @author krab
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class DateValueDescriptor extends ValueDescriptor {

    /**
     * Constructor for DateValueDescriptor.
     * 
     * @param type
     * @param repository
     */
    public DateValueDescriptor(TypeRepository repository) {
        super(Date.class, repository);
    }

    Object copyObject(Object orig, CopyState state) {
        Date result = (Date) (((Date) orig).clone());
        state.put(orig, result);
        return result;
    }

}
