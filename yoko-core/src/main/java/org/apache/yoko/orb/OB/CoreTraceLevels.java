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

final public class CoreTraceLevels {

    public CoreTraceLevels(Logger logger, java.util.Properties properties) {

        String propRoot = "yoko.orb.trace.";
        java.util.Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            if (!key.startsWith(propRoot))
                continue;

            String value = properties.getProperty(key);
            Assert.ensure(value != null);

            if (key.equals("yoko.orb.trace.connections")) {
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException ex) {
                    logger.warning("ORB.init: invalid value for " + key);
                }
            } else if (key.equals("yoko.orb.trace.retry")) {
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException ex) {
                    logger.warning("ORB.init: invalid value for " + key);
                }
            } else if (key.equals("yoko.orb.trace.requests")) {
                try {
                    Integer.parseInt(value);
                    Integer.parseInt(value);
                } catch (NumberFormatException ex) {
                    logger.warning("ORB.init: invalid value for " + key);
                }
            } else if (key.equals("yoko.orb.trace.requests_in")) {
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException ex) {
                    logger.warning("ORB.init: invalid value for " + key);
                }
            } else if (key.equals("yoko.orb.trace.requests_out")) {
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException ex) {
                    logger.warning("ORB.init: invalid value for " + key);
                }
            } else {
                logger.warning("ORB.init: unknown property: " + key);
            }
        }
    }
}
