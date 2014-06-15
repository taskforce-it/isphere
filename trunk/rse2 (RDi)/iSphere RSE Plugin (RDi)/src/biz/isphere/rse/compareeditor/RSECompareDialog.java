/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.compareeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.compareeditor.CompareDialog;
import biz.isphere.core.internal.Member;
import biz.isphere.rse.Messages;
import biz.isphere.rse.internal.RSEMember;

import com.ibm.etools.iseries.rse.ui.widgets.IBMiConnectionCombo;
import com.ibm.etools.iseries.rse.ui.widgets.QSYSMemberPrompt;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class RSECompareDialog extends CompareDialog {

    private Group ancestorGroup;
    private RSEMember rseLeftMember;

    private IBMiConnectionCombo rightConnectionCombo;
    private QSYSMemberPrompt rightMemberPrompt;
    private IBMiConnection rightConnection;
    private String rightLibrary;
    private String rightFile;
    private String rightMember;

    private IBMiConnectionCombo ancestorConnectionCombo;
    private QSYSMemberPrompt ancestorMemberPrompt;
    private IBMiConnection ancestorConnection;
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
        rightGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        rightConnectionCombo = new IBMiConnectionCombo(rightGroup, rseLeftMember.getRSEConnection(), false);
        rightConnectionCombo.setLayoutData(new GridData());
        GridData gd = new GridData();
        gd.widthHint = 200;
        rightConnectionCombo.getCombo().setLayoutData(gd);

        rightConnectionCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getOkButton().setEnabled(canFinish());
                rightMemberPrompt.setSystemConnection(rightConnectionCombo.getHost());
            }
        });

        rightMemberPrompt = new QSYSMemberPrompt(rightGroup, SWT.NONE, false, true, QSYSMemberPrompt.FILETYPE_SRC);
        rightMemberPrompt.setSystemConnection(rightConnectionCombo.getHost());
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
        ancestorGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        ancestorConnectionCombo = new IBMiConnectionCombo(ancestorGroup, rseLeftMember.getRSEConnection(), false);
        ancestorConnectionCombo.setLayoutData(new GridData());
        GridData gd = new GridData();
        gd.widthHint = 200;
        ancestorConnectionCombo.getCombo().setLayoutData(gd);

        ancestorConnectionCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getOkButton().setEnabled(canFinish());
                ancestorMemberPrompt.setSystemConnection(ancestorConnectionCombo.getHost());
            }
        });

        ancestorMemberPrompt = new QSYSMemberPrompt(ancestorGroup, SWT.NONE, false, true, QSYSMemberPrompt.FILETYPE_SRC);
        ancestorMemberPrompt.setSystemConnection(ancestorConnectionCombo.getHost());
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
            rightConnection = IBMiConnection.getConnection(rightConnectionCombo.getHost());
            rightLibrary = rightMemberPrompt.getLibraryName();
            rightFile = rightMemberPrompt.getFileName();
            rightMember = rightMemberPrompt.getMemberName();
            if (isThreeWay()) {
                ancestorConnection = IBMiConnection.getConnection(ancestorConnectionCombo.getHost());
                ancestorLibrary = ancestorMemberPrompt.getLibraryName();
                ancestorFile = ancestorMemberPrompt.getFileName();
                ancestorMember = ancestorMemberPrompt.getMemberName();
            }
        }
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
                && rightConnectionCombo.getHost().getHostName().equals(ancestorConnectionCombo.getHost().getHostName())) return false;
            if (rightMemberPrompt.getLibraryName().equalsIgnoreCase(rseLeftMember.getLibrary())
                && rightMemberPrompt.getFileName().equalsIgnoreCase(rseLeftMember.getSourceFile())
                && rightMemberPrompt.getMemberName().equalsIgnoreCase(rseLeftMember.getMember())
                && rightConnectionCombo.getHost().getHostName().equals(rseLeftMember.getRSEConnection().getHostName())) return false;
            if (ancestorMemberPrompt.getLibraryName().equalsIgnoreCase(rseLeftMember.getLibrary())
                && ancestorMemberPrompt.getFileName().equalsIgnoreCase(rseLeftMember.getSourceFile())
                && ancestorMemberPrompt.getMemberName().equalsIgnoreCase(rseLeftMember.getMember())
                && ancestorConnectionCombo.getHost().getHostName().equals(rseLeftMember.getRSEConnection().getHostName())) return false;
        } else {
            if (rightMemberPrompt.getMemberName() == null || rightMemberPrompt.getMemberName().trim().length() == 0) return false;
            if (rightMemberPrompt.getMemberName().equalsIgnoreCase(rseLeftMember.getMember())
                && rightMemberPrompt.getFileName().equalsIgnoreCase(rseLeftMember.getSourceFile())
                && rightMemberPrompt.getLibraryName().equalsIgnoreCase(rseLeftMember.getLibrary())
                && rightConnectionCombo.getHost().getHostName().equalsIgnoreCase(rseLeftMember.getRSEConnection().getHostName())) return false;
        }
        return true;
    }

    public RSEMember getLeftRSEMember() {
        return rseLeftMember;
    }

    public RSEMember getRightRSEMember() throws Exception {
        return new RSEMember(rightConnection.getMember(rightLibrary, rightFile, rightMember, null));
    }

    public IBMiConnection getRightConnection() {
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

    public IBMiConnection getAncestorConnection() {
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

}
