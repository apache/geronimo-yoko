/*
 * =============================================================================
 * Copyright (c) 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package acme;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Processor extends Remote {
    @FunctionalInterface
    interface NullaryOp extends Serializable {
        void perform() throws Throwable;
        default String name() { return this.getClass().getName(); }
    }

    void performRemotely(NullaryOp op) throws RemoteException;

    @FunctionalInterface
    interface UnaryOp<T> extends Serializable {
        T process(T t) throws Throwable;
        default String name() { return this.getClass().getName(); }
    }

    /**
     * Marshal <code>t</code> as an abstract,
     * run the provided {@link UnaryOp} on it,
     * and marshal the return value as an abstract.
     */
    <T extends Abstract> T processAbstract(UnaryOp<T> op, T operand) throws RemoteException;

    /**
     * Marshal <code>t</code> as an Any,
     * run the provided {@link UnaryOp} on it,
     * and marshal the return value as an Any.
     */
    <T extends Serializable> T processAny(UnaryOp<T> op, T operand) throws RemoteException;

    /**
     * Marshal <code>t</code> as a value type,
     * run the provided {@link UnaryOp} on it,
     * and marshal the return value as a value type.
     */
    <T extends Value> T processValue(UnaryOp<T> op, T operand) throws RemoteException;
}
