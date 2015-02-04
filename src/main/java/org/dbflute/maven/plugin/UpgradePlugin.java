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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.dbflute.maven.plugin.officialcopy.DfPublicProperties;
import org.dbflute.maven.plugin.upgrade.DBFluteUpgrader;
import org.dbflute.maven.plugin.util.LogUtil;

/**
 * UpgradePlugin provides upgrade goal to download a zip file of dbflute and replace _project.*.
 * 
 * @goal upgrade
 * 
 * @author shinsuke
 * @author jflute
 */
public class UpgradePlugin extends AbstractMojo {

    /**
     * @parameter property="dbflute.version" 
     */
    protected String dbfluteVersion;

    /**
     * @parameter property="dbflute.downloadFilePrefix" default-value="dbflute-"
     */
    protected String downloadFilePrefix;

    /**
     * @parameter property="dbflute.downloadDirUrl" default-value="http://dbflute.org/download/dbflute/"
     */
    protected String downloadDirUrl;

    /**
     * @parameter property="dbflute.downloadFileExtension" default-value=".zip"
     */
    protected String downloadFileExtension;

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

    /** name of DBFlute containing its version same as DBFlute directory name under 'mydbflute'. (NullAllowed: until execution) */
    private String dbfluteName;

    /** path of download for DBFlute engine. (NullAllowed: until execution) */
    private String downloadPath;

    /** public properties that contains version info, and DBFlute provides officially (NullAllowed: lazy-loaded) */
    private DfPublicProperties publicProperties;

    public void execute() throws MojoExecutionException, MojoFailureException {
        LogUtil.init(getLog());

        initDBFluteVersionIfPossible();
        if (StringUtils.isBlank(dbfluteVersion)) {
            throw new MojoFailureException("Missing dbfluteVersion property.");
        }

        dbfluteName = downloadFilePrefix + dbfluteVersion;
        downloadPath = downloadDirUrl + dbfluteName + downloadFileExtension;

        DBFluteUpgrader downloader = new DBFluteUpgrader(this);
        downloader.execute();
    }

    /**
     * Initialize dbfluteVersion if possible. <br>
     * Set up the version as latest release by public properties. <br>
     * No action if it already exists and if cannot get public properties.
     */
    private void initDBFluteVersionIfPossible() {
        if (!StringUtils.isBlank(dbfluteVersion)) {
            return;
        }
        if (publicProperties == null) {
            LogUtil.getLog().info("...Loading public properties");
            publicProperties = new DfPublicProperties();
            publicProperties.load();
        }
        dbfluteVersion = publicProperties.getDBFluteLatestReleaseVersion();
        LogUtil.getLog().info("Using DBFlute latest release version: " + dbfluteVersion);
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

    public InputStream getDownloadInputStream() throws MojoExecutionException {
        try {
            URL url = new URL(downloadPath);
            return url.openStream();
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Could not open a connection of "
                            + downloadPath
                            + "\n\nIf you want to use a proxy server,\n"
                            + "run \"mvn dbflute:download -Dhttp.proxyHost=<hostname> -Dhttp.proxyPort=<port>\".",
                    e);
        }
    }

    public String getDbfluteName() {
        return dbfluteName;
    }
}
