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
import org.dbflute.maven.plugin.DownloadPlugin;
import org.dbflute.maven.plugin.officialcopy.DfPublicProperties;

/**
 * DBFluteDownloader downloads dbflute-*.zip and extracts it.
 * 
 * @author shinsuke
 * @author jflute
 */
public class DBFluteDownloader {

    protected final DownloadPlugin plugin;

    public DBFluteDownloader(DownloadPlugin plugin) {
        this.plugin = plugin;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        createEngineDownloadHandler().download();
    }

    protected EngineDownloadHandler createEngineDownloadHandler() throws MojoExecutionException, MojoFailureException {
        final String dbfluteVersion = plugin.getDbfluteVersion();
        final File mydbfluteDir = plugin.getMydbfluteDir();
        final String downloadFilePrefix = plugin.getDownloadFilePrefix();
        final String downloadUrl = plugin.getDownloadUrl();
        final DfPublicProperties publicProp = plugin.getPublicProperties();
        return new EngineDownloadHandler(dbfluteVersion, mydbfluteDir, downloadFilePrefix, downloadUrl, publicProp);
    }
}
