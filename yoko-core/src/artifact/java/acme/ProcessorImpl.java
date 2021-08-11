/*
 * =============================================================================
 * Copyright (c) 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
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

    public <T extends Abstract> T processAbstract(UnaryOp<T> op, T operand) throws RemoteException { return process(op, operand); }
    public <T extends Serializable> T processAny(UnaryOp<T> op, T operand) throws RemoteException { return process(op, operand); }
    public <T extends Value> T processValue(UnaryOp<T> op, T operand) throws RemoteException { return process(op, operand); }
}
