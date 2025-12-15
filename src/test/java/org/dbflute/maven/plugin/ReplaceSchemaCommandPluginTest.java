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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link ReplaceSchemaCommandPlugin}.
 * @author jflute
 */
public class ReplaceSchemaCommandPluginTest {

    private ReplaceSchemaCommandPlugin plugin;

    @Before
    public void setUp() {
        plugin = new ReplaceSchemaCommandPlugin();
    }

    @Test
    public void test_pluginInheritance() {
        assertTrue(plugin instanceof CommandPlugin);
    }

    @Test
    public void test_updateArgs() {
        List<String> cmds = new ArrayList<>();
        plugin.updateArgs(cmds);

        // ReplaceSchemaCommandPlugin doesn't add any args
        assertTrue(cmds.isEmpty());
    }

    @Test
    public void test_notNull() {
        assertNotNull(plugin);
    }
}
