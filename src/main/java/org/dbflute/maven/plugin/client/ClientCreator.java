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
package org.dbflute.maven.plugin.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.dbflute.maven.plugin.CreateClientPlugin;
import org.dbflute.maven.plugin.util.LogUtil;
import org.dbflute.maven.plugin.util.ResourceFileUtil;

/**
 * ClientCreator create dbflute client directory.
 * 
 * @author shinsuke
 *
 */
public class ClientCreator {
    protected CreateClientPlugin plugin;

    public ClientCreator(CreateClientPlugin context) {
        this.plugin = context;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        File dbfluteDir = plugin.getDbfluteDir();
        File dbfluteClientDir = plugin.getDbfluteClientDir();
        if (dbfluteClientDir == null) {
            String clientProject = plugin.getClientProject();
            if (StringUtils.isBlank(clientProject)) {
                clientProject = "client";
            }
            dbfluteClientDir = new File(plugin.getBasedir(), "dbflute_"
                    + clientProject);
        }
        if (dbfluteClientDir.isDirectory()) {
            LogUtil.getLog().info(
                    dbfluteClientDir.getAbsolutePath() + " already exists.");
            return;
        }

        LogUtil.getLog().info("Creating " + dbfluteClientDir.getAbsolutePath());
        File clientZipFile = new File(dbfluteDir,
                "etc/client-template/dbflute_dfclient.zip");
        LogUtil.getLog().info("Unzip " + clientZipFile.getAbsolutePath());
        if (!clientZipFile.exists()) {
            throw new MojoFailureException(clientZipFile.getAbsolutePath()
                    + " does not exist.");
        }

        // create temp dir
        File tempDir = ResourceFileUtil.createTempDir("dbflute-client", "");

        try {
            ResourceFileUtil.unzip(new FileInputStream(clientZipFile), tempDir);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(clientZipFile.getAbsolutePath()
                    + " is not found.", e);
        }

        try {
            LogUtil.getLog().info(
                    "Creating " + dbfluteClientDir.getAbsolutePath());
            FileUtils.copyDirectory(new File(tempDir, "dbflute_dfclient"),
                    dbfluteClientDir);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not create "
                    + dbfluteClientDir.getAbsolutePath(), e);
        }

        // Check clientProject
        if (StringUtils.isBlank(plugin.getClientProject())) {
            throw new MojoFailureException("Missing clientProject.");
        }

        Map<String, String> params = new HashMap<String, String>();
        if ("false".equalsIgnoreCase(plugin.getEnablePause())) {
            for (File batFile : dbfluteClientDir
                    .listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.endsWith(".bat");
                        }
                    })) {
                ResourceFileUtil.replaceContent(batFile, params, true);
            }
        }

        // _project.sh
        params.clear();
        putParam(params, "export MY_PROJECT_NAME=[^\r\n]+",
                "export MY_PROJECT_NAME=", plugin.getClientProject());
        putParam(params, "export DBFLUTE_HOME=../mydbflute/[^\r\n]+",
                "export DBFLUTE_HOME=../mydbflute/", plugin.getDbfluteName());
        ResourceFileUtil.replaceContent(new File(dbfluteClientDir,
                "_project.sh"), params);

        // _project.bat
        params.clear();
        putParam(params, "set MY_PROJECT_NAME=[^\r\n]+",
                "set MY_PROJECT_NAME=", plugin.getClientProject());
        putParam(params, "set DBFLUTE_HOME=..\\\\mydbflute\\\\[^\r\n]+",
                "set DBFLUTE_HOME=..\\\\mydbflute\\\\", plugin.getDbfluteName());
        ResourceFileUtil.replaceContent(new File(dbfluteClientDir,
                "_project.bat"), params);

        // build-dfclient.properties
        params.clear();
        putParam(params, "torque.project *= *[^\r\n]+", "torque.project = ",
                plugin.getClientProject());
        // 0.8.x
        putParam(params, "torque.database *= *[^\r\n]+", "torque.database = ",
                plugin.getDatabase());
        putParam(params, "torque.packageBase *= *[^\r\n]+",
                "torque.packageBase = ", plugin.getDatabase());
        File propertyFile = new File(dbfluteClientDir,
                "build-dfclient.properties");
        if (!propertyFile.exists()) {
            // from 0.9.5.5            
            propertyFile = new File(dbfluteClientDir, "build.properties");
            ResourceFileUtil.replaceContent(propertyFile, params);
            propertyFile
                    .renameTo(new File(dbfluteClientDir, "build.properties"));
        } else {
            ResourceFileUtil.replaceContent(propertyFile, params);
            propertyFile.renameTo(new File(dbfluteClientDir, "build-"
                    + plugin.getClientProject() + ".properties"));
        }

        // dfprop/basicInfoMap.dfprop
        params.clear();
        putParam(params, "@database@", "", plugin.getDatabase());
        putParam(params, "@targetLanguage@", "", plugin.getTargetLanguage());
        putParam(params, "@targetContainer@", "", plugin.getTargetContainer());
        putParam(params, "@packageBase@", "", plugin.getPackageBase());
        ResourceFileUtil.replaceContent(new File(dbfluteClientDir,
                "dfprop/basicInfoMap.dfprop"), params);

        // dfprop/databaseInfoMap.dfprop
        params.clear();
        putParam(params, "@driver@", "", plugin.getDatabaseDriver());
        putParam(params, "@url@", "", plugin.getDatabaseUrl());
        putParam(params, "@schema@", "", plugin.getDatabaseSchema());
        putParam(params, "@user@", "", plugin.getDatabaseUser());
        putParam(params, "@password@", "", plugin.getDatabasePassword());
        ResourceFileUtil.replaceContent(new File(dbfluteClientDir,
                "dfprop/databaseInfoMap.dfprop"), params);

    }

    protected void putParam(Map<String, String> params, String key,
            String prefix, String value) {
        if (value != null) {
            params.put(key, prefix + value);
        }
    }
}
