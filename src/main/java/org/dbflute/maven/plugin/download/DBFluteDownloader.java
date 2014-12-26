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
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.dbflute.maven.plugin.DownloadPlugin;
import org.dbflute.maven.plugin.util.LogUtil;
import org.dbflute.maven.plugin.util.ResourceUtil;

/**
 * DBFluteDownloader downloads dbflute-*.zip and extracts it.
 * 
 * @author shinsuke
 *
 */
public class DBFluteDownloader {

    private static final String VERSION_PLACEHOLDER = "$$version$$";

    private static final String DBFLUTE_ENGINE_DOWNLOAD_URL = "dbflute.engine.download.url";

    private static final String DBFLUTE_LATEST_RELEASE_VERSION = "dbflute.latest.release.version";

    protected DownloadPlugin plugin;

    public DBFluteDownloader(DownloadPlugin plugin) {
        this.plugin = plugin;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {

        final Map<String, String> paramMap = parsePublicPropertyMap();

        final String dbfluteVersion;
        if (plugin.getDbfluteVersion() == null) {
            dbfluteVersion = paramMap.get(DBFLUTE_LATEST_RELEASE_VERSION);
            if (dbfluteVersion == null) {
                throw new MojoFailureException(
                        "Set <dbfluteVersion> in pom.xml of -Ddbflute.version=<version>.");
            }
        } else {
            dbfluteVersion = plugin.getDbfluteVersion();
        }

        File dbfluteDir = new File(plugin.getMydbfluteDir(),
                plugin.getDownloadFilePrefix() + dbfluteVersion);
        if (!dbfluteDir.exists()) {
            String downloadUrl = plugin.getDownloadUrl();
            if (downloadUrl == null) {
                downloadUrl = paramMap.get(DBFLUTE_ENGINE_DOWNLOAD_URL);
                if (downloadUrl == null) {
                    throw new MojoFailureException(
                            "Set <downloadUrl> in pom.xml of -Ddbflute.downloadUrl=<url>.");
                }
            }
            downloadUrl = downloadUrl.replace(VERSION_PLACEHOLDER,
                    dbfluteVersion);
            LogUtil.getLog().info("Creating " + dbfluteDir.getAbsolutePath());
            ResourceUtil.unzip(downloadUrl, dbfluteDir);
        } else {
            LogUtil.getLog().info(dbfluteDir.getAbsolutePath() + " exists.");
        }

    }

    private Map<String, String> parsePublicPropertyMap()
            throws MojoExecutionException, MojoFailureException {
        final String content = ResourceUtil.readText(
                plugin.getPublicPropertyUrl(), "UTF-8");
        if (content == null || content.trim().length() == 0) {
            throw new MojoFailureException("The content of "
                    + plugin.getPublicPropertyUrl() + " is empty.");
        }

        LogUtil.getLog().debug(
                plugin.getPublicPropertyUrl() + File.separator + content);

        final Map<String, String> paramMap = new HashMap<>();
        for (String line : content.split("\n")) {
            if (line != null && line.trim().length() != 0
                    && !line.trim().startsWith("#")) {
                String[] values = line.split("=");
                if (values.length == 2) {
                    paramMap.put(values[0].trim(), values[1].trim());
                }
            }
        }

        LogUtil.getLog().debug("Public Properties: " + paramMap);
        return paramMap;
    }
}
