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
    interface Processable extends Serializable {
        void process() throws Throwable;
    }

    void process(Processable p) throws RemoteException;
}
