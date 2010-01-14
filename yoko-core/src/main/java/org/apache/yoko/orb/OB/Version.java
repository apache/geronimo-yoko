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

package org.apache.yoko.orb.OB;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Holds Yoko version information.
 */
public final class Version {

    private static final String VERSION = readVersionFromMavenPom(
            "org.apache.yoko", "yoko-core");

    private static final String UNKNOWN = "[version unknown]";

    public static String getVersion() {
        return VERSION;
    }

    /**
     * Reads the version from the pom.properties that Maven2 puts in the
     * META-INF of the generated jar.
     * 
     * @param groupId
     *            the Maven groupId of yoko
     * @param artifactId
     *            the Maven artifact id
     * @return the version string from the POM, or
     *         <code>"[version unknown]"</code> if the pom.properties cannot
     *         be loaded by the current thread's classloader
     */
    private static String readVersionFromMavenPom(String groupId,
            String artifactId) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();

        String propFileName = "META-INF/maven/" + groupId + "/" + artifactId
                + "/pom.properties";
        InputStream a = cl.getResourceAsStream(propFileName);
        if (a == null)
            return UNKNOWN;
        try {
            props.load(a);
        } catch (IOException e) {
            return UNKNOWN;
        }
        final String version = props.getProperty("version");
        if (version == null) {
            return UNKNOWN;
        }
        return version;
    }

    public static void main(String[] args) {
        System.out.println("Yoko " + getVersion());
    }
}
