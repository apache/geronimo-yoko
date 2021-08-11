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
package versioned;

import testify.bus.Bus;

import java.rmi.RemoteException;

/**
 * This class provides no new function.
 * It allows a test to load the processor from a child loader
 * thereby providing an invocation context for any remote method calls.
 */
public class VersionedProcessorImpl extends acme.ProcessorImpl implements VersionedProcessor {
    public VersionedProcessorImpl(Bus bus) { super(bus); }

    @Override
    public String getVersion() throws RemoteException {
        return "v2";
    }
}
