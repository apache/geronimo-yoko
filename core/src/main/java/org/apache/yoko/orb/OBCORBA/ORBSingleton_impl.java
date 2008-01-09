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

// This class must be public
public class ORBSingleton_impl extends org.omg.CORBA_2_4.ORB {
    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String[] list_initial_services() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.Object resolve_initial_references(String name)
            throws org.omg.CORBA.ORBPackage.InvalidName {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void register_initial_reference(String name, org.omg.CORBA.Object obj)
            throws org.omg.CORBA.ORBPackage.InvalidName {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public String object_to_string(org.omg.CORBA.Object object) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.Object string_to_object(String str) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.NVList create_list(int count) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * @deprecated Deprecated by CORBA 2.3.
     */
    public org.omg.CORBA.NVList create_operation_list(
            org.omg.CORBA.OperationDef oper) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.NVList create_operation_list(org.omg.CORBA.Object oper) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.NamedValue create_named_value(String name,
            org.omg.CORBA.Any value, int flags) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.ExceptionList create_exception_list() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.ContextList create_context_list() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.Context get_default_context() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.Environment create_environment() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void send_multiple_requests_oneway(org.omg.CORBA.Request[] requests) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void send_multiple_requests_deferred(org.omg.CORBA.Request[] requests) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public boolean poll_next_response() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.Request get_next_response()
            throws org.omg.CORBA.WrongTransaction {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    final public org.omg.CORBA.TypeCode create_struct_tc(String id,
            String name, org.omg.CORBA.StructMember[] members) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createStructTC(id, name,
                members);
    }

    final public org.omg.CORBA.TypeCode create_union_tc(String id, String name,
            org.omg.CORBA.TypeCode discriminator_type,
            org.omg.CORBA.UnionMember[] members) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createUnionTC(id, name,
                discriminator_type, members);
    }

    final public org.omg.CORBA.TypeCode create_enum_tc(String id, String name,
            String[] members) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createEnumTC(id, name,
                members);
    }

    final public org.omg.CORBA.TypeCode create_alias_tc(String id, String name,
            org.omg.CORBA.TypeCode original_type) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createAliasTC(id, name,
                original_type);
    }

    final public org.omg.CORBA.TypeCode create_exception_tc(String id,
            String name, org.omg.CORBA.StructMember[] members) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createExceptionTC(id,
                name, members);
    }

    final public org.omg.CORBA.TypeCode create_interface_tc(String id,
            String name) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createInterfaceTC(id,
                name);
    }

    final public org.omg.CORBA.TypeCode create_string_tc(int bound) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createStringTC(bound);
    }

    final public org.omg.CORBA.TypeCode create_wstring_tc(int bound) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createWStringTC(bound);
    }

    final public org.omg.CORBA.TypeCode create_fixed_tc(short digits,
            short scale) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createFixedTC(digits,
                scale);
    }

    final public org.omg.CORBA.TypeCode create_sequence_tc(int bound,
            org.omg.CORBA.TypeCode element_type) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createSequenceTC(bound,
                element_type);
    }

    /**
     * @deprecated
     */
    final public org.omg.CORBA.TypeCode create_recursive_sequence_tc(int bound,
            int offset) {
        return org.apache.yoko.orb.OB.TypeCodeFactory
                .createRecursiveSequenceTC(bound, offset);
    }

    final public org.omg.CORBA.TypeCode create_array_tc(int length,
            org.omg.CORBA.TypeCode element_type) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createArrayTC(length,
                element_type);
    }

    final public org.omg.CORBA.TypeCode create_value_tc(String id, String name,
            short type_modifier, org.omg.CORBA.TypeCode concrete_base,
            org.omg.CORBA.ValueMember[] members) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createValueTC(id, name,
                type_modifier, concrete_base, members);
    }

    final public org.omg.CORBA.TypeCode create_value_box_tc(String id,
            String name, org.omg.CORBA.TypeCode boxed_type) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createValueBoxTC(id,
                name, boxed_type);
    }

    final public org.omg.CORBA.TypeCode create_native_tc(String id, String name) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createNativeTC(id, name);
    }

    final public org.omg.CORBA.TypeCode create_recursive_tc(String id) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createRecursiveTC(id);
    }

    final public org.omg.CORBA.TypeCode create_abstract_interface_tc(String id,
            String name) {
        return org.apache.yoko.orb.OB.TypeCodeFactory
                .createAbstractInterfaceTC(id, name);
    }

    final public org.omg.CORBA.TypeCode create_local_interface_tc(String id,
            String name) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createLocalInterfaceTC(
                id, name);
    }

    final public org.omg.CORBA.TypeCode get_primitive_tc(
            org.omg.CORBA.TCKind kind) {
        return org.apache.yoko.orb.OB.TypeCodeFactory.createPrimitiveTC(kind);
    }

    public boolean work_pending() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void perform_work() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void run() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void shutdown(boolean wait_for_completion) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void destroy() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.Any create_any() {
        return new org.apache.yoko.orb.CORBA.Any(_OB_ORBInstance());
    }

    public org.omg.CORBA.portable.OutputStream create_output_stream() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void connect(org.omg.CORBA.Object obj) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void disconnect(org.omg.CORBA.Object obj) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.Policy create_policy(int policy_type,
            org.omg.CORBA.Any val) throws org.omg.CORBA.PolicyError {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    protected void set_parameters(String[] args, java.util.Properties props) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    protected void set_parameters(java.applet.Applet app,
            java.util.Properties props) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public ORBSingleton_impl() {
    }

    public org.apache.yoko.orb.OB.ORBInstance _OB_ORBInstance() {
        return null;
    }
}
