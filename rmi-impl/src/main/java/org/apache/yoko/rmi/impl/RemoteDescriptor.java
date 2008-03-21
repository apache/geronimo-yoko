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

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import javax.rmi.PortableRemoteObject;

public abstract class RemoteDescriptor extends TypeDescriptor {
    private java.util.Map method_map;

    private java.util.Map refl_method_map;

    private MethodDescriptor[] operations;

    private Class[] remote_interfaces;

    protected List super_descriptors;

    public RemoteInterfaceDescriptor getRemoteInterface() {
        RemoteInterfaceDescriptor result = super.getRemoteInterface();
        if (result != null) { 
            return result;
        }

        if (this instanceof RemoteInterfaceDescriptor) {
            result = (RemoteInterfaceDescriptor) this;
        } else {
            Class[] remotes = collect_remote_interfaces(getJavaClass());
            if (remotes.length == 0) {
                throw new RuntimeException(getJavaClass().getName()
                        + " has no remote interfaces");
            }
            Class most_specific_interface = remotes[0];

            result = (RemoteInterfaceDescriptor) repository
                    .getDescriptor(most_specific_interface);
        }

        setRemoteInterface(result);

        return result;
    }

    static final Class REMOTE_CLASS = java.rmi.Remote.class;

    static final Class OBJECT_CLASS = java.lang.Object.class;

    static final java.lang.Class REMOTE_EXCEPTION = java.rmi.RemoteException.class;

    String[] _ids;

    public String[] all_interfaces() {
        if (_ids == null) {
            Class[] ifaces = collect_remote_interfaces(getJavaClass());
            int len = ifaces.length;
            String[] ids = new String[len];
            for (int i = 0; i < len; i++) {
                TypeDescriptor desc = repository.getDescriptor(ifaces[i]);
                ids[i] = desc.getRepositoryID();
            }

            _ids = ids;
        }

        return _ids;
    }

    public MethodDescriptor getMethod(String idl_name) {
        if (operations == null) {
            init_methods();
        }

        if (method_map == null) {
            method_map = new java.util.HashMap();
            for (int i = 0; i < operations.length; i++) {
                method_map.put(operations[i].getIDLName(), operations[i]);
            }
        }

        return (MethodDescriptor) method_map.get(idl_name);
    }

    void debugMethodMap() {
        if (logger.isLoggable(Level.FINER)) {
            logger.finer("METHOD MAP FOR " + getJavaClass().getName());

            Iterator it = method_map.keySet().iterator();
            while (it.hasNext()) {
                String idl_name = (String) it.next();
                MethodDescriptor desc = (MethodDescriptor) method_map.get(idl_name);
                logger.finer("IDL " + idl_name + " -> "+ desc.reflected_method);
            }
        }
    }

    public MethodDescriptor getMethod(Method refl_method) {
        if (operations == null) {
            init_methods();
        }

        if (refl_method_map == null) {
            refl_method_map = new java.util.HashMap();
            for (int i = 0; i < operations.length; i++) {
                refl_method_map.put(operations[i].getReflectedMethod(), operations[i]);
            }
        }

        return (MethodDescriptor) refl_method_map.get(refl_method);
    }

    RemoteDescriptor(Class type, TypeRepository repository) {
        super(type, repository);
    }

    public void init() {
    }

    public MethodDescriptor[] getMethods() {
        if (operations == null) {
            init_methods();
        }
        return operations;
    }

    public synchronized void init_methods() {
        if (operations != null) {
            return;
        }

        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                init_methods0();
                return null;
            }
        });
    }

    private void init_methods0() {

        ArrayList method_list = new ArrayList();

        // first step is to build the helpers for any super classes
        Class[] supers = getJavaClass().getInterfaces();
        super_descriptors = new ArrayList();

        Map all_methods = new HashMap();
        Map lower_case_names = new HashMap();
        for (int i = 0; i < supers.length; i++) {
            Class iface = supers[i];

            if (!REMOTE_CLASS.equals(iface) && !OBJECT_CLASS.equals(iface)
                    && REMOTE_CLASS.isAssignableFrom(iface)
                    && iface.isInterface()) {
                RemoteDescriptor superHelper = (RemoteDescriptor) repository
                        .getDescriptor(iface);

                super_descriptors.add(superHelper);

                MethodDescriptor[] superOps = superHelper.getMethods();
                for (int j = 0; j < superOps.length; j++) {
                    MethodDescriptor op = superOps[j];

                    method_list.add(op);
                    addMethodOverloading(all_methods, op.getReflectedMethod());
                    addMethodCaseSensitive(lower_case_names, op.getReflectedMethod());
                }
            }
        }

        // next, build the method helpers for this class
        Method[] methods = getLocalMethods();

        // register methods
        for (int i = 0; i < methods.length; i++) {
            addMethodOverloading(all_methods, methods[i]);
            addMethodCaseSensitive(lower_case_names, methods[i]);
        }

        Set overloaded_names = new HashSet();
        Iterator it = all_methods.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String mname = (String) entry.getKey();
            Set s = (Set) entry.getValue();
            if (s.size() > 1) {
                overloaded_names.add(mname);
            }
        }

        for (int i = 0; i < methods.length; i++) {
            MethodDescriptor op = new MethodDescriptor(methods[i], repository);

            String mname = op.getJavaName();

            // is there another method that differs only in case?
            Set same_case_names = (Set) lower_case_names.get(mname.toLowerCase());
            if (same_case_names.size() > 1) {
                op.setCaseSensitive(true);
            }

            // is this method overloaded?
            Set overload_names = (Set) all_methods.get(mname);
            if (overload_names.size() > 1) {
                op.setOverloaded(true);
            }

            op.init();

            method_list.add(op);
        }

        // init method map...
        method_map = new java.util.HashMap();
        for (int i = 0; i < method_list.size(); i++) {
            MethodDescriptor desc = (MethodDescriptor) method_list.get(i);
            logger.finer("Adding method " + desc.getJavaName() + " to method map under " + desc.getIDLName()); 
            method_map.put(desc.getIDLName(), desc);
        }

        //
        // initialize "operations" from the values of the map, such
        // that repeat methods are eliminated.
        //
        operations = (MethodDescriptor[]) method_map.values().toArray(
                new MethodDescriptor[0]);

        debugMethodMap();
    }

    private void addMethodOverloading(Map map, Method m) {
        String mname = m.getName();
        Set entry = (Set) map.get(mname);

        if (entry == null) {
            entry = new HashSet();
            map.put(mname, entry);
        }

        entry.add(createMethodSelector(m));
    }

    Method[] getLocalMethods() {
        ArrayList result = new ArrayList();

        addNonRemoteInterfaceMethods(getJavaClass(), result);

        Method[] out = new Method[result.size()];
        result.toArray(out);
        return out;
    }

    void addNonRemoteInterfaceMethods(Class clz, ArrayList result) {
        Method[] methods;
        try {
            methods = clz.getDeclaredMethods();
        } catch (NoClassDefFoundError e) {
            ClassLoader clzClassLoader = clz.getClassLoader();
            logger.log(Level.FINER, "cannot find class " + e.getMessage() + " from "
                    + clz.getName() + " (classloader " + clzClassLoader + "): "
                    + e.getMessage(), e);
            throw e;
        }
        for (int j = 0; j < methods.length; j++) {
            // since this is a remote interface, we need to add everything
            result.add(methods[j]);
        }

        Class[] ifaces = clz.getInterfaces();
        for (int i = 0; i < ifaces.length; i++) {
            if (!REMOTE_CLASS.isAssignableFrom(ifaces[i])) {
                addNonRemoteInterfaceMethods(ifaces[i], result);
            }
        }
    }

    boolean isRemoteMethod(Method m) {
        Class[] ex = m.getExceptionTypes();

        for (int i = 0; i < ex.length; i++) {
            if (REMOTE_EXCEPTION.isAssignableFrom(ex[i]))
                return true;
        }

        return false;
    }

    private static String createMethodSelector(java.lang.reflect.Method m) {
        StringBuffer sb = new StringBuffer(m.getName());
        sb.append('(');
        Class[] parameterTypes = m.getParameterTypes();
        for (int n = 0; n < parameterTypes.length; n++) {
            sb.append(parameterTypes[n].getName());
            if (n < parameterTypes.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(')');
        return sb.toString().intern();
    }

    private void addMethodCaseSensitive(Map map, Method m) {
        String mname = m.getName();
        String lowname = mname.toLowerCase();
        Set entry = (Set) map.get(lowname);

        if (entry == null) {
            entry = new HashSet();
            map.put(lowname, entry);
        }

        entry.add(mname);
    }

    private void collect_interfaces(Set s, Class c) {
        if (c.isInterface() && !REMOTE_CLASS.equals(c))
            s.add(c);

        Class sup = c.getSuperclass();
        if (sup != null && !OBJECT_CLASS.equals(sup)) {
            collect_interfaces(s, sup);
        }

        Class[] supers = c.getInterfaces();

        for (int i = 0; i < supers.length; i++) {
            Class iface = supers[i];

            if (!REMOTE_CLASS.equals(iface)
                    && REMOTE_CLASS.isAssignableFrom(iface)) {
                collect_interfaces(s, iface);
            }
        }
    }

    protected Class[] collect_remote_interfaces(Class c) {
        if (remote_interfaces != null)
            return remote_interfaces;

        Set s = new TreeSet(new Comparator() {
            public int compare(Object o1, Object o2) {
                Class c1 = (Class) o1;
                Class c2 = (Class) o2;

                if (c1.equals(c2))
                    return 0;

                if (c1.isAssignableFrom(c2)) {
                    // c2 is more specific (so it should come first)

                    return 1;
                } else if (c2.isAssignableFrom(c1)) {
                    // c1 is more specific (so it should come first)

                    return -1;
                } else // they are unrelated
                {
                    // just define some consistent order...
                    return c1.getName().compareTo(c2.getName());
                }
            }
        });

        collect_interfaces(s, c);
        remote_interfaces = new Class[s.size()];
        s.toArray(remote_interfaces);
        return remote_interfaces;
    }

    /** Read an instance of this value from a CDR stream */
    public Object read(org.omg.CORBA.portable.InputStream in) {
        return javax.rmi.PortableRemoteObject.narrow(in.read_Object(),
                getJavaClass());
    }

    /** Write an instance of this value to a CDR stream */
    public void write(org.omg.CORBA.portable.OutputStream out, Object val) {
        javax.rmi.CORBA.Util.writeRemoteObject(out, val);
    }

    org.omg.CORBA.TypeCode getTypeCode() {
        if (_type_code == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            return orb.create_interface_tc(getRepositoryID(), getJavaClass()
                    .getName());
        }

        return _type_code;
    }

    void writeMarshalValue(java.io.PrintWriter pw, String outName,
            String paramName) {
        pw.print("javax.rmi.CORBA.Util.writeRemoteObject(");
        pw.print(outName);
        pw.print(',');
        pw.print(paramName);
        pw.print(')');
    }

    void writeUnmarshalValue(java.io.PrintWriter pw, String inName) {
        pw.print('(');
        pw.print(getJavaClass().getName());
        pw.print(')');
        pw.print(PortableRemoteObject.class.getName());
        pw.print(".narrow(");
        pw.print(inName);
        pw.print('.');
        pw.print("read_Object(),");
        pw.print(getJavaClass().getName());
        pw.print(".class)");
    }

    static String classNameFromStub(String name) {
        if (name.startsWith("org.omg.stub."))
            name = name.substring("org.omg.stub.".length());

        // strip xx._X_Stub -> xx.X
        int idx = name.lastIndexOf('.');
        if (name.charAt(idx + 1) == '_' && name.endsWith("_Stub")) {
            if (idx == -1) {
                return name.substring(1, name.length() - 5);
            } else {
                return name.substring(0, idx + 1) /* package. */
                        + name.substring(idx + 2, name.length() - 5);
            }
        }

        return null;
    }

    static String stubClassName(Class c) {

        String cname = c.getName();

        String pkgname = null;
        int idx = cname.lastIndexOf('.');
        if (idx == -1) {
            pkgname = "org.omg.stub";
        } else {
            pkgname = "org.omg.stub." + cname.substring(0, idx);
        }

        String cplain = cname.substring(idx + 1);

        return pkgname + "._" + cplain + "_Stub";
    }

    void writeStubClass(java.io.PrintWriter pw) {

        Class c = getJavaClass();
        String cname = c.getName();
        String fullname = stubClassName(c);
        //String stubname = fullname.substring(fullname.lastIndexOf('.') + 1);
        String pkgname = fullname.substring(0, fullname.lastIndexOf('.'));
        String cplain = cname.substring(cname.lastIndexOf('.') + 1);

        pw.println("/** ");
        pw.println(" *  RMI/IIOP stub for " + cname);
        pw.println(" *  Generated using Apache Yoko stub generator.");
        pw.println(" */");

        pw.println("package " + pkgname + ";\n");

        pw.println("public class _" + cplain + "_Stub");
        pw.println("\textends javax.rmi.CORBA.Stub");
        pw.println("\timplements " + cname);
        pw.println("{");

        //
        // construct String[] _ids;
        //
        String[] all_interfaces = all_interfaces();
        pw.println("\tprivate static final String[] _ids = {");
        for (int i = 0; i < all_interfaces.length; i++) {
            pw.println("\t\t\"" + all_interfaces[i] + "\",");
        }
        pw.println("\t};\n");

        pw.println("\tpublic String[] _ids() {");
        pw.println("\t\treturn _ids;");
        pw.println("\t}");

        //
        // now, construct stub methods
        //
        MethodDescriptor[] meths = getMethods();
        for (int i = 0; i < meths.length; i++) {
            meths[i].writeStubMethod(pw);
        }

        pw.println("}");
    }

    String getStubClassName() {
        Class c = getJavaClass();
        String cname = c.getName();

        String pkgname = null;
        int idx = cname.lastIndexOf('.');
        if (idx == -1) {
            pkgname = "org.omg.stub";
        } else {
            pkgname = "org.omg.stub." + cname.substring(0, idx);
        }

        String cplain = cname.substring(idx + 1);

        return pkgname + "." + "_" + cplain + "_Stub";
    }

    void addDependencies(Set classes) {
        Class c = getJavaClass();

        if (c == java.rmi.Remote.class || classes.contains(c))
            return;

        classes.add(c);

        if (c.getSuperclass() != null) {
            TypeDescriptor desc = getTypeRepository().getDescriptor(
                    c.getSuperclass());
            desc.addDependencies(classes);
        }

        Class[] ifaces = c.getInterfaces();
        for (int i = 0; i < ifaces.length; i++) {
            TypeDescriptor desc = getTypeRepository().getDescriptor(ifaces[i]);
            desc.addDependencies(classes);
        }

        MethodDescriptor[] mths = getMethods();
        for (int i = 0; i < mths.length; i++) {
            mths[i].addDependencies(classes);
        }
    }

    boolean copyInStub() {
        return false;
    }
}
