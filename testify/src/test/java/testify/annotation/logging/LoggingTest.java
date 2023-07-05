/*
 * Copyright 2023 IBM Corporation and others.
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
package testify.annotation.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import testify.annotation.ConfigurePartRunner;
import testify.bus.key.VoidSpec;
import testify.parts.PartRunner;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD;
import static testify.annotation.logging.LoggingTest.SyncPoint.JOIN;
import static testify.annotation.logging.LoggingTest.SyncPoint.SYNC_POINT;

@ConfigurePartRunner
@Logging("test.logging")
@TestInstance(PER_METHOD)
public class LoggingTest {
    StringWriter textOut;

    @BeforeEach
    void redirectOutput(LogPublisher controller) {
        textOut = new StringWriter();
        controller.setOut(new PrintWriter(textOut));

    }

    @Test
    void testLocalLogging(LogPublisher controller, TestInfo testInfo) {
        log("test.logging", "This message should be logged");
        log("other", "This message should not be logged");
        controller.flushLogs(testInfo.getDisplayName());
        String logText = textOut.toString();
        System.out.println(logText);
        assertThat(logText, containsString("This message should be logged"));
        assertThat(logText, not(containsString("This message should not be logged")));
    }

    private static void log(String logger, String msg) {
        System.out.printf("### about to log \"%s\" to \"%s\"%n", msg, logger);
        Logger.getLogger(logger).finest(msg);
    }

    enum SyncPoint implements VoidSpec {SYNC_POINT, JOIN; }

    @Test
    void testForkedLogging(PartRunner runner, LogPublisher controller, TestInfo testInfo) {
        runner.fork("PART_ONE", bus -> {
            log("test.logging", "p1 log msg");
            bus.put(SYNC_POINT);
            bus.get(JOIN);
        }).endWith(JOIN::send);
        runner.fork("PART_TWO", bus -> {
            log("other", "p2 log msg");
            bus.put(SYNC_POINT);
            bus.get(JOIN);
        }).endWith(JOIN::send);
        // wait for threads to finish
        runner.bus("PART_ONE").get(SYNC_POINT);
        runner.bus("PART_TWO").get(SYNC_POINT);
        controller.flushLogs(testInfo.getDisplayName());
        String logText = textOut.toString();
        System.out.println(logText);
        assertThat(logText, containsString("PART_ONE"));
        assertThat(logText, not(containsString("PART_TWO")));
        assertThat(logText, containsString("p1 log msg"));
        assertThat(logText, not(containsString("p2 log msg")));
    }

    @Test
    void testForkedProcessLogging(PartRunner runner, LogPublisher controller, TestInfo testInfo) {
        runner.useNewJVMWhenForking();
        testForkedLogging(runner, controller, testInfo);
    }
}
