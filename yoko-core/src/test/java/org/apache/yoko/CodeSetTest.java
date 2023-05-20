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

import test.codesets.Client;
import test.codesets.Server;

public class CodeSetTest extends AbstractOrbTestBase {
    private static final Class<?> SERVER_CLASS = Server.class;
    private static final Class<?> CLIENT_CLASS = Client.class;
    private static final String OUTPUT_FILE = "TestCodeSets.ref";

    public void setUp() throws Exception {
        super.setUp();
        setWaitForFile(OUTPUT_FILE);
    }
        
    public void testStandardCodeSet() throws Exception {
        runServerClientTest(SERVER_CLASS, CLIENT_CLASS);
    }
        
    public void testStandardCodeSet_11() throws Exception {
        runServerClientTest(SERVER_CLASS, new String[] {"-OAversion", "1.1"}, CLIENT_CLASS, new String[0]);
    }
        
    public void testCodeSet8859_5() throws Exception {
        String[] args = {"-ORBnative_cs", "ISO/IEC",  "8859-5" };
        runServerClientTest(SERVER_CLASS, args, CLIENT_CLASS, args);
    }
        
    public void testCodeSet8859_5_11() throws Exception {
        runServerClientTest(SERVER_CLASS, new String[] {"-ORBnative_cs", "ISO/IEC", "8859-5"}, 
                            CLIENT_CLASS, new String[] {"-ORBnative_cs", "ISO/IEC", "8859-5", "-OAversion", "1.1" });
    }
        
    public void testCodeSet8859_1_vs_8859_4() throws Exception {
        runServerClientTest(SERVER_CLASS, new String[] {"-ORBnative_cs", "ISO/IEC", "8859-4"},
                            CLIENT_CLASS, new String[] {"-ORBnative_cs", "ISO/IEC", "8859-1" });
    }
        
    public void testCodeSet8859_1_vs_8859_4_11() throws Exception {
        runServerClientTest(SERVER_CLASS, new String[] {"-ORBnative_cs", "ISO/IEC", "8859-4", "-OAversion", "1.1"},
                            CLIENT_CLASS, new String[] {"-ORBnative_cs", "ISO/IEC", "8859-1"});
    }
}
