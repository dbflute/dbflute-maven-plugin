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
package org.dbflute.maven.plugin.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.dbflute.maven.plugin.CommandPlugin;
import org.dbflute.maven.plugin.util.LogUtil;
import org.dbflute.maven.plugin.util.SystemUtil;

/**
 * CommandExecutor executes dbflute's command.
 * 
 * @author shinsuke
 *
 */
public class CommandExecutor {
    protected CommandPlugin plugin;

    public Map<String, String> environment = new HashMap<String, String>();

    public CommandExecutor(CommandPlugin context) {
        this.plugin = context;
    }

    public void execute(String cmd) throws MojoExecutionException,
            MojoFailureException {
        File dbfluteClientDir = plugin.getDbfluteClientDir();
        if (!dbfluteClientDir.isDirectory()) {
            LogUtil.getLog().info(
                    "Create dbflute client directory. "
                            + "Try to run \'mvn dbflute:create-client\'.");
            return;
        }

        List<String> cmds = new ArrayList<String>();
        if (SystemUtil.isWindows()) {
            cmds.add("cmd.exe");
            cmds.add("/c");
            cmds.add(cmd + ".bat");
            environment.put("pause_at_end", "n");
        } else {
            cmds.add("/bin/bash");
            cmds.add(cmd + ".sh");
        }
        // TODO Mac?

        plugin.updateArgs(cmds);

        LogUtil.getLog().info(
                "Running " + StringUtils.join(cmds.toArray(), " "));
        ProcessBuilder builder = new ProcessBuilder(cmds);
        if (environment.size() > 0) {
            builder.environment().putAll(environment);
        }
        Process process;
        try {
            process = builder.directory(dbfluteClientDir)
                    .redirectErrorStream(true).start();
        } catch (IOException e) {
            throw new MojoExecutionException("Could not run the command.", e);
        }

        try (InputStream stdin = process.getInputStream();
                OutputStream stdout = process.getOutputStream()) {
            InputStreamThread ist = new InputStreamThread(stdin);
            OutputStreamThread ost = new OutputStreamThread(System.in, stdout);
            ist.start();
            ost.start();

            int exitValue = process.waitFor();

            ist.join();
            //ost.join();

            if (exitValue != 0) {
                throw new MojoFailureException(
                        "Build Failed. The exit value is " + exitValue + ".");
            }
        } catch (InterruptedException e) {
            throw new MojoExecutionException("Could not wait a process.", e);
        } catch (IOException e) {
            throw new MojoExecutionException("I/O error.", e);
        }
    }

    private static class InputStreamThread extends Thread {

        private Reader reader;

        public InputStreamThread(InputStream in) {
            reader = new InputStreamReader(in);
        }

        @Override
        public void run() {
            try {
                int ch;
                while ((ch = reader.read()) != -1) {
                    System.out.print((char) ch);
                }
            } catch (Exception e) {
                LogUtil.getLog().debug(e);
            }
        }

    }

    private static class OutputStreamThread extends Thread {

        private BufferedReader br;

        private Writer writer;

        public OutputStreamThread(InputStream in, OutputStream out) {
            br = new BufferedReader(new InputStreamReader(in));
            writer = new OutputStreamWriter(out);
        }

        @Override
        public void run() {
            char[] cs = new char[1];
            cs[0] = '\n';
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    try {
                        writer.write(line);
                        writer.write(cs);
                        writer.flush();
                    } catch (Exception e) {
                        LogUtil.getLog().info(
                                "Could not send bytes to the bat file.", e);
                    }
                }
            } catch (Exception e) {
                LogUtil.getLog().debug(e);
            }
        }

    }
}
