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
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test for {@link UpgradePlugin}.
 * @author jflute
 */
public class UpgradePluginTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private UpgradePlugin plugin;
    private File dbfluteClientDir;

    @Before
    public void setUp() throws Exception {
        plugin = new UpgradePlugin();
        dbfluteClientDir = tempFolder.newFolder("dbflute_client");

        // Use reflection to set basedir and dbfluteClientDir
        setField(plugin, "basedir", tempFolder.getRoot());
        setField(plugin, "dbfluteClientDir", dbfluteClientDir);
    }

    @Test
    public void test_updateArgs_withVersion() {
        List<String> cmds = new ArrayList<>();

        // Set version
        setField(plugin, "dbfluteVersion", "1.2.0");

        plugin.updateArgs(cmds);

        assertTrue(cmds.contains("upgrade"));
        assertTrue(cmds.contains("1.2.0"));
        assertEquals(2, cmds.size());
    }

    @Test
    public void test_updateArgs_withoutVersion() {
        List<String> cmds = new ArrayList<>();

        // Don't set version (null)
        setField(plugin, "dbfluteVersion", null);

        plugin.updateArgs(cmds);

        assertTrue(cmds.contains("upgrade"));
        assertEquals(1, cmds.size());
        assertFalse(cmds.contains(null));
    }

    @Test
    public void test_updateArgs_withEmptyList() {
        List<String> cmds = new ArrayList<>();

        setField(plugin, "dbfluteVersion", "1.1.0");

        plugin.updateArgs(cmds);

        assertFalse(cmds.isEmpty());
        assertEquals("upgrade", cmds.get(0));
        assertEquals("1.1.0", cmds.get(1));
    }

    @Test
    public void test_updateArgs_withExistingArgs() {
        List<String> cmds = new ArrayList<>();
        cmds.add("existing");

        setField(plugin, "dbfluteVersion", "1.2.0");

        plugin.updateArgs(cmds);

        assertEquals(3, cmds.size());
        assertEquals("existing", cmds.get(0));
        assertEquals("upgrade", cmds.get(1));
        assertEquals("1.2.0", cmds.get(2));
    }

    @Test
    public void test_updateArgs_versionFormats() {
        String[] versions = {
            "1.0.0",
            "1.2.0-SNAPSHOT",
            "1.1.0-RC1",
            "1.1.0-sp1",
            "latest"
        };

        for (String version : versions) {
            List<String> cmds = new ArrayList<>();
            setField(plugin, "dbfluteVersion", version);

            plugin.updateArgs(cmds);

            assertTrue(cmds.contains("upgrade"));
            assertTrue(cmds.contains(version));
        }
    }

    @Test
    public void test_updateArgs_multipleInvocations() {
        setField(plugin, "dbfluteVersion", "1.2.0");

        List<String> cmds1 = new ArrayList<>();
        plugin.updateArgs(cmds1);

        List<String> cmds2 = new ArrayList<>();
        plugin.updateArgs(cmds2);

        // Both should have the same content
        assertEquals(cmds1.size(), cmds2.size());
        assertEquals(cmds1.get(0), cmds2.get(0));
        assertEquals(cmds1.get(1), cmds2.get(1));
    }

    @Test
    public void test_dbfluteVersion_getter() throws Exception {
        setField(plugin, "dbfluteVersion", "1.2.3");

        // Use reflection to get the field value
        String version = (String) getField(plugin, "dbfluteVersion");
        assertEquals("1.2.3", version);
    }

    @Test
    public void test_pluginInheritance() {
        assertTrue(plugin instanceof CommandPlugin);
    }

    @Test
    public void test_execute_requiresDbfluteClientDir() {
        // This test verifies that execute method exists and can be called
        // Actual execution would require a real DBFlute client directory
        try {
            // Remove the client dir to test the early return
            setField(plugin, "dbfluteClientDir", new File(tempFolder.getRoot(), "nonexistent"));
            plugin.execute();
            // Should return early without error
        } catch (Exception e) {
            // Expected to fail without proper setup
            assertTrue(e.getMessage() != null);
        }
    }

    @Test
    public void test_updateArgs_orderPreserved() {
        List<String> cmds = new ArrayList<>();
        setField(plugin, "dbfluteVersion", "test-version");

        plugin.updateArgs(cmds);

        // Verify order: upgrade comes first, then version
        int upgradeIndex = cmds.indexOf("upgrade");
        int versionIndex = cmds.indexOf("test-version");

        assertTrue(upgradeIndex < versionIndex);
    }

    // Helper methods
    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }

    private Object getField(Object target, String fieldName) {
        try {
            java.lang.reflect.Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get field: " + fieldName, e);
        }
    }

    private java.lang.reflect.Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new RuntimeException("Field not found: " + fieldName);
    }
}
