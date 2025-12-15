/*
 * Copyright 2014-2025 The DBFlute Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.dbflute.maven.plugin.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link LogUtil}.
 * @author jflute
 */
public class LogUtilTest {

    private Log originalLog;

    @Before
    public void setUp() {
        // Store the original log
        originalLog = LogUtil.getLog();
    }

    @After
    public void tearDown() {
        // Reset to original state
        LogUtil.init(null);
    }

    @Test
    public void test_getLog_defaultInstance() {
        // Reset to get default instance
        LogUtil.init(null);

        Log log = LogUtil.getLog();

        assertNotNull(log);
        assertTrue(log instanceof SystemStreamLog);
    }

    @Test
    public void test_getLog_returnsNonNull() {
        Log log = LogUtil.getLog();
        assertNotNull(log);
    }

    @Test
    public void test_getLog_returnsSameInstance() {
        Log log1 = LogUtil.getLog();
        Log log2 = LogUtil.getLog();

        assertSame(log1, log2);
    }

    @Test
    public void test_init_setsLog() {
        Log mockLog = mock(Log.class);

        LogUtil.init(mockLog);

        Log retrievedLog = LogUtil.getLog();
        assertSame(mockLog, retrievedLog);
    }

    @Test
    public void test_init_replacesExistingLog() {
        Log firstLog = mock(Log.class);
        Log secondLog = mock(Log.class);

        LogUtil.init(firstLog);
        assertSame(firstLog, LogUtil.getLog());

        LogUtil.init(secondLog);
        assertSame(secondLog, LogUtil.getLog());

        assertNotSame(firstLog, secondLog);
    }

    @Test
    public void test_init_withNull() {
        Log mockLog = mock(Log.class);
        LogUtil.init(mockLog);
        assertSame(mockLog, LogUtil.getLog());

        // Init with null
        LogUtil.init(null);

        // Should create default SystemStreamLog
        Log log = LogUtil.getLog();
        assertNotNull(log);
        assertTrue(log instanceof SystemStreamLog);
    }

    @Test
    public void test_logFunctionality_info() {
        Log mockLog = mock(Log.class);
        LogUtil.init(mockLog);

        LogUtil.getLog().info("Test message");

        verify(mockLog).info("Test message");
    }

    @Test
    public void test_logFunctionality_debug() {
        Log mockLog = mock(Log.class);
        LogUtil.init(mockLog);

        LogUtil.getLog().debug("Debug message");

        verify(mockLog).debug("Debug message");
    }

    @Test
    public void test_logFunctionality_warn() {
        Log mockLog = mock(Log.class);
        LogUtil.init(mockLog);

        LogUtil.getLog().warn("Warning message");

        verify(mockLog).warn("Warning message");
    }

    @Test
    public void test_logFunctionality_error() {
        Log mockLog = mock(Log.class);
        LogUtil.init(mockLog);

        LogUtil.getLog().error("Error message");

        verify(mockLog).error("Error message");
    }

    @Test
    public void test_logFunctionality_withException() {
        Log mockLog = mock(Log.class);
        LogUtil.init(mockLog);
        Exception exception = new RuntimeException("Test exception");

        LogUtil.getLog().error("Error occurred", exception);

        verify(mockLog).error("Error occurred", exception);
    }

    @Test
    public void test_isDebugEnabled() {
        Log mockLog = mock(Log.class);
        when(mockLog.isDebugEnabled()).thenReturn(true);
        LogUtil.init(mockLog);

        boolean isDebugEnabled = LogUtil.getLog().isDebugEnabled();

        assertTrue(isDebugEnabled);
        verify(mockLog).isDebugEnabled();
    }

    @Test
    public void test_isInfoEnabled() {
        Log mockLog = mock(Log.class);
        when(mockLog.isInfoEnabled()).thenReturn(true);
        LogUtil.init(mockLog);

        boolean isInfoEnabled = LogUtil.getLog().isInfoEnabled();

        assertTrue(isInfoEnabled);
        verify(mockLog).isInfoEnabled();
    }

    @Test
    public void test_multipleThreads() throws InterruptedException {
        Log mockLog = mock(Log.class);
        LogUtil.init(mockLog);

        Thread thread1 = new Thread(() -> {
            Log log = LogUtil.getLog();
            assertSame(mockLog, log);
        });

        Thread thread2 = new Thread(() -> {
            Log log = LogUtil.getLog();
            assertSame(mockLog, log);
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    @Test
    public void test_staticBehavior() {
        // Test that LogUtil maintains static state correctly
        Log log1 = LogUtil.getLog();
        Log log2 = LogUtil.getLog();

        assertNotNull(log1);
        assertNotNull(log2);
        assertSame(log1, log2);
    }
}
