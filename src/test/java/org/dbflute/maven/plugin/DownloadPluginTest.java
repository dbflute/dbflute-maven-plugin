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
package org.dbflute.maven.plugin;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.dbflute.maven.plugin.DownloadPlugin;

public class DownloadPluginTest extends AbstractMojoTestCase {
    public void test_parameter() throws Exception {
        File testPom = new File(getBasedir(), "src/test/resources/poms/pom-download.xml");
        DownloadPlugin mojo = (DownloadPlugin) lookupMojo("download",
                testPom);
        String version = (String) getVariableValueFromObject(mojo,
                "dbfluteVersion");
        assertEquals("0.9.9.9", version);

    }
}
