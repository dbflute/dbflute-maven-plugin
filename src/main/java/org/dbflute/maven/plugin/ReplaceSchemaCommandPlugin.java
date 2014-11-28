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

import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.dbflute.maven.plugin.command.CommandExecutor;
import org.dbflute.maven.plugin.util.LogUtil;

/**
 * ReplaceSchemaCommandPlugin provides replace-schema goal to run replace-schema.[sh|bat].
 * 
 * @goal replace-schema
 * 
 * @author shinsuke
 *
 */
public class ReplaceSchemaCommandPlugin extends CommandPlugin {

    /**
     * @parameter property="dbflute.forceExecution"
     */
    protected String forceExecution;

    public void execute() throws MojoExecutionException, MojoFailureException {
        LogUtil.init(getLog());
        int input;
        if ("true".equalsIgnoreCase(forceExecution)
                || "y".equalsIgnoreCase(forceExecution)
                || "yes".equalsIgnoreCase(forceExecution)) {
            input = 'y';
        } else if ("false".equalsIgnoreCase(forceExecution)
                || "n".equalsIgnoreCase(forceExecution)
                || "no".equalsIgnoreCase(forceExecution)) {
            input = 'n';
        } else {
            System.out
                    .println("Database will be initialized. Are you ready? (y or n)");
            try {
                input = System.in.read();
            } catch (IOException e) {
                throw new MojoExecutionException("I/O error.", e);
            }
        }

        if (input == 'y' || input == 'Y') {
            CommandExecutor creator = new CommandExecutor(this);
            creator.environment.put("answer", "y");
            creator.execute("manage");
        }
    }

    /**
     * @param cmds arguments for a command line
     */
    @Override
    public void updateArgs(List<String> cmds) {
        cmds.add("replace-schema");
    }
}
