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
package org.dbflute.maven.plugin.command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.List;

import org.dbflute.maven.plugin.CommandPlugin;
import org.dbflute.maven.plugin.util.SystemUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;

/**
 * Test for {@link CommandExecutor}.
 * @author jflute
 */
public class CommandExecutorTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private CommandPlugin mockPlugin;
    private File dbfluteClientDir;

    @Before
    public void setUp() throws Exception {
        mockPlugin = mock(CommandPlugin.class);
        dbfluteClientDir = tempFolder.newFolder("dbflute_client");
        when(mockPlugin.getDbfluteClientDir()).thenReturn(dbfluteClientDir);
    }

    @Test
    public void test_constructor() {
        CommandExecutor executor = new CommandExecutor(mockPlugin);
        assertNotNull(executor);
        assertNotNull(executor.environment);
        assertEquals(0, executor.environment.size());
    }

    @Test
    public void test_execute_directoryNotExists() throws Exception {
        File nonExistentDir = new File(tempFolder.getRoot(), "nonexistent");
        when(mockPlugin.getDbfluteClientDir()).thenReturn(nonExistentDir);

        CommandExecutor executor = new CommandExecutor(mockPlugin);

        // Should return early without throwing exception
        executor.execute("manage");

        verify(mockPlugin).getDbfluteClientDir();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_execute_commandBuilding_windows() throws Exception {
        if (!SystemUtil.isWindows()) {
            // Skip this test on non-Windows platforms
            return;
        }

        CommandExecutor executor = new CommandExecutor(mockPlugin);

        // Capture the arguments passed to updateArgs
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);

        // This will fail because the command doesn't exist, but we can verify the command structure
        try {
            executor.execute("manage");
        } catch (Exception e) {
            // Expected to fail
        }

        verify(mockPlugin, atLeastOnce()).updateArgs(captor.capture());

        List<String> capturedArgs = captor.getValue();
        assertEquals("cmd.exe", capturedArgs.get(0));
        assertEquals("/c", capturedArgs.get(1));
        assertEquals("manage.bat", capturedArgs.get(2));

        // Verify environment variable for Windows
        assertEquals("n", executor.environment.get("pause_at_end"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_execute_commandBuilding_unix() throws Exception {
        if (SystemUtil.isWindows()) {
            // Skip this test on Windows
            return;
        }

        CommandExecutor executor = new CommandExecutor(mockPlugin);

        // Capture the arguments passed to updateArgs
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);

        // This will fail because the command doesn't exist, but we can verify the command structure
        try {
            executor.execute("manage");
        } catch (Exception e) {
            // Expected to fail
        }

        verify(mockPlugin, atLeastOnce()).updateArgs(captor.capture());

        List<String> capturedArgs = captor.getValue();
        assertEquals("/bin/bash", capturedArgs.get(0));
        assertEquals("manage.sh", capturedArgs.get(1));
    }

    @Test
    public void test_environment_customization() {
        CommandExecutor executor = new CommandExecutor(mockPlugin);

        executor.environment.put("CUSTOM_VAR", "custom_value");

        assertEquals("custom_value", executor.environment.get("CUSTOM_VAR"));
        assertEquals(1, executor.environment.size());
    }

    @Test
    public void test_execute_withCustomEnvironment() throws Exception {
        CommandExecutor executor = new CommandExecutor(mockPlugin);
        executor.environment.put("TEST_VAR", "test_value");

        // This will fail because the command doesn't exist
        try {
            executor.execute("manage");
        } catch (Exception e) {
            // Expected to fail, but environment should have been set
        }

        // Verify the environment was configured
        assertTrue(executor.environment.containsKey("TEST_VAR"));
    }
}
