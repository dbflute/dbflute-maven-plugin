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
package org.dbflute.maven.plugin;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test for {@link PluginSystemException}.
 * @author jflute
 */
public class PluginSystemExceptionTest {

    @Test
    public void test_defaultConstructor() {
        PluginSystemException exception = new PluginSystemException();
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_messageConstructor() {
        String message = "Test error message";
        PluginSystemException exception = new PluginSystemException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_causeConstructor() {
        Throwable cause = new RuntimeException("Original exception");
        PluginSystemException exception = new PluginSystemException(cause);

        assertNotNull(exception);
        assertSame(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("RuntimeException"));
    }

    @Test
    public void test_messageAndCauseConstructor() {
        String message = "Wrapper message";
        Throwable cause = new IllegalArgumentException("Original cause");
        PluginSystemException exception = new PluginSystemException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    public void test_exceptionInheritance() {
        PluginSystemException exception = new PluginSystemException();
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void test_throwAndCatch() {
        try {
            throw new PluginSystemException("Test throw");
        } catch (PluginSystemException e) {
            assertEquals("Test throw", e.getMessage());
        }
    }

    @Test
    public void test_throwWithCause() {
        Exception originalCause = new Exception("Original");

        try {
            throw new PluginSystemException("Wrapped", originalCause);
        } catch (PluginSystemException e) {
            assertEquals("Wrapped", e.getMessage());
            assertSame(originalCause, e.getCause());
        }
    }

    @Test
    public void test_stackTrace() {
        PluginSystemException exception = new PluginSystemException("Stack trace test");
        StackTraceElement[] stackTrace = exception.getStackTrace();

        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
    }

    @Test
    public void test_serialVersionUID() throws Exception {
        // Verify that serialVersionUID is defined
        java.lang.reflect.Field field = PluginSystemException.class.getDeclaredField("serialVersionUID");
        assertNotNull(field);
        assertEquals(1L, field.getLong(null));
    }

    @Test
    public void test_nullMessage() {
        PluginSystemException exception = new PluginSystemException((String) null);
        assertNull(exception.getMessage());
    }

    @Test
    public void test_nullCause() {
        PluginSystemException exception = new PluginSystemException((Throwable) null);
        assertNotNull(exception);
    }

    @Test
    public void test_emptyMessage() {
        PluginSystemException exception = new PluginSystemException("");
        assertEquals("", exception.getMessage());
    }

    @Test
    public void test_longMessage() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Long message ");
        }
        String longMessage = sb.toString();

        PluginSystemException exception = new PluginSystemException(longMessage);
        assertEquals(longMessage, exception.getMessage());
    }

    @Test
    public void test_causeChain() {
        Throwable rootCause = new Exception("Root");
        Throwable middleCause = new RuntimeException("Middle", rootCause);
        PluginSystemException exception = new PluginSystemException("Top", middleCause);

        assertSame(middleCause, exception.getCause());
        assertSame(rootCause, exception.getCause().getCause());
    }

    @Test
    public void test_messageFormatting() {
        String message = "Error in file: %s at line: %d";
        PluginSystemException exception = new PluginSystemException(
            String.format(message, "test.java", 42));

        assertTrue(exception.getMessage().contains("test.java"));
        assertTrue(exception.getMessage().contains("42"));
    }
}
