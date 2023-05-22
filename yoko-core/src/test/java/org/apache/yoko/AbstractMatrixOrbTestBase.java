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
package org.apache.yoko;

import junit.framework.TestSuite;

import java.io.File;
import java.util.Arrays;

/**
 * 
 * Generates a test matrix of server- and client configurations. A default 
 * matrix is provided.
 */
public class AbstractMatrixOrbTestBase extends AbstractOrbTestBase {
    static final String[][] SERVER_ORB_ARGS = new String[][] {
        new String[] { "-OAthreaded" },
        new String[] { "-OAthread_per_client" },
        new String[] { "-OAthread_per_request" },
        new String[] { "-OAthread_pool" , "10" } };

    private static final String[][] CLIENT_ORB_ARGS = new String[][] {
        new String[] { "-ORBthreaded" }};
        
    private Class<?> serverClass, clientClass;
    private String[] serverArgs, clientArgs;

    public AbstractMatrixOrbTestBase(String name) {
        super(name);
    }
    private void init(Class<?> serverClass, String[] serverArgs, Class<?> clientClass, String[] clientArgs, File waitForFile) {
        this.serverClass = serverClass;
        this.clientClass = clientClass;
        this.serverArgs = serverArgs;
        this.clientArgs = clientArgs;
        this.waitForFile = waitForFile;
    }

    @Override
    protected void runTest() throws Throwable {
        runServerClientTest(serverClass, serverArgs, clientClass, clientArgs);
    }
                
    public static TestSuite generateTestSuite(Class<?> serverClass, Class<?> clientClass, File waitForFile) {
        TestSuite suite = new TestSuite(clientClass.getName() + "->" + serverClass.getName());
        for(String[] serverArgs: AbstractMatrixOrbTestBase.SERVER_ORB_ARGS) {
            for(String[] clientArgs: AbstractMatrixOrbTestBase.CLIENT_ORB_ARGS) {
                String name = String.format("%s(%s) -> %s(%s)",
                        clientClass, Arrays.toString(clientArgs),
                        serverClass, Arrays.toString(serverArgs));
                AbstractMatrixOrbTestBase test = new AbstractMatrixOrbTestBase(name);
                test.init(serverClass, serverArgs, clientClass, clientArgs, waitForFile);
                suite.addTest(test);
            }
        }
        return suite;
    }
}
