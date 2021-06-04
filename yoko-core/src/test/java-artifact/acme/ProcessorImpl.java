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

import org.apache.yoko.rmi.api.RemoteOnewayException;
import testify.bus.Bus;

import java.rmi.RemoteException;

public class ProcessorImpl implements Processor {
    final Bus bus;

    public ProcessorImpl(Bus bus) {this.bus = bus;}

    @Override
    public void process(Processable p) throws RemoteException {
        try {
            bus.log("ProcessorImpl processing '" + p + "'");
            p.process();
            bus.log("ProcessorImpl processing succeeded");
        } catch (Error | RuntimeException | RemoteException e) {
            throw e;
        } catch (Throwable t) {
            throw new RemoteException("Processing failed", t);
        }
    }
}
