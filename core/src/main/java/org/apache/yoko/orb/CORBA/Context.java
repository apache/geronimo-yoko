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

package org.apache.yoko.orb.CORBA;
 
import org.apache.yoko.orb.OB.MinorCodes;
import org.omg.CORBA.CompletionStatus;

final public class Context extends org.omg.CORBA.Context {
    private org.omg.CORBA.ORB orb_;

    private String name_;

    private Context parent_;

    private java.util.Hashtable values_;

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String context_name() {
        return name_;
    }

    public org.omg.CORBA.Context parent() {
        return parent_;
    }

    public org.omg.CORBA.Context create_child(String child_ctx_name) {
        if (child_ctx_name == null) {
            throw new org.omg.CORBA.BAD_PARAM(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidName),
                org.apache.yoko.orb.OB.MinorCodes.MinorInvalidName, CompletionStatus.COMPLETED_NO);
        }

        Context ctx = new Context(orb_, child_ctx_name);
        ctx.parent_ = this;

        return ctx;
    }

    public void set_one_value(String propname, org.omg.CORBA.Any propvalue) {
        if (propname == null) {
            throw new org.omg.CORBA.BAD_PARAM(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidName),
                org.apache.yoko.orb.OB.MinorCodes.MinorInvalidName, CompletionStatus.COMPLETED_NO);
        }
        
        String s;
        try {
            s = propvalue.extract_string();
        } catch (org.omg.CORBA.BAD_OPERATION ex) {
            throw new org.omg.CORBA.BAD_TYPECODE(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPropertyType),
                org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPropertyType, CompletionStatus.COMPLETED_NO);
        }

        //
        // Set value
        //
        values_.put(propname, s);
    }

    public void set_values(org.omg.CORBA.NVList values) {
        //
        // Create new, empty list
        //
        values_.clear();

        //
        // Copy named values
        //
        for (int i = 0; i < values.count(); i++) {
            org.omg.CORBA.NamedValue nv = null;
            try {
                nv = values.item(i);
            } catch (org.omg.CORBA.Bounds ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            }

            String s = null;
            try {
                s = nv.value().extract_string();
            } catch (org.omg.CORBA.SystemException ex) {
                throw new org.omg.CORBA.BAD_TYPECODE(
                    org.apache.yoko.orb.OB.MinorCodes
                        .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPropertyType),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPropertyType, CompletionStatus.COMPLETED_NO);
            }

            if (nv.flags() != 0) {
                throw new org.omg.CORBA.INV_FLAG("Unsupported named value flag");
            }

            values_.put(nv.name(), s);
        }
    }

    public void delete_values(String pattern) {
        if (pattern == null) {
            throw new org.omg.CORBA.BAD_PARAM(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPattern),
                org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPattern, CompletionStatus.COMPLETED_NO);
        }

        //
        // Match the pattern
        //
        char last = 0;
        if (pattern.length() > 0)
            last = pattern.charAt(pattern.length() - 1);

        boolean found = false;

        java.util.Enumeration enumerator = values_.keys();
        while (enumerator.hasMoreElements()) {
            String key = (String) enumerator.nextElement();
            boolean match = false;

            if (last == '*') {
                //
                // Wildcard match?
                //
                if (key.startsWith(pattern.substring(0, pattern.length() - 1)))
                    match = true;
            } else {
                //
                // Regular match?
                //
                if (key.equals(pattern))
                    match = true;
            }

            if (match) {
                values_.remove(key);
                found = true;
            }
        }

        if (!found) {
            throw new org.omg.CORBA.BAD_CONTEXT(
               org.apache.yoko.orb.OB.MinorCodes
                   .describeBadContext(org.apache.yoko.orb.OB.MinorCodes.MinorNoPatternMatch),
               org.apache.yoko.orb.OB.MinorCodes.MinorNoPatternMatch, CompletionStatus.COMPLETED_NO);
        }
    }

    public org.omg.CORBA.NVList get_values(String start_scope, int op_flags,
            String pattern) {
        if (start_scope == null) {
            throw new org.omg.CORBA.BAD_PARAM(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidScope),
                org.apache.yoko.orb.OB.MinorCodes.MinorInvalidScope, CompletionStatus.COMPLETED_NO);
        }

        if (pattern == null) {
            throw new org.omg.CORBA.BAD_PARAM(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPattern),
                org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPattern, CompletionStatus.COMPLETED_NO);
        }

        java.util.Vector seq = new java.util.Vector();
        _OB_getValues(start_scope, op_flags, pattern, seq);

        if (seq.isEmpty())
            throw new org.omg.CORBA.BAD_CONTEXT(
               org.apache.yoko.orb.OB.MinorCodes
                   .describeBadContext(org.apache.yoko.orb.OB.MinorCodes.MinorNoPatternMatch),
               org.apache.yoko.orb.OB.MinorCodes.MinorNoPatternMatch, CompletionStatus.COMPLETED_NO);

        NVList values = new NVList(orb_);

        for (int i = 0; i < seq.size(); i += 2) {
            org.omg.CORBA.Any any = orb_.create_any();
            any.insert_string((String) seq.elementAt(i + 1));
            values.add_value((String) seq.elementAt(i), any, 0);
        }

        return values;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public Context(org.omg.CORBA.ORB orb, String name) {
        orb_ = orb;
        name_ = name;
        values_ = new java.util.Hashtable();
    }

    public Context(org.omg.CORBA.ORB orb, String name, String[] strings) {
        orb_ = orb;
        name_ = name;
        values_ = new java.util.Hashtable();
        for (int i = 0; i < strings.length; i += 2)
            values_.put(strings[i], strings[i + 1]);
    }

    void _OB_getValues(String start_scope, int op_flags, String pattern,
            java.util.Vector seq) {
        //
        // Don't do anything in this context if start_scope doesn't match name_
        //
        if (start_scope.length() != 0 && !start_scope.equals(name_)) {
            if (parent_ == null) {
                throw new org.omg.CORBA.BAD_CONTEXT(
                   org.apache.yoko.orb.OB.MinorCodes
                       .describeBadContext(org.apache.yoko.orb.OB.MinorCodes.MinorNoPatternMatch),
                   org.apache.yoko.orb.OB.MinorCodes.MinorNoPatternMatch, CompletionStatus.COMPLETED_NO);
            }

            parent_._OB_getValues(start_scope, op_flags, pattern, seq);
            return;
        }

        //
        // If there is a parent and scope is not restricted, get values
        // from parent. Otherwise create new value list.
        //
        if (op_flags != org.omg.CORBA.CTX_RESTRICT_SCOPE.value
                && parent_ != null)
            parent_._OB_getValues("", op_flags, pattern, seq);

        //
        // Match the pattern
        //
        char last = 0;
        if (pattern.length() > 0)
            last = pattern.charAt(pattern.length() - 1);

        java.util.Enumeration enumerator = values_.keys();
        while (enumerator.hasMoreElements()) {
            String key = (String) enumerator.nextElement();
            boolean match = false;

            if (last == '*') {
                //
                // Wildcard match?
                //
                if (key.startsWith(pattern.substring(0, pattern.length() - 1)))
                    match = true;
            } else {
                //
                // Regular match?
                //
                if (key.equals(pattern))
                    match = true;
            }

            if (match) {
                String value = (String) values_.get(key);

                //
                // First try to replace value
                //
                int j;
                for (j = 0; j < seq.size(); j += 2)
                    if (seq.elementAt(j).equals(key)) {
                        seq.setElementAt(value, j + 1);
                        break;
                    }

                //
                // Value not found, add to list
                //
                if (j == seq.size()) {
                    seq.addElement(key);
                    seq.addElement(value);
                }
            }
        }
    }

    java.util.Hashtable _OB_getValues() {
        return values_;
    }
}
