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
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test for {@link CommandPlugin}.
 * @author jflute
 */
public class CommandPluginTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private TestCommandPlugin plugin;
    private MavenProject mockProject;
    private File basedir;

    @Before
    public void setUp() throws Exception {
        plugin = new TestCommandPlugin();
        mockProject = mock(MavenProject.class);
        basedir = tempFolder.getRoot();

        when(mockProject.getArtifactId()).thenReturn("testproject");

        setField(plugin, "basedir", basedir);
        setField(plugin, "project", mockProject);
    }

    @Test
    public void test_getDbfluteClientDir_defaultFromArtifactId() {
        File clientDir = plugin.getDbfluteClientDir();

        assertNotNull(clientDir);
        assertEquals(new File(basedir, "dbflute_testproject"), clientDir);
    }

    @Test
    public void test_getDbfluteClientDir_fromClientProject() {
        setField(plugin, "clientProject", "maihamadb");

        File clientDir = plugin.getDbfluteClientDir();

        assertNotNull(clientDir);
        assertEquals(new File(basedir, "dbflute_maihamadb"), clientDir);
    }

    @Test
    public void test_getDbfluteClientDir_explicitlySet() {
        File explicitDir = new File(basedir, "custom_dbflute");
        setField(plugin, "dbfluteClientDir", explicitDir);

        File clientDir = plugin.getDbfluteClientDir();

        assertNotNull(clientDir);
        assertEquals(explicitDir, clientDir);
    }

    @Test
    public void test_getDbfluteClientDir_cached() {
        File firstCall = plugin.getDbfluteClientDir();
        File secondCall = plugin.getDbfluteClientDir();

        assertSame(firstCall, secondCall);
    }

    @Test
    public void test_getDbfluteClientDir_withEmptyClientProject() {
        setField(plugin, "clientProject", "");

        File clientDir = plugin.getDbfluteClientDir();

        // Should use artifactId when clientProject is empty
        assertEquals(new File(basedir, "dbflute_testproject"), clientDir);
    }

    @Test
    public void test_getDbfluteClientDir_withNullClientProject() {
        setField(plugin, "clientProject", null);

        File clientDir = plugin.getDbfluteClientDir();

        // Should use artifactId when clientProject is null
        assertEquals(new File(basedir, "dbflute_testproject"), clientDir);
    }

    @Test
    public void test_getDbfluteClientDir_withWhitespaceClientProject() {
        setField(plugin, "clientProject", "   ");

        File clientDir = plugin.getDbfluteClientDir();

        // Should use artifactId when clientProject is blank
        assertEquals(new File(basedir, "dbflute_testproject"), clientDir);
    }

    @Test
    public void test_getDbfluteClientDir_clientProjectPriority() {
        setField(plugin, "clientProject", "custom");

        File clientDir = plugin.getDbfluteClientDir();

        // Should prefer clientProject over artifactId
        assertEquals(new File(basedir, "dbflute_custom"), clientDir);
        verify(mockProject, never()).getArtifactId();
    }

    @Test
    public void test_getDbfluteClientDir_multipleClientProjects() {
        String[] projects = {"maihamadb", "hangardb", "resortlinedb"};

        for (String project : projects) {
            TestCommandPlugin testPlugin = new TestCommandPlugin();
            setField(testPlugin, "basedir", basedir);
            setField(testPlugin, "project", mockProject);
            setField(testPlugin, "clientProject", project);

            File clientDir = testPlugin.getDbfluteClientDir();
            assertEquals(new File(basedir, "dbflute_" + project), clientDir);
        }
    }

    @Test
    public void test_updateArgs_default() {
        List<String> cmds = new ArrayList<>();
        plugin.updateArgs(cmds);

        // Default implementation does nothing
        assertTrue(cmds.isEmpty());
    }

    @Test
    public void test_updateArgs_canBeOverridden() {
        TestCommandPluginWithArgs pluginWithArgs = new TestCommandPluginWithArgs();
        List<String> cmds = new ArrayList<>();

        pluginWithArgs.updateArgs(cmds);

        assertFalse(cmds.isEmpty());
        assertTrue(cmds.contains("custom-arg"));
    }

    @Test
    public void test_inheritance() {
        assertTrue(plugin instanceof org.apache.maven.plugin.AbstractMojo);
    }

    @Test
    public void test_basedirField() throws Exception {
        File testBasedir = new File("/test/path");
        setField(plugin, "basedir", testBasedir);

        File retrievedBasedir = (File) getField(plugin, "basedir");
        assertEquals(testBasedir, retrievedBasedir);
    }

    @Test
    public void test_projectField() throws Exception {
        MavenProject testProject = mock(MavenProject.class);
        setField(plugin, "project", testProject);

        MavenProject retrievedProject = (MavenProject) getField(plugin, "project");
        assertSame(testProject, retrievedProject);
    }

    @Test
    public void test_getDbfluteClientDir_pathConstruction() {
        setField(plugin, "clientProject", "myproject");

        File clientDir = plugin.getDbfluteClientDir();
        String path = clientDir.getPath();

        assertTrue(path.contains("dbflute_myproject"));
    }

    @Test
    public void test_getDbfluteClientDir_absolutePath() {
        File clientDir = plugin.getDbfluteClientDir();

        // The path should include the basedir
        assertTrue(clientDir.getAbsolutePath().contains(basedir.getAbsolutePath()));
    }

    // Test plugin implementations
    private static class TestCommandPlugin extends CommandPlugin {
        @Override
        public void execute() throws MojoExecutionException, MojoFailureException {
            // Test implementation
        }
    }

    private static class TestCommandPluginWithArgs extends CommandPlugin {
        @Override
        public void execute() throws MojoExecutionException, MojoFailureException {
            // Test implementation
        }

        @Override
        public void updateArgs(List<String> cmds) {
            cmds.add("custom-arg");
        }
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
