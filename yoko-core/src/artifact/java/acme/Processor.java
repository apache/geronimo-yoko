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
    <T extends AbstractInterface> T processAbstract(UnaryOp<T> op, T operand) throws RemoteException;

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
