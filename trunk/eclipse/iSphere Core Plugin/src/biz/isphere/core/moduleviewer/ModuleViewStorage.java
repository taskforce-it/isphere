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
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;

import biz.isphere.core.ISpherePlugin;

public class ModuleViewStorage extends PlatformObject implements IStorage {

    private String systemName;
    private String program;
    private String library;
    private String objectType;
    private String module;
    private int viewId;
    private String[] lines;

    public ModuleViewStorage(String systemName, String program, String library, String objectType, String module, int viewId, String[] lines) {

        this.systemName = systemName;
        this.program = program;
        this.library = library;
        this.objectType = objectType;
        this.module = module;
        this.viewId = viewId;
        this.lines = lines;
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
        return getFullQualifiedName();
    }

    public InputStream getContents() throws CoreException {

        StringBuilder buffer = new StringBuilder();

        for (String line : lines) {
            if (buffer.length() > 0) {
                buffer.append("\n");
            }
            buffer.append(line);
        }

        InputStream stream = null;

        try {
            stream = new ByteArrayInputStream(buffer.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            stream = new ByteArrayInputStream(e.getLocalizedMessage().getBytes());
        }

        return stream;
    }

    public IPath getFullPath() {
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }

    private String logException(String text, Exception exception) {
        ISpherePlugin.logError(text, exception);
        return text;
    }
}
