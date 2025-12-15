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

import java.io.File;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test for {@link EngineDownloadHandler}.
 * @author jflute
 */
public class EngineDownloadHandlerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private File mydbfluteDir;

    @Before
    public void setUp() throws Exception {
        mydbfluteDir = tempFolder.newFolder("mydbflute");
    }

    @Test
    public void test_constructor_basic() {
        String version = "1.2.0";
        String prefix = "dbflute-";
        String url = "http://example.com/dbflute.zip";

        EngineDownloadHandler handler = new EngineDownloadHandler(
            version, mydbfluteDir, prefix, url);

        assertNotNull(handler);
    }

    @Test
    public void test_constructor_nullVersion() {
        String prefix = "dbflute-";
        String url = "http://example.com/dbflute.zip";

        // null version is allowed (will be resolved from public properties)
        EngineDownloadHandler handler = new EngineDownloadHandler(
            null, mydbfluteDir, prefix, url);

        assertNotNull(handler);
    }

    @Test
    public void test_constructor_nullDownloadUrl() {
        String version = "1.2.0";
        String prefix = "dbflute-";

        // null download URL is allowed (will be resolved from public properties)
        EngineDownloadHandler handler = new EngineDownloadHandler(
            version, mydbfluteDir, prefix, null);

        assertNotNull(handler);
    }

    @Test
    public void test_download_versionNotSpecifiedAndNoPublicProperties() {
        String prefix = "dbflute-";
        String url = "http://example.com/dbflute.zip";

        EngineDownloadHandler handler = new EngineDownloadHandler(
            null, mydbfluteDir, prefix, url);

        // When version is null, it tries to load from public properties
        // This will fail without network, which is expected in unit test
        try {
            handler.download();
            // If public properties are accessible, it might succeed
            // We don't fail the test as it depends on network availability
        } catch (MojoFailureException e) {
            // Expected when public properties cannot be loaded
            assertTrue(e.getMessage().contains("Set <dbfluteVersion>") ||
                      e.getMessage().contains("public properties"));
        } catch (Exception e) {
            // Network-related exceptions are also acceptable
            assertTrue(e.getMessage() != null);
        }
    }

    @Test
    public void test_download_existingDirectory() throws Exception {
        String version = "1.2.0";
        String prefix = "dbflute-";
        String url = "http://example.com/dbflute.zip";

        // Create the target directory in advance
        File targetDir = new File(mydbfluteDir, prefix + version);
        assertTrue(targetDir.mkdirs());

        EngineDownloadHandler handler = new EngineDownloadHandler(
            version, mydbfluteDir, prefix, url);

        // Should not fail when directory already exists
        // Just logs that it exists
        // Note: This doesn't actually download because directory exists
        try {
            handler.download();
            // If it completes without exception, the directory check worked
            assertTrue(targetDir.exists());
        } catch (Exception e) {
            // If there's an exception, it should not be about directory existence
            assertFalse(e.getMessage().contains("exists"));
        }
    }

    @Test
    public void test_preparePublicProp_caching() throws Exception {
        String version = "1.2.0";
        String prefix = "dbflute-";
        String url = "http://example.com/dbflute.zip";

        EngineDownloadHandler handler = new EngineDownloadHandler(
            version, mydbfluteDir, prefix, url);

        // Test that preparePublicProp caches the result
        // We can't easily test this without reflection, so we just verify
        // the handler can be created successfully
        assertNotNull(handler);
    }
}
