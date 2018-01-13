/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.lpex.LocalSourceLocation;

import com.ibm.etools.iseries.comm.interfaces.ISeriesHostObjectLock;

public class IProjectMember extends Member {

    private IFile file;

    private String connectionName;
    private String libraryName;
    private String fileName;
    private String memberName;
    private String label;

    private Boolean hasSequenceNumbersAndDateFields;

    public IProjectMember(IFile file) throws Exception {
        super();

        this.file = file;

        LocalSourceLocation localSourceLocation = new LocalSourceLocation(file.getFullPath().toString());
        this.libraryName = localSourceLocation.getLibraryName();
        this.fileName = localSourceLocation.getFileName();
        this.memberName = localSourceLocation.getMemberName();
        this.connectionName = IBMiHostContributionsHandler.getConnectionName(localSourceLocation.getProjectName());

        this.hasSequenceNumbersAndDateFields = null;
    }

    @Override
    public String getConnection() {
        return connectionName;
    }

    @Override
    public String getLibrary() {
        return libraryName;
    }

    @Override
    public String getSourceFile() {
        return fileName;
    }

    @Override
    public String getMember() {
        return memberName;
    }

    @Override
    public boolean exists() {
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    @Override
    public boolean download(IProgressMonitor monitor) throws Exception {
        throw produceUnsupportedOperationException();
    }

    @Override
    public String upload(IProgressMonitor monitor) throws Exception {
        throw produceUnsupportedOperationException();
    }

    public ISeriesHostObjectLock queryLocks() throws Exception {
        throw produceUnsupportedOperationException();
    }

    public String getMemberLockedMessages(ISeriesHostObjectLock lock) {
        throw produceUnsupportedOperationException();
    }

    @Override
    public IFile getLocalResource() {
        return file;
    }

    @Override
    public void setContents(String[] contents) throws Exception {
        throw produceUnsupportedOperationException();
    }

    @Override
    public String[] getContents() throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(getLocalResource().getContents(), "UTF-8")); //$NON-NLS-1$
        List<String> contents = new ArrayList<String>();
        String line = null;
        while ((line = br.readLine()) != null) {
            contents.add(line.substring(12)); // strip seq. number and date
        }

        return contents.toArray(new String[contents.size()]);
    }

    public String getDescription() {
        return file.getFullPath().toString();
    }

    public String getSourceType() {
        return file.getFileExtension();
    }

    @Override
    public void openStream() throws Exception {
        throw produceUnsupportedOperationException();
    }

    @Override
    public void closeStream() throws Exception {
        throw produceUnsupportedOperationException();
    }

    @Override
    public void addIgnoreFile() {
        throw produceUnsupportedOperationException();
    }

    @Override
    public void removeIgnoreFile() {
        throw produceUnsupportedOperationException();
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean isArchive() {
        return false;
    }

    @Override
    public void setArchive(boolean archive) {
        throw produceUnsupportedOperationException();
    }

    @Override
    public String getArchiveLibrary() {
        throw produceUnsupportedOperationException();
    }

    @Override
    public void setArchiveLibrary(String archiveLibrary) {
        throw produceUnsupportedOperationException();
    }

    @Override
    public String getArchiveFile() {
        throw produceUnsupportedOperationException();
    }

    @Override
    public void setArchiveFile(String archiveFile) {
        throw produceUnsupportedOperationException();
    }

    @Override
    public String getArchiveMember() {
        throw produceUnsupportedOperationException();
    }

    @Override
    public void setArchiveMember(String archiveMember) {
        throw produceUnsupportedOperationException();
    }

    @Override
    public String getArchiveDate() {
        throw produceUnsupportedOperationException();
    }

    @Override
    public void setArchiveDate(String archiveDate) {
        throw produceUnsupportedOperationException();
    }

    @Override
    public String getArchiveTime() {
        throw produceUnsupportedOperationException();
    }

    @Override
    public void setArchiveTime(String archiveTime) {
        throw produceUnsupportedOperationException();
    }

    @Override
    public SourceLine[] downloadSourceMember(IProgressMonitor monitor) throws Exception {
        throw produceUnsupportedOperationException();
    }

    @Override
    public String uploadSourceMember(SourceLine[] sourceLines, IProgressMonitor monitor) throws Exception {
        throw produceUnsupportedOperationException();
    }

    public boolean hasSequenceNumbersAndDateFields() {

        if (hasSequenceNumbersAndDateFields == null) {

            File file = this.file.getLocation().toFile();

            BufferedReader in = null;

            try {

                in = new BufferedReader(new FileReader(file));

                int count = 10;
                String line;
                String pattern = "^[0-9]{12}.*"; //$NON-NLS-1$
                while ((line = in.readLine()) != null && count > 0 && !Boolean.FALSE.equals(hasSequenceNumbersAndDateFields)) {

                    line = StringHelper.trimR(line);
                    if (line.length() < 12) {
                        hasSequenceNumbersAndDateFields = false;
                    } else if (!line.matches(pattern)) {
                        hasSequenceNumbersAndDateFields = false;
                    } else {
                        hasSequenceNumbersAndDateFields = true;
                    }

                    count--;
                }

            } catch (Throwable e) {
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }

        }

        return hasSequenceNumbersAndDateFields;
    }

    private UnsupportedOperationException produceUnsupportedOperationException() {

        String methodName = null;
        StackTraceElement[] e = Thread.currentThread().getStackTrace();

        boolean doNext = false;
        for (StackTraceElement s : e) {
            methodName = s.getMethodName();
            if (doNext) {
                System.out.println(methodName);
            }
            doNext = methodName.equals("produceUnsupportedOperationException");
        }

        return new UnsupportedOperationException(Messages.bind("Operation {0} is not supported for i Project members", methodName));
    }

    @Override
    public String toString() {

        if (file == null) {
            return super.toString();
        }

        if (getConnection() != null) {
            return getConnection() + ":" + file.toString();
        } else {
            return file.toString();
        }
    }

}
