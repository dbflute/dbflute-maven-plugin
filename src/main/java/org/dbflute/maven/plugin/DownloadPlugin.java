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
import org.dbflute.maven.plugin.download.DBFluteDownloader;
import org.dbflute.maven.plugin.officialcopy.DfPublicProperties;
import org.dbflute.maven.plugin.util.LogUtil;

/**
 * Download Plugin provides download goal to download a zip file of dbflute.
 * 
 * @goal download
 * 
 * @author shinsuke
 * @author jflute
 */
public class DownloadPlugin extends AbstractMojo {

    /**
     * @parameter property="dbflute.version" 
     */
    protected String dbfluteVersion;

    /**
     * @parameter property="dbflute.downloadFilePrefix" default-value="dbflute-"
     */
    protected String downloadFilePrefix;

    /**
     * @parameter property="dbflute.downloadUrl"
     */
    protected String downloadUrl;

    /**
     * @parameter property="dbflute.publicPropertyUrl" default-value="http://dbflute.org/meta/public.properties"
     */
    protected String publicPropertyUrl;

    /** public properties that contains version info, and DBFlute provides officially (NullAllowed: lazy-loaded) */
    private DfPublicProperties publicProperties;

    /**
     * @parameter property="dbflute.mydbfluteDir" default-value="${basedir}/mydbflute"
     */
    protected File mydbfluteDir;

    public void execute() throws MojoExecutionException, MojoFailureException {
        LogUtil.init(getLog());

        initDBFluteVersionIfPossible();
        if (StringUtils.isBlank(dbfluteVersion)) {
            throw new MojoFailureException("Missing dbfluteVersion property.");
        }

        DBFluteDownloader downloader = new DBFluteDownloader(this);
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

    public String getPublicPropertyUrl() {
        return publicPropertyUrl;
    }

    public String getDbfluteVersion() {
        return dbfluteVersion;
    }

    public String getDownloadFilePrefix() {
        return downloadFilePrefix;
    }

    public File getMydbfluteDir() {
        return mydbfluteDir;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
