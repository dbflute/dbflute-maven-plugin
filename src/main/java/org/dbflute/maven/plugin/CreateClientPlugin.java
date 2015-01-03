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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
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
     * The current Maven project.
     *
     * @parameter property="project"
     * @required
     * @readonly
     */
    protected MavenProject project;

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
     * @parameter property="dbflute.database"
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
     * @parameter property="dbflute.packageBase"
     */
    protected String packageBase;

    /**
     * @parameter property="dbflute.databaseDriver"
     */
    protected String databaseDriver;

    /**
     * @parameter property="dbflute.databaseUrl"
     */
    protected String databaseUrl;

    /**
     * @parameter property="dbflute.databaseSchema"
     */
    protected String databaseSchema;

    /**
     * @parameter property="dbflute.databaseUser"
     */
    protected String databaseUser;

    /**
     * @parameter property="dbflute.databasePassword"
     */
    protected String databasePassword;

    private String dbfluteName;

    public void execute() throws MojoExecutionException, MojoFailureException {
        LogUtil.init(getLog());

        if (StringUtils.isBlank(dbfluteVersion)) {
            throw new MojoFailureException("Missing dbfluteVersion property.");
        }

        dbfluteName = downloadFilePrefix + dbfluteVersion;

        initDatabase();
        ClientCreator creator = new ClientCreator(this);
        creator.execute();
    }

    public void initDatabase() throws MojoFailureException {
        DatabaseType dbType = DatabaseType.UNKNOWN;
        if (database == null) {
            List<Dependency> dependencies = project.getModel().getDependencies();
            for (Dependency dependency : dependencies) {
                // mysql
                if ("mysql".equals(dependency.getGroupId()) && "mysql-connector-java".equals(dependency.getArtifactId())) {
                    dbType = DatabaseType.MYSQL;
                    break;
                }
                // postgresql
                if (("org.postgresql".equals(dependency.getGroupId()) || "postgresql".equals(dependency.getGroupId()))
                        && "postgresql".equals(dependency.getArtifactId())) {
                    dbType = DatabaseType.POSTGRESQL;
                    break;
                }
            }

            switch (dbType) {
            case MYSQL:
                database = "mysql";
                break;
            case POSTGRESQL:
                database = "postgresql";
                break;
            default:
                database = "h2";
                dbType = DatabaseType.H2;
                break;
            }
        } else if ("h2".equals(database)) {
            dbType = DatabaseType.H2;
        } else if ("mysql".equals(database)) {
            dbType = DatabaseType.MYSQL;
        } else if ("postgresql".equals(database)) {
            dbType = DatabaseType.POSTGRESQL;
        }

        if (databaseDriver == null) {
            switch (dbType) {
            case H2:
                databaseDriver = "org.h2.Driver";
                break;
            case MYSQL:
                databaseDriver = "com.mysql.jdbc.Driver";
                break;
            case POSTGRESQL:
                databaseDriver = "org.postgresql.Driver";
                break;
            default:
                throw new MojoFailureException("Missing databaseDriver property.");
            }
        }

        if (databaseUrl == null) {
            switch (dbType) {
            case H2:
                databaseUrl = "jdbc:h2:file:../src/main/resources/" + clientProject;
                break;
            case MYSQL:
                databaseUrl = "jdbc:mysql://localhost:3306/" + clientProject + "?characterEncoding=UTF-8";
                break;
            case POSTGRESQL:
                databaseUrl = "jdbc:postgresql://localhost:5432/" + clientProject;
                break;
            default:
                throw new MojoFailureException("Missing databaseUrl property.");
            }
        }

        if (databaseSchema == null) {
            switch (dbType) {
            case H2:
                databaseSchema = " ";
                break;
            case MYSQL:
                databaseSchema = " ";
                break;
            case POSTGRESQL:
                databaseSchema = "public";
                break;
            default:
                throw new MojoFailureException("Missing databaseSchema property.");
            }
        }

        if (databaseUser == null) {
            switch (dbType) {
            case H2:
                databaseUser = "sa";
                break;
            case MYSQL:
                databaseUser = clientProject;
                break;
            case POSTGRESQL:
                databaseUser = clientProject;
                break;
            default:
                throw new MojoFailureException("Missing databaseUser property.");
            }
        }

        if (databasePassword == null) {
            switch (dbType) {
            case H2:
                databasePassword = " ";
                break;
            case MYSQL:
                databasePassword = clientProject;
                break;
            case POSTGRESQL:
                databasePassword = clientProject;
                break;
            default:
                throw new MojoFailureException("Missing databasePassword property.");
            }
        }
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

    private enum DatabaseType {
        MYSQL, POSTGRESQL, H2, UNKNOWN;
    }
}
