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

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.dbflute.maven.plugin.command.CommandExecutor;
import org.dbflute.maven.plugin.util.LogUtil;

/**
 * SchemaSyncCheckCommandPlugin provides schema-sync-check goal to run manage.[sh|bat] schema-sync-check.
 * 
 * @goal schema-sync-check
 * 
 * @author shinsuke
 *
 */
public class SchemaSyncCheckCommandPlugin extends CommandPlugin {

    public void execute() throws MojoExecutionException, MojoFailureException {
        LogUtil.init(getLog());

        CommandExecutor creator = new CommandExecutor(this);
        creator.execute("manage");
    }

    /**
     * @param cmds arguments for a command line
     */
    @Override
    public void updateArgs(List<String> cmds) {
        cmds.add("schema-sync-check");
    }
}
