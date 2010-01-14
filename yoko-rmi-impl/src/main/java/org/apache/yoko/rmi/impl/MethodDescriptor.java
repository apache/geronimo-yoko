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

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.rmi.MarshalException;
import java.rmi.RemoteException;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.rmi.CORBA.Util;

import org.omg.CORBA.ORB;

public final class MethodDescriptor extends ModelElement {
    static final Logger logger = Logger.getLogger(MethodDescriptor.class.getName());

    /** The refleced method object for this method */
    java.lang.reflect.Method reflected_method;

    java.lang.Object invocation_block_selector;

    boolean onewayInitialized = false;

    boolean oneway = false;

    public boolean responseExpected() {
        if (!onewayInitialized) {

            if (!reflected_method.getReturnType().equals(Void.TYPE)) {
                onewayInitialized = true;
                oneway = false;
                return true;
            }

            Class[] exceptions = reflected_method.getExceptionTypes();
            for (int i = 0; i < exceptions.length; i++) {
                if (exceptions[i] == org.apache.yoko.rmi.api.RemoteOnewayException.class) {
                    oneway = true;
                    break;
                }
            }

            onewayInitialized = true;
        }

        return !oneway;
    }

    public java.lang.reflect.Method getReflectedMethod() {
        return reflected_method;
    }

    MethodDescriptor(java.lang.reflect.Method method, TypeRepository repository) {
        reflected_method = method;
        setTypeRepository(repository);
        setJavaName(method.getName());
    }

    /** The number of arguments */
    int parameter_count;

    /** The argument's type descriptors */
    TypeDescriptor[] parameter_types;

    /** The return value's type descriptor */
    TypeDescriptor return_type;

    /** The declared exception's type descriptors */
    ExceptionDescriptor[] exception_types;

    boolean copyWithinState;

    /**
     * Copy a set of arguments. If sameState=true, then we're invoking on the
     * same RMIState, i.e. an environment with the same context class loader.
     */
    Object[] copyArguments(Object[] args, boolean sameState, ORB orb)
            throws RemoteException {
        if (!sameState) {

            try {
                org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) orb
                        .create_output_stream();

                for (int i = 0; i < args.length; i++) {
                    if (parameter_types[i].copyBetweenStates()) {
                        parameter_types[i].write(out, args[i]);
                    }
                }

                org.omg.CORBA_2_3.portable.InputStream in = (org.omg.CORBA_2_3.portable.InputStream) out
                        .create_input_stream();

                for (int i = 0; i < args.length; i++) {
                    if (parameter_types[i].copyBetweenStates()) {
                        args[i] = parameter_types[i].read(in);
                    }
                }

            } catch (org.omg.CORBA.SystemException ex) {
                logger.log(Level.FINE, "Exception occurred copying arguments", ex); 
                throw Util.mapSystemException(ex);
            }

        } else if (copyWithinState) {
            CopyState state = new CopyState(getTypeRepository());
            for (int i = 0; i < args.length; i++) {
                if (parameter_types[i].copyWithinState()) {
                    try {
                        args[i] = state.copy(args[i]);
                    } catch (CopyRecursionException e) {
                        final int idx = i;
                        final Object[] args_arr = args;
                        state.registerRecursion(new CopyRecursionResolver(
                                args[i]) {
                            public void resolve(Object value) {
                                args_arr[idx] = value;
                            }
                        });
                    }
                }
            }
        }

        return args;
    }

    Object copyResult(Object result, boolean sameState, ORB orb)
            throws RemoteException {
        if (result == null) {
            return null;
        }
        if (!sameState) {
            if (return_type.copyBetweenStates()) {
                try {
                    org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) orb
                            .create_output_stream();

                    return_type.write(out, result);

                    org.omg.CORBA_2_3.portable.InputStream in = (org.omg.CORBA_2_3.portable.InputStream) out
                            .create_input_stream();

                    return return_type.read(in);
                } catch (org.omg.CORBA.SystemException ex) {
                    logger.log(Level.FINE, "Exception occurred copying result", ex); 
                    throw Util.mapSystemException(ex);
                }
            }

        } else if (copyWithinState) {
            CopyState state = new CopyState(getTypeRepository());
            try {
                return state.copy(result);
            } catch (CopyRecursionException e) {
                throw new MarshalException("cannot copy recursive value?");
            }
        }

        return result;
    }

    /**
     * read the arguments to this method, and return them as an array of objects
     */
    public Object[] readArguments(org.omg.CORBA.portable.InputStream in) {
        Object[] args = new Object[parameter_count];
        for (int i = 0; i < parameter_count; i++) {
            args[i] = parameter_types[i].read(in);
        }
        return args;
    }

    public void writeArguments(org.omg.CORBA.portable.OutputStream out,
            Object[] args) {
        /*
         * if (log.isDebugEnabled ()) { java.util.Map recurse = new
         * org.apache.yoko.rmi.util.IdentityHashMap (); java.io.CharArrayWriter cw = new
         * java.io.CharArrayWriter (); java.io.PrintWriter pw = new
         * java.io.PrintWriter (cw);
         * 
         * pw.print ("invoking "); pw.print (reflected_method.toString ());
         * 
         * for (int i = 0; i < parameter_count; i++) { pw.print (" arg["+i+"] =
         * "); if (args[i] == null) { pw.write ("null"); } else { TypeDescriptor
         * desc;
         * 
         * try { desc = getTypeRepository ().getDescriptor (args[i].getClass
         * ()); } catch (RuntimeException ex) { desc = parameter_types[i]; }
         * 
         * desc.print (pw, recurse, args[i]); } }
         * 
         * pw.close (); log.debug (cw.toString ()); }
         */

        for (int i = 0; i < parameter_count; i++) {
            parameter_types[i].write(out, args[i]);
        }
    }

    /** write the result of this method */
    public void writeResult(org.omg.CORBA.portable.OutputStream out,
            Object value) {
        return_type.write(out, value);
    }

    static final String UNKNOWN_EXCEPTION_ID = "IDL:omg.org/CORBA/portable/UnknownException:1.0";

    public org.omg.CORBA.portable.OutputStream writeException(
            org.omg.CORBA.portable.ResponseHandler response, Throwable ex) {
        for (int i = 0; i < exception_types.length; i++) {
            if (exception_types[i].getJavaClass().isInstance(ex)) {
                org.omg.CORBA.portable.OutputStream out = response
                        .createExceptionReply();
                org.omg.CORBA_2_3.portable.OutputStream out2 = (org.omg.CORBA_2_3.portable.OutputStream) out;

                out2
                        .write_string(exception_types[i]
                                .getExceptionRepositoryID());

                out2.write_value((java.io.Serializable) ex);
                return out;
            }
        }

        logger.log(Level.WARNING, "unhandled exception: " + ex.getMessage(), ex);

        throw new org.omg.CORBA.portable.UnknownException(ex);
    }

    public void readException(org.omg.CORBA.portable.InputStream in)
            throws Throwable {
        org.omg.CORBA_2_3.portable.InputStream in2 = (org.omg.CORBA_2_3.portable.InputStream) in;

        String ex_id = in.read_string();
        Throwable ex = null;

        for (int i = 0; i < exception_types.length; i++) {
            if (ex_id.equals(exception_types[i].getExceptionRepositoryID())) {
                ex = (Throwable) in2.read_value();
                throw ex;
            }
        }

        ex = (Throwable) in2.read_value();

        if (ex instanceof Exception) {
            throw new java.rmi.UnexpectedException(ex.getMessage(),
                    (Exception) ex);
        } else if (ex instanceof Error) {
            throw ex;
        }
    }

    public Object readResult(org.omg.CORBA.portable.InputStream in) {
        Object result = return_type.read(in);

        /*
         * if (log.isDebugEnabled ()) { java.util.Map recurse = new
         * org.apache.yoko.rmi.util.IdentityHashMap (); java.io.CharArrayWriter cw = new
         * java.io.CharArrayWriter (); java.io.PrintWriter pw = new
         * java.io.PrintWriter (cw);
         * 
         * pw.print ("returning from "); pw.println (reflected_method.toString
         * ()); pw.print (" => ");
         * 
         * if (result == null) { pw.write ("null"); } else {
         * 
         * TypeDescriptor desc;
         * 
         * try { desc = getTypeRepository ().getDescriptor (result.getClass ()); }
         * catch (RuntimeException ex) { desc = return_type; }
         * 
         * desc.print (pw, recurse, result); }
         * 
         * pw.close (); log.debug (cw.toString ()); }
         */

        return result;
    }

    String transformOverloading(String mname) {
        StringBuffer buf = new StringBuffer(mname);

        if (parameter_types.length == 0) {
            buf.append("__");
        } else {
            for (int i = 0; i < parameter_types.length; i++) {
                buf.append("__");
                buf.append(parameter_types[i].getIDLName());
            }
        }

        return buf.toString();
    }

    boolean isOverloaded = false;

    boolean isCaseSensitive = false;

    protected void setOverloaded(boolean val) {
        isOverloaded = val;
    }

    protected void setCaseSensitive(boolean val) {
        isCaseSensitive = val;
    }

    public void init() {
        Class[] param_types = reflected_method.getParameterTypes();
        parameter_types = new TypeDescriptor[param_types.length];

        for (int i = 0; i < param_types.length; i++) {
            try {
                parameter_types[i] = getTypeRepository().getDescriptor(
                        param_types[i]);
                copyWithinState |= parameter_types[i].copyWithinState();

            } catch (RuntimeException ex) {
                throw ex;
            }
        }

        Class result_type = reflected_method.getReturnType();
        return_type = getTypeRepository().getDescriptor(result_type);

        Class[] exc_types = reflected_method.getExceptionTypes();
        exception_types = new ExceptionDescriptor[exc_types.length];
        for (int i = 0; i < exc_types.length; i++) {
            exception_types[i] = (ExceptionDescriptor) getTypeRepository()
                    .getDescriptor(exc_types[i]);
        }

        parameter_count = param_types.length;
        generateIDLName();
    }

    void generateIDLName() {
        String idl_name = null;

        if (isSetterMethod()) {
            idl_name = "_set_" + transformIdentifier(attributeName());
        } else if (isGetterMethod()) {
            idl_name = "_get_" + transformIdentifier(attributeName());
        } else {
            idl_name = transformIdentifier(getJavaName());
        }

        if (isCaseSensitive) {
            idl_name = transformCaseSensitive(idl_name);
        }

        if (isOverloaded) {
            idl_name = transformOverloading(idl_name);
        }

        setIDLName(idl_name);
    }

    private String attributeName() {
        String methodName = getJavaName();
        StringBuffer buf = new StringBuffer();

        int pfxLen;
        if (methodName.startsWith("get"))
            pfxLen = 3;
        else if (methodName.startsWith("is"))
            pfxLen = 2;
        else if (methodName.startsWith("set"))
            pfxLen = 3;
        else
            throw new RuntimeException("methodName " + methodName
                    + " is not attribute");

        if (methodName.length() >= (pfxLen + 2)
                && Character.isUpperCase(methodName.charAt(pfxLen))
                && Character.isUpperCase(methodName.charAt(pfxLen + 1))) {
            return methodName.substring(pfxLen);
        }

        buf.append(Character.toLowerCase(methodName.charAt(pfxLen)));
        buf.append(methodName.substring(pfxLen + 1));
        return buf.toString();
    }

    private boolean isSetterMethod() {
        Method m = getReflectedMethod();

        String name = m.getName();
        if (!name.startsWith("set"))
            return false;

        if (name.length() == 3)
            return false;

        if (!Character.isUpperCase(name.charAt(3)))
            return false;

        if (!m.getReturnType().equals(Void.TYPE))
            return false;

        if (m.getParameterTypes().length == 1)
            return true;

        else
            return false;
    }

    private boolean isGetterMethod() {
        Method m = getReflectedMethod();

        String name = m.getName();

        int pfxLen;
        if (name.startsWith("get"))
            pfxLen = 3;
        else if (name.startsWith("is")
                && m.getReturnType().equals(Boolean.TYPE))
            pfxLen = 2;
        else
            return false;

        if (name.length() == pfxLen)
            return false;

        if (!Character.isUpperCase(name.charAt(pfxLen)))
            return false;

        if (m.getReturnType().equals(Void.TYPE))
            return false;

        if (m.getParameterTypes().length == 0)
            return true;

        else
            return false;
    }

    static String transformIdentifier(String name) {
        StringBuffer buf = new StringBuffer();

        // it cannot start with underscore; prepend a 'J'
        if (name.charAt(0) == '_')
            buf.append('J');

        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);

            // basic identifier elements
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9') || (ch == '_')) {
                buf.append(ch);
            }

            // dot becomes underscore
            else if (ch == '.') {
                buf.append('_');
            }

            // else, use translate to UXXXX unicode
            else {
                buf.append('U');
                String hex = Integer.toHexString((int) ch);
                switch (hex.length()) {
                case 1:
                    buf.append('0');
                case 2:
                    buf.append('0');
                case 3:
                    buf.append('0');
                }
                buf.append(hex);
            }
        }

        return buf.toString();
    }

    private static String transformCaseSensitive(String name) {
        StringBuffer buf = new StringBuffer(name);
        buf.append('_');

        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);

            // basic identifier elements
            if (Character.isUpperCase(ch)) {
                buf.append('_');
                buf.append(i);
            }
        }

        return buf.toString();
    }

    void writeStubMethod(PrintWriter pw) {
        pw.println("\t/**");
        pw.println("\t *");
        pw.println("\t */");

        writeMethodHead(pw);
        pw.println("\t{");

        pw.println("\t\tboolean marshal = !" + UTIL + ".isLocal (this);");
        pw.println("\t\tmarshal: while(true) {");
        pw.println("\t\tif(marshal) {");

        writeMarshalCall(pw);

        pw.println("\t\t} else {");

        writeLocalCall(pw);

        pw.println("\t\t}}");

        // pw.println ("throw new org.omg.CORBA.NO_IMPLEMENT(\"local
        // invocation\")");

        pw.println("\t}\n");
    }

    void writeMethodHead(PrintWriter pw) {
        pw.print("\tpublic ");
        writeJavaType(pw, reflected_method.getReturnType());

        pw.print(' ');
        pw.print(getJavaName());

        pw.print(" (");
        Class[] args = reflected_method.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
            if (i != 0) {
                pw.print(", ");
            }

            writeJavaType(pw, args[i]);
            pw.print(" arg");
            pw.print(i);
        }
        pw.println(")");

        Class[] ex = reflected_method.getExceptionTypes();
        pw.print("\t\tthrows ");
        for (int i = 0; i < ex.length; i++) {
            if (i != 0) {
                pw.print(", ");
            }

            writeJavaType(pw, ex[i]);
        }

        pw.println();
    }

    static void writeJavaType(PrintWriter pw, Class type) {
        if (type.isArray()) {
            writeJavaType(pw, type.getComponentType());
            pw.print("[]");
        } else {
            pw.print(type.getName());
        }
    }

    static final String SERVANT = "org.omg.CORBA.portable.ServantObject";

    static final String INPUT = "org.omg.CORBA_2_3.portable.InputStream";

    static final String OUTPUT = "org.omg.CORBA_2_3.portable.OutputStream";

    static final String UTIL = "javax.rmi.CORBA.Util";

    void writeMarshalCall(PrintWriter pw) {
        pw.println("\t\t" + INPUT + " in = null;");
        pw.println("\t\ttry {");

        pw.println("\t\t\t" + OUTPUT + " out " + "= (" + OUTPUT
                + ")_request (\"" + getIDLName() + "\", true);");
        pw.println("\t\t\ttry{");

        Class[] args = reflected_method.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
            TypeDescriptor desc = getTypeRepository().getDescriptor(args[i]);
            pw.print("\t\t\t");
            desc.writeMarshalValue(pw, "out", "arg" + i);
            pw.println(";");
        }

        pw.println("\t\t\tin = (" + INPUT + ")_invoke(out);");

        Class rtype = reflected_method.getReturnType();
        if (rtype == Void.TYPE) {
            pw.println("\t\t\treturn;");
        } else {
            pw.print("\t\t\treturn (");
            writeJavaType(pw, rtype);
            pw.print(")");

            TypeDescriptor desc = getTypeRepository().getDescriptor(rtype);
            desc.writeUnmarshalValue(pw, "in");
            pw.println(";");
        }

        pw
                .println("\t\t} catch (org.omg.CORBA.portable.ApplicationException ex) {");

        pw.println("\t\t\t" + INPUT + " exin = (" + INPUT
                + ")ex.getInputStream();");
        pw.println("\t\t\tString exname = exin.read_string();");

        Class[] ex = reflected_method.getExceptionTypes();
        for (int i = 0; i < ex.length; i++) {
            if (java.rmi.RemoteException.class.isAssignableFrom(ex[i])) {
                continue;
            }

            ExceptionDescriptor exd = (ExceptionDescriptor) getTypeRepository()
                    .getDescriptor(ex[i]);

            pw.println("\t\t\tif (exname.equals(\""
                    + exd.getExceptionRepositoryID() + "\"))");
            pw.print("\t\t\t\tthrow (");
            writeJavaType(pw, ex[i]);
            pw.print(")exin.read_value(");
            writeJavaType(pw, ex[i]);
            pw.println(".class);");
        }

        pw.println("\t\t\tthrow new java.rmi.UnexpectedException(exname,ex);");

        pw
                .println("\t\t} catch (org.omg.CORBA.portable.RemarshalException ex) {");
        pw.println("\t\t\tcontinue marshal;");
        pw.println("\t\t} finally {");
        pw.println("\t\t\t if(in != null) _releaseReply(in);");
        pw.println("\t\t}");

        pw.println("\t\t} catch (org.omg.CORBA.SystemException ex) {");
        pw.println("\t\t\t throw " + UTIL + ".mapSystemException(ex);");
        pw.println("\t\t}");
    }

    void writeLocalCall(PrintWriter pw) {
        Class thisClass = reflected_method.getDeclaringClass();

        pw.println("\t\t\t" + SERVANT + " so = _servant_preinvoke (");
        pw.println("\t\t\t\t\"" + getIDLName() + "\",");
        pw.print("\t\t\t\t");
        writeJavaType(pw, thisClass);
        pw.println(".class);");
        pw
                .print("\t\t\tif (so==null || so.servant==null || !(so.servant instanceof ");
        writeJavaType(pw, thisClass);
        pw.print("))");
        pw.println(" { marshal=true; continue marshal; }");

        pw.println("\t\t\ttry {");

        // copy arguments
        Class[] args = reflected_method.getParameterTypes();
        if (args.length == 1) {
            if (getTypeRepository().getDescriptor(args[0]).copyInStub()) {
                pw.print("\t\t\t\targ0 = (");
                writeJavaType(pw, args[0]);
                pw.println(")" + UTIL + ".copyObject(arg0, _orb());");
            }
        } else if (args.length > 1) {
            boolean[] copy = new boolean[args.length];
            int copyCount = 0;

            for (int i = 0; i < args.length; i++) {
                TypeDescriptor td = getTypeRepository().getDescriptor(args[i]);
                copy[i] = td.copyInStub();
                if (copy[i]) {
                    copyCount += 1;
                }
            }

            if (copyCount > 0) {
                pw.println("\t\t\t\tObject[] args = new Object[" + copyCount
                        + "];");
                int pos = 0;
                for (int i = 0; i < args.length; i++) {
                    if (copy[i]) {
                        pw.println("\t\t\t\targs[" + (pos++) + "] = arg" + i
                                + ";");
                    }
                }
                pw.println("\t\t\t\targs=" + UTIL
                        + ".copyObjects(args,_orb());");
                pos = 0;
                for (int i = 0; i < args.length; i++) {
                    if (copy[i]) {
                        pw.print("\t\t\t\targ" + i + "=(");
                        writeJavaType(pw, args[i]);
                        pw.println(")args[" + (pos++) + "];");
                    }
                }
            }
        }

        // now, invoke!
        Class out = reflected_method.getReturnType();
        pw.print("\t\t\t\t");
        if (out != Void.TYPE) {
            writeJavaType(pw, out);
            pw.print(" result = ");
        }

        pw.print("((");
        writeJavaType(pw, thisClass);
        pw.print(")so.servant).");
        pw.print(getJavaName());
        pw.print("(");

        for (int i = 0; i < args.length; i++) {
            if (i != 0) {
                pw.print(',');
            }
            pw.print("arg" + i);
        }

        pw.println(");");

        pw.print("\t\t\t\treturn ");
        if (out != Void.TYPE) {
            TypeDescriptor td = getTypeRepository().getDescriptor(out);
            if (td.copyInStub()) {
                pw.print('(');
                writeJavaType(pw, out);
                pw.print(')');
                pw.println(UTIL + ".copyObject (result, _orb());");

            } else {
                pw.println("result;");
            }
        } else {
            pw.println(";");
        }

        pw.println("\t\t\t} finally {");
        pw.println("\t\t\t\t_servant_postinvoke (so);");
        pw.println("\t\t\t}");
    }

    void addDependencies(java.util.Set classes) {
        TypeRepository rep = getTypeRepository();

        TypeDescriptor desc = null;

        desc = rep.getDescriptor(reflected_method.getReturnType());
        desc.addDependencies(classes);

        Class[] param = reflected_method.getParameterTypes();
        for (int i = 0; i < param.length; i++) {
            desc = rep.getDescriptor(param[i]);
            desc.addDependencies(classes);
        }

        Class[] ex = reflected_method.getExceptionTypes();
        for (int i = 0; i < ex.length; i++) {
            desc = rep.getDescriptor(ex[i]);
            desc.addDependencies(classes);
        }
    }

    static final java.lang.Class REMOTE_EXCEPTION = java.rmi.RemoteException.class;

    boolean isRemoteOperation() {
        Class[] ex = reflected_method.getExceptionTypes();

        for (int i = 0; i < ex.length; i++) {
            if (REMOTE_EXCEPTION.isAssignableFrom(ex[i]))
                return true;
        }

        return false;
    }

}
