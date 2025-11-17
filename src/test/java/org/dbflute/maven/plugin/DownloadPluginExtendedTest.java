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

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Extended test for {@link DownloadPlugin}.
 * @author jflute
 */
public class DownloadPluginExtendedTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private DownloadPlugin plugin;

    @Before
    public void setUp() {
        plugin = new DownloadPlugin();
    }

    @Test
    public void test_getDbfluteVersion() {
        setField(plugin, "dbfluteVersion", "1.2.0");
        assertEquals("1.2.0", plugin.getDbfluteVersion());
    }

    @Test
    public void test_getDbfluteVersion_null() {
        setField(plugin, "dbfluteVersion", null);
        assertNull(plugin.getDbfluteVersion());
    }

    @Test
    public void test_getDownloadFilePrefix_default() {
        setField(plugin, "downloadFilePrefix", "dbflute-");
        assertEquals("dbflute-", plugin.getDownloadFilePrefix());
    }

    @Test
    public void test_getDownloadFilePrefix_custom() {
        setField(plugin, "downloadFilePrefix", "custom-");
        assertEquals("custom-", plugin.getDownloadFilePrefix());
    }

    @Test
    public void test_getMydbfluteDir_default() throws Exception {
        File testDir = tempFolder.newFolder();
        setField(plugin, "mydbfluteDir", testDir);
        assertEquals(testDir, plugin.getMydbfluteDir());
    }

    @Test
    public void test_getDownloadUrl() {
        String url = "http://example.com/dbflute.zip";
        setField(plugin, "downloadUrl", url);
        assertEquals(url, plugin.getDownloadUrl());
    }

    @Test
    public void test_getDownloadUrl_null() {
        setField(plugin, "downloadUrl", null);
        assertNull(plugin.getDownloadUrl());
    }

    @Test
    public void test_allGetters() {
        setField(plugin, "dbfluteVersion", "1.2.3");
        setField(plugin, "downloadFilePrefix", "test-");
        setField(plugin, "downloadUrl", "http://test.com/file.zip");
        File testDir = new File("/test/dir");
        setField(plugin, "mydbfluteDir", testDir);

        assertEquals("1.2.3", plugin.getDbfluteVersion());
        assertEquals("test-", plugin.getDownloadFilePrefix());
        assertEquals("http://test.com/file.zip", plugin.getDownloadUrl());
        assertEquals(testDir, plugin.getMydbfluteDir());
    }

    @Test
    public void test_multipleInstances() {
        DownloadPlugin plugin1 = new DownloadPlugin();
        DownloadPlugin plugin2 = new DownloadPlugin();

        setField(plugin1, "dbfluteVersion", "1.0.0");
        setField(plugin2, "dbfluteVersion", "2.0.0");

        assertEquals("1.0.0", plugin1.getDbfluteVersion());
        assertEquals("2.0.0", plugin2.getDbfluteVersion());
    }

    @Test
    public void test_versionFormats() {
        String[] versions = {
            "1.0.0",
            "1.2.0-SNAPSHOT",
            "1.1.0-RC1",
            "1.1.0-sp1",
            "latest",
            "0.9.9.9"
        };

        for (String version : versions) {
            setField(plugin, "dbfluteVersion", version);
            assertEquals(version, plugin.getDbfluteVersion());
        }
    }

    @Test
    public void test_urlFormats() {
        String[] urls = {
            "http://dbflute.org/download/dbflute-1.2.0.zip",
            "https://example.com/path/to/file.zip",
            "http://localhost:8080/file.zip"
        };

        for (String url : urls) {
            setField(plugin, "downloadUrl", url);
            assertEquals(url, plugin.getDownloadUrl());
        }
    }

    @Test
    public void test_prefixFormats() {
        String[] prefixes = {"dbflute-", "custom-", "test_", ""};

        for (String prefix : prefixes) {
            setField(plugin, "downloadFilePrefix", prefix);
            assertEquals(prefix, plugin.getDownloadFilePrefix());
        }
    }

    @Test
    public void test_directoryPaths() throws Exception {
        File[] dirs = {
            tempFolder.newFolder("mydbflute"),
            tempFolder.newFolder("custom"),
            tempFolder.newFolder("test", "nested")
        };

        for (File dir : dirs) {
            setField(plugin, "mydbfluteDir", dir);
            assertEquals(dir, plugin.getMydbfluteDir());
        }
    }

    @Test
    public void test_pluginNotNull() {
        assertNotNull(plugin);
    }

    @Test
    public void test_inheritance() {
        assertTrue(plugin instanceof org.apache.maven.plugin.AbstractMojo);
    }

    // Helper method
    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}
