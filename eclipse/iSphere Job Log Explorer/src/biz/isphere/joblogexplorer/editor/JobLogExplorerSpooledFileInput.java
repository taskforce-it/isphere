/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.IBMiHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.DateTimeHelper;
import biz.isphere.core.internal.QualifiedJobName;
import biz.isphere.core.preferencepages.IPreferences;
import biz.isphere.core.spooledfiles.ISpooledFileBrief;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.SpooledFileFactory;
import biz.isphere.joblogexplorer.exceptions.BasicJobLogLoaderException;
import biz.isphere.joblogexplorer.exceptions.DownloadSpooledFileException;
import biz.isphere.joblogexplorer.model.JobLog;
import biz.isphere.joblogexplorer.model.JobLogParser;

public class JobLogExplorerSpooledFileInput extends AbstractJobLogExplorerInput {

    private static final String INPUT_TYPE = "splf2://"; //$NON-NLS-1$
    private static final String DELIMITER = "_";

    private String connectionName;
    private String splfName;
    private int splfNumber;
    private QualifiedJobName qualifiedJobName;
    private String jobSystemName;
    private Date creationTimestamp;

    private SpooledFile spooledFile;
    private IFile localSpooledFilePath;
    private JobLog jobLog;

    public JobLogExplorerSpooledFileInput(ISpooledFileBrief spooledFileId) {
        init(spooledFileId.getConnectionName(), spooledFileId.getFile(), spooledFileId.getFileNumber(), spooledFileId.getJobName(),
            spooledFileId.getJobUser(), spooledFileId.getJobNumber(), spooledFileId.getJobSystem(),
            produceCreationTimeStamp(spooledFileId.getCreationDate(), spooledFileId.getCreationTime()));

        if (spooledFileId instanceof SpooledFile) {
            this.spooledFile = (SpooledFile)spooledFileId;
        }
    }

    public JobLogExplorerSpooledFileInput(String connectionName, String splfName, int splfNumber, String jobName, String userName, String jobNumber) {
        init(connectionName, splfName, splfNumber, jobName, userName, jobNumber, null, null);
    }

    public JobLogExplorerSpooledFileInput(String connectionName, String splfName, int splfNumber, String jobName, String userName, String jobNumber,
        String jobSystemName, Date creationTimestamp) {
        init(connectionName, splfName, splfNumber, jobName, userName, jobNumber, jobSystemName, creationTimestamp);
    }

    public JobLog load(IProgressMonitor monitor) throws BasicJobLogLoaderException {

        try {

            if (spooledFile == null) {
                if (jobSystemName != null && creationTimestamp != null) {
                    // Use extended spooled file identification attributes
                    spooledFile = new SpooledFileFactory().getSpooledFile(connectionName, splfName, splfNumber, qualifiedJobName.getJob(),
                        qualifiedJobName.getUser(), qualifiedJobName.getNumber(), jobSystemName, creationTimestamp);
                } else {
                    spooledFile = new SpooledFileFactory().getSpooledFile(connectionName, splfName, splfNumber, qualifiedJobName.getJob(),
                        qualifiedJobName.getUser(), qualifiedJobName.getNumber());
                }
            }

            String format = IPreferences.OUTPUT_FORMAT_TEXT;
            IFile target = ISpherePlugin.getDefault().getSpooledFilesProject().getFile(spooledFile.getTemporaryName(format));

            localSpooledFilePath = spooledFile.downloadSpooledFile(format, target);

        } catch (Exception e) {
            ISpherePlugin.logError("*** Failed downloading spooled file ***", e);
            throw new DownloadSpooledFileException(ExceptionHelper.getLocalizedMessage(e));
        }

        final String filePath = localSpooledFilePath.getLocation().toOSString();
        JobLogExplorerFileInput editorInput = new JobLogExplorerFileInput(filePath);

        JobLogParser reader = new JobLogParser(monitor);
        jobLog = reader.loadFromStmf(editorInput.getPath());

        return jobLog;
    }

    public String getName() {
        return jobLog.getQualifiedJobName();
    }

    public String getToolTipText() {
        return spooledFile.getToolTip(IPreferences.OUTPUT_FORMAT_TEXT);
    }

    @Override
    public String getContentId() {
        return INPUT_TYPE + getAbsoluteNameInternal();
    }

    private void init(String connectionName, String splfName, int splfNumber, String jobName, String userName, String jobNumber,
        String jobSystemName, Date creationTimestamp) {

        this.connectionName = connectionName;
        this.splfName = splfName;
        this.splfNumber = splfNumber;
        this.qualifiedJobName = new QualifiedJobName(jobName, userName, jobNumber);

        if (jobSystemName != null && creationTimestamp != null) {
            this.jobSystemName = jobSystemName;
            this.creationTimestamp = creationTimestamp;
        }
    }

    private Date produceCreationTimeStamp(String creationDate, String creationTime) {

        Date creationTimestamp;
        if (creationDate != null && creationTime != null) {
            Date tCreationDate = IBMiHelper.cyymmddToDate(creationDate);
            Date tCreationTime = IBMiHelper.hhmmssToTime(creationTime);
            creationTimestamp = DateTimeHelper.combineDateTime(tCreationDate, tCreationTime);
        } else {
            creationTimestamp = null;
        }

        return creationTimestamp;
    }

    private String getAbsoluteNameInternal() {
        if (jobSystemName != null && creationTimestamp != null) {
            // Use extended spooled file identification attributes
            return splfName + DELIMITER + splfNumber + DELIMITER + qualifiedJobName.getJob() + DELIMITER + qualifiedJobName.getUser() + DELIMITER
                + qualifiedJobName.getNumber() + DELIMITER + jobSystemName + DELIMITER + DateTimeHelper.getTimestampFormattedISO(creationTimestamp);
        } else {
            return splfName + DELIMITER + splfNumber + DELIMITER + qualifiedJobName.getJob() + DELIMITER + qualifiedJobName.getUser() + DELIMITER
                + qualifiedJobName.getNumber();
        }
    }
}
