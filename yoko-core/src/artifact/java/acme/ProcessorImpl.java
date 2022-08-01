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

import testify.bus.Bus;

import java.io.Serializable;
import java.rmi.RemoteException;

public class ProcessorImpl implements Processor {
    final Bus bus;

    public ProcessorImpl(Bus bus) {this.bus = bus;}


    @Override
    public void performRemotely(NullaryOp op) throws RemoteException {
        try {
            bus.log("ProcessorImpl processing '" + op.name() + "'");
            op.perform();
            bus.log("ProcessorImpl processing succeeded");
        } catch (Error | RuntimeException | RemoteException e) {
            throw e;
        } catch (Throwable t) {
            throw new RemoteException("Processing failed", t);
        }
    }


    private <T> T process(UnaryOp<T> p, T t) throws RemoteException {
        try {
            bus.log("ProcessorImpl processing '" + p.name() + "'");
            T newT = p.process(t);
            bus.log("ProcessorImpl processing succeeded");
            return newT;
        } catch (Error | RuntimeException | RemoteException e) {
            throw e;
        } catch (Throwable e) {
            throw new RemoteException("Processing failed", e);
        }
    }

    public <T extends AbstractInterface> T processAbstract(UnaryOp<T> op, T operand) throws RemoteException { return process(op, operand); }
    public <T extends Serializable> T processAny(UnaryOp<T> op, T operand) throws RemoteException { return process(op, operand); }
    public <T extends AbstractValue> T processValue(UnaryOp<T> op, T operand) throws RemoteException { return process(op, operand); }
}
