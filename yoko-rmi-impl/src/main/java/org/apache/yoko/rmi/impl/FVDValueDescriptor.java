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

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;

/**
 * @author krab
 */
class FVDValueDescriptor extends ValueDescriptor {
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

        // don't override custom loading. Our local version could work differently.
//        if (!fvd.is_custom) {
//            _read_object_method = null;
//            _write_object_method = null;
//            _is_externalizable = false;
//        }

        ValueMember[] members = fvd.members;
        FieldDescriptor[] new_fields = new FieldDescriptor[members.length];
        for (int i = 0; i < members.length; i++) {
            ValueMember valueMember = members[i];
            new_fields[i] = findField(valueMember);
        }

        _fields = new_fields;
    }

    FieldDescriptor findField(ValueMember valueMember) {
        FieldDescriptor result = null;

        for (Class c = getJavaClass(); c != null; c = c.getSuperclass()) {
            TypeDescriptor td = repo.getDescriptor(c);
            if (td instanceof ValueDescriptor) {
                ValueDescriptor vd = (ValueDescriptor) td;
                FieldDescriptor[] fds = vd._fields;

                if (fds == null) {
                    continue;
                }

                for (int i = 0; i < fds.length; i++) {
                    if (fds[i].getIDLName().equals(valueMember.name)) {
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
    public TypeCode getTypeCode() {
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
