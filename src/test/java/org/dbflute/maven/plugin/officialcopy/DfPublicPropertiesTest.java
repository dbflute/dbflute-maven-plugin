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
package org.dbflute.maven.plugin.officialcopy;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test for {@link DfPublicProperties}.
 * @author jflute
 */
public class DfPublicPropertiesTest {

    @Test
    public void test_getPublicPropertiesUrl_default() {
        DfPublicProperties properties = new DfPublicProperties();
        assertEquals(DfPublicProperties.PUBLIC_PROP_URL, properties.getPublicPropertiesUrl());
    }

    @Test
    public void test_getPublicPropertiesUrl_specified() {
        DfPublicProperties properties = new DfPublicProperties();
        String customUrl = "https://example.com/public.properties";
        properties.specifyUrl(customUrl);
        assertEquals(customUrl, properties.getPublicPropertiesUrl());
    }

    @Test
    public void test_getProperty_notLoaded() {
        DfPublicProperties properties = new DfPublicProperties();
        try {
            properties.getDBFluteLatestReleaseVersion();
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("Not loaded public properties"));
        }
    }

    @Test
    public void test_buildDBFluteDownloadUrl() {
        DfPublicProperties properties = new DfPublicProperties();
        String template = "http://dbflute.org/download/dbflute-$$version$$.zip";
        String version = "1.2.0";

        // Use reflection to test protected method
        try {
            java.lang.reflect.Method method = DfPublicProperties.class.getDeclaredMethod(
                "buildDBFluteDownloadUrl", String.class, String.class);
            method.setAccessible(true);
            String result = (String) method.invoke(properties, template, version);

            assertEquals("http://dbflute.org/download/dbflute-1.2.0.zip", result);
        } catch (Exception e) {
            fail("Failed to test buildDBFluteDownloadUrl: " + e.getMessage());
        }
    }

    @Test
    public void test_replace_basic() {
        DfPublicProperties properties = new DfPublicProperties();

        try {
            java.lang.reflect.Method method = DfPublicProperties.class.getDeclaredMethod(
                "replace", String.class, String.class, String.class);
            method.setAccessible(true);

            String result = (String) method.invoke(properties, "Hello $$name$$!", "$$name$$", "World");
            assertEquals("Hello World!", result);
        } catch (Exception e) {
            fail("Failed to test replace: " + e.getMessage());
        }
    }

    @Test
    public void test_replace_multiple() {
        DfPublicProperties properties = new DfPublicProperties();

        try {
            java.lang.reflect.Method method = DfPublicProperties.class.getDeclaredMethod(
                "replace", String.class, String.class, String.class);
            method.setAccessible(true);

            String result = (String) method.invoke(properties,
                "$$var$$ and $$var$$ again", "$$var$$", "test");
            assertEquals("test and test again", result);
        } catch (Exception e) {
            fail("Failed to test replace: " + e.getMessage());
        }
    }

    @Test
    public void test_replace_notFound() {
        DfPublicProperties properties = new DfPublicProperties();

        try {
            java.lang.reflect.Method method = DfPublicProperties.class.getDeclaredMethod(
                "replace", String.class, String.class, String.class);
            method.setAccessible(true);

            String original = "Hello World";
            String result = (String) method.invoke(properties, original, "$$notfound$$", "test");
            assertEquals(original, result);
        } catch (Exception e) {
            fail("Failed to test replace: " + e.getMessage());
        }
    }

    @Test
    public void test_replace_nullArguments() {
        DfPublicProperties properties = new DfPublicProperties();

        try {
            java.lang.reflect.Method method = DfPublicProperties.class.getDeclaredMethod(
                "replace", String.class, String.class, String.class);
            method.setAccessible(true);

            try {
                method.invoke(properties, null, "from", "to");
                fail("Should throw IllegalArgumentException for null str");
            } catch (java.lang.reflect.InvocationTargetException e) {
                assertTrue(e.getCause() instanceof IllegalArgumentException);
            }

            try {
                method.invoke(properties, "text", null, "to");
                fail("Should throw IllegalArgumentException for null fromStr");
            } catch (java.lang.reflect.InvocationTargetException e) {
                assertTrue(e.getCause() instanceof IllegalArgumentException);
            }

            try {
                method.invoke(properties, "text", "from", null);
                fail("Should throw IllegalArgumentException for null toStr");
            } catch (java.lang.reflect.InvocationTargetException e) {
                assertTrue(e.getCause() instanceof IllegalArgumentException);
            }
        } catch (Exception e) {
            fail("Failed to test replace with null: " + e.getMessage());
        }
    }
}
