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

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.dbflute.maven.plugin.client.ClientCreator;
import org.dbflute.maven.plugin.util.LogUtil;

/**
 * CreateClientPlugin provides create-client goal to create dbflute client.
 * 
 * @goal create-client
 * 
 * @author shinsuke
 *
 */
public class CreateClientPlugin extends AbstractMojo {
    /**
     * Project base directory (prepended for relative file paths).
     *
     * @parameter property="basedir"
     * @required
     */
    protected File basedir;

    /**
     * @parameter property="dbflute.version" 
     */
    protected String dbfluteVersion;

    /**
     * @parameter property="dbflute.downloadFilePrefix" default-value="dbflute-"
     */
    protected String downloadFilePrefix;

    /**
     * @parameter property="dbflute.mydbfluteDir" default-value="${basedir}/mydbflute"
     */
    protected File mydbfluteDir;

    /**
     * @parameter property="dbflute.dbfluteClientDir" 
     */
    private File dbfluteClientDir;

    /**
     * @parameter property="dbflute.clientProject"
     */
    protected String clientProject;

    /**
     * @parameter property="dbflute.enablePause" default-value="false"
     */
    protected String enablePause;

    /**
     * @parameter property="dbflute.database" default-value="h2"
     */
    protected String database;

    /**
     * @parameter property="dbflute.targetLanguage" default-value="java"
     */
    protected String targetLanguage;

    /**
     * @parameter property="dbflute.targetContainer" default-value="spring"
     */
    protected String targetContainer;

    /**
     * @parameter property="dbflute.packageBase" default-value="${rootPackage}"
     */
    protected String packageBase;

    /**
     * @parameter property="dbflute.databaseDriver" default-value="org.h2.Driver"
     */
    protected String databaseDriver;

    // default-value="jdbc:h2:file:../src/main/webapp/WEB-INF/db/..."
    /**
     * @parameter property="dbflute.databaseUrl"
     */
    protected String databaseUrl;

    /**
     * @parameter property="dbflute.databaseSchema" default-value=" "
     */
    protected String databaseSchema;

    /**
     * @parameter property="dbflute.databaseUser" default-value="sa"
     */
    protected String databaseUser;

    /**
     * @parameter property="dbflute.databasePassword" default-value=" "
     */
    protected String databasePassword;

    private String dbfluteName;

    public void execute() throws MojoExecutionException, MojoFailureException {
        LogUtil.init(getLog());

        if (StringUtils.isBlank(dbfluteVersion)) {
            throw new MojoFailureException("Missing dbfluteVersion property.");
        }

        dbfluteName = downloadFilePrefix + dbfluteVersion;

        ClientCreator creator = new ClientCreator(this);
        creator.execute();
    }

    public File getDbfluteDir() {
        return new File(mydbfluteDir, dbfluteName);
    }

    public File getDbfluteClientDir() {
        return dbfluteClientDir;
    }

    public String getClientProject() {
        return clientProject;
    }

    public String getEnablePause() {
        return enablePause;
    }

    public String getDbfluteName() {
        return dbfluteName;
    }

    public String getDatabase() {
        return database;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public String getTargetContainer() {
        return targetContainer;
    }

    public String getPackageBase() {
        return packageBase;
    }

    public String getDatabaseDriver() {
        return databaseDriver;
    }

    public String getDatabaseUrl() {
        if (databaseUrl == null) {
            databaseUrl = "jdbc:h2:file:../src/main/webapp/WEB-INF/db/"
                    + clientProject;
        }
        return databaseUrl;
    }

    public String getDatabaseSchema() {
        return databaseSchema;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public File getBasedir() {
        return basedir;
    }
}
