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
package test.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// Use this by passing the -Djava.util.logging.config.class=test.logging.LogYokoToConsole to the JVM
public class LogYokoToConsole {
    public LogYokoToConsole() {
        try {
            System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$-7s [%3$s] (%2$s) %5$s %6$s%n");

            final ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.FINEST);
            consoleHandler.setFormatter(new SimpleFormatter());

            final Logger yoko = Logger.getLogger("org.apache.yoko");
            yoko.setLevel(Level.FINEST);
            yoko.addHandler(consoleHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
