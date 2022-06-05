/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.compareeditor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.Charset;

import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;

public class CompareStreamFileNode extends BufferedContent implements ITypedElement, IEditableContent, IEncodedStreamContentAccessor {

    private static final String CRLF = "\r\n"; //$NON-NLS-1$

    private IResource fResource;
    private boolean ignoreCase;
    private File tempFile;

    public CompareStreamFileNode(IResource fResource, boolean considerDate, boolean ignoreCase, boolean hasCompareFilters) {

        this.fResource = fResource;
        this.ignoreCase = ignoreCase;

        Assert.isNotNull(fResource);
    }

    public String getName() {
        return fResource.getName();
    }

    public String getType() {
        return fResource.getFileExtension();
    }

    public Image getImage() {
        return CompareUI.getImage(fResource);
    }

    public boolean isEditable() {
        return true;
    }

    public ITypedElement replace(ITypedElement child, ITypedElement other) {
        return child;
    }

    @Override
    protected InputStream createStream() throws CoreException {
        try {
            return new BufferedInputStream(new FileInputStream(getTempFile(ignoreCase)));
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not create BufferedInputStream ***", e); //$NON-NLS-1$
            return null;
        }
    }

    public File getTempFile(boolean ignoreCase) {
        try {
            if (tempFile == null) {
                File file = fResource.getLocation().toFile();
                tempFile = new File(file.getPath() + "_temp"); //$NON-NLS-1$
                BufferedReader in = new BufferedReader(new FileReader(file));
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(tempFile)));
                String oldString;
                while ((oldString = in.readLine()) != null) {
                    String newString = StringHelper.trimR(new String(oldString.getBytes()));
                    if (ignoreCase) {
                        newString = newString.toLowerCase();
                    }
                    out.println(newString);
                }
                in.close();
                out.close();
            }
            return tempFile;
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not create temporary file ***", e); //$NON-NLS-1$
            return null;
        }
    }

    public void refreshTempFile() {
        tempFile.delete();
        tempFile = null;
        getTempFile(ignoreCase);
    }

    public void commit(IProgressMonitor pm) throws Exception {
        IFile file = (IFile)fResource;
        byte[] bytes = getContent();
        ByteArrayInputStream is = null;
        try {
            String contents = new String(bytes);
            StringBuffer updatedContents = new StringBuffer();
            BufferedReader in = new BufferedReader(new StringReader(contents));
            String s;
            while ((s = in.readLine()) != null) {
                String newStr = new String(s.getBytes());
                updatedContents.append(newStr + CRLF);
            }
            in.close();
            is = new ByteArrayInputStream(updatedContents.toString().getBytes());
            if (file.exists())
                file.setContents(is, false, true, pm);
            else
                file.create(is, false, pm);
        } finally {
            if (is != null) try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        file.refreshLocal(IFile.DEPTH_INFINITE, pm);
    }

    public String getCharset() throws CoreException {

        if (fResource instanceof IFile) {
            return ((IFile)fResource).getCharset();
        }

        return Charset.defaultCharset().name();
    }

}
