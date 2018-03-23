/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse.compareeditor;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.compareeditor.CompareDialog;
import biz.isphere.core.internal.Member;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.internal.RSEMember;

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.ui.widgets.ISeriesConnectionCombo;
import com.ibm.etools.iseries.core.ui.widgets.ISeriesMemberPrompt;

public class RSECompareDialog extends CompareDialog {

    private Group ancestorGroup;
    private RSEMember rseLeftMember;

    private ISeriesConnectionCombo rightConnectionCombo;
    private ISeriesMemberPrompt rightMemberPrompt;
    private ISeriesConnection rightConnection;
    private String rightLibrary;
    private String rightFile;
    private String rightMember;

    private ISeriesConnectionCombo ancestorConnectionCombo;
    private ISeriesMemberPrompt ancestorMemberPrompt;
    private ISeriesConnection ancestorConnection;
    private String ancestorLibrary;
    private String ancestorFile;
    private String ancestorMember;

    public RSECompareDialog(Shell parentShell, boolean selectEditable, RSEMember leftMember, RSEMember rightMember, RSEMember ancestorMember) {
        super(parentShell, selectEditable, leftMember, rightMember, ancestorMember);
        this.rseLeftMember = leftMember;
        initializeRightMember(rightMember);
    }

    public RSECompareDialog(Shell parentShell, boolean selectEditable, RSEMember leftMember, RSEMember rightMember) {
        super(parentShell, selectEditable, leftMember, rightMember);
        this.rseLeftMember = leftMember;
        initializeRightMember(rightMember);
    }

    public RSECompareDialog(Shell parentShell, boolean selectEditable, RSEMember leftMember) {
        super(parentShell, selectEditable, leftMember);
        this.rseLeftMember = leftMember;
    }

    private void initializeRightMember(RSEMember rightMember) {
        this.rightConnection = rightMember.getRSEConnection();
        this.rightLibrary = rightMember.getLibrary();
        this.rightFile = rightMember.getSourceFile();
        this.rightMember = rightMember.getMember();
    }

    @Override
    public void createRightArea(Composite parent) {

        Group rightGroup = new Group(parent, SWT.NONE);
        rightGroup.setText(Messages.Right);
        GridLayout rightLayout = new GridLayout();
        rightLayout.numColumns = 1;
        rightGroup.setLayout(rightLayout);
        rightGroup.setLayoutData(getGridData());

        rightConnectionCombo = new ISeriesConnectionCombo(rightGroup, rseLeftMember.getRSEConnection(), false);
        rightConnectionCombo.setLayoutData(getGridData());
        rightConnectionCombo.getCombo().setLayoutData(getGridData());

        rightConnectionCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getOkButton().setEnabled(canFinish());
                rightMemberPrompt.setSystemConnection(rightConnectionCombo.getSystemConnection());
            }
        });

        rightMemberPrompt = new ISeriesMemberPrompt(rightGroup, SWT.NONE, false, true, ISeriesMemberPrompt.FILETYPE_SRC);
        rightMemberPrompt.setSystemConnection(rightConnectionCombo.getSystemConnection());
        rightMemberPrompt.setLibraryName(rseLeftMember.getLibrary());
        rightMemberPrompt.setFileName(rseLeftMember.getSourceFile());
        rightMemberPrompt.setMemberName(rseLeftMember.getMember());

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getOkButton().setEnabled(canFinish());
            }
        };

        rightMemberPrompt.getMemberCombo().addModifyListener(modifyListener);
        rightMemberPrompt.getFileCombo().addModifyListener(modifyListener);
        rightMemberPrompt.getLibraryCombo().addModifyListener(modifyListener);
        rightMemberPrompt.getLibraryCombo().setFocus();
    }

    @Override
    public void createAncestorArea(Composite parent) {

        ancestorGroup = new Group(parent, SWT.NONE);
        ancestorGroup.setText(Messages.Ancestor);
        GridLayout ancestorLayout = new GridLayout();
        ancestorLayout.numColumns = 1;
        ancestorGroup.setLayout(ancestorLayout);
        ancestorGroup.setLayoutData(getGridData());

        ancestorConnectionCombo = new ISeriesConnectionCombo(ancestorGroup, rseLeftMember.getRSEConnection(), false);
        ancestorConnectionCombo.setLayoutData(getGridData());
        ancestorConnectionCombo.getCombo().setLayoutData(getGridData());

        ancestorConnectionCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getOkButton().setEnabled(canFinish());
                ancestorMemberPrompt.setSystemConnection(ancestorConnectionCombo.getSystemConnection());
            }
        });

        ancestorMemberPrompt = new ISeriesMemberPrompt(ancestorGroup, SWT.NONE, false, true, ISeriesMemberPrompt.FILETYPE_SRC);
        ancestorMemberPrompt.setSystemConnection(ancestorConnectionCombo.getSystemConnection());
        ancestorMemberPrompt.setLibraryName(rseLeftMember.getLibrary());
        ancestorMemberPrompt.setFileName(rseLeftMember.getSourceFile());
        ancestorMemberPrompt.setMemberName(rseLeftMember.getMember());

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getOkButton().setEnabled(canFinish());
            }
        };

        ancestorMemberPrompt.getMemberCombo().addModifyListener(modifyListener);
        ancestorMemberPrompt.getFileCombo().addModifyListener(modifyListener);
        ancestorMemberPrompt.getLibraryCombo().addModifyListener(modifyListener);
        ancestorMemberPrompt.getLibraryCombo().setFocus();

    }

    @Override
    protected void setAncestorVisible(boolean visible) {
        ancestorGroup.setVisible(visible);
    }

    @Override
    protected void okPressed() {
        if (!isDefined()) {
            rightConnection = ISeriesConnection.getConnection(rightConnectionCombo.getSystemConnection());
            rightLibrary = rightMemberPrompt.getLibraryName();
            rightFile = rightMemberPrompt.getFileName();
            rightMember = rightMemberPrompt.getMemberName();

            if (!getRightRSEMember().exists()) {
                String message = biz.isphere.core.Messages.bind(biz.isphere.core.Messages.Member_2_file_1_in_library_0_not_found, new Object[] {
                    rightLibrary, rightFile, rightMember });
                MessageDialog.openError(getShell(), biz.isphere.core.Messages.Error, message);
                rightMemberPrompt.getMemberCombo().setFocus();
                return;
            }

            if (isThreeWay()) {
                ancestorConnection = ISeriesConnection.getConnection(ancestorConnectionCombo.getSystemConnection());
                ancestorLibrary = ancestorMemberPrompt.getLibraryName();
                ancestorFile = ancestorMemberPrompt.getFileName();
                ancestorMember = ancestorMemberPrompt.getMemberName();
            }
        }

        // Close dialog
        super.okPressed();
    }

    @Override
    public boolean canFinish() {
        if (isThreeWay()) {
            if (rightMemberPrompt.getMemberName() == null || rightMemberPrompt.getMemberName().trim().length() == 0
                || rightMemberPrompt.getFileName() == null || rightMemberPrompt.getFileName().trim().length() == 0
                || rightMemberPrompt.getLibraryName() == null || rightMemberPrompt.getLibraryName().trim().length() == 0
                || ancestorMemberPrompt.getMemberName() == null || ancestorMemberPrompt.getMemberName().trim().length() == 0
                || ancestorMemberPrompt.getFileName() == null || ancestorMemberPrompt.getFileName().trim().length() == 0
                || ancestorMemberPrompt.getLibraryName() == null || ancestorMemberPrompt.getLibraryName().trim().length() == 0) return false;
            if (rightMemberPrompt.getMemberName().equalsIgnoreCase(ancestorMemberPrompt.getMemberName())
                && rightMemberPrompt.getFileName().equalsIgnoreCase(ancestorMemberPrompt.getFileName())
                && rightMemberPrompt.getLibraryName().equalsIgnoreCase(ancestorMemberPrompt.getLibraryName())
                && rightConnectionCombo.getSystemConnection().getHostName().equals(ancestorConnectionCombo.getSystemConnection().getHostName()))
                return false;
            if (rightMemberPrompt.getLibraryName().equalsIgnoreCase(rseLeftMember.getLibrary())
                && rightMemberPrompt.getFileName().equalsIgnoreCase(rseLeftMember.getSourceFile())
                && rightMemberPrompt.getMemberName().equalsIgnoreCase(rseLeftMember.getMember())
                && rightConnectionCombo.getSystemConnection().getHostName().equals(rseLeftMember.getRSEConnection().getHostName())) return false;
            if (ancestorMemberPrompt.getLibraryName().equalsIgnoreCase(rseLeftMember.getLibrary())
                && ancestorMemberPrompt.getFileName().equalsIgnoreCase(rseLeftMember.getSourceFile())
                && ancestorMemberPrompt.getMemberName().equalsIgnoreCase(rseLeftMember.getMember())
                && ancestorConnectionCombo.getSystemConnection().getHostName().equals(rseLeftMember.getRSEConnection().getHostName())) return false;
        } else {
            if (rightMemberPrompt.getMemberName() == null || rightMemberPrompt.getMemberName().trim().length() == 0) return false;
            if (rightMemberPrompt.getMemberName().equalsIgnoreCase(rseLeftMember.getMember())
                && rightMemberPrompt.getFileName().equalsIgnoreCase(rseLeftMember.getSourceFile())
                && rightMemberPrompt.getLibraryName().equalsIgnoreCase(rseLeftMember.getLibrary())
                && rightConnectionCombo.getSystemConnection().getHostName().equalsIgnoreCase(rseLeftMember.getRSEConnection().getHostName()))
                return false;
        }
        return true;
    }

    public RSEMember getLeftRSEMember() {
        return rseLeftMember;
    }

    public RSEMember getRightRSEMember() {
        try {
            return new RSEMember(rightConnection.getISeriesMember(getShell(), rightLibrary, rightFile, rightMember));
        } catch (Exception e) {
            return null;
        }
    }

    public ISeriesConnection getRightConnection() {
        return rightConnection;
    }

    public String getRightLibrary() {
        return rightLibrary;
    }

    public String getRightFile() {
        return rightFile;
    }

    public String getRightMember() {
        return rightMember;
    }

    public ISeriesConnection getAncestorConnection() {
        return ancestorConnection;
    }

    public String getAncestorLibrary() {
        return ancestorLibrary;
    }

    public String getAncestorFile() {
        return ancestorFile;
    }

    public String getAncestorMember() {
        return ancestorMember;
    }

    @Override
    protected void switchLeftAndRightMember(Member leftMember, Member rightMember) {
        super.switchLeftAndRightMember(leftMember, rightMember);
        initializeRightMember((RSEMember)leftMember);
        this.rseLeftMember = (RSEMember)rightMember;
    }

    /**
     * Overriden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISphereRSEPlugin.getDefault().getDialogSettings());
    }

}
