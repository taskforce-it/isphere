/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.tn5250j.core.Messages;
import biz.isphere.tn5250j.core.session.ISession;
import biz.isphere.tn5250j.core.session.Session;
import biz.isphere.tn5250j.core.tn5250jeditor.TN5250JEditorInput;
import biz.isphere.tn5250j.core.tn5250jpart.DisplaySession;
import biz.isphere.tn5250j.core.tn5250jpart.ITN5250JPart;
import biz.isphere.tn5250j.rse.TN5250JRSEPlugin;
import biz.isphere.tn5250j.rse.designereditor.DesignerEditor;
import biz.isphere.tn5250j.rse.designerpart.DesignerInfo;
import biz.isphere.tn5250j.rse.designerview.DesignerView;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.Job;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.etools.iseries.services.qsys.api.IQSYSJob;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteSourceMember;

public class DesignerOpenWithAction implements IObjectActionDelegate {

    // private Shell shell;

    private QSYSRemoteSourceMember[] selectedMembers;
    private List<QSYSRemoteSourceMember> selectedMembersList;

    public DesignerOpenWithAction() {
        selectedMembersList = new ArrayList<QSYSRemoteSourceMember>();
    }

    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {
        // shell = workbenchPart.getSite().getShell();
    }

    public void run(IAction arg0) {
        for (int idx = 0; idx < selectedMembers.length; idx++) {
            startDesigner(selectedMembers[idx].getConnection(), selectedMembers[idx], getMode());
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        selectedMembers = getMembersFromSelection((IStructuredSelection)selection);
        if (selectedMembers.length >= 1) {
            action.setEnabled(true);
        } else {
            action.setEnabled(false);
        }
    }

    private QSYSRemoteSourceMember[] getMembersFromSelection(IStructuredSelection structuredSelection) {

        selectedMembersList.clear();

        try {
            if (structuredSelection != null && structuredSelection.size() > 0) {
                Object[] objects = structuredSelection.toArray();
                for (Object object : objects) {
                    if (object instanceof QSYSRemoteSourceMember) {
                        selectedMembersList.add((QSYSRemoteSourceMember)object);
                    }
                }
            }
        } catch (Exception e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
        }

        return selectedMembersList.toArray(new QSYSRemoteSourceMember[selectedMembersList.size()]);
    }

    private void startDesigner(IBMiConnection ibmiConnection, IQSYSMember member, String mode) {
        try {
            AS400 as400 = ibmiConnection.getAS400ToolboxObject();
            IQSYSJob iseriesJob = ibmiConnection.getServerJob(null);
            Job job = new Job(as400, iseriesJob.getJobName(), iseriesJob.getUserName(), iseriesJob.getJobNumber());
            String stringCurrentLibrary = "*CRTDFT";
            String stringLibraryList = "";
            if (job.getCurrentLibraryExistence()) {
                stringCurrentLibrary = job.getCurrentLibrary();
            }
            String[] user = job.getUserLibraryList();
            for (int y = 0; y < user.length; y++) {
                stringLibraryList = stringLibraryList + " " + user[y];
            }

            String sessionDirectory = TN5250JRSEPlugin.getRSESessionDirectory(ibmiConnection.getProfileName(), ibmiConnection.getConnectionName());
            String connection = ibmiConnection.getProfileName() + "-" + ibmiConnection.getConnectionName();
            String name = ISession.DESIGNER;

            Session session = Session.load(sessionDirectory, connection, name);
            if (session != null) {

                String area = session.getArea();

                ITN5250JPart tn5250jPart = null;

                if (ISession.AREA_VIEW.equals(area)) {

                    tn5250jPart = (ITN5250JPart)(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(DesignerView.ID));

                } else if (ISession.AREA_EDITOR.equals(area)) {

                    TN5250JEditorInput editorInput = new TN5250JEditorInput(DesignerEditor.ID, Messages.iSphere_5250_Designer, "TN5250J",
                        TN5250JRSEPlugin.getDefault().getImageRegistry().get(TN5250JRSEPlugin.IMAGE_TN5250J));

                    tn5250jPart = (ITN5250JPart)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .openEditor(editorInput, DesignerEditor.ID);

                }

                if (tn5250jPart != null) {

                    DesignerInfo designerInfo = new DesignerInfo(tn5250jPart);
                    designerInfo.setRSEProfil(ibmiConnection.getProfileName());
                    designerInfo.setRSEConnection(ibmiConnection.getConnectionName());
                    designerInfo.setSession(ISession.DESIGNER);
                    designerInfo.setLibrary(member.getLibrary());
                    designerInfo.setSourceFile(member.getFile());
                    designerInfo.setMember(member.getName());
                    String editor = "*SEU";
                    if (member.getType().equals("DSPF")) {
                        editor = "*SDA";
                    } else if (member.getType().equals("PRTF")) {
                        editor = "*RLU";
                    }
                    designerInfo.setEditor(editor);
                    designerInfo.setMode(mode);
                    designerInfo.setCurrentLibrary(stringCurrentLibrary);
                    designerInfo.setLibraryList(stringLibraryList);

                    DisplaySession.run(designerInfo);

                }

            }

        } catch (SystemMessageException e2) {
        } catch (AS400SecurityException e) {
        } catch (ErrorCompletingRequestException e) {
        } catch (InterruptedException e) {
        } catch (IOException e) {
        } catch (ObjectDoesNotExistException e) {
        } catch (PartInitException e) {
        }
    }

    protected String getMode() {
        return "*EDIT";
    }

}
