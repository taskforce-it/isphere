/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import biz.isphere.base.internal.ClipboardHelper;
import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.retrievebindersource.RetrieveBinderSourceDialog;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400File;
import com.ibm.as400.access.AS400FileRecordDescription;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.Record;
import com.ibm.as400.access.RecordFormat;
import com.ibm.as400.access.SequentialFile;
import com.ibm.etools.iseries.core.ISeriesSystemPlugin;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.api.ISeriesMessage;
import com.ibm.etools.iseries.core.api.ISeriesObject;
import com.ibm.etools.iseries.core.descriptors.ISeriesDataElementDescriptorType;
import com.ibm.etools.iseries.core.dstore.common.ISeriesDataElementHelpers;
import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.systems.core.messages.SystemMessageException;
import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.actions.ISystemDynamicPopupMenuExtension;
import com.ibm.etools.systems.dstore.core.model.DataElement;

public class RetrieveBinderSourceAction extends ISeriesSystemBaseAction implements ISystemDynamicPopupMenuExtension {

    /** Record field */
    public static final int SRCSEQ_INDEX = 0;
    /** Record field */
    public static final int SRCDAT_INDEX = 1;
    /** Record field */
    public static final int SRCDTA_INDEX = 2;

    private static final String NEW_LINE = "\n";

    private static final String SINGLE_QUOTE = "'"; //$NON-NLS-1$

    protected ArrayList arrayListSelection;

    private static final String RESID_MSGTYPE_ESCAPE = ISeriesSystemPlugin.getString("com.ibm.etools.systems.as400.ui.msgType.escape");

    public RetrieveBinderSourceAction() {
        super(Messages.iSphere_Retrieve_Binder_Source, "", null);
        arrayListSelection = new ArrayList();
        setContextMenuGroup("group.generate");
        allowOnMultipleSelection(false);
        setHelp("");
        setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_RETRIEVE_BINDER_SOURCE));
    }

    public void populateMenu(Shell shell, SystemMenuManager menu, IStructuredSelection selection, String menuGroup) {
        setShell(shell);
        menu.add("group.generate", this);
    }

    public boolean supportsSelection(IStructuredSelection selection) {

        this.arrayListSelection.clear();

        ArrayList<DataElement> arrayListSelection = new ArrayList<DataElement>();

        for (Iterator iterSelection = selection.iterator(); iterSelection.hasNext();) {
            Object objSelection = iterSelection.next();
            if (objSelection instanceof DataElement) {
                DataElement dataElement = (DataElement)objSelection;
                ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
                if (type.isObject()) {
                    String strType = ISeriesDataElementHelpers.getType(dataElement);
                    if (strType.equalsIgnoreCase(ISeries.SRVPGM)) {
                        arrayListSelection.add(dataElement);
                    }
                }
            }
        }

        if (arrayListSelection.isEmpty()) {
            return false;
        }

        this.arrayListSelection = arrayListSelection;
        return true;

    }

    @Override
    public void run() {

        if (arrayListSelection.size() > 0) {

            for (Iterator iterObjects = arrayListSelection.iterator(); iterObjects.hasNext();) {

                DataElement dataElement = (DataElement)iterObjects.next();

                ISeriesObject qsysRemoteObject = new ISeriesObject(dataElement);

                if (qsysRemoteObject != null) {

                    String serviceProgramConnectionName = qsysRemoteObject.getISeriesConnection().getSystemConnection().getAliasName();
                    String serviceProgram = qsysRemoteObject.getName();
                    String serviceProgramLibrary = qsysRemoteObject.getLibrary();
                    String sourceMemberDescription = qsysRemoteObject.getDescription();

                    RetrieveBinderSourceDialog dialog = new RetrieveBinderSourceDialog(getShell());
                    dialog.setConnectionName(serviceProgramConnectionName);
                    dialog.setSourceMember(serviceProgram);
                    dialog.setSourceMemberDescription(sourceMemberDescription);
                    if (dialog.open() == Dialog.OK) {
                        String sourceFileConnectionName = dialog.getConnectionName();
                        String sourceFileLibrary = dialog.getSourceFileLibrary();
                        String sourceFile = dialog.getSourceFile();
                        String sourceMember = dialog.getSourceMember();
                        boolean copyToClipboard = dialog.isCopyToClipboard();
                        sourceMemberDescription = dialog.getSourceMemberDescription();

                        ISeriesConnection serviceProgramConnection = ISeriesConnection.getConnection(serviceProgramConnectionName);

                        if (copyToClipboard) {
                            performRetrieveBinderSourceToClipboard(serviceProgramConnection, serviceProgramLibrary, serviceProgram,
                                sourceFileLibrary, sourceFile, sourceMember);
                        } else {
                            ISeriesConnection sourceFileConnection = ISeriesConnection.getConnection(sourceFileConnectionName);
                            performRetrieveBinderSourceToSourceMember(serviceProgramConnection, serviceProgramLibrary, serviceProgram,
                                sourceFileConnection, sourceFileLibrary, sourceFile, sourceMember, sourceMemberDescription);
                        }
                    }
                }
            }
        }
    }

    private void performRetrieveBinderSourceToClipboard(final ISeriesConnection connection, final String library, final String serviceProgram,
        final String sourceLibrary, final String sourceFile, final String sourceMember) {

        final StringBuilder binderSource = new StringBuilder();

        IRunnableWithProgress job = new IRunnableWithProgress() {

            public void run(IProgressMonitor arg0) throws InvocationTargetException, InterruptedException {

                try {

                    SequentialFile file = getSequentialFile(sourceLibrary, sourceFile, sourceMember, connection);

                    if (!retrieveBinderSourceToSourceMember(file, library, serviceProgram, sourceLibrary, sourceFile, sourceMember)) {
                        return;
                    }

                    String[] binderSourceLines = download(file);

                    for (String line : binderSourceLines) {
                        if (binderSource.length() > 0) {
                            binderSource.append(NEW_LINE);
                        }
                        binderSource.append(line);
                    }

                    StringBuilder clCommand;
                    String errorMessage;

                    clCommand = new StringBuilder();
                    clCommand.append("RMVM FILE("); //$NON-NLS-1$
                    clCommand.append(sourceLibrary);
                    clCommand.append("/"); //$NON-NLS-1$
                    clCommand.append(sourceFile);
                    clCommand.append(") MBR("); //$NON-NLS-1$
                    clCommand.append(sourceMember);
                    clCommand.append(")"); //$NON-NLS-1$

                    errorMessage = executeCommand(file, clCommand.toString());
                    if (errorMessage != null) {
                        MessageDialogAsync.displayError(getShell(), errorMessage);
                        return;
                    }

                } catch (Exception e) {
                    MessageDialogAsync.displayError(getShell(), ExceptionHelper.getLocalizedMessage(e));
                }
            }

        };

        try {
            IProgressService service = PlatformUI.getWorkbench().getProgressService();
            service.busyCursorWhile(job);
        } catch (Exception e) {
            // ignore InterruptedException
            // ignore InvocationTargetException
        }

        ClipboardHelper.setText(binderSource.toString());
    }

    private void performRetrieveBinderSourceToSourceMember(final ISeriesConnection connection, final String library, final String serviceProgram,
        final ISeriesConnection sourceFileConnection, final String sourceLibrary, final String sourceFile, final String sourceMember,
        final String description) {

        IRunnableWithProgress job = new IRunnableWithProgress() {

            public void run(IProgressMonitor arg0) throws InvocationTargetException, InterruptedException {

                try {

                    SequentialFile file = getSequentialFile(sourceLibrary, sourceFile, sourceMember, connection);

                    if (!retrieveBinderSourceToSourceMember(file, library, serviceProgram, sourceLibrary, sourceFile, sourceMember)) {
                        return;
                    }

                    String tDescription;
                    if (!description.startsWith(SINGLE_QUOTE)) {
                        tDescription = SINGLE_QUOTE + description + SINGLE_QUOTE;
                    } else {
                        tDescription = description;
                    }

                    StringBuilder clCommand;
                    String errorMessage;

                    clCommand = new StringBuilder();
                    clCommand.append("CHGPFM FILE("); //$NON-NLS-1$
                    clCommand.append(sourceLibrary);
                    clCommand.append("/"); //$NON-NLS-1$
                    clCommand.append(sourceFile);
                    clCommand.append(") MBR("); //$NON-NLS-1$
                    clCommand.append(sourceMember);
                    clCommand.append(") SRCTYPE("); //$NON-NLS-1$
                    clCommand.append("BND"); //$NON-NLS-1$
                    clCommand.append(") TEXT("); //$NON-NLS-1$
                    clCommand.append(tDescription);
                    clCommand.append(")"); //$NON-NLS-1$

                    errorMessage = executeCommand(file, clCommand.toString());
                    if (errorMessage != null) {
                        MessageDialogAsync.displayError(getShell(), errorMessage);
                        return;
                    }

                } catch (Exception e) {
                    MessageDialogAsync.displayError(getShell(), ExceptionHelper.getLocalizedMessage(e));
                }

            }
        };

        try {
            IProgressService service = PlatformUI.getWorkbench().getProgressService();
            service.busyCursorWhile(job);
        } catch (Exception e) {
            // ignore InterruptedException
            // ignore InvocationTargetException
        }
    }

    private boolean retrieveBinderSourceToSourceMember(SequentialFile connection, String library, String serviceProgram, String sourceLibrary,
        String sourceFile, String sourceMember) throws SystemMessageException {

        StringBuilder clCommand;
        String errorMessage;

        clCommand = new StringBuilder();
        clCommand.append("RTVBNDSRC SRVPGM("); //$NON-NLS-1$
        clCommand.append(library);
        clCommand.append("/"); //$NON-NLS-1$
        clCommand.append(serviceProgram);
        clCommand.append(") SRCFILE("); //$NON-NLS-1$
        clCommand.append(sourceLibrary);
        clCommand.append("/"); //$NON-NLS-1$
        clCommand.append(sourceFile);
        clCommand.append(") SRCMBR("); //$NON-NLS-1$
        clCommand.append(sourceMember);
        clCommand.append(") MBROPT(*REPLACE)"); //$NON-NLS-1$

        errorMessage = executeCommand(connection, clCommand.toString());
        if (errorMessage != null) {
            MessageDialogAsync.displayError(getShell(), errorMessage);
            return false;
        }

        return true;
    }

    private SequentialFile getSequentialFile(String sourceLibrary, String sourceFile, String sourceMember, ISeriesConnection connection)
        throws SystemMessageException {

        AS400 system = connection.getAS400ToolboxObject(null);
        SequentialFile file = new SequentialFile(system, new QSYSObjectPathName(sourceLibrary, sourceFile, sourceMember, "MBR").getPath());

        return file;
    }

    private String[] download(AS400File as400File) throws Exception {

        List<String> sourceLines = new LinkedList<String>();

        AS400FileRecordDescription recordDescription = new AS400FileRecordDescription(as400File.getSystem(), as400File.getPath());

        try {

            RecordFormat[] format = recordDescription.retrieveRecordFormat();

            as400File.setRecordFormat(format[0]);
            as400File.open(AS400File.READ_ONLY, 1000, AS400File.COMMIT_LOCK_LEVEL_NONE);

            Record record = as400File.readNext();
            while (record != null) {
                String sourceData = (String)record.getField(SRCDTA_INDEX);
                sourceLines.add(sourceData);
                record = as400File.readNext();
            }

            as400File.close();

        } finally {
            try {
                as400File.close();
            } catch (Throwable e) {
                MessageDialogAsync.displayError(getShell(), ExceptionHelper.getLocalizedMessage(e));
            }
        }

        return sourceLines.toArray(new String[sourceLines.size()]);
    }

    private String executeCommand(SequentialFile connection, String clCommand) {

        try {

            boolean isError = false;
            StringBuilder messageText = new StringBuilder();

            Object[] messages = connection.runCommand(clCommand);
            for (Object object : messages) {
                if (object instanceof ISeriesMessage) {
                    ISeriesMessage message = (ISeriesMessage)object;
                    if (messageText.length() > 0) {
                        messageText.append(NEW_LINE); //$NON-NLS-N$
                    }
                    messageText.append(message.getClass() + ": " + message.getMessageText());
                    if (RESID_MSGTYPE_ESCAPE.equals(message.getMessageType())) {
                        isError = true;
                    }
                }
            }

            if (isError) {
                return messageText.toString();
            }

            return null;

        } catch (Exception e) {
            return ExceptionHelper.getLocalizedMessage(e);
        }
    }
}
