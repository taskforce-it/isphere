/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.helpbuilder.model;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import biz.isphere.core.helpbuilder.configuration.Configuration;
import biz.isphere.core.helpbuilder.exception.JobCanceledException;
import biz.isphere.core.helpbuilder.utils.FileUtil;

public class Project {

    private String name;
    private String id;
    private String toc;
    private Bundle bundle;
    private String helpDirectory;

    public Project(String configString) throws JobCanceledException {

        name = configString;

        String projectPath = Configuration.getInstance().getWorkspace().getPath() + File.separator + name;
        toc = findToc(projectPath);
        id = getProjectId(projectPath);
        helpDirectory = findHelpDirectory(projectPath);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getToc() {
        return toc;
    }

    public String getHelpDirectory() {
        return helpDirectory;
    }

    private String getProjectId(String projectPath) throws JobCanceledException {
        return getOrLoadBundle(projectPath).getSymbolicName();
    }

    private String findToc(String path) throws JobCanceledException {

        File tocDir = new File(path, "toc");
        String[] tocFiles = tocDir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.startsWith("toc") && name.endsWith(".xml")) {
                    return true;
                }
                return false;
            }
        });

        if (tocFiles.length != 1) {
            throw new JobCanceledException("Table of content file not found.");
        }

        try {
            return FileUtil.resolvePath(tocDir.getPath(), tocFiles[0]);
        } catch (IOException e) {
            throw new JobCanceledException("Table of content file not found.", e);
        }
    }

    private String findManifest(String path) throws IOException {

        File metainfDir = new File(path);
        String[] manifestFiles = metainfDir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.equalsIgnoreCase("MANIFEST.MF")) {
                    return true;
                }
                return false;
            }
        });

        if (manifestFiles.length != 1) {
            return null;
        }

        return FileUtil.resolvePath(path, manifestFiles[0]);
    }

    private String findHelpDirectory(String path) throws JobCanceledException {

        try {
            return FileUtil.resolvePath(path, "html");
        } catch (IOException e) {
            throw new JobCanceledException("Table of content file not found.", e);
        }
    }

    private Bundle getOrLoadBundle(String projectPath) throws JobCanceledException {

        if (bundle == null) {
            try {
                String manifest = findManifest(projectPath + File.separator + "META-INF");
                bundle = new Bundle(manifest);
            } catch (IOException e) {
                throw new JobCanceledException(e.getLocalizedMessage(), e);
            }
        }

        return bundle;
    }
}
