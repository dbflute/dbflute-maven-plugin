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
import org.dbflute.maven.plugin.download.DBFluteDownloader;
import org.dbflute.maven.plugin.util.LogUtil;

/**
 * Download Plugin provides download goal to download a zip file of dbflute.
 * 
 * @goal download
 * 
 * @author shinsuke
 *
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

    private String dbfluteName;

    private String downloadPath;

    public void execute() throws MojoExecutionException, MojoFailureException {
        LogUtil.init(getLog());

        if (StringUtils.isBlank(dbfluteVersion)) {
            throw new MojoFailureException("Missing dbfluteVersion property.");
        }

        dbfluteName = downloadFilePrefix + dbfluteVersion;
        downloadPath = downloadDirUrl + dbfluteName + downloadFileExtension;

        DBFluteDownloader downloader = new DBFluteDownloader(this);
        downloader.execute();
    }

    public File getDbfluteDir() {
        return new File(mydbfluteDir, dbfluteName);
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
}
