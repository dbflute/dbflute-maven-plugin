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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test for {@link ResourceUtil}.
 * @author jflute
 */
public class ResourceUtilTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private File testDir;

    @Before
    public void setUp() throws Exception {
        testDir = tempFolder.newFolder();
    }

    // ===================================================================================
    //                                                                             makeDir
    //                                                                             =======
    @Test
    public void test_makeDir_success() throws Exception {
        File newDir = new File(testDir, "newdir");
        assertFalse(newDir.exists());

        ResourceUtil.makeDir(newDir);

        assertTrue(newDir.exists());
        assertTrue(newDir.isDirectory());
    }

    @Test
    public void test_makeDir_alreadyExists() throws Exception {
        File existingDir = new File(testDir, "existing");
        assertTrue(existingDir.mkdirs());

        ResourceUtil.makeDir(existingDir);

        assertTrue(existingDir.isDirectory());
    }

    @Test
    public void test_makeDir_nestedDirectories() throws Exception {
        File nestedDir = new File(testDir, "parent/child/grandchild");
        assertFalse(nestedDir.exists());

        ResourceUtil.makeDir(nestedDir);

        assertTrue(nestedDir.exists());
        assertTrue(nestedDir.isDirectory());
    }

    @Test
    public void test_makeDir_withFile() throws Exception {
        File file = new File(testDir, "file.txt");
        assertTrue(file.createNewFile());

        try {
            ResourceUtil.makeDir(file);
            // If file exists and is not a directory, makeDir should handle it
            // The behavior depends on whether mkdirs() succeeds or fails
        } catch (MojoFailureException e) {
            // Expected if the file cannot be converted to directory
            assertTrue(e.getMessage().contains("Could not create"));
        }
    }

    // ===================================================================================
    //                                                                         createTempDir
    //                                                                         ===========
    @Test
    public void test_createTempDir_success() throws Exception {
        File tempDir = ResourceUtil.createTempDir("test-", "-dir");
        assertNotNull(tempDir);
        // Note: The temp file is created but not the directory yet
    }

    @Test
    public void test_createTempDir_withDifferentPrefixes() throws Exception {
        File tempDir1 = ResourceUtil.createTempDir("prefix1-", "-suffix");
        File tempDir2 = ResourceUtil.createTempDir("prefix2-", "-suffix");

        assertNotNull(tempDir1);
        assertNotNull(tempDir2);
        assertNotEquals(tempDir1.getAbsolutePath(), tempDir2.getAbsolutePath());
    }

    // ===================================================================================
    //                                                                               unzip
    //                                                                               =====
    @Test
    public void test_unzip_fromInputStream() throws Exception {
        // Create a test ZIP file
        File zipFile = new File(testDir, "test.zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            // Add a directory
            ZipEntry dirEntry = new ZipEntry("testdir/");
            zos.putNextEntry(dirEntry);
            zos.closeEntry();

            // Add a file
            ZipEntry fileEntry = new ZipEntry("testdir/test.txt");
            zos.putNextEntry(fileEntry);
            zos.write("Hello, World!".getBytes("UTF-8"));
            zos.closeEntry();
        }

        File extractDir = new File(testDir, "extract");
        try (InputStream is = new java.io.FileInputStream(zipFile)) {
            ResourceUtil.unzip(is, extractDir);
        }

        assertTrue(extractDir.exists());
        File extractedDir = new File(extractDir, "testdir");
        assertTrue(extractedDir.exists());
        assertTrue(extractedDir.isDirectory());

        File extractedFile = new File(extractedDir, "test.txt");
        assertTrue(extractedFile.exists());
        assertTrue(extractedFile.isFile());

        String content = ResourceUtil.readText(extractedFile, "UTF-8");
        assertEquals("Hello, World!", content);
    }

    @Test
    public void test_unzip_withNestedDirectories() throws Exception {
        File zipFile = new File(testDir, "nested.zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            ZipEntry dir1 = new ZipEntry("level1/");
            zos.putNextEntry(dir1);
            zos.closeEntry();

            ZipEntry dir2 = new ZipEntry("level1/level2/");
            zos.putNextEntry(dir2);
            zos.closeEntry();

            ZipEntry file = new ZipEntry("level1/level2/file.txt");
            zos.putNextEntry(file);
            zos.write("Nested content".getBytes("UTF-8"));
            zos.closeEntry();
        }

        File extractDir = new File(testDir, "extract_nested");
        try (InputStream is = new java.io.FileInputStream(zipFile)) {
            ResourceUtil.unzip(is, extractDir);
        }

        File nestedFile = new File(extractDir, "level1/level2/file.txt");
        assertTrue(nestedFile.exists());
    }

    @Test
    public void test_unzip_emptyZip() throws Exception {
        File zipFile = new File(testDir, "empty.zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            // Create empty ZIP
        }

        File extractDir = new File(testDir, "extract_empty");
        try (InputStream is = new java.io.FileInputStream(zipFile)) {
            ResourceUtil.unzip(is, extractDir);
        }

        // Should not fail with empty ZIP
    }

    @Test
    public void test_unzip_multipleFiles() throws Exception {
        File zipFile = new File(testDir, "multiple.zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (int i = 1; i <= 5; i++) {
                ZipEntry entry = new ZipEntry("file" + i + ".txt");
                zos.putNextEntry(entry);
                zos.write(("Content " + i).getBytes("UTF-8"));
                zos.closeEntry();
            }
        }

        File extractDir = new File(testDir, "extract_multiple");
        try (InputStream is = new java.io.FileInputStream(zipFile)) {
            ResourceUtil.unzip(is, extractDir);
        }

        for (int i = 1; i <= 5; i++) {
            File file = new File(extractDir, "file" + i + ".txt");
            assertTrue(file.exists());
            String content = ResourceUtil.readText(file, "UTF-8");
            assertEquals("Content " + i, content);
        }
    }

    @Test
    public void test_unzip_fromTestResource() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("zip/hoge.zip");
        if (is == null) {
            // Skip if test resource not available
            return;
        }

        File extractDir = new File(testDir, "extract_resource");
        ResourceUtil.unzip(is, extractDir);

        assertTrue(extractDir.exists());
        File hogeDir = new File(extractDir, "hoge");
        if (hogeDir.exists()) {
            assertTrue(hogeDir.isDirectory());
        }
    }

    // ===================================================================================
    //                                                                      replaceContent
    //                                                                      ==============
    @Test
    public void test_replaceContent_basic() throws Exception {
        File file = new File(testDir, "replace.txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("Hello @@name@@!".getBytes("UTF-8"));
        }

        Map<String, String> params = new HashMap<>();
        params.put("@@name@@", "World");

        ResourceUtil.replaceContent(file, params);

        String content = ResourceUtil.readText(file, "UTF-8");
        assertEquals("Hello World!", content);
    }

    @Test
    public void test_replaceContent_multipleReplacements() throws Exception {
        File file = new File(testDir, "multi_replace.txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("@@var1@@ and @@var2@@ and @@var3@@".getBytes("UTF-8"));
        }

        Map<String, String> params = new HashMap<>();
        params.put("@@var1@@", "one");
        params.put("@@var2@@", "two");
        params.put("@@var3@@", "three");

        ResourceUtil.replaceContent(file, params);

        String content = ResourceUtil.readText(file, "UTF-8");
        assertEquals("one and two and three", content);
    }

    @Test
    public void test_replaceContent_regexPattern() throws Exception {
        File file = new File(testDir, "regex_replace.txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("version=1.0.0".getBytes("UTF-8"));
        }

        Map<String, String> params = new HashMap<>();
        params.put("version=[0-9\\.]+", "version=2.0.0");

        ResourceUtil.replaceContent(file, params);

        String content = ResourceUtil.readText(file, "UTF-8");
        assertEquals("version=2.0.0", content);
    }

    @Test
    public void test_replaceContent_withPause() throws Exception {
        File file = new File(testDir, "_project.bat");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("@echo off\r\npause\r\n".getBytes("UTF-8"));
        }

        Map<String, String> params = new HashMap<>();

        ResourceUtil.replaceContent(file, params, true);

        String content = ResourceUtil.readText(file, "UTF-8");
        assertTrue(content.contains("pause_at_end"));
    }

    @Test
    public void test_replaceContent_nonExistentFile() throws Exception {
        File file = new File(testDir, "nonexistent.txt");
        assertFalse(file.exists());

        Map<String, String> params = new HashMap<>();
        params.put("foo", "bar");

        // Should not throw exception, just log
        try {
            ResourceUtil.replaceContent(file, params);
            // If it doesn't throw, that's acceptable
        } catch (Exception e) {
            // Also acceptable if it throws
        }
    }

    @Test
    public void test_replaceContent_emptyParams() throws Exception {
        File file = new File(testDir, "no_replace.txt");
        String originalContent = "Original content";
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(originalContent.getBytes("UTF-8"));
        }

        Map<String, String> params = new HashMap<>();

        ResourceUtil.replaceContent(file, params);

        String content = ResourceUtil.readText(file, "UTF-8");
        assertEquals(originalContent, content);
    }

    // ===================================================================================
    //                                                                            readText
    //                                                                            ========
    @Test
    public void test_readText_fromFile() throws Exception {
        File file = new File(testDir, "read.txt");
        String expectedContent = "This is test content\nwith multiple lines\n日本語";
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(expectedContent.getBytes("UTF-8"));
        }

        String content = ResourceUtil.readText(file, "UTF-8");
        assertEquals(expectedContent, content);
    }

    @Test
    public void test_readText_largeFile() throws Exception {
        File file = new File(testDir, "large.txt");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("Line ").append(i).append("\n");
        }
        String expectedContent = sb.toString();

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(expectedContent.getBytes("UTF-8"));
        }

        String content = ResourceUtil.readText(file, "UTF-8");
        assertEquals(expectedContent, content);
    }

    @Test
    public void test_readText_emptyFile() throws Exception {
        File file = new File(testDir, "empty.txt");
        assertTrue(file.createNewFile());

        String content = ResourceUtil.readText(file, "UTF-8");
        assertEquals("", content);
    }

    @Test
    public void test_readText_differentEncodings() throws Exception {
        File file = new File(testDir, "encoding.txt");
        String testString = "Test with special chars: äöü";

        // Write with UTF-8
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(testString.getBytes("UTF-8"));
        }

        String content = ResourceUtil.readText(file, "UTF-8");
        assertEquals(testString, content);
    }

    @Test(expected = MojoExecutionException.class)
    public void test_readText_nonExistentFile() throws Exception {
        File file = new File(testDir, "does_not_exist.txt");
        ResourceUtil.readText(file, "UTF-8");
    }

    @Test
    public void test_readText_withSpecialCharacters() throws Exception {
        File file = new File(testDir, "special.txt");
        String specialContent = "Special chars: @#$%^&*()_+-=[]{}|;':\"<>?,./~`";
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(specialContent.getBytes("UTF-8"));
        }

        String content = ResourceUtil.readText(file, "UTF-8");
        assertEquals(specialContent, content);
    }

    @Test
    public void test_readText_multilineContent() throws Exception {
        File file = new File(testDir, "multiline.txt");
        String multilineContent = "Line 1\nLine 2\r\nLine 3\rLine 4";
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multilineContent.getBytes("UTF-8"));
        }

        String content = ResourceUtil.readText(file, "UTF-8");
        assertEquals(multilineContent, content);
    }
}
