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
package org.dbflute.maven.plugin.upgrade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.dbflute.maven.plugin.UpgradePlugin;
import org.dbflute.maven.plugin.util.LogUtil;
import org.dbflute.maven.plugin.util.ResourceUtil;

/**
 * DBFluteUpgrader downloads dbflute-*.zip, extracts it and replaces _project.*.
 * 
 * @author shinsuke
 * @author jflute
 */
public class DBFluteUpgrader {

    protected final UpgradePlugin plugin;

    public DBFluteUpgrader(UpgradePlugin context) {
        this.plugin = context;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        File dbfluteDir = plugin.getDbfluteDir();
        File dbfluteClientDir = plugin.getDbfluteClientDir();
        if (dbfluteClientDir == null) {
            throw new MojoFailureException("Missing dbfluteClientDir.");
        }
        if (!dbfluteClientDir.isDirectory()) {
            throw new MojoFailureException(dbfluteClientDir.getAbsolutePath() + " does not exist.");
        }

        // Check clientProject
        if (StringUtils.isBlank(plugin.getClientProject())) {
            throw new MojoFailureException("Missing clientProject.");
        }

        if (!dbfluteDir.exists()) {
            LogUtil.getLog().info("Creating " + dbfluteDir.getAbsolutePath());
            try (InputStream in = plugin.getDownloadInputStream()) {
                ResourceUtil.unzip(in, dbfluteDir);
            } catch (IOException e) {
                throw new MojoExecutionException("I/O error.", e);
            }
        }

        LogUtil.getLog().info("Creating " + dbfluteClientDir.getAbsolutePath());
        File clientZipFile = new File(dbfluteDir, "etc/client-template/dbflute_dfclient.zip");
        if (!clientZipFile.exists()) {
            throw new MojoFailureException(clientZipFile.getAbsolutePath() + " does not exist.");
        }

        // create temp dir
        File tempDir = ResourceUtil.createTempDir("dbflute-client", "");

        try {
            ResourceUtil.unzip(new FileInputStream(clientZipFile), tempDir);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(clientZipFile.getAbsolutePath() + " is not found.", e);
        }

        // copy _project.*
        File srcFile;
        File destFile;
        try {
            srcFile = new File(tempDir, "dbflute_dfclient" + File.separator + "_project.sh");
            destFile = new File(dbfluteClientDir, "_project.sh");
            LogUtil.getLog().info("Replacing " + destFile.getAbsolutePath());
            FileUtils.copyFile(srcFile, destFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not replace _project.sh.", e);
        }
        try {
            srcFile = new File(tempDir, "dbflute_dfclient" + File.separator + "_project.bat");
            destFile = new File(dbfluteClientDir, "_project.bat");
            LogUtil.getLog().info("Replacing " + destFile.getAbsolutePath());
            FileUtils.copyFile(srcFile, destFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not replace _project.bat.", e);
        }

        // _project.sh
        Map<String, String> params = new HashMap<String, String>();
        putParam(params, "export MY_PROJECT_NAME=[^\r\n]+", "export MY_PROJECT_NAME=", plugin.getClientProject());
        putParam(params, "export DBFLUTE_HOME=../mydbflute/[^\r\n]+", "export DBFLUTE_HOME=../mydbflute/", plugin.getDbfluteName());
        ResourceUtil.replaceContent(new File(plugin.getDbfluteClientDir(), "_project.sh"), params);

        // _project.bat
        params.clear();
        putParam(params, "set MY_PROJECT_NAME=[^\r\n]+", "set MY_PROJECT_NAME=", plugin.getClientProject());
        putParam(params, "set DBFLUTE_HOME=..\\\\mydbflute\\\\[^\r\n]+", "set DBFLUTE_HOME=..\\\\mydbflute\\\\", plugin.getDbfluteName());
        ResourceUtil.replaceContent(new File(plugin.getDbfluteClientDir(), "_project.bat"), params);
    }

    protected void putParam(Map<String, String> params, String key, String prefix, String value) {
        if (value != null) {
            params.put(key, prefix + value);
        }
    }
}
