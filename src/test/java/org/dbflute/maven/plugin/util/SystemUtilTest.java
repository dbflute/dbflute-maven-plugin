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

import org.junit.Test;

/**
 * Test for {@link SystemUtil}.
 * @author jflute
 */
public class SystemUtilTest {

    @Test
    public void test_isWindows() {
        // This test verifies that the method doesn't throw an exception
        // The actual result depends on the OS where the test is run
        boolean result = SystemUtil.isWindows();

        String osName = System.getProperty("os.name").toLowerCase();
        boolean expectedWindows = osName.contains("windows");

        assertEquals(expectedWindows, result);
    }

    @Test
    public void test_isWindows_consistentResult() {
        // Call multiple times and verify the result is consistent
        boolean firstCall = SystemUtil.isWindows();
        boolean secondCall = SystemUtil.isWindows();

        assertEquals(firstCall, secondCall);
    }

    @Test
    public void test_osName_property() {
        // Verify that os.name property is accessible
        String osName = System.getProperty("os.name");
        assertNotNull(osName);
        assertTrue(osName.length() > 0);
    }
}
