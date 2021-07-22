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
package org.apache.yoko.rmi.util.stub;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Synthetic;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ARETURN;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ATHROW;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DLOAD;
import org.apache.bcel.generic.DRETURN;
import org.apache.bcel.generic.FLOAD;
import org.apache.bcel.generic.FRETURN;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.IRETURN;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LLOAD;
import org.apache.bcel.generic.LRETURN;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.Type;

import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

class BCELClassBuilder {
    static final Logger logger = Logger.getLogger(BCELClassBuilder.class.getName());

    static Class make(ClassLoader loader, Class superClass, Class[] interfaces,
            MethodRef[] methods, MethodRef[] superMethodRefs, Object[] data,
            MethodRef handlerMethodRef, String className,
            StubInitializer initializer)

    throws IllegalAccessException, InstantiationException,
            IllegalArgumentException {
        String superClassName = superClass.getName();
        String[] interfaceNames = new String[interfaces.length + 1];
        for (int i = 0; i < interfaces.length; i++)
            interfaceNames[i] = interfaces[i].getName();
        interfaceNames[interfaces.length] = Stub.class.getName();

        ClassGen newStubClass = new ClassGen(className, superClassName,
                "generated", // file name
                Constants.ACC_PUBLIC | Constants.ACC_FINAL, interfaceNames);

        ConstantPoolGen cp = newStubClass.getConstantPool();

        if (handlerMethodRef == null)
            throw new IllegalArgumentException("handler method is null");

        //
        // Check that the handler method is valid
        //
        Class[] paramTypes = handlerMethodRef.getParameterTypes();
        if (paramTypes.length != 3) {
            throw new IllegalArgumentException(
                    "handler method must have three arguments");
        }

        if (!paramTypes[0].isAssignableFrom(superClass)) {
            throw new IllegalArgumentException(
                    "Handler's 1st argument must be super-type for "
                            + superClass);
        }

        // the type of data fields
        Type typeOfDataFields = translate(paramTypes[1]);

        if (Object[].class != paramTypes[2]) {
            throw new IllegalArgumentException(
                    "Handler's 3rd argument must be Object[]");
        }

        //
        // Construct field for the handler reference
        //
        Class handlerClass = handlerMethodRef.getDeclaringClass();
        FieldGen handlerFieldGen = new FieldGen(Constants.ACC_PRIVATE
                | Constants.ACC_FINAL, translate(handlerClass), Util
                .handlerFieldName(), cp);
        newStubClass.addField(handlerFieldGen.getField());

        //
        // Construct the method that gets the stub handler.
        //
        generateHandlerGetter(newStubClass, handlerFieldGen);

        //
        // construct the field that holds the initializer
        //
        FieldGen initializerFieldGen = new FieldGen(Constants.ACC_PRIVATE
                | Constants.ACC_STATIC, translate(StubInitializer.class), Util
                .initializerFieldName(), cp);
        newStubClass.addField(initializerFieldGen.getField());

        //
        // Emit constructor
        //
        emitInitializerConstructor(newStubClass, handlerFieldGen,
                initializerFieldGen);

        //
        // Construct data fields
        //
        FieldGen[] dataFieldGens = new FieldGen[methods.length];
        for (int i = 0; i < methods.length; i++) {
            MethodRef method = methods[i];

            dataFieldGens[i] = new FieldGen(Constants.ACC_PRIVATE
                    | Constants.ACC_STATIC, typeOfDataFields, Util
                    .methodFieldName(i), cp);

            newStubClass.addField(dataFieldGens[i].getField());
        }

        //
        // Construct method stubs
        //
        for (int i = 0; i < methods.length; i++) {
            generate(newStubClass, methods[i], dataFieldGens[i],
                    handlerFieldGen, handlerMethodRef);
        }

        //
        // Construct super-method trampolines
        //
        for (int i = 0; i < superMethodRefs.length; i++) {
            generateSuperMethod(newStubClass, superMethodRefs[i]);
        }

        JavaClass javaClass = newStubClass.getJavaClass();
        byte[] classData = javaClass.getBytes();

        try {
            if (Boolean.getBoolean("org.apache.yoko.rmi.util.stub.debug")) {
                java.io.File out = new java.io.File(className + ".class");
                // System.out.println ("dumping to file "+out);
                javaClass.dump(out);
            }
        } catch (java.io.IOException ex) {
            logger.log(Level.WARNING, "", ex);
        }

        Class proxyClass = Util.defineClass(loader, className, classData);

        // initialize the static data fields
        for (int i = 0; i < methods.length; i++) {
            try {
                java.lang.reflect.Field f = proxyClass
                        .getDeclaredField(dataFieldGens[i].getName());

                f.setAccessible(true);
                f.set(null, data[i]);
                f.setAccessible(false);
            } catch (NoSuchFieldException ex) {
                logger
                        .log(
                                Level.WARNING,
                                "cannot find field "
                                        + dataFieldGens[i].getName()
                                        + " for stub class "
                                        + className
                                        + " extends: "
                                        + superClassName
                                        + "implements: "
                                        + interfaceNames[0]
                                        + (interfaceNames.length > 2 ? " (among others) "
                                                : ""), ex);
                throw new Error("internal error!", ex);
            }
        }

        // set the initializer
        try {
            java.lang.reflect.Field f = proxyClass.getDeclaredField(Util
                    .initializerFieldName());
            f.setAccessible(true);
            f.set(null, initializer);
        } catch (NoSuchFieldException ex) {
            throw new Error("internal error!", ex);
        }

        return proxyClass;

    }

    static Class make(ClassLoader loader, Class superClass, Class[] interfaces,
            MethodRef[] methods, Object[] data, MethodRef handlerMethodRef,
            String className)

    throws IllegalAccessException, InstantiationException,
            IllegalArgumentException {
        // construct the name of the new class
        String superClassName = superClass.getName();
        String[] interfaceNames = new String[interfaces.length + 1];
        for (int i = 0; i < interfaces.length; i++)
            interfaceNames[i] = interfaces[i].getName();
        interfaceNames[interfaces.length] = Stub.class.getName();

        ClassGen newStubClass = new ClassGen(className, superClassName,
                "generated", // file name
                Constants.ACC_PUBLIC | Constants.ACC_FINAL, interfaceNames);

        ConstantPoolGen cp = newStubClass.getConstantPool();

        if (handlerMethodRef == null)
            throw new IllegalArgumentException("handler method is null");

        //
        // Check that the handler method is valid
        //
        Class[] paramTypes = handlerMethodRef.getParameterTypes();
        if (paramTypes.length != 3) {
            throw new IllegalArgumentException(
                    "handler method must have three arguments");
        }

        if (!paramTypes[0].isAssignableFrom(superClass)) {
            throw new IllegalArgumentException(
                    "Handler's 1st argument must be super-type for "
                            + superClass);
        }

        // the type of data fields
        Type typeOfDataFields = translate(paramTypes[1]);

        if (Object[].class != paramTypes[2]) {
            throw new IllegalArgumentException(
                    "Handler's 3rd argument must be Object[]");
        }

        //
        // Construct field for the handler reference
        //
        Class handlerClass = handlerMethodRef.getDeclaringClass();
        FieldGen handlerFieldGen = new FieldGen(Constants.ACC_PRIVATE
                | Constants.ACC_FINAL, translate(handlerClass), Util
                .handlerFieldName(), cp);
        newStubClass.addField(handlerFieldGen.getField());

        //
        // Construct the method that gets the stub handler.
        //
        generateHandlerGetter(newStubClass, handlerFieldGen);

        //
        // Emit constructor
        //
        emitOneArgConstructor(newStubClass, handlerFieldGen);

        //
        // Construct data fields
        //
        FieldGen[] dataFieldGens = new FieldGen[methods.length];
        for (int i = 0; i < methods.length; i++) {
            MethodRef method = methods[i];

            dataFieldGens[i] = new FieldGen(Constants.ACC_PRIVATE
                    | Constants.ACC_STATIC, typeOfDataFields, Util
                    .methodFieldName(i), cp);

            newStubClass.addField(dataFieldGens[i].getField());
        }

        //
        // Construct method stubs
        //
        for (int i = 0; i < methods.length; i++) {
            generate(newStubClass, methods[i], dataFieldGens[i],
                    handlerFieldGen, handlerMethodRef);
        }

        JavaClass javaClass = newStubClass.getJavaClass();
        byte[] classData = javaClass.getBytes();

        try {
            if (Boolean.getBoolean("org.apache.yoko.rmi.util.stub.debug")) {
                java.io.File out = new java.io.File(className + ".class");
                // System.out.println ("dumping to file "+out);
                javaClass.dump(out);
            }
        } catch (java.io.IOException ex) {
            logger.log(Level.WARNING, "", ex);
        }

        Class proxyClass = Util.defineClass(loader, className, classData);

        // initialize the static data fields
        for (int i = 0; i < methods.length; i++) {
            try {
                java.lang.reflect.Field f = proxyClass
                        .getDeclaredField(dataFieldGens[i].getName());

                f.setAccessible(true);
                f.set(null, data[i]);
                f.setAccessible(false);
            } catch (NoSuchFieldException ex) {
                throw new Error("internal error!", ex);
            }
        }

        return proxyClass;
    }

    static Type translate(Class clazz) {
        if (clazz.isPrimitive()) {

            if (clazz == Integer.TYPE) {
                return Type.INT;
            } else if (clazz == Boolean.TYPE) {
                return Type.BOOLEAN;
            } else if (clazz == Short.TYPE) {
                return Type.SHORT;
            } else if (clazz == Byte.TYPE) {
                return Type.BYTE;
            } else if (clazz == Long.TYPE) {
                return Type.LONG;
            } else if (clazz == Double.TYPE) {
                return Type.DOUBLE;
            } else if (clazz == Float.TYPE) {
                return Type.FLOAT;
            } else if (clazz == Character.TYPE) {
                return Type.CHAR;
            } else if (clazz == Void.TYPE) {
                return Type.VOID;
            } else {
                throw new InternalError();
            }

        } else if (clazz.isArray()) {
            return new ArrayType(translate(clazz.getComponentType()), 1);

        } else {

            return new ObjectType(clazz.getName());
        }
    }

    static Type[] translate(Class[] clazz) {
        Type[] result = new Type[clazz.length];
        for (int i = 0; i < clazz.length; i++) {
            result[i] = translate(clazz[i]);
        }
        return result;
    }

    public static MethodRef[] getAbstractMethods(Class base, Class[] interfaces) {
        if (base == null)
            base = Object.class;

        MethodRef[] methods = collectMethods(base, interfaces);

        return methods;
    }

    /**
     * Collect the set of method objects that are would be abstract in a
     * subclass of <code>super_class</code>, implementing
     * <code>interfaces</code>.
     */
    public static MethodRef[] collectMethods(Class super_class,
            Class[] interfaces) {
        HashMap methods = new HashMap();

        if (interfaces != null) {
            for (int i = 0; i < interfaces.length; i++)
                collectAbstractMethods(methods, interfaces[i]);
        }

        collectAbstractMethods(methods, super_class);
        removeImplementedMethods(methods, super_class);

        Collection c = methods.values();
        return (MethodRef[]) c.toArray(new MethodRef[c.size()]);
    }

    /**
     * Collect all methods to be generated. We'll only collect each method once;
     * so multiple redeclations will be eliminetd.
     */
    private static void collectAbstractMethods(HashMap methods, Class type) {
        if (type == java.lang.Object.class || type == null)
            return;

        Class[] if_types = type.getInterfaces();
        for (int i = 0; i < if_types.length; i++) {
            collectAbstractMethods(methods, if_types[i]);
        }

        collectAbstractMethods(methods, type.getSuperclass());

        boolean type_is_interface = type.isInterface();

        java.lang.reflect.Method[] declared = type.getDeclaredMethods();
        for (int i = 0; i < declared.length; i++) {
            MethodRef m = new MethodRef(declared[i]);

            if (type_is_interface
                    || java.lang.reflect.Modifier.isAbstract(m.getModifiers())) {
                String key = m.getName() + m.getSignature();

                if (!methods.containsKey(key)) {
                    methods.put(key, m);
                }
            }
        }
    }

    /**
     * This is used in the second phase of collect, to remove methods that have
     * been collected in collectAbstractMethods.
     */
    private static void removeImplementedMethods(HashMap methods, Class type) {
        if (type == java.lang.Object.class || type == null)
            return;

        removeImplementedMethods(methods, type.getSuperclass());

        java.lang.reflect.Method[] declared = type.getDeclaredMethods();
        for (int i = 0; i < declared.length; i++) {
            MethodRef m = new MethodRef(declared[i]);

            if (!java.lang.reflect.Modifier.isAbstract(m.getModifiers())) {
                String key = m.getName() + m.getSignature();
                methods.remove(key);
            }
        }
    }

    static String className(String packageName, Class superClass,
            Class[] interfaces) {
        String className;
        String fullName;

        if (packageName == null) {
            if (!java.lang.reflect.Modifier.isPublic(superClass.getModifiers())) {
                packageName = Util.getPackageName(superClass);
            } else {
                for (int i = 0; i < interfaces.length; i++) {
                    if (java.lang.reflect.Modifier.isProtected(interfaces[i]
                            .getModifiers())) {
                        packageName = Util.getPackageName(interfaces[i]);
                    }
                }
            }

            if (packageName == null) {
                packageName = "org.apache.yoko.rmi.util.stub.gen";
            }
        }

        synchronized (BCELClassBuilder.class) {
            className = "Stub$$" + counter++;
        }

        return packageName + "." + className;
    }

    static int counter = 0;

    static Type stubHandlerType = translate(StubHandler.class);

    static Type initializerType = translate(StubInitializer.class);

    static MethodRef getStubHandlerRef;

    static {
        try {
            getStubHandlerRef = new MethodRef(StubInitializer.class
                    .getDeclaredMethod("getStubHandler", new Class[0]));
        } catch (NoSuchMethodException ex) {
            throw new Error(ex.getMessage(), ex);
        }
    }

    //
    // Constructor for a stub with an initializer
    //
    static void emitInitializerConstructor(ClassGen stubClass,
            FieldGen handlerField, FieldGen initializerField) {
        String stubClassName = stubClass.getClassName();
        ConstantPoolGen cp = stubClass.getConstantPool();
        InstructionList il = new InstructionList();

        MethodGen mg = new MethodGen(Constants.ACC_PUBLIC, Type.VOID,
                Type.NO_ARGS, null, "<init>", stubClassName, il, cp);

        InstructionFactory fac = new InstructionFactory(stubClass, cp);

        // call super-constructor
        il.append(InstructionFactory.createThis());
        il.append(fac.createInvoke(stubClass.getSuperclassName(), "<init>",
                Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL));

        // push "this"
        il.append(InstructionFactory.createThis());

        // get static initializer
        il.append(fac.createGetStatic(stubClassName,
                initializerField.getName(), initializerField.getType()));

        emitInvoke(il, fac, getStubHandlerRef);

        // checkCast
        il.append(fac.createCast(Type.OBJECT, handlerField.getType()));

        // put handlerField
        il.append(new PUTFIELD(cp.addFieldref(stubClassName, handlerField
                .getName(), handlerField.getSignature())));

        // return
        il.append(InstructionConstants.RETURN);

        // compute stack and locals...
        mg.setMaxStack();
        mg.setMaxLocals();

        stubClass.addMethod(mg.getMethod());
    }

    //
    // Constructor for a stub with an initializer
    //
    static void emitOneArgConstructor(ClassGen stubClass, FieldGen handlerField) {
        String stubClassName = stubClass.getClassName();
        ConstantPoolGen cp = stubClass.getConstantPool();
        InstructionList il = new InstructionList();

        Type[] args = new Type[] { handlerField.getType() };

        MethodGen mg = new MethodGen(Constants.ACC_PUBLIC, Type.VOID, args,
                null, "<init>", stubClassName, il, cp);

        InstructionFactory fac = new InstructionFactory(stubClass, cp);

        // call super-constructor
        il.append(InstructionFactory.createThis());
        il.append(fac.createInvoke(stubClass.getSuperclassName(), "<init>",
                Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL));

        // push this again...
        il.append(InstructionFactory.createThis());

        // push the handler
        il.append(InstructionFactory.createLoad(handlerField.getType(), 1));

        // put handlerField
        il.append(new PUTFIELD(cp.addFieldref(stubClassName, handlerField
                .getName(), handlerField.getSignature())));

        // return
        il.append(InstructionConstants.RETURN);

        // compute stack and locals...
        mg.setMaxStack();
        mg.setMaxLocals();

        stubClass.addMethod(mg.getMethod());
    }

    static void generateHandlerGetter(ClassGen clazz, FieldGen handlerField) {

        java.lang.reflect.Method[] stub_methods = Stub.class
                .getDeclaredMethods();
        if (stub_methods.length != 1) {
            throw new IllegalStateException("" + Stub.class
                    + " has wrong # methods");
        }
        String handlerGetName = stub_methods[0].getName();

        ConstantPoolGen cp = clazz.getConstantPool();
        InstructionList il = new InstructionList();
        InstructionFactory fac = new InstructionFactory(clazz, cp);

        Type methodReturnType = translate(Object.class);
        Type[] methodArgTypes = new Type[0];

        MethodGen mg = new MethodGen(
                Constants.ACC_FINAL | Constants.ACC_PUBLIC, methodReturnType,
                methodArgTypes, null, // arg names
                handlerGetName, clazz.getClassName(), il, cp);

        mg.addAttribute(new Synthetic(cp.addUtf8("Synthetic"), 0, null, cp
                .getConstantPool()));

        //
        // construct method body
        //

        il.append(InstructionFactory.createThis());

        il.append(fac.createGetField(clazz.getClassName(), handlerField
                .getName(), handlerField.getType()));

        emitReturn(il, methodReturnType);

        //
        // finish up...
        //

        mg.setMaxStack();
        mg.setMaxLocals();

        clazz.addMethod(mg.getMethod());
    }

    static void generate(ClassGen clazz, MethodRef method, FieldGen dataField,
            FieldGen handlerField, MethodRef handlerMethodRef) {
        ConstantPoolGen cp;
        InstructionList il;

        cp = clazz.getConstantPool();
        il = new InstructionList();

        InstructionFactory fac = new InstructionFactory(clazz, cp);

        Type methodReturnType = translate(method.getReturnType());
        Type[] methodArgTypes = translate(method.getParameterTypes());

        MethodGen mg = new MethodGen(
                Constants.ACC_FINAL | Constants.ACC_PUBLIC, methodReturnType,
                methodArgTypes, null, // arg names
                method.getName(), clazz.getClassName(), il, cp);

        mg.addAttribute(new Synthetic(cp.addUtf8("Synthetic"), 0, null, cp
                .getConstantPool()));

        Class[] throwsException = method.getExceptionTypes();
        for (int i = 0; i < throwsException.length; i++) {
            mg.addException(throwsException[i].getName());
        }

        //
        // BODY
        //

        il.append(InstructionFactory.createThis());

        il.append(fac.createGetField(clazz.getClassName(), handlerField
                .getName(), handlerField.getType()));

        // push "this" as invoke's first argument
        il.append(InstructionFactory.createThis());

        // load data value
        if (dataField.isStatic()) {
            il.append(fac.createGetStatic(clazz.getClassName(), dataField
                    .getName(), dataField.getType()));
        } else {
            il.append(InstructionFactory.createThis());
            il.append(fac.createGetField(clazz.getClassName(), dataField
                    .getName(), dataField.getType()));
        }

        il.append(new PUSH(cp, methodArgTypes.length));
        il.append((Instruction) fac.createNewArray(Type.OBJECT, (short) 1));

        int index = 1;
        for (int i = 0; i < methodArgTypes.length; i++) {
            // dup array ref
            il.append(InstructionConstants.DUP);

            // push index
            il.append(new PUSH(cp, i));

            // transform parameter
            il.append(InstructionFactory.createLoad(methodArgTypes[i], index));
            emitCoerceToObject(il, fac, methodArgTypes[i]);

            // and store into array
            il.append(InstructionFactory.createArrayStore(Type.OBJECT));

            index += methodArgTypes[i].getSize();
        }

        //
        // invoke handler's method
        //
        InstructionHandle tryStart = emitInvoke(il, fac, handlerMethodRef);

        // convert to primitive type
        emitCoerceFromObject(il, fac, methodReturnType);

        // and return

        InstructionHandle tryEnd = emitReturn(il, methodReturnType);

        //
        // catch...
        //
        InstructionHandle rethrowLocation = il.append(new ATHROW());

        Class[] exceptions = method.getExceptionTypes();
        boolean handle_throwable_exception = true;
        boolean handle_runtime_exception = true;
        if (exceptions != null) {
            for (int i = 0; i < exceptions.length; i++) {
                Class ex = exceptions[i];

                if (ex == java.lang.Throwable.class)
                    handle_throwable_exception = false;

                if (ex == java.lang.RuntimeException.class
                        || ex == java.lang.Exception.class)
                    handle_runtime_exception = false;

                mg.addExceptionHandler(tryStart, tryEnd, rethrowLocation,
                        (ObjectType) translate(ex));
            }
        }

        // A RuntimeException should not cause an
        // UndeclaredThrowableException, so we catch and re-throw it
        // that before throwable.
        if (handle_throwable_exception && handle_runtime_exception) {
            mg.addExceptionHandler(tryStart, tryEnd, rethrowLocation,
                    new ObjectType("java.lang.RuntimeException"));
        }

        // If anything else is thrown, it is wrapped in an
        // UndeclaredThrowable
        if (handle_throwable_exception) {
            InstructionHandle handlerStart = il.append(new ASTORE(1));

            il
                    .append(new NEW(
                            cp
                                    .addClass("java.lang.reflect.UndeclaredThrowableException")));
            il.append(InstructionConstants.DUP);
            il.append(new ALOAD(1));
            il.append(new INVOKESPECIAL(cp.addMethodref(
                    "java.lang.reflect.UndeclaredThrowableException", "<init>",
                    "(Ljava/lang/Throwable;)V")));

            il.append(new ATHROW());

            mg.addExceptionHandler(tryStart, tryEnd, handlerStart,
                    new ObjectType("java.lang.Throwable"));
        }

        //
        // DONE
        //

        mg.setMaxStack();
        mg.setMaxLocals();

        clazz.addMethod(mg.getMethod());
    }

    static void generateSuperMethod(ClassGen clazz, MethodRef method) {
        ConstantPoolGen cp;
        InstructionList il;

        cp = clazz.getConstantPool();
        il = new InstructionList();

        InstructionFactory fac = new InstructionFactory(clazz, cp);

        Type methodReturnType = translate(method.getReturnType());
        Type[] methodArgTypes = translate(method.getParameterTypes());

        MethodGen mg = new MethodGen(
                Constants.ACC_FINAL | Constants.ACC_PUBLIC, methodReturnType,
                methodArgTypes, null, // arg names
                method.getName(), clazz.getClassName(), il, cp);

        mg.addAttribute(new Synthetic(cp.addUtf8("Synthetic"), 0, null, cp
                .getConstantPool()));

        Class[] throwsException = method.getExceptionTypes();
        for (int i = 0; i < throwsException.length; i++) {
            mg.addException(throwsException[i].getName());
        }

        // push this
        il.append(InstructionFactory.createThis());

        // push arguments
        int index = 1;
        for (int i = 0; i < methodArgTypes.length; i++) {
            emitLoad(il, index, methodArgTypes[i]);
            index += methodArgTypes[i].getSize();
        }

        // call method
        il.append(new INVOKESPECIAL(cp.addMethodref(method.getDeclaringClass()
                .getName(), method.getName(), method.getSignature())));

        emitReturn(il, methodReturnType);

        //
        // DONE
        //

        mg.setMaxStack();
        mg.setMaxLocals();

        clazz.addMethod(mg.getMethod());
    }

    static InstructionHandle emitLoad(InstructionList il, int index, Type type) {
        switch (type.getType()) {
        case Constants.T_BOOLEAN:
        case Constants.T_CHAR:
        case Constants.T_BYTE:
        case Constants.T_SHORT:
        case Constants.T_INT:
            return il.append(new ILOAD(index));

        case Constants.T_LONG:
            return il.append(new LLOAD(index));

        case Constants.T_FLOAT:
            return il.append(new FLOAD(index));

        case Constants.T_DOUBLE:
            return il.append(new DLOAD(index));

        default:
            return il.append(new ALOAD(index));
        }
    }

    static InstructionHandle emitReturn(InstructionList il, Type type) {
        switch (type.getType()) {
        case Constants.T_BOOLEAN:
        case Constants.T_CHAR:
        case Constants.T_BYTE:
        case Constants.T_SHORT:
        case Constants.T_INT:
            return il.append(new IRETURN());

        case Constants.T_LONG:
            return il.append(new LRETURN());

        case Constants.T_FLOAT:
            return il.append(new FRETURN());

        case Constants.T_DOUBLE:
            return il.append(new DRETURN());

        case Constants.T_VOID:
            return il.append(InstructionConstants.RETURN);

        default:
            return il.append(new ARETURN());
        }
    }

    static void emitCoerceToObject(InstructionList il, InstructionFactory fac,
            Type type) {
        int tag = type.getType();
        switch (tag) {

        case Constants.T_BOOLEAN:
        case Constants.T_CHAR:
        case Constants.T_BYTE:
        case Constants.T_SHORT:
        case Constants.T_INT:
        case Constants.T_FLOAT:

            // float
            il.append(fac.createNew(new ObjectType(BASIC_CLASS_NAMES[tag])));

            // float Float
            il.append(InstructionConstants.DUP_X1);

            // Float float Float
            il.append(InstructionConstants.SWAP);

            // Float Float float
            il.append(fac.createInvoke(BASIC_CLASS_NAMES[tag], "<init>",
                    Type.VOID, new Type[] { type }, Constants.INVOKESPECIAL));

            // Float
            return;

        case Constants.T_DOUBLE:
        case Constants.T_LONG:

            // double/2
            il.append(fac.createNew(new ObjectType(BASIC_CLASS_NAMES[tag])));

            // double/2 Double
            il.append(InstructionConstants.DUP_X2);

            // Double double/2 Double
            il.append(InstructionConstants.DUP_X2);

            // Double Double double/2 Double
            il.append(InstructionConstants.POP);

            // Double Double double/2
            il.append(fac.createInvoke(BASIC_CLASS_NAMES[tag], "<init>",
                    Type.VOID, new Type[] { type }, Constants.INVOKESPECIAL));

            // Double
            return;

        case Constants.T_VOID:
            il.append(InstructionConstants.ACONST_NULL);

        default:
            return;
        }
    }

    public static final String[] BASIC_CLASS_NAMES = { null, null, null, null,
            "java.lang.Boolean", "java.lang.Character", "java.lang.Float",
            "java.lang.Double", "java.lang.Byte", "java.lang.Short",
            "java.lang.Integer", "java.lang.Long", "java.lang.Void", null,
            null, null, null };

    static MethodRef[] UNBOXING_METHOD = new MethodRef[Constants.T_VOID];

    static {
        try {
            UNBOXING_METHOD[Constants.T_BOOLEAN] = new MethodRef(
                    java.lang.Boolean.class.getDeclaredMethod("booleanValue",
                            new Class[0]));

            UNBOXING_METHOD[Constants.T_CHAR] = new MethodRef(
                    java.lang.Character.class.getDeclaredMethod("charValue",
                            new Class[0]));

            UNBOXING_METHOD[Constants.T_BYTE] = new MethodRef(
                    java.lang.Byte.class.getDeclaredMethod("byteValue",
                            new Class[0]));

            UNBOXING_METHOD[Constants.T_SHORT] = new MethodRef(
                    java.lang.Short.class.getDeclaredMethod("shortValue",
                            new Class[0]));

            UNBOXING_METHOD[Constants.T_INT] = new MethodRef(
                    java.lang.Integer.class.getDeclaredMethod("intValue",
                            new Class[0]));

            UNBOXING_METHOD[Constants.T_LONG] = new MethodRef(
                    java.lang.Long.class.getDeclaredMethod("longValue",
                            new Class[0]));

            UNBOXING_METHOD[Constants.T_FLOAT] = new MethodRef(
                    java.lang.Float.class.getDeclaredMethod("floatValue",
                            new Class[0]));

            UNBOXING_METHOD[Constants.T_DOUBLE] = new MethodRef(
                    java.lang.Double.class.getDeclaredMethod("doubleValue",
                            new Class[0]));
        } catch (NoSuchMethodException ex) {
            throw new Error(ex);
        }
    }

    static InstructionHandle emitCoerceFromObject(InstructionList il,
            InstructionFactory fac, Type type) {
        int tag = type.getType();
        switch (tag) {
        case Constants.T_BOOLEAN:
        case Constants.T_CHAR:
        case Constants.T_BYTE:
        case Constants.T_SHORT:
        case Constants.T_INT:
        case Constants.T_LONG:
        case Constants.T_FLOAT:
        case Constants.T_DOUBLE:
            il.append(fac.createCast(Type.OBJECT, new ObjectType(
                    BASIC_CLASS_NAMES[tag])));
            return emitInvoke(il, fac, UNBOXING_METHOD[tag]);

        case Constants.T_OBJECT:
        case Constants.T_ARRAY:
            return il.append(fac.createCast(Type.OBJECT, type));

        case Constants.T_VOID:
            return il.append(InstructionConstants.POP);

        default:
            throw new RuntimeException("internal error");
        }
    }

    static InstructionHandle emitInvoke(InstructionList il,
            InstructionFactory fac, MethodRef method) {
        String signature = method.getSignature();
        Type[] args = Type.getArgumentTypes(signature);
        Type ret = Type.getReturnType(signature);
        String mname = method.getName();
        String cname = method.getDeclaringClass().getName();

        short kind;
        if (method.getDeclaringClass().isInterface()) {
            kind = Constants.INVOKEINTERFACE;

        } else if (java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
            kind = Constants.INVOKESTATIC;

        } else if (method.getName().charAt(0) == '<') {
            kind = Constants.INVOKESPECIAL;

        } else {
            kind = Constants.INVOKEVIRTUAL;
        }

        return il.append(fac.createInvoke(cname, mname, ret, args, kind));
    }

}
