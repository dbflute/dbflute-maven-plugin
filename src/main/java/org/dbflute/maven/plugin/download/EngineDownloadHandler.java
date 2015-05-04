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
package org.dbflute.maven.plugin.download;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.dbflute.maven.plugin.officialcopy.DfPublicProperties;
import org.dbflute.maven.plugin.util.LogUtil;
import org.dbflute.maven.plugin.util.ResourceUtil;

/**
 * @author jflute
 */
public class EngineDownloadHandler {

    protected String dbfluteVersion; // null allowed (then from properties)
    protected final File mydbfluteDir;
    protected final String downloadFilePrefix;
    protected String downloadUrl; // null allowed (then from properties)
    protected final DfPublicProperties publicProp;

    public EngineDownloadHandler(String dbfluteVersion, File mydbfluteDir, String downloadFilePrefix, String downloadUrl, DfPublicProperties publicProp) {
        this.dbfluteVersion = dbfluteVersion;
        this.mydbfluteDir = mydbfluteDir;
        this.downloadFilePrefix = downloadFilePrefix;
        this.downloadUrl = downloadUrl;
        this.publicProp = publicProp;
    }

    public void download() throws MojoExecutionException, MojoFailureException {
        if (dbfluteVersion == null) {
            dbfluteVersion = publicProp.getDBFluteLatestReleaseVersion();
            if (dbfluteVersion == null) {
                throw new MojoFailureException("Set <dbfluteVersion> in pom.xml of -Ddbflute.version=<version>.");
            }
            LogUtil.getLog().info("Using DBFlute latest release version: " + dbfluteVersion);
        }
        File dbfluteDir = new File(mydbfluteDir, downloadFilePrefix + dbfluteVersion);
        if (!dbfluteDir.exists()) {
            if (downloadUrl == null) {
                downloadUrl = publicProp.getDBFluteDownloadUrl(dbfluteVersion);
                if (downloadUrl == null) {
                    throw new MojoFailureException(
                            "Set <downloadUrl> in pom.xml of "
                                    + "-Ddbflute.downloadUrl=http://dbflute.org/download/dbflute/dbflute-$$version$$.zip.");
                }
            }
            LogUtil.getLog().info("Creating " + dbfluteDir.getAbsolutePath());
            ResourceUtil.unzip(downloadUrl, dbfluteDir);
        } else {
            LogUtil.getLog().info(dbfluteDir.getAbsolutePath() + " exists.");
        }
    }
}
