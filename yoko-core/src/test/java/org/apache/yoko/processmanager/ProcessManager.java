/*
 * Copyright 2019 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.processmanager;

import org.apache.yoko.processmanager.internal.ProcessAgent;
import org.apache.yoko.processmanager.internal.ProcessManagerRemote;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ProcessManager extends UnicastRemoteObject implements ProcessManagerRemote {
    private final Map<String, JavaProcess> javaProcesses;
    private final String name = "ProcessManager";

    public ProcessManager(int registryPort) throws RemoteException {
        javaProcesses = new HashMap<>();
        Registry rmiRegistry;
        try {
            rmiRegistry = LocateRegistry.createRegistry(registryPort);
        } catch (RemoteException e) {
            try {
                rmiRegistry = LocateRegistry.getRegistry(registryPort);
            } catch (RemoteException e2) {
                throw new Error(e2);
            }
        } try {
            rmiRegistry.rebind(name, this);
        } catch (Throwable e) {
            e.printStackTrace(); throw new Error(e);
        }
    }

    public void registerProcess(JavaProcess process) {
        javaProcesses.put(process.getName(), process);
    }

    public void agentReady(ProcessAgent agent) throws RemoteException {
        JavaProcess process = javaProcesses.get(agent.getName()); if (process == null) {
            throw new Error("Unexpected: agentReady from unregistered agent");
        } else {
            process.setAgent(agent); process.processStarted.countDown();
        }
    }

    public String getName() { return name; }

    public void agentExited(ProcessAgent agent) throws RemoteException {
        JavaProcess process = javaProcesses.get(agent.getName()); if (process == null) {
            throw new Error("Unexpected: agentExited from unregistered agent");
        } else {
            process.setAgent(agent); process.processExited.countDown();
        }
    }

    public void isAlive() {}
}
