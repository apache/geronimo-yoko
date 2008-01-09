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

import org.omg.CORBA.AttributeDescription;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;

/**
 * @author krab
 */
public class FVDValueDescriptor extends ValueDescriptor {
    FullValueDescription fvd;

    String repid;

    FVDValueDescriptor(FullValueDescription fvd, Class clazz,
            TypeRepository rep, String repid, ValueDescriptor super_desc) {
        super(clazz, rep);

        this.repid = repid;
        this.fvd = fvd;

        init();

        this._super_descriptor = super_desc;
    }

    public void init() {
        super.init();

        // iverride custom loading
        if (!fvd.is_custom) {
            _read_object_method = null;
            _write_object_method = null;
            _is_externalizable = false;
        }

        AttributeDescription[] atts = fvd.attributes;
        FieldDescriptor[] new_fields = new FieldDescriptor[atts.length];
        for (int i = 0; i < atts.length; i++) {
            AttributeDescription att = atts[i];
            new_fields[i] = findField(att);
        }

        _fields = new_fields;
    }

    FieldDescriptor findField(AttributeDescription att) {
        FieldDescriptor result = null;

        for (Class c = getJavaClass(); c != null; c = c.getSuperclass()) {
            TypeDescriptor td = getTypeRepository().getDescriptor(c);
            if (td instanceof ValueDescriptor) {
                ValueDescriptor vd = (ValueDescriptor) td;
                FieldDescriptor[] fds = vd._fields;

                if (fds == null) {
                    continue;
                }

                for (int i = 0; i < fds.length; i++) {
                    if (fds[i].getIDLName().equals(att.name)) {
                        return fds[0];
                    }
                }
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.yoko.rmi.impl.TypeDescriptor#getRepositoryID()
     */
    public String getRepositoryID() {
        return repid;
    }

    org.omg.CORBA.ValueDefPackage.FullValueDescription getFullValueDescription() {
        return fvd;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.yoko.rmi.impl.TypeDescriptor#getTypeCode()
     */
    TypeCode getTypeCode() {
        return fvd.type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.yoko.rmi.impl.TypeDescriptor#isCustomMarshalled()
     */
    public boolean isCustomMarshalled() {
        return fvd.is_custom;
    }

}
