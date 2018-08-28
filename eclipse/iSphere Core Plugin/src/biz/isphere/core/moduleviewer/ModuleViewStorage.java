/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.moduleviewer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;

public class ModuleViewStorage extends PlatformObject implements IStorage {

    private String systemName;
    private String program;
    private String library;
    private String objectType;
    private String module;
    private int viewId;
    private String description;
    private String[] lines;

    public ModuleViewStorage(String systemName, String program, String library, String objectType, String module, int viewId, String description,
        String[] lines) {

        this.systemName = systemName;
        this.program = program;
        this.library = library;
        this.objectType = objectType;
        this.module = module;
        this.viewId = viewId;
        this.description = description;
        this.lines = lines;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getFullQualifiedName() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(systemName);
        buffer.append(":");
        buffer.append(library);
        buffer.append("/");
        buffer.append(program);
        buffer.append(".");
        buffer.append(module);
        buffer.append("[");
        buffer.append(viewId);
        buffer.append("]");

        return buffer.toString();
    }

    public String getName() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(library);
        buffer.append("/");
        buffer.append(program);
        buffer.append(".");
        buffer.append(module);

        return buffer.toString();
    }

    public String getDescription() {
        return description;
    }

    public InputStream getContents() throws CoreException {

        StringBuilder buffer = new StringBuilder();

        for (String line : lines) {
            if (buffer.length() > 0) {
                buffer.append("\n");
            }
            buffer.append(line);
        }

        return new ByteArrayInputStream(buffer.toString().getBytes());
    }

    public IPath getFullPath() {
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }
}
