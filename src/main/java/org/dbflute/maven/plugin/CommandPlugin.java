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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

/**
 * CommandPlugin is an abstract class for command plugins.
 * 
 * @author shinsuke
 *
 */
public abstract class CommandPlugin extends AbstractMojo {
    /**
     * Project base directory (prepended for relative file paths).
     *
     * @parameter property="basedir"
     * @required
     */
    protected File basedir;

    /**
     * The current Maven project.
     *
     * @parameter property="project"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @parameter property="dbflute.dbfluteClientDir" 
     */
    protected File dbfluteClientDir;

    /**
     * @parameter property="dbflute.clientProject"
     */
    protected String clientProject;

    public File getDbfluteClientDir() {
        if (dbfluteClientDir == null) {
            if (StringUtils.isBlank(clientProject)) {
                dbfluteClientDir = new File(basedir, "dbflute_"
                        + project.getArtifactId());
            } else {
                dbfluteClientDir = new File(basedir, "dbflute_" + clientProject);
            }
        }
        return dbfluteClientDir;
    }

    /**
     * @param cmds
     */
    public void updateArgs(List<String> cmds) {
    }
}
