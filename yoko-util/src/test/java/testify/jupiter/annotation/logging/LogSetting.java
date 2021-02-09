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
package testify.jupiter.annotation.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Applies a log setting and remembers how to undo it. */
class LogSetting {
    private final Logger logger;
    private final Level oldLevel;
    private final Handler handler;

    LogSetting(Logging annotation, Handler handler) {
        this.logger = Logger.getLogger(annotation.value());
        this.handler = handler;
        this.oldLevel = logger.getLevel();
        logger.setLevel(annotation.level().level);
        logger.addHandler(handler);
    }

    void undo() {
        logger.setLevel(oldLevel); // this might be a no-op but that's ok
        logger.removeHandler(handler);
    }
}
