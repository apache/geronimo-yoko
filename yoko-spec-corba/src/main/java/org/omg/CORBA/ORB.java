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

import org.apache.yoko.osgi.ProviderLocator;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.portable.OutputStream;

import java.applet.Applet;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;
import java.util.Properties;

import static java.lang.Thread.currentThread;
import static java.security.AccessController.doPrivileged;
import static org.omg.CORBA.ORB.OrbSingletonHolder.ORB_SINGLETON;

public abstract class ORB {

    public abstract String[] list_initial_services();

    public abstract org.omg.CORBA.Object resolve_initial_references(String object_name) throws InvalidName;

    public abstract String object_to_string(org.omg.CORBA.Object object);

    public abstract org.omg.CORBA.Object string_to_object(String str);

    public abstract NVList create_list(int count);

    /**
     * @deprecated Deprecated by CORBA 2.3.
     */
    public abstract NVList create_operation_list(OperationDef oper);

    // Empty method for binary compatibility with the 1.5
    public NVList create_operation_list(org.omg.CORBA.Object oper) {return null;};

    public abstract NamedValue create_named_value(String name, Any value,
            int flags);

    public abstract ExceptionList create_exception_list();

    public abstract ContextList create_context_list();

    public abstract Context get_default_context();

    public abstract Environment create_environment();

    public abstract void send_multiple_requests_oneway(Request[] req);

    public abstract void send_multiple_requests_deferred(Request[] req);

    public abstract boolean poll_next_response();

    public abstract Request get_next_response() throws WrongTransaction;

    public boolean get_service_information(short service_type, ServiceInformationHolder service_info) {
        throw new NO_IMPLEMENT();
    }

    public abstract TypeCode create_struct_tc(String id, String name,
            StructMember[] members);

    public abstract TypeCode create_union_tc(String id, String name,
            TypeCode discriminatorType, UnionMember[] members);

    public abstract TypeCode create_enum_tc(String id, String name,
            String[] members);

    public abstract TypeCode create_alias_tc(String id, String name,
            TypeCode originalType);

    public abstract TypeCode create_exception_tc(String id, String name,
            StructMember[] members);

    public abstract TypeCode create_interface_tc(String id, String name);

    public abstract TypeCode create_string_tc(int bound);

    public abstract TypeCode create_wstring_tc(int bound);

    public TypeCode create_fixed_tc(short digits, short scale) {
        throw new NO_IMPLEMENT();
    }

    public abstract TypeCode create_sequence_tc(int bound, TypeCode elementType);

    /**
     * @deprecated Deprecated by CORBA 2.3.
     */
    public abstract TypeCode create_recursive_sequence_tc(int bound, int offset);

    public abstract TypeCode create_array_tc(int length, TypeCode elementType);

    public TypeCode create_value_tc(String id, String name, short type_modifier, TypeCode concrete_base, ValueMember[] members) {
        throw new NO_IMPLEMENT();
    }

    public TypeCode create_value_box_tc(String id, String name, TypeCode boxed_type) {
        throw new NO_IMPLEMENT();
    }

    public TypeCode create_native_tc(String id, String name) {
        throw new NO_IMPLEMENT();
    }

    public TypeCode create_recursive_tc(String id) {
        throw new NO_IMPLEMENT();
    }

    public TypeCode create_abstract_interface_tc(String id, String name) {
        throw new NO_IMPLEMENT();
    }

    public abstract TypeCode get_primitive_tc(TCKind kind);

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

    public abstract Any create_any();

    public abstract OutputStream create_output_stream();

    // Empty method for binary compatibility with the 1.5
    public void connect(org.omg.CORBA.Object obj) {};

    // Empty method for binary compatibility with the 1.5
    public void disconnect(org.omg.CORBA.Object obj) {};

    public Policy create_policy(int policy_type, Any val) throws PolicyError {
        throw new NO_IMPLEMENT();
    }

    /**
     * @deprecated Deprecated by CORBA 2.2.
     */
    public Current get_current() {
        throw new NO_IMPLEMENT();
    }

    private static final String ORBClassPropertyKey = "org.omg.CORBA.ORBClass";
    private static final String ORBSingletonPropertyKey = "org.omg.CORBA.ORBSingletonClass";

    enum OrbSingletonHolder {
        ;
        static final ORB ORB_SINGLETON = createSingletonOrb();
    }

    public static ORB init(String[] args, Properties props) {

        ORB orb = newOrb(props);

        orb.set_parameters(args, props);

        return orb;
    }

    public static ORB init(Applet app, Properties props) {
        ORB orb = newOrb(props);

        orb.set_parameters(app, props);

        return orb;
    }

    private static ORB newOrb(Properties props) {
        return newOrb(ORBClassPropertyKey, (null == props) ? null : props.getProperty(ORBClassPropertyKey));
    }

    private static ORB createSingletonOrb() {
        return newOrb(ORBSingletonPropertyKey, null);
    }

    private static ORB newOrb(String propertyKey, String orbClassName) {
        final ClassLoader contextClassLoader = doPriv(currentThread()::getContextClassLoader);

        if (null == orbClassName) {
            try {
                return Objects.requireNonNull((ORB)ProviderLocator.getService(propertyKey, ORB.class, contextClassLoader));
            } catch (NullPointerException ignored) { // ORB not found, but without exception
            } catch (Exception ex) {
                throw (INITIALIZE)new INITIALIZE(String.format("Invalid %s class from osgi: ",propertyKey)).initCause(ex);
            }
            orbClassName = doPriv(() -> System.getProperty(propertyKey, "org.apache.yoko.orb.CORBA.ORB"));
        }

        try {
            final Class<? extends ORB> orbClass = ProviderLocator.loadClass(orbClassName, ORB.class, contextClassLoader);
            return doPrivEx(orbClass::getConstructor).newInstance();
        } catch (Throwable ex) {
            throw (INITIALIZE)new INITIALIZE(String.format("Invalid %s class: %s", propertyKey, orbClassName)).initCause(ex);
        }
    }

    private static <T> T doPriv(PrivilegedAction<T> action) { return doPrivileged(action); }
    private static <T> T doPrivEx(PrivilegedExceptionAction<T> action) throws PrivilegedActionException { return doPrivileged(action); }


    public static ORB init() {
        return ORB_SINGLETON;
    }

    protected abstract void set_parameters(String[] args, Properties props);

    protected abstract void set_parameters(Applet app, Properties props);


}
