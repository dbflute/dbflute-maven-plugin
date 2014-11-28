/*
 * Copyright 2014-2014 The DBFlute Project and the Others.
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

import java.io.File;
import java.io.InputStream;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class ResourceFileUtilTest extends AbstractMojoTestCase {

    public void test_makeDir() throws Exception {
        File tempDir = File.createTempFile("mdp-", "");
        assertTrue(tempDir.delete());
        ResourceFileUtil.makeDir(tempDir);
        assertTrue(tempDir.isDirectory());
    }

    public void test_makeDir_exist() throws Exception {
        File tempDir = File.createTempFile("mdp-", "");
        assertTrue(tempDir.delete());
        tempDir.mkdirs();
        ResourceFileUtil.makeDir(tempDir);
        assertTrue(tempDir.isDirectory());
    }

    public void test_unzip() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("zip/hoge.zip");
        File tempDir = File.createTempFile("mdp-", "");
        assertTrue(tempDir.delete());
        ResourceFileUtil.unzip(is, tempDir);
        assertTrue(tempDir.isDirectory());
        File hogeDir = new File(tempDir, "hoge");
        assertTrue(hogeDir.isDirectory());
        File fooFile = new File(hogeDir, "foo.txt");
        assertTrue(fooFile.isFile());
    }
}
