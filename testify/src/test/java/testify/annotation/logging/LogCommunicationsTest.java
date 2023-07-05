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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testify.annotation.RetriedTest;
import testify.bus.key.VoidSpec;
import testify.parts.PartRunner;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static testify.annotation.logging.LogCommunicationsTest.SyncPoint.SYNC_POINT_1;
import static testify.annotation.logging.LogCommunicationsTest.SyncPoint.SYNC_POINT_2;

public class LogCommunicationsTest {

    static final List<Logger> LOGGERS = Stream.of("test.foo util blob test".split(" ")).map(Logger::getLogger).collect(Collectors.toList());
    static final LogSetting TEST_FOO_FINE = new LogSetting("test.foo", Level.FINE);
    static final LogSetting UTIL_FINEST = new LogSetting("util", Level.FINEST);
    static final LogSetting BLOB_FINER = new LogSetting("blob", Level.FINER);
    static final LogSetting TEST_CONFIG = new LogSetting("test", Level.CONFIG);

    static final List<LogSetting> LOG_SETTINGS_1 = singletonList(TEST_FOO_FINE);
    static final List<LogSetting> LOG_SETTINGS_2 = Arrays.asList(UTIL_FINEST, BLOB_FINER);
    static final List<LogSetting> LOG_SETTINGS_3 = singletonList(TEST_CONFIG);

    static final Logger TEST_FOO_LOGGER = Logger.getLogger("test.foo");
    static final Logger BLOB_LOGGER = Logger.getLogger("blob");
    static final Logger BLOB_FOO_LOGGER = Logger.getLogger("blob.foo");

    PartRunner runner;
    LogPublisher publisher;
    StringWriter stringWriter;

    @BeforeAll
    static void clearLoggerLevels() { LOGGERS.forEach(l -> l.setLevel(null));}

    @BeforeEach
    void setup() {
        runner = PartRunner.create();
        publisher = LogPublisher.create(runner);
        stringWriter = new StringWriter();
        publisher.setOut(new PrintWriter(stringWriter));
    }

    @AfterEach
    void cleanUpAndCheckLoggerLevels() {
        publisher.close();
        runner.join();
        LOGGERS.forEach(l -> assertThat(l.getName(), l.getLevel(), is(nullValue())));
    }

    @Test
    void testRecorderSolo() {
        LogRecorder recorder = LogRecorder.create((runner.bus("one")));
        publisher.pushSettings(LOG_SETTINGS_1);
        Deque<List<LogSetting>> settingsStack = recorder.copySettingsStack();
        List<LogSetting> settings = settingsStack.pop();
        // the settings should match what was sent to the publisher
        assertThat(settings, equalTo(LOG_SETTINGS_1));
        // the objects should have gone via the bus and therefore not be the same instance
        assertThat(settings.get(0), is(not(sameInstance(LOG_SETTINGS_1.get(0)))));
        // there should not be any other settings
        assertThat(settingsStack, is(empty()));
    }

    @Test
    void testRecorderSoloLogging() {
        try {
            TEST_FOO_LOGGER.fine("should not appear");
            LogRecorder.create((runner.bus("one")));
            TEST_FOO_LOGGER.fine("also should not appear");
            publisher.pushSettings(LOG_SETTINGS_1);
            TEST_FOO_LOGGER.fine("should appear");
            publisher.flushLogs("junit");
            String output = stringWriter.toString();
            System.out.println(output);
            assertThat(output, containsString("should appear"));
            assertThat(output, not(containsString("should not appear")));
        } finally {
            runner.dumpBuses();
        }
    }

    @RetriedTest(maxRuns = 50)
    void testRecorderDuet() {
        LogRecorder recorder1 = LogRecorder.create((runner.bus("one")));
        assertSettingsStack(recorder1);
        publisher.pushSettings(LOG_SETTINGS_1);
        assertSettingsStack(recorder1, LOG_SETTINGS_1);
        LogRecorder recorder2 = LogRecorder.create((runner.bus("two")));
        assertSettingsStack(recorder2, LOG_SETTINGS_1);
        publisher.pushSettings(LOG_SETTINGS_2);
        assertSettingsStack(recorder1, LOG_SETTINGS_2, LOG_SETTINGS_1);
        assertSettingsStack(recorder2, LOG_SETTINGS_2, LOG_SETTINGS_1);
        publisher.popSettings();
        assertSettingsStack(recorder1, LOG_SETTINGS_1);
        assertSettingsStack(recorder2, LOG_SETTINGS_1);
        publisher.popSettings();
        assertSettingsStack(recorder1);
        assertSettingsStack(recorder2);
        runner.dumpBuses();
    }

    @Test
    void testRecorderDuetLogging() {
        LogRecorder.create((runner.bus("one")));
        TEST_FOO_LOGGER.fine("should not appear #1");
        publisher.pushSettings(LOG_SETTINGS_1);
        TEST_FOO_LOGGER.fine("should appear #1");
        LogRecorder.create((runner.bus("two")));
        TEST_FOO_LOGGER.fine("should appear #2");
        publisher.pushSettings(LOG_SETTINGS_2);
        TEST_FOO_LOGGER.fine("should appear #3");
        BLOB_FOO_LOGGER.finer("should appear #4");
        BLOB_LOGGER.finest("should not appear #2");
        publisher.popSettings();
        TEST_FOO_LOGGER.fine("should appear #5");
        BLOB_FOO_LOGGER.finer("should not appear #3");
        publisher.popSettings();
        TEST_FOO_LOGGER.fine("should not appear #4");
        publisher.flushLogs("junit");
        String output = stringWriter.toString();
        System.out.println(output);
        assertThat(output, not(containsString("should not appear")));

        assertThat(output, containsString("one      AAA  [test.foo]    FINE: should appear #1"));

        assertThat(output, containsString("one      AAA  [test.foo]    FINE: should appear #2"));
        assertThat(output, containsString("two      AAA  [test.foo]    FINE: should appear #2"));

        assertThat(output, containsString("one      AAA  [test.foo]    FINE: should appear #3"));
        assertThat(output, containsString("two      AAA  [test.foo]    FINE: should appear #3"));

        assertThat(output, containsString("one      AAA  [blob.foo]   FINER: should appear #4"));
        assertThat(output, containsString("two      AAA  [blob.foo]   FINER: should appear #4"));

        assertThat(output, containsString("one      AAA  [test.foo]    FINE: should appear #5"));
        assertThat(output, containsString("two      AAA  [test.foo]    FINE: should appear #5"));
    }

    @RetriedTest(maxRuns = 50)
    void testRecorderQuartet() {
        LogRecorder recorder1 = LogRecorder.create((runner.bus("one")));
        publisher.pushSettings(LOG_SETTINGS_1);
        LogRecorder recorder2 = LogRecorder.create((runner.bus("two")));
        publisher.pushSettings(LOG_SETTINGS_2);
        LogRecorder recorder3 = LogRecorder.create((runner.bus("three")));
        publisher.pushSettings(LOG_SETTINGS_3);
        LogRecorder recorder4 = LogRecorder.create((runner.bus("four")));
        assertSettingsStack(recorder1, LOG_SETTINGS_3, LOG_SETTINGS_2, LOG_SETTINGS_1);
        assertSettingsStack(recorder2, LOG_SETTINGS_3, LOG_SETTINGS_2, LOG_SETTINGS_1);
        assertSettingsStack(recorder3, LOG_SETTINGS_3, LOG_SETTINGS_2, LOG_SETTINGS_1);
        assertSettingsStack(recorder4, LOG_SETTINGS_3, LOG_SETTINGS_2, LOG_SETTINGS_1);
        publisher.popSettings();
        publisher.popSettings();
        assertSettingsStack(recorder1, LOG_SETTINGS_1);
        assertSettingsStack(recorder2, LOG_SETTINGS_1);
        assertSettingsStack(recorder3, LOG_SETTINGS_1);
        assertSettingsStack(recorder4, LOG_SETTINGS_1);
    }

    enum SyncPoint implements VoidSpec {SYNC_POINT_1, SYNC_POINT_2}
    @Test
    void testRecorderInNewJVM() {
        try {
            LogRecorder.create((runner.bus("one")));
            publisher.pushSettings(LOG_SETTINGS_1);
            runner.useNewJVMWhenForking().fork("two", bus -> {
                LogRecorder.create(bus);
                TEST_FOO_LOGGER.fine("should appear #1");
                TEST_FOO_LOGGER.finer("should not appear #1");
                bus.get(SYNC_POINT_1);
                BLOB_FOO_LOGGER.fine("should appear #2");
                bus.put(SYNC_POINT_2);
            });
            TEST_FOO_LOGGER.fine("should appear #3");
            publisher.pushSettings(LOG_SETTINGS_2);
            BLOB_FOO_LOGGER.fine("should appear #4");
            runner.bus("two").put(SYNC_POINT_1).get(SYNC_POINT_2);
            publisher.flushLogs("junit");
            String output = stringWriter.toString();
            System.out.println(output);
        } finally {
            runner.dumpBuses();
        }
    }

    @SafeVarargs
    static void assertSettingsStack(LogRecorder rec, List<LogSetting>...expected) {
        List<?> expectedList = Arrays.asList(expected);
        List<?> actualList = new ArrayList<>(rec.copySettingsStack());
        assertThat(actualList, equalTo(expectedList));
    }
}
