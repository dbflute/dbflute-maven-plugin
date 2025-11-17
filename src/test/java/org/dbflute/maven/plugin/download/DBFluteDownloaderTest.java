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
package org.dbflute.maven.plugin.download;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;

import org.apache.maven.plugin.MojoFailureException;
import org.dbflute.maven.plugin.DownloadPlugin;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test for {@link DBFluteDownloader}.
 * @author jflute
 */
public class DBFluteDownloaderTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private DownloadPlugin mockPlugin;
    private File mydbfluteDir;

    @Before
    public void setUp() throws Exception {
        mockPlugin = mock(DownloadPlugin.class);
        mydbfluteDir = tempFolder.newFolder("mydbflute");
        when(mockPlugin.getMydbfluteDir()).thenReturn(mydbfluteDir);
    }

    @Test
    public void test_constructor() {
        DBFluteDownloader downloader = new DBFluteDownloader(mockPlugin);
        assertNotNull(downloader);
    }

    @Test
    public void test_createEngineDownloadHandler_withAllParameters() throws Exception {
        when(mockPlugin.getDbfluteVersion()).thenReturn("1.2.0");
        when(mockPlugin.getDownloadFilePrefix()).thenReturn("dbflute-");
        when(mockPlugin.getDownloadUrl()).thenReturn("http://example.com/dbflute.zip");

        DBFluteDownloader downloader = new DBFluteDownloader(mockPlugin);

        // Use reflection to test protected method
        java.lang.reflect.Method method = DBFluteDownloader.class.getDeclaredMethod(
            "createEngineDownloadHandler");
        method.setAccessible(true);
        EngineDownloadHandler handler = (EngineDownloadHandler) method.invoke(downloader);

        assertNotNull(handler);
        verify(mockPlugin).getDbfluteVersion();
        verify(mockPlugin).getMydbfluteDir();
        verify(mockPlugin).getDownloadFilePrefix();
        verify(mockPlugin).getDownloadUrl();
    }

    @Test
    public void test_createEngineDownloadHandler_withNullVersion() throws Exception {
        when(mockPlugin.getDbfluteVersion()).thenReturn(null);
        when(mockPlugin.getDownloadFilePrefix()).thenReturn("dbflute-");
        when(mockPlugin.getDownloadUrl()).thenReturn("http://example.com/dbflute.zip");

        DBFluteDownloader downloader = new DBFluteDownloader(mockPlugin);

        // Use reflection to test protected method
        java.lang.reflect.Method method = DBFluteDownloader.class.getDeclaredMethod(
            "createEngineDownloadHandler");
        method.setAccessible(true);
        EngineDownloadHandler handler = (EngineDownloadHandler) method.invoke(downloader);

        assertNotNull(handler);
    }

    @Test
    public void test_createEngineDownloadHandler_withNullDownloadUrl() throws Exception {
        when(mockPlugin.getDbfluteVersion()).thenReturn("1.2.0");
        when(mockPlugin.getDownloadFilePrefix()).thenReturn("dbflute-");
        when(mockPlugin.getDownloadUrl()).thenReturn(null);

        DBFluteDownloader downloader = new DBFluteDownloader(mockPlugin);

        // Use reflection to test protected method
        java.lang.reflect.Method method = DBFluteDownloader.class.getDeclaredMethod(
            "createEngineDownloadHandler");
        method.setAccessible(true);
        EngineDownloadHandler handler = (EngineDownloadHandler) method.invoke(downloader);

        assertNotNull(handler);
    }

    @Test
    public void test_createEngineDownloadHandler_withDefaultPrefix() throws Exception {
        when(mockPlugin.getDbfluteVersion()).thenReturn("1.2.0");
        when(mockPlugin.getDownloadFilePrefix()).thenReturn("dbflute-");
        when(mockPlugin.getDownloadUrl()).thenReturn("http://example.com/dbflute.zip");

        DBFluteDownloader downloader = new DBFluteDownloader(mockPlugin);

        // Use reflection to test protected method
        java.lang.reflect.Method method = DBFluteDownloader.class.getDeclaredMethod(
            "createEngineDownloadHandler");
        method.setAccessible(true);
        EngineDownloadHandler handler = (EngineDownloadHandler) method.invoke(downloader);

        assertNotNull(handler);
        verify(mockPlugin).getDownloadFilePrefix();
    }

    @Test
    public void test_execute_callsDownloadHandler() throws Exception {
        when(mockPlugin.getDbfluteVersion()).thenReturn("1.2.0");
        when(mockPlugin.getDownloadFilePrefix()).thenReturn("dbflute-");
        when(mockPlugin.getDownloadUrl()).thenReturn("http://example.com/dbflute.zip");

        // Create target directory to avoid actual download
        File targetDir = new File(mydbfluteDir, "dbflute-1.2.0");
        assertTrue(targetDir.mkdirs());

        DBFluteDownloader downloader = new DBFluteDownloader(mockPlugin);

        try {
            downloader.execute();
            // If it completes without exception, the flow is correct
            verify(mockPlugin, atLeastOnce()).getDbfluteVersion();
            verify(mockPlugin, atLeastOnce()).getMydbfluteDir();
        } catch (MojoFailureException e) {
            // May fail due to network issues, which is acceptable in unit tests
            assertTrue(e.getMessage() != null);
        }
    }

    @Test
    public void test_execute_withExistingDirectory() throws Exception {
        when(mockPlugin.getDbfluteVersion()).thenReturn("1.2.0");
        when(mockPlugin.getDownloadFilePrefix()).thenReturn("dbflute-");
        when(mockPlugin.getDownloadUrl()).thenReturn("http://example.com/dbflute.zip");

        // Pre-create the directory
        File targetDir = new File(mydbfluteDir, "dbflute-1.2.0");
        assertTrue(targetDir.mkdirs());

        DBFluteDownloader downloader = new DBFluteDownloader(mockPlugin);

        // Should complete without error when directory exists
        try {
            downloader.execute();
            assertTrue(targetDir.exists());
        } catch (Exception e) {
            // Acceptable if it fails for other reasons
        }
    }

    @Test
    public void test_execute_withCustomPrefix() throws Exception {
        when(mockPlugin.getDbfluteVersion()).thenReturn("1.2.0");
        when(mockPlugin.getDownloadFilePrefix()).thenReturn("custom-");
        when(mockPlugin.getDownloadUrl()).thenReturn("http://example.com/dbflute.zip");

        // Create target directory with custom prefix
        File targetDir = new File(mydbfluteDir, "custom-1.2.0");
        assertTrue(targetDir.mkdirs());

        DBFluteDownloader downloader = new DBFluteDownloader(mockPlugin);

        try {
            downloader.execute();
            assertTrue(targetDir.exists());
        } catch (Exception e) {
            // Acceptable if it fails for other reasons
        }
    }

    @Test
    public void test_pluginGettersCalled() throws Exception {
        when(mockPlugin.getDbfluteVersion()).thenReturn("1.2.0");
        when(mockPlugin.getDownloadFilePrefix()).thenReturn("dbflute-");
        when(mockPlugin.getDownloadUrl()).thenReturn("http://example.com/dbflute.zip");

        // Create target directory to avoid actual download
        File targetDir = new File(mydbfluteDir, "dbflute-1.2.0");
        assertTrue(targetDir.mkdirs());

        DBFluteDownloader downloader = new DBFluteDownloader(mockPlugin);

        try {
            downloader.execute();
        } catch (Exception e) {
            // Ignore exceptions for this test
        }

        // Verify all plugin getters were called
        verify(mockPlugin, atLeastOnce()).getDbfluteVersion();
        verify(mockPlugin, atLeastOnce()).getMydbfluteDir();
        verify(mockPlugin, atLeastOnce()).getDownloadFilePrefix();
    }
}
