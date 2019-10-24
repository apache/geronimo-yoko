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

package org.apache.yoko.orb.OBCORBA;

import org.apache.yoko.orb.OB.ORBInstance;
import org.apache.yoko.orb.OB.TypeCodeFactory;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.OperationDef;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.Request;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.WrongTransaction;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA_2_4.ORB;

import java.applet.Applet;
import java.util.Properties;

// This class must be public
public class ORBSingleton_impl extends ORB {
    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String[] list_initial_services() {
        throw new NO_IMPLEMENT();
    }

    public org.omg.CORBA.Object resolve_initial_references(String name)
            throws InvalidName {
        throw new NO_IMPLEMENT();
    }

    public void register_initial_reference(String name, org.omg.CORBA.Object obj)
            throws InvalidName {
        throw new NO_IMPLEMENT();
    }

    public String object_to_string(org.omg.CORBA.Object object) {
        throw new NO_IMPLEMENT();
    }

    public org.omg.CORBA.Object string_to_object(String str) {
        throw new NO_IMPLEMENT();
    }

    public NVList create_list(int count) {
        throw new NO_IMPLEMENT();
    }

    /**
     * @deprecated Deprecated by CORBA 2.3.
     */
    public NVList create_operation_list(
            OperationDef oper) {
        throw new NO_IMPLEMENT();
    }

    public NVList create_operation_list(org.omg.CORBA.Object oper) {
        throw new NO_IMPLEMENT();
    }

    public NamedValue create_named_value(String name,
            Any value, int flags) {
        throw new NO_IMPLEMENT();
    }

    public ExceptionList create_exception_list() {
        throw new NO_IMPLEMENT();
    }

    public ContextList create_context_list() {
        throw new NO_IMPLEMENT();
    }

    public Context get_default_context() {
        throw new NO_IMPLEMENT();
    }

    public Environment create_environment() {
        throw new NO_IMPLEMENT();
    }

    public void send_multiple_requests_oneway(Request[] requests) {
        throw new NO_IMPLEMENT();
    }

    public void send_multiple_requests_deferred(Request[] requests) {
        throw new NO_IMPLEMENT();
    }

    public boolean poll_next_response() {
        throw new NO_IMPLEMENT();
    }

    public Request get_next_response()
            throws WrongTransaction {
        throw new NO_IMPLEMENT();
    }

    final public TypeCode create_struct_tc(String id,
            String name, StructMember[] members) {
        return TypeCodeFactory.createStructTC(id, name,
                members);
    }

    final public TypeCode create_union_tc(String id, String name,
            TypeCode discriminator_type,
            UnionMember[] members) {
        return TypeCodeFactory.createUnionTC(id, name,
                discriminator_type, members);
    }

    final public TypeCode create_enum_tc(String id, String name,
            String[] members) {
        return TypeCodeFactory.createEnumTC(id, name,
                members);
    }

    final public TypeCode create_alias_tc(String id, String name,
            TypeCode original_type) {
        return TypeCodeFactory.createAliasTC(id, name,
                original_type);
    }

    final public TypeCode create_exception_tc(String id,
            String name, StructMember[] members) {
        return TypeCodeFactory.createExceptionTC(id,
                name, members);
    }

    final public TypeCode create_interface_tc(String id,
            String name) {
        return TypeCodeFactory.createInterfaceTC(id,
                name);
    }

    final public TypeCode create_string_tc(int bound) {
        return TypeCodeFactory.createStringTC(bound);
    }

    final public TypeCode create_wstring_tc(int bound) {
        return TypeCodeFactory.createWStringTC(bound);
    }

    final public TypeCode create_fixed_tc(short digits,
            short scale) {
        return TypeCodeFactory.createFixedTC(digits,
                scale);
    }

    final public TypeCode create_sequence_tc(int bound,
            TypeCode element_type) {
        return TypeCodeFactory.createSequenceTC(bound,
                element_type);
    }

    /**
     * @deprecated
     */
    final public TypeCode create_recursive_sequence_tc(int bound,
            int offset) {
        return TypeCodeFactory
                .createRecursiveSequenceTC(bound, offset);
    }

    final public TypeCode create_array_tc(int length,
            TypeCode element_type) {
        return TypeCodeFactory.createArrayTC(length,
                element_type);
    }

    final public TypeCode create_value_tc(String id, String name,
            short type_modifier, TypeCode concrete_base,
            ValueMember[] members) {
        return TypeCodeFactory.createValueTC(id, name,
                type_modifier, concrete_base, members);
    }

    final public TypeCode create_value_box_tc(String id,
            String name, TypeCode boxed_type) {
        return TypeCodeFactory.createValueBoxTC(id,
                name, boxed_type);
    }

    final public TypeCode create_native_tc(String id, String name) {
        return TypeCodeFactory.createNativeTC(id, name);
    }

    final public TypeCode create_recursive_tc(String id) {
        return TypeCodeFactory.createRecursiveTC(id);
    }

    final public TypeCode create_abstract_interface_tc(String id,
            String name) {
        return TypeCodeFactory
                .createAbstractInterfaceTC(id, name);
    }

    final public TypeCode create_local_interface_tc(String id,
            String name) {
        return TypeCodeFactory.createLocalInterfaceTC(
                id, name);
    }

    final public TypeCode get_primitive_tc(
            TCKind kind) {
        return TypeCodeFactory.createPrimitiveTC(kind);
    }

    public boolean work_pending() {
        throw new NO_IMPLEMENT();
    }

    public void perform_work() {
        throw new NO_IMPLEMENT();
    }

    public void run() {
        throw new NO_IMPLEMENT();
    }

    public void shutdown(boolean wait_for_completion) {
        throw new NO_IMPLEMENT();
    }

    public void destroy() {
        throw new NO_IMPLEMENT();
    }

    public Any create_any() {
        return new org.apache.yoko.orb.CORBA.Any(_OB_ORBInstance());
    }

    public OutputStream create_output_stream() {
        throw new NO_IMPLEMENT();
    }

    public void connect(org.omg.CORBA.Object obj) {
        throw new NO_IMPLEMENT();
    }

    public void disconnect(org.omg.CORBA.Object obj) {
        throw new NO_IMPLEMENT();
    }

    public Policy create_policy(int policy_type,
            Any val) throws PolicyError {
        throw new NO_IMPLEMENT();
    }

    protected void set_parameters(String[] args, Properties props) {
        throw new NO_IMPLEMENT();
    }

    protected void set_parameters(Applet app,
                                  Properties props) {
        throw new NO_IMPLEMENT();
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public ORBSingleton_impl() {
    }

    public ORBInstance _OB_ORBInstance() {
        return null;
    }
}
