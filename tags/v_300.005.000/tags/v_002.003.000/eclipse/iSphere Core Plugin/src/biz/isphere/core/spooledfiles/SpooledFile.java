/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.BrowserEditorInput;
import biz.isphere.core.internal.ISphereHelper;
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

    private AS400 as400;

    private String file;

    private int fileNumber;

    private String jobName;

    private String jobUser;

    private String jobNumber;

    private String jobSystem;

    private String creationDate;

    /* Lazy loaded and cached! Do not access directly */
    private String creationDateFormatted;

    private String creationTime;

    /* Lazy loaded and cached! Do not access directly */
    private String creationTimeFormatted;

    private String status;

    private String outputQueue;

    private String outputQueueLibrary;

    private String outputPriority;

    private String userData;

    private String formType;

    private int copies;

    private int pages;

    private int currentPage;

    private Date creationTimestamp;

    private Object data;

    private com.ibm.as400.access.SpooledFile toolboxSpooledFile;

    public SpooledFile() {
        as400 = null;
        file = "";
        fileNumber = 0;
        jobName = "";
        jobUser = "";
        jobNumber = "";
        jobSystem = "";
        creationDate = "";
        creationDateFormatted = null;
        creationTime = "";
        creationTimeFormatted = null;
        status = "";
        outputQueue = "";
        outputQueueLibrary = "";
        outputPriority = "";
        userData = "";
        formType = "";
        copies = 0;
        pages = 0;
        currentPage = 0;
        creationTimestamp = null;
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

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
        creationDateFormatted = null;
    }
    
    private void setCreationDate(Date creationDate) {
        DateFormat formatter = new SimpleDateFormat("yyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(creationDate);
        if (calendar.get(Calendar.YEAR) >= 2000) {
            this.creationDate = "1" + formatter.format(creationDate);
        } else {
            this.creationDate = "0" + formatter.format(creationDate);
        }
        creationDateFormatted = null;
    }
    
    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
        creationTimeFormatted = null;
    }

    private void setCreationTime(Time creationTime) {
        DateFormat formatter = new SimpleDateFormat("HHmmss");
        this.creationTime = formatter.format(creationTime);
        creationTimeFormatted = null;
    }

    public void setCreationTimestamp(Date date, Time time) {
        this.creationTimestamp = new Date(date.getTime() + time.getTime());
        setCreationDate(date);
        setCreationTime(time);
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

    public String getOutputQueueFormated() {
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
    public String getCreationDateFormated() {
        if (creationDateFormatted == null) {
            DateFormat formatter = new SimpleDateFormat("dd.MM.yy");
            creationDateFormatted = formatter.format(creationTimestamp);
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
    public String getCreationTimeFormated() {
        if (creationTimeFormatted == null) {
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            creationTimeFormatted = formatter.format(creationTimestamp);
        }
        return creationTimeFormatted;
    }

    public String getCreationTimestampFormatted() {
        return getCreationDateFormated() + "   " + getCreationTimeFormated();
    }

    public String open(String format) {

        String source = "/tmp/" + getTemporaryName(format);
        String target = ISpherePlugin.getDefault().getSpooledFilesDirectory() + File.separator + getTemporaryName(format);

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

            if (!ISpherePlugin.getDefault().getSpooledFilesProject().isOpen()) {
                ISpherePlugin.getDefault().getSpooledFilesProject().open(null);
            }

            IFile file = ISpherePlugin.getWorkspace().getRoot().getFileForLocation(new Path(target));
            file.refreshLocal(1, null);

            if (format.equals(IPreferences.OUTPUT_FORMAT_TEXT)) {

                IWorkbenchPage page = ISpherePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
                org.eclipse.ui.ide.IDE.openEditor(page, file);

            } else if (format.equals(IPreferences.OUTPUT_FORMAT_HTML)) {

                BrowserEditorInput editorInput = new BrowserEditorInput(getTemporaryName(format), getTemporaryName(format), "iSphereSpooledFiles/"
                    + getTemporaryName(format), null, target);
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .openEditor(editorInput, "biz.isphere.core.internal.BrowserEditor");

            } else if (format.equals(IPreferences.OUTPUT_FORMAT_PDF)) {

                BrowserEditorInput editorInput = new BrowserEditorInput(getTemporaryName(format), getTemporaryName(format), "iSphereSpooledFiles/"
                    + getTemporaryName(format), null, target);
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .openEditor(editorInput, "biz.isphere.core.internal.BrowserEditor");

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

        return null;

    }

    private boolean doTransformSpooledFile(String format) {
        // TODO: Remove disabled statements 'DE.TASKFORCE'
        // IPreferenceStore store =
        // ISpherePlugin.getDefault().getPreferenceStore();
        Preferences store = Preferences.getInstance();

        boolean doTransformSpooledFile = false;
        if (format.equals(IPreferences.OUTPUT_FORMAT_TEXT)) {
            // doTransformSpooledFile =
            // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT").equals(IPreferences.SPLF_CONVERSION_TRANSFORM);
            doTransformSpooledFile = store.getSpooledFileConversionText().equals(IPreferences.SPLF_CONVERSION_TRANSFORM);
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_HTML)) {
            // doTransformSpooledFile =
            // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML").equals(IPreferences.SPLF_CONVERSION_TRANSFORM);
            doTransformSpooledFile = store.getSpooledFileConversionHTML().equals(IPreferences.SPLF_CONVERSION_TRANSFORM);
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_PDF)) {
            // doTransformSpooledFile =
            // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF").equals(IPreferences.SPLF_CONVERSION_TRANSFORM);
            doTransformSpooledFile = store.getSpooledFileConversionPDF().equals(IPreferences.SPLF_CONVERSION_TRANSFORM);
        }
        return doTransformSpooledFile;
    }

    private boolean createStreamFile(String format) throws Exception {
        // TODO: Remove disabled statements 'DE.TASKFORCE'
        // IPreferenceStore store =
        // ISpherePlugin.getDefault().getPreferenceStore();
        Preferences store = Preferences.getInstance();

        boolean _default = true;
        String conversionCommand = "";
        String conversionCommandLibrary = "";
        if (format.equals(IPreferences.OUTPUT_FORMAT_TEXT)) {
            // if
            // (store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT").equals(IPreferences.SPLF_CONVERSION_USER_DEFINED))
            // {
            if (store.getSpooledFileConversionText().equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {
                _default = false;
                // conversionCommand =
                // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.COMMAND");
                // conversionCommandLibrary =
                // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.LIBRARY");
                conversionCommand = store.getSpooledFileConversionTextCommand();
                conversionCommandLibrary = store.getSpooledFileConversionTextLibrary();
            }
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_HTML)) {
            // if
            // (store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML").equals(IPreferences.SPLF_CONVERSION_USER_DEFINED))
            // {
            if (store.getSpooledFileConversionHTML().equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {
                _default = false;
                // conversionCommand =
                // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.COMMAND");
                // conversionCommandLibrary =
                // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.LIBRARY");
                conversionCommand = store.getSpooledFileConversionHTMLCommand();
                conversionCommandLibrary = store.getSpooledFileConversionHTMLLibrary();
            }
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_PDF)) {
            // if
            // (store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF").equals(IPreferences.SPLF_CONVERSION_USER_DEFINED))
            // {
            if (store.getSpooledFileConversionPDF().equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {
                _default = false;
                // conversionCommand =
                // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.COMMAND");
                // conversionCommandLibrary =
                // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.LIBRARY");
                conversionCommand = store.getSpooledFileConversionPDFCommand();
                conversionCommandLibrary = store.getSpooledFileConversionPDFLibrary();
            }
        }

        String command = null;
        String library = null;

        if (_default) {
            command = "CVTSPLF FROMFILE(&SPLF) SPLNBR(&SPLFNBR) JOB(&JOBNBR/&JOBUSR/&JOBNAME) TOSTREAM('&STMF') TODIR('&STMFDIR') STCODPAG(&CODPAG) TOFMT(&FMT) STOPT(*REPLACE)";
            library = ISpherePlugin.getISphereLibrary();

        } else {
            command = conversionCommand;
            library = conversionCommandLibrary;
        }

        command = command.replaceAll("&SPLFNBR", Integer.toString(fileNumber));
        command = command.replaceAll("&SPLF", file);
        command = command.replaceAll("&JOBNBR", jobNumber);
        command = command.replaceAll("&JOBUSR", jobUser);
        command = command.replaceAll("&JOBNAME", jobName);
        command = command.replaceAll("&STMFDIR", "/tmp");
        command = command.replaceAll("&STMF", getTemporaryName(format));
        command = command.replaceAll("&CODPAG", "1252");
        command = command.replaceAll("&FMT", format);

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

    private boolean transformSpooledFile(String format, String target) throws Exception {

        ISpooledFileTransformer transformer = null;

        if (IPreferences.OUTPUT_FORMAT_TEXT.equals(format)) {
            transformer = new SpooledFileTransformerText(getToolboxSpooledFile());
        } else if (IPreferences.OUTPUT_FORMAT_HTML.equals(format)) {
            transformer = new SpooledFileTransformerHTML(getToolboxSpooledFile());
        } else if (IPreferences.OUTPUT_FORMAT_PDF.equals(format)) {
            transformer = new SpooledFileTransformerPDF(getToolboxSpooledFile());
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

            byte[] buffer = new byte[8 * 1024];
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

    public String getTemporaryName(String format) {

        String fileExtension = "";
        if (format.equals(IPreferences.OUTPUT_FORMAT_TEXT)) {
            fileExtension = ".txt";
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_HTML)) {
            fileExtension = ".html";
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_PDF)) {
            fileExtension = ".pdf";
        }

        return "iSphere_Spooled_File_" + getAbsoluteNameInternal("_") + fileExtension;
    }

    private String getAbsoluteNameInternal(String delimiter) {
        return file + delimiter + fileNumber + delimiter + jobName + delimiter + jobUser + delimiter + jobNumber + delimiter + jobSystem + delimiter
            + getCreationDate() + delimiter + getCreationTime();
    }

    public String save(Shell shell, String format) {

        String fileDescription = "";
        String fileExtension = "";
        if (format.equals(IPreferences.OUTPUT_FORMAT_TEXT)) {
            fileDescription = "Text Files";
            fileExtension = ".txt";
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_HTML)) {
            fileDescription = "HTML Files";
            fileExtension = ".html";
        } else if (format.equals(IPreferences.OUTPUT_FORMAT_PDF)) {
            fileDescription = "PDF Files";
            fileExtension = ".pdf";
        }

        WidgetFactoryContributionsHandler factory = new WidgetFactoryContributionsHandler();
        IFileDialog dialog = factory.getFileDialog(shell, SWT.SAVE);

        dialog.setFilterNames(new String[] { fileDescription, "All Files" });
        dialog.setFilterExtensions(new String[] { fileExtension, "*.*" });
        dialog.setFilterPath(getSaveDirectory());
        dialog.setFileName("spooled_file" + fileExtension);
        dialog.setOverwrite(true);
        String file = dialog.open();

        if (file != null) {
            storeSaveDirectory(file);

            String source = "/tmp/" + getTemporaryName(format);
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
        // TODO: Remove disabled statements 'DE.TASKFORCE'
        // IPreferenceStore store =
        // ISpherePlugin.getDefault().getPreferenceStore();
        // String file =
        // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.SAVE.DIRECTORY");
        // if (file == null || file.length() == 0) {
        // return "C:\\";
        // }
        return Preferences.getInstance().getSpooledFileSaveDirectory();
    }

    private void storeSaveDirectory(String file) {
        String directory = new File(file).getParent();
        // TODO: Remove disabled statements 'DE.TASKFORCE'
        // IPreferenceStore store =
        // ISpherePlugin.getDefault().getPreferenceStore();
        // store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.SAVE.DIRECTORY",
        // directory);
        Preferences.getInstance().setSpooledFileSaveDirectory(directory);
    }

}
