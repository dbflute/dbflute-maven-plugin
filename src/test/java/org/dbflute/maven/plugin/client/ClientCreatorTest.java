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
package org.dbflute.maven.plugin.client;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.MojoFailureException;
import org.dbflute.maven.plugin.CreateClientPlugin;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test for {@link ClientCreator}.
 * @author jflute
 */
public class ClientCreatorTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private CreateClientPlugin mockPlugin;
    private File baseDir;
    private File dbfluteDir;

    @Before
    public void setUp() throws Exception {
        mockPlugin = mock(CreateClientPlugin.class);
        baseDir = tempFolder.newFolder("project");
        dbfluteDir = tempFolder.newFolder("mydbflute", "dbflute-1.2.0");

        when(mockPlugin.getBasedir()).thenReturn(baseDir);
        when(mockPlugin.getDbfluteDir()).thenReturn(dbfluteDir);
    }

    @Test
    public void test_constructor() {
        ClientCreator creator = new ClientCreator(mockPlugin);
        assertNotNull(creator);
    }

    @Test
    public void test_execute_clientDirAlreadyExists() throws Exception {
        File existingClientDir = new File(baseDir, "dbflute_client");
        assertTrue(existingClientDir.mkdirs());

        when(mockPlugin.getDbfluteClientDir()).thenReturn(existingClientDir);
        when(mockPlugin.getClientProject()).thenReturn("client");

        ClientCreator creator = new ClientCreator(mockPlugin);

        // Should return early without error when directory exists
        creator.execute();

        verify(mockPlugin).getDbfluteDir();
        verify(mockPlugin).getDbfluteClientDir();
    }

    @Test
    public void test_execute_missingClientZip() throws Exception {
        File clientDir = new File(baseDir, "dbflute_client");

        when(mockPlugin.getDbfluteClientDir()).thenReturn(clientDir);
        when(mockPlugin.getClientProject()).thenReturn("client");

        ClientCreator creator = new ClientCreator(mockPlugin);

        try {
            creator.execute();
            fail("Should throw MojoFailureException when client zip doesn't exist");
        } catch (MojoFailureException e) {
            assertTrue(e.getMessage().contains("does not exist"));
        }
    }

    @Test
    public void test_putParam_withValue() throws Exception {
        ClientCreator creator = new ClientCreator(mockPlugin);

        Map<String, String> params = new HashMap<String, String>();

        // Use reflection to test protected method
        java.lang.reflect.Method method = ClientCreator.class.getDeclaredMethod(
            "putParam", Map.class, String.class, String.class, String.class);
        method.setAccessible(true);

        method.invoke(creator, params, "key1", "prefix_", "value1");

        assertEquals("prefix_value1", params.get("key1"));
    }

    @Test
    public void test_putParam_withNullValue() throws Exception {
        ClientCreator creator = new ClientCreator(mockPlugin);

        Map<String, String> params = new HashMap<String, String>();

        // Use reflection to test protected method
        java.lang.reflect.Method method = ClientCreator.class.getDeclaredMethod(
            "putParam", Map.class, String.class, String.class, String.class);
        method.setAccessible(true);

        method.invoke(creator, params, "key1", "prefix_", null);

        assertFalse(params.containsKey("key1"));
    }

    @Test
    public void test_putParam_withNullPrefix() throws Exception {
        ClientCreator creator = new ClientCreator(mockPlugin);

        Map<String, String> params = new HashMap<String, String>();

        // Use reflection to test protected method
        java.lang.reflect.Method method = ClientCreator.class.getDeclaredMethod(
            "putParam", Map.class, String.class, String.class, String.class);
        method.setAccessible(true);

        method.invoke(creator, params, "key1", null, "value1");

        assertEquals("value1", params.get("key1"));
    }

    @Test
    public void test_defaultClientProject() throws Exception {
        File clientDir = new File(baseDir, "dbflute_client");

        when(mockPlugin.getDbfluteClientDir()).thenReturn(null);
        when(mockPlugin.getClientProject()).thenReturn(null);

        ClientCreator creator = new ClientCreator(mockPlugin);

        // This will fail due to missing client zip, but we can verify
        // the default client project logic was invoked
        try {
            creator.execute();
        } catch (MojoFailureException e) {
            // Expected to fail
            verify(mockPlugin).getClientProject();
        }
    }
}
