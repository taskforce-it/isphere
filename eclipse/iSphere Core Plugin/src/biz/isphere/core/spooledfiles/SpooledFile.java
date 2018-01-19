/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.base.internal.Buffer;
import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.IBMiHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.annotations.CMOne;
import biz.isphere.core.internal.BrowserEditor;
import biz.isphere.core.internal.BrowserEditorInput;
import biz.isphere.core.internal.DateTimeHelper;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.internal.ReadOnlyEditor;
import biz.isphere.core.preferencepages.IPreferences;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.extension.handler.WidgetFactoryContributionsHandler;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.IFSFile;
import com.ibm.as400.access.IFSFileInputStream;
import com.ibm.as400.access.PrintObject;
import com.ibm.as400.access.RequestNotSupportedException;

public class SpooledFile {

    private static final String ISPHERE_SPOOLED_FILE_NAME_PREFIX = "iSphere_Spooled_File_"; //$NON-NLS-1$

    public static final String VARIABLE_SPLFNBR = "&SPLFNBR";
    public static final String VARIABLE_SPLF = "&SPLF";
    public static final String VARIABLE_JOBNBR = "&JOBNBR";
    public static final String VARIABLE_JOBUSR = "&JOBUSR";
    public static final String VARIABLE_JOBNAME = "&JOBNAME";
    public static final String VARIABLE_JOBSYS = "&JOBSYS";
    public static final String VARIABLE_STMFDIR = "&STMFDIR";
    public static final String VARIABLE_STMF = "&STMF";
    public static final String VARIABLE_CODPAG = "&CODPAG";
    public static final String VARIABLE_FMT = "&FMT";

    public static final String VARIABLE_STATUS = "&STATUS";
    public static final String VARIABLE_CTIME_STAMP = "&CTIMESTAMP";
    public static final String VARIABLE_CDATE = "&CDATE";
    public static final String VARIABLE_CTIME = "&CTIME";
    public static final String VARIABLE_USRDTA = "&USRDTA";

    private static final String IBMI_FILE_SEPARATOR = "/";
    private static final String ISPHERE_IFS_TMP_DIRECTORY = IBMI_FILE_SEPARATOR + "tmp"; //$NON-NLS-N$

    private AS400 as400;

    private String file;

    private int fileNumber;

    private String jobName;

    private String jobUser;

    private String jobNumber;

    private String jobSystem;

    private Date creationDate;

    private String creationDate_cyymmdd;

    private Date creationTime;

    private String creationTime_hhmmss;

    /* Lazy loaded and cached! Do not access directly */
    private String creationDateFormatted;

    /* Lazy loaded and cached! Do not access directly */
    private String creationTimeFormatted;

    /* Lazy loaded and cached! Do not access directly */
    private Date creationTimestamp;

    private String status;

    private String outputQueue;

    private String outputQueueLibrary;

    private String outputPriority;

    private String userData;

    private String formType;

    private int copies;

    private int pages;

    private int currentPage;

    private Object data;

    private com.ibm.as400.access.SpooledFile toolboxSpooledFile;

    private String connectionName;

    public SpooledFile() {
        as400 = null;
        file = "";
        fileNumber = 0;
        jobName = "";
        jobUser = "";
        jobNumber = "";
        jobSystem = "";
        creationDate = null;
        creationDate_cyymmdd = "";
        creationDateFormatted = null;
        creationTime = null;
        creationTime_hhmmss = "";
        creationTimeFormatted = null;
        creationTimestamp = null;
        status = "";
        outputQueue = "";
        outputQueueLibrary = "";
        outputPriority = "";
        userData = "";
        formType = "";
        copies = 0;
        pages = 0;
        currentPage = 0;
        data = null;
        toolboxSpooledFile = null;
    }

    public AS400 getAS400() {
        return as400;
    }

    public void setAS400(AS400 as400) {
        this.as400 = as400;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(int fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobUser() {
        return jobUser;
    }

    public void setJobUser(String jobUser) {
        this.jobUser = jobUser;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getJobSystem() {
        return jobSystem;
    }

    public void setJobSystem(String jobSystem) {
        this.jobSystem = jobSystem;
    }

    @CMOne(info = "Don`t change this method due to CMOne compatibility reasons")
    public String getCreationDate() {
        return creationDate_cyymmdd;
    }

    public Date getCreationDateAsDate() {
        return creationDate;
    }

    @CMOne(info = "Don`t change this method due to CMOne compatibility reasons")
    public void setCreationDate(String creationDate) {

        this.creationDate_cyymmdd = creationDate;

        this.creationDate = IBMiHelper.cyymmddToDate(this.creationDate_cyymmdd);

        this.creationDateFormatted = null; // Lazy loaded
        this.creationTimestamp = null; // Lazy loaded
    }

    @CMOne(info = "Don`t change this method due to CMOne compatibility reasons")
    public String getCreationTime() {
        return creationTime_hhmmss;
    }

    public Date getCreationTimeAsDate() {
        return creationTime;
    }

    @CMOne(info = "Don`t change this method due to CMOne compatibility reasons")
    public void setCreationTime(String creationTime) {

        this.creationTime_hhmmss = creationTime;

        this.creationTime = IBMiHelper.hhmmssToTime(this.creationTime_hhmmss);

        this.creationTimeFormatted = null; // Lazy loaded
        this.creationTimestamp = null; // Lazy loaded
    }

    public Date getCreationTimestamp() {

        if (creationTimestamp == null) {
            creationTimestamp = DateTimeHelper.combineDateTime(this.creationDate, this.creationTime);
        }

        return creationTimestamp;
    }

    public void setCreationTimestamp(Date date, Time time) {

        this.creationDate = date;
        this.creationTime = time;

        this.creationDate_cyymmdd = IBMiHelper.dateToCyymmdd(this.creationDate, "");
        this.creationTime_hhmmss = IBMiHelper.timeToHhmmss(this.creationTime, "");

        this.creationDateFormatted = null; // Lazy loaded
        this.creationTimeFormatted = null; // Lazy loaded
        this.creationTimestamp = null; // Lazy loaded
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutputQueue() {
        return outputQueue;
    }

    public void setOutputQueue(String outputQueue) {
        this.outputQueue = outputQueue;
    }

    public String getOutputQueueLibrary() {
        return outputQueueLibrary;
    }

    public void setOutputQueueLibrary(String outputQueueLibrary) {
        this.outputQueueLibrary = outputQueueLibrary;
    }

    public String getOutputPriority() {
        return outputPriority;
    }

    public void setOutputPriority(String outputPriority) {
        this.outputPriority = outputPriority;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    private com.ibm.as400.access.SpooledFile getToolboxSpooledFile() {
        return new com.ibm.as400.access.SpooledFile(as400, file, fileNumber, jobName, jobUser, jobNumber, jobSystem, getCreationDate(),
            getCreationTime());
    }

    public String hold() {
        if (toolboxSpooledFile == null) {
            toolboxSpooledFile = getToolboxSpooledFile();
        }
        try {
            toolboxSpooledFile.hold(null);
            refreshSpooledFile();
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String release() {
        if (toolboxSpooledFile == null) {
            toolboxSpooledFile = getToolboxSpooledFile();
        }
        try {
            toolboxSpooledFile.release();
            refreshSpooledFile();
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String delete() {
        if (toolboxSpooledFile == null) {
            toolboxSpooledFile = getToolboxSpooledFile();
        }
        try {
            toolboxSpooledFile.delete();
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public AS400Message getMessage() {
        if (toolboxSpooledFile == null) {
            toolboxSpooledFile = getToolboxSpooledFile();
        }
        try {
            return toolboxSpooledFile.getMessage();
        } catch (Exception e) {
            return null;
        }
    }

    public String answerMessage(String reply) {
        if (toolboxSpooledFile == null) {
            toolboxSpooledFile = getToolboxSpooledFile();
        }
        try {
            toolboxSpooledFile.answerMessage(reply);
            refreshSpooledFile();
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String replyMessage() {
        if (toolboxSpooledFile == null) {
            toolboxSpooledFile = getToolboxSpooledFile();
        }
        AS400Message message = null;
        try {
            message = toolboxSpooledFile.getMessage();
        } catch (Exception e) {
        }
        if (message == null) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.Error,
                Messages.bind(Messages.No_Messages, new String[] { file, Integer.toString(fileNumber) }));
            // Messages.getString("No_Messages").replaceAll("&1",
            // file).replaceAll("&2", Integer.toString(fileNumber)));
        } else {
            SpooledFileMessageDialog dialog = new SpooledFileMessageDialog(Display.getCurrent().getActiveShell(), this);
            dialog.open();
        }
        return null;
    }

    private void refreshSpooledFile() {
        if (toolboxSpooledFile == null) {
            toolboxSpooledFile = getToolboxSpooledFile();
        } else {
            try {
                toolboxSpooledFile.update();
            } catch (AS400Exception e) {
            } catch (AS400SecurityException e) {
            } catch (ErrorCompletingRequestException e) {
            } catch (IOException e) {
            } catch (InterruptedException e) {
            } catch (RequestNotSupportedException e) {
            }
        }
        try {
            status = toolboxSpooledFile.getStringAttribute(PrintObject.ATTR_SPLFSTATUS);
        } catch (Exception e) {
        }
        try {
            String outqdev = toolboxSpooledFile.getStringAttribute(PrintObject.ATTR_OUTPUT_QUEUE);
            if (outqdev.endsWith(".OUTQ")) {
                outqdev = outqdev.substring(10);
                int slash = outqdev.indexOf("/");
                String library = outqdev.substring(0, slash - 4);
                String outq = outqdev.substring(slash + 1);
                outputQueue = outq.substring(0, outq.indexOf(".OUTQ"));
                outputQueueLibrary = library;
            } else {
                outputQueue = outqdev;
                outputQueueLibrary = "*LIBL";
            }
        } catch (Exception e) {
        }
        try {
            outputPriority = toolboxSpooledFile.getStringAttribute(PrintObject.ATTR_OUTPTY);
        } catch (Exception e) {
        }
        try {
            userData = toolboxSpooledFile.getStringAttribute(PrintObject.ATTR_USERDATA);
        } catch (Exception e) {
        }
        try {
            formType = toolboxSpooledFile.getStringAttribute(PrintObject.ATTR_FORMTYPE);
        } catch (Exception e) {
        }
        try {
            copies = toolboxSpooledFile.getIntegerAttribute(PrintObject.ATTR_COPIES);
        } catch (Exception e) {
        }
        try {
            pages = toolboxSpooledFile.getIntegerAttribute(PrintObject.ATTR_PAGES);
        } catch (Exception e) {
        }
        try {
            currentPage = toolboxSpooledFile.getIntegerAttribute(PrintObject.ATTR_CURPAGE);
        } catch (Exception e) {
        }
    }

    public String getCommandChangeAttribute() {
        return "CHGSPLFA FILE(" + file + ") JOB(" + jobNumber + "/" + jobUser + "/" + jobName + ") SPLNBR(" + fileNumber + ")";
    }

    public String changeAttribute(String command) {
        String message = executeCommand(command);
        if (message == null) {
            refreshSpooledFile();
        }
        return message;
    }

    public String executeCommand(String command) {
        try {
            CommandCall commandCall = new CommandCall(as400, command);
            if (!commandCall.run()) {
                AS400Message message = commandCall.getMessageList(0);
                if (message != null) {
                    return message.getText();
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }

    public String getOutputQueueFormatted() {
        return outputQueueLibrary + "/" + outputQueue;
    }

    /**
     * Returns the formatted date the spooled file was created.
     * <p>
     * This property is lazy loaded because it is not used before the user
     * selects "Show in Table" from the RSE tree.
     * 
     * @return date the spooled file was created
     */
    public String getCreationDateFormatted() {
        if (creationDateFormatted == null) {
            if (creationDate == null) {
                return "";
            }

            creationDateFormatted = DateTimeHelper.getDateFormatted(this.creationDate);
        }
        return creationDateFormatted;
    }

    /**
     * Returns the formatted time the spooled file was created.
     * <p>
     * This property is lazy loaded because it is not used before the user
     * selects "Show in Table" from the RSE tree.
     * 
     * @return time the spooled file was created
     */
    public String getCreationTimeFormatted() {
        if (creationTimeFormatted == null) {
            if (creationTime == null) {
                return "";
            }

            creationTimeFormatted = DateTimeHelper.getTimeFormatted(this.creationTime);
        }
        return creationTimeFormatted;
    }

    /**
     * Return the formatted timestamp the spooled file was created.
     * 
     * @return timestamp the spooled file was created
     */
    public String getCreationTimestampFormatted() {
        return DateTimeHelper.getTimestampFormatted(creationTimestamp);
    }

    public void asyncOpen(final String format, final Shell shell) {

        Job job = new Job(Messages.Loading_spooled_file) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {

                String source = null;
                boolean hasSpooledFile = false;

                try {

                    source = ISPHERE_IFS_TMP_DIRECTORY + IBMI_FILE_SEPARATOR + getTemporaryName(format);
                    final IFile file = getLocalSpooledFile(format, source);
                    if (file == null) {
                        MessageDialogAsync.displayError(shell, Messages.Could_not_create_stream_file_for_spooled_file_on_host);
                        return Status.OK_STATUS;
                    }

                    UIJob uiJob = new UIJob("") {

                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            try {
                                openSpooledFileInEditor(format, file);
                            } catch (Exception e) {
                                MessageDialog.openError(shell, Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
                            }
                            return Status.OK_STATUS;
                        }
                    };
                    uiJob.schedule();

                } catch (Exception e) {
                    MessageDialogAsync.displayError(shell, ExceptionHelper.getLocalizedMessage(e));
                } finally {

                    if (hasSpooledFile) {
                        try {
                            deleteStreamFile(source);
                        } catch (Exception e) {
                            MessageDialogAsync.displayError(shell, ExceptionHelper.getLocalizedMessage(e));
                        }
                    }

                }

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    public String open(String format) {

        if (Preferences.getInstance().isLoadSpooledFilesAsynchronousliy()) {
            asyncOpen(format, Display.getCurrent().getActiveShell());
            return null;
        }

        String source = null;
        boolean hasSpooledFile = false;

        try {

            source = ISPHERE_IFS_TMP_DIRECTORY + IBMI_FILE_SEPARATOR + getTemporaryName(format);
            IFile file = getLocalSpooledFile(format, source);
            if (file == null) {
                return Messages.Could_not_create_stream_file_for_spooled_file_on_host;
            }

            openSpooledFileInEditor(format, file);

        } catch (Exception e) {
            return ExceptionHelper.getLocalizedMessage(e);
        } finally {

            if (hasSpooledFile) {
                try {
                    deleteStreamFile(source);
                } catch (Exception e) {
                    return ExceptionHelper.getLocalizedMessage(e);
                }
            }

        }

        return null;

    }

    private void openSpooledFileInEditor(String format, IFile file) throws PartInitException {

        Image image = ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_SPOOLED_FILE);

        if (format.equals(IPreferences.OUTPUT_FORMAT_TEXT)) {

            boolean useEditor = Preferences.getInstance().isSpooledFileConversionTextEditAllowed();
            if (useEditor) {
                IWorkbenchPage page = ISpherePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
                org.eclipse.ui.ide.IDE.openEditor(page, file);
            } else {
                // BrowserEditorInput browserInput = new
                // BrowserEditorInput(getTemporaryName(format),
                // getTemporaryName(format), getToolTip(format),
                // image, file.getLocation().toOSString());
                // PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(browserInput,
                // BrowserEditor.ID);
                FileEditorInput editorInput = new FileEditorInput(file);
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, ReadOnlyEditor.ID);
            }

        } else if (format.equals(IPreferences.OUTPUT_FORMAT_HTML)) {

            boolean useEditor = Preferences.getInstance().isSpooledFileConversionHTMLEditAllowed();
            if (useEditor) {
                IWorkbenchPage page = ISpherePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
                org.eclipse.ui.ide.IDE.openEditor(page, file);
            } else {
                BrowserEditorInput editorInput = new BrowserEditorInput(getTemporaryName(format), getTemporaryName(format), getToolTip(format),
                    image, file.getLocation().toOSString());
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, BrowserEditor.ID);
            }

        } else if (format.equals(IPreferences.OUTPUT_FORMAT_PDF)) {

            BrowserEditorInput editorInput = new BrowserEditorInput(getTemporaryName(format), getTemporaryName(format), getToolTip(format), image,
                file.getLocation().toOSString());
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, BrowserEditor.ID);
        }
    }

    public IFile downloadSpooledFile(String format, String target) throws Exception {

        String source = ISPHERE_IFS_TMP_DIRECTORY + IBMI_FILE_SEPARATOR + getTemporaryName(format);
        IFile file = getLocalSpooledFile(format, source, target);

        return file;
    }

    private IFile getLocalSpooledFile(String format, String source) throws Exception {

        String target = ISpherePlugin.getDefault().getSpooledFilesDirectory() + File.separator + getTemporaryName(format);

        return getLocalSpooledFile(format, source, target);
    }

    private IFile getLocalSpooledFile(String format, String source, String target) throws Exception {

        boolean hasSpooledFile = false;

        if (doTransformSpooledFile(format)) {
            hasSpooledFile = transformSpooledFile(format, target);
        } else {
            if (createStreamFile(format)) {
                hasSpooledFile = uploadStreamFile(source, target);
            }
        }

        if (!hasSpooledFile) {
            return null;
        }

        if (!ISpherePlugin.getDefault().getSpooledFilesProject().isOpen()) {
            ISpherePlugin.getDefault().getSpooledFilesProject().open(null);
        }

        IFile file = ISpherePlugin.getWorkspace().getRoot().getFileForLocation(new Path(target));
        file.refreshLocal(1, null);

        return file;
    }

    private boolean doTransformSpooledFile(String format) {
        Preferences store = Preferences.getInstance();

        boolean doTransformSpooledFile = false;
        if (format.equals(IPreferences.OUTPUT_FORMAT_TEXT)) {
            doTransformSpooledFile = store.getSpooledFileConversionText().equals(IPreferences.SPLF_CONVERSION_TRANSFORM);
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_HTML)) {
            doTransformSpooledFile = store.getSpooledFileConversionHTML().equals(IPreferences.SPLF_CONVERSION_TRANSFORM);
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_PDF)) {
            doTransformSpooledFile = store.getSpooledFileConversionPDF().equals(IPreferences.SPLF_CONVERSION_TRANSFORM);
        }

        if (doTransformSpooledFile && !ISphereHelper.canTransformSpooledFile(getAS400())) {
            doTransformSpooledFile = false;
        }

        return doTransformSpooledFile;
    }

    private boolean createStreamFile(String format) throws Exception {
        Preferences store = Preferences.getInstance();

        boolean _default = true;
        String conversionCommand = "";
        String conversionCommandLibrary = "";
        if (format.equals(IPreferences.OUTPUT_FORMAT_TEXT)) {
            if (store.getSpooledFileConversionText().equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {
                _default = false;
                conversionCommand = store.getSpooledFileConversionTextCommand();
                conversionCommandLibrary = store.getSpooledFileConversionTextLibrary();
            }
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_HTML)) {
            if (store.getSpooledFileConversionHTML().equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {
                _default = false;
                conversionCommand = store.getSpooledFileConversionHTMLCommand();
                conversionCommandLibrary = store.getSpooledFileConversionHTMLLibrary();
            }
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_PDF)) {
            if (store.getSpooledFileConversionPDF().equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {
                _default = false;
                conversionCommand = store.getSpooledFileConversionPDFCommand();
                conversionCommandLibrary = store.getSpooledFileConversionPDFLibrary();
            }
        }

        String command = null;
        String library = null;

        if (_default) {
            command = "CVTSPLF FROMFILE(" + SpooledFile.VARIABLE_SPLF + ") SPLNBR(" + SpooledFile.VARIABLE_SPLFNBR + ") JOB("
                + SpooledFile.VARIABLE_JOBNBR + "/" + SpooledFile.VARIABLE_JOBUSR + "/" + SpooledFile.VARIABLE_JOBNAME + ") TOSTREAM('"
                + SpooledFile.VARIABLE_STMF + "') TODIR('" + SpooledFile.VARIABLE_STMFDIR + "') STCODPAG(" + SpooledFile.VARIABLE_CODPAG + ") TOFMT("
                + SpooledFile.VARIABLE_FMT + ") STOPT(*REPLACE)";
            library = ISpherePlugin.getISphereLibrary(connectionName);

        } else {
            command = conversionCommand;
            library = conversionCommandLibrary;
        }

        command = replaceVariables(command, format);

        String currentLibrary = null;

        boolean cleanUp = false;

        try {

            currentLibrary = ISphereHelper.getCurrentLibrary(as400);

            if (currentLibrary != null) {

                if (ISphereHelper.setCurrentLibrary(as400, library)) {

                    cleanUp = true;

                    String messageId = ISphereHelper.executeCommand(as400, command);

                    if (messageId != null && messageId.equals("")) {
                        return true;
                    }

                }

            }

        } finally {

            if (cleanUp) {

                ISphereHelper.setCurrentLibrary(as400, currentLibrary);

            }

        }

        return false;

    }

    public String replaceVariables(String mask) {
        return replaceVariables(mask, null);
    }

    public String replaceVariables(String mask, String outputFormat) {

        String tFormat;
        if (outputFormat == null) {
            tFormat = ""; //$NON-NLS-1$
        } else {
            tFormat = outputFormat;
        }

        try {

            mask = replace(mask, VARIABLE_SPLFNBR, Integer.toString(getFileNumber()));
            mask = replace(mask, VARIABLE_SPLF, getFile());
            mask = replace(mask, VARIABLE_JOBNBR, getJobNumber());
            mask = replace(mask, VARIABLE_JOBUSR, getJobUser());
            mask = replace(mask, VARIABLE_JOBNAME, getJobName());
            mask = replace(mask, VARIABLE_JOBSYS, getJobSystem());
            mask = replace(mask, VARIABLE_STMFDIR, ISPHERE_IFS_TMP_DIRECTORY);
            mask = replace(mask, VARIABLE_STMF, getTemporaryName(tFormat));
            mask = replace(mask, VARIABLE_CODPAG, "1252"); //$NON-NLS-1$
            mask = replace(mask, VARIABLE_FMT, tFormat); //$NON-NLS-1$

            mask = replace(mask, VARIABLE_STATUS, getStatus());
            mask = replace(mask, VARIABLE_CTIME_STAMP, getCreationTimestampFormatted());
            mask = replace(mask, VARIABLE_CDATE, getCreationDateFormatted());
            mask = replace(mask, VARIABLE_CTIME, getCreationTimeFormatted());
            mask = replace(mask, VARIABLE_USRDTA, getUserData());

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Could not replace spooled file variable ***", e);
            return getFile();
        }

        return mask;
    }

    private String replace(String mask, String variable, String value) {
        return mask.replaceAll(variable, value.replaceAll("\\$", "\\\\\\$"));
    }

    private boolean transformSpooledFile(String format, String target) throws Exception {

        ISpooledFileTransformer transformer = null;

        if (IPreferences.OUTPUT_FORMAT_TEXT.equals(format)) {
            transformer = new SpooledFileTransformerText(connectionName, getToolboxSpooledFile());
        } else if (IPreferences.OUTPUT_FORMAT_HTML.equals(format)) {
            transformer = new SpooledFileTransformerHTML(connectionName, getToolboxSpooledFile());
        } else if (IPreferences.OUTPUT_FORMAT_PDF.equals(format)) {
            transformer = new SpooledFileTransformerPDF(connectionName, getToolboxSpooledFile());
        } else {
            return false;
        }

        return transformer.transformSpooledFile(target);

    }

    private boolean uploadStreamFile(String source, String target) throws Exception {

        IFSFileInputStream in = null;
        FileOutputStream out = null;

        boolean cleanUp = false;

        try {

            in = new IFSFileInputStream(as400, source);
            out = new FileOutputStream(new File(target));

            // byte[] buffer = new byte[8 * 1024];
            byte[] buffer = new byte[Buffer.size("8k")];
            int count = 0;
            do {
                count = in.read(buffer, 0, buffer.length);
                if (count > 0) {
                    // byte[] converted = new String(buffer, 0, count,
                    // "Cp1252").getBytes();
                    // out.write(converted, 0, converted.length);
                    out.write(buffer, 0, count);
                }
            } while (count != -1);

            cleanUp = true;
        } finally {

            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }

        }

        return cleanUp;
    }

    private void deleteStreamFile(String streamFile) throws Exception {
        IFSFile file = new IFSFile(as400, streamFile);
        if (file.exists()) {
            file.delete();
        }
    }

    public String getAbsoluteName() {
        return "Spooled_File/" + getAbsoluteNameInternal("/");
    }

    public String getToolTip(String format) {

        return "iSphereSpooledFiles/" + getTemporaryName(format); //$NON-NLS-1$
    }

    public String getQualifiedName() {
        return getAbsoluteNameInternal("_");
    }

    public String getTemporaryName(String format) {

        String fileExtension = "";
        if (format.equals(IPreferences.OUTPUT_FORMAT_TEXT)) {
            fileExtension = ".txt";
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_HTML)) {
            fileExtension = ".html";
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_PDF)) {
            fileExtension = ".pdf";
        }

        return ISPHERE_SPOOLED_FILE_NAME_PREFIX + getAbsoluteNameInternal("_") + fileExtension;
    }

    private String getAbsoluteNameInternal(String delimiter) {
        return file + delimiter + fileNumber + delimiter + jobName + delimiter + jobUser + delimiter + jobNumber + delimiter + jobSystem + delimiter
            + getCreationDate() + delimiter + getCreationTime();
    }

    public String save(Shell shell, String format) {

        String fileName = replaceVariables(Preferences.getInstance().getSuggestedSpooledFileName(), format);
        String fileDescription = "";
        String fileExtension = "";
        if (format.equals(IPreferences.OUTPUT_FORMAT_TEXT)) {
            fileDescription = "Text Files (*.txt)";
            fileExtension = "*.txt";
            fileName = fileName + ".txt";
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_HTML)) {
            fileDescription = "HTML Files (*.html)";
            fileExtension = "*.html";
            fileName = fileName + ".html";
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_PDF)) {
            fileDescription = "PDF Files (*.pdf)";
            fileExtension = "*.pdf";
            fileName = fileName + ".pdf";
        }

        WidgetFactoryContributionsHandler factory = new WidgetFactoryContributionsHandler();
        IFileDialog dialog = factory.getFileDialog(shell, SWT.SAVE);

        dialog.setFilterNames(new String[] { fileDescription, "All Files (*.*)" });
        dialog.setFilterExtensions(new String[] { fileExtension, "*.*" });
        dialog.setFilterPath(getSaveDirectory());
        dialog.setFileName(fileName);
        // Xystem.out.println(fileName);
        dialog.setOverwrite(true);
        String file = dialog.open();

        if (file != null) {
            // Xystem.out.println(file);
            storeSaveDirectory(file);

            String source = ISPHERE_IFS_TMP_DIRECTORY + IBMI_FILE_SEPARATOR + getTemporaryName(format);
            String target = file;

            boolean doTransformSpooledFile = doTransformSpooledFile(format);
            boolean hasSpooledFile = false;

            try {

                if (doTransformSpooledFile) {
                    hasSpooledFile = transformSpooledFile(format, target);
                } else {
                    if (createStreamFile(format)) {
                        hasSpooledFile = uploadStreamFile(source, target);
                    }
                }

                if (!hasSpooledFile) {
                    return Messages.Could_not_create_stream_file_for_spooled_file_on_host;
                }

            } catch (Exception e) {
                return e.getMessage();
            } finally {

                if (hasSpooledFile) {
                    try {
                        deleteStreamFile(source);
                    } catch (Exception e) {
                        return e.getMessage();
                    }
                }

            }

        }

        return null;

    }

    private String getSaveDirectory() {
        return Preferences.getInstance().getSpooledFileSaveDirectory();
    }

    private void storeSaveDirectory(String file) {
        String directory = new File(file).getParent();
        Preferences.getInstance().setSpooledFileSaveDirectory(directory);
    }

    @Override
    public int hashCode() {
        /*
         * Siehe: http://www.ibm.com/developerworks/library/j-jtp05273/
         */
        int hash = 5;
        hash = hash * 19 + getFile().hashCode();
        hash = hash * 19 + getFileNumber();
        hash = hash * 19 + getJobName().hashCode();
        hash = hash * 19 + getJobUser().hashCode();
        hash = hash * 19 + getJobNumber().hashCode();
        hash = hash * 19 + getJobSystem().hashCode();

        return hash;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        SpooledFile other = (SpooledFile)obj;
        // @formatter:off
        if (!getFile().equals(other.getFile()) 
            || !(getFileNumber() == other.getFileNumber()) 
            || !getJobName().equals(other.getJobName())
            || !getJobUser().equals(other.getJobUser())
            || !getJobNumber().equals(other.getJobNumber()) 
            || !getJobSystem().equals(other.getJobSystem())) {
            return false;
        }
        // @formatter:on

        return true;
    }
}
