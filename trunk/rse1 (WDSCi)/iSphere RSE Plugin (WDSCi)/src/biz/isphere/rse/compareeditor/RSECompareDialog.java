/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.compareeditor;

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

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.compareeditor.CompareDialog;
import biz.isphere.core.internal.Member;
import biz.isphere.rse.Messages;
import biz.isphere.rse.internal.RSEMember;

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.api.ISeriesFile;
import com.ibm.etools.iseries.core.api.ISeriesLibrary;
import com.ibm.etools.iseries.core.ui.widgets.ISeriesConnectionCombo;
import com.ibm.etools.iseries.core.ui.widgets.ISeriesMemberPrompt;

public class RSECompareDialog extends CompareDialog {

    private Group ancestorGroup;

    private ISeriesConnectionCombo leftConnectionCombo;
    private ISeriesMemberPrompt leftMemberPrompt;
    private ISeriesConnection leftConnection;
    private String leftLibrary;
    private String leftFile;
    private String leftMember;

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

    /**
     * Creates a three-way compare dialog.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option "Open for
     *        browse/edit" is displayed
     * @param leftMember - the left selected member
     * @param rightMember - the right selected member
     * @param ancestorMember - the ancestor member
     */
    public RSECompareDialog(Shell parentShell, boolean selectEditable, RSEMember leftMember, RSEMember rightMember, RSEMember ancestorMember) {
        super(parentShell, selectEditable, leftMember, rightMember, ancestorMember);
        initializeLeftMember(leftMember);
        initializeRightMember(rightMember);
    }

    /**
     * Creates the compare dialog, for 3 and more selected member.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option "Open for
     *        browse/edit" is displayed
     * @param selectedMembers - the selected members that go to the left side of
     *        the compare dialog
     */
    public RSECompareDialog(Shell parentShell, boolean selectEditable, RSEMember[] selectedMembers) {
        super(parentShell, selectEditable, selectedMembers);
        initializeLeftMember(selectedMembers[0]);
        initializeRightMember(selectedMembers[0]);
    }

    /**
     * Creates the compare dialog, for 2 selected member.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option "Open for
     *        browse/edit" is displayed
     * @param leftMember - the left selected member
     * @param rightMember - the right selected member
     */
    public RSECompareDialog(Shell parentShell, boolean selectEditable, RSEMember leftMember, RSEMember rightMember) {
        super(parentShell, selectEditable, leftMember, rightMember);
        initializeLeftMember(leftMember);
        initializeRightMember(rightMember);
    }

    /**
     * Creates the compare dialog, for 1 selected member.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option "Open for
     *        browse/edit" is displayed
     * @param leftMember - the left selected member
     */
    public RSECompareDialog(Shell parentShell, boolean selectEditable, RSEMember leftMember) {
        super(parentShell, selectEditable, leftMember);
        initializeLeftMember(leftMember);
    }

    /**
     * Creates the compare dialog, for 0 selected members.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option "Open for
     *        browse/edit" is displayed
     */
    public RSECompareDialog(Shell parentShell, boolean selectEditable) {
        super(parentShell, selectEditable);
    }

    private void initializeLeftMember(RSEMember leftMember) {
        this.leftConnection = leftMember.getRSEConnection();
        this.leftLibrary = leftMember.getLibrary();
        this.leftFile = leftMember.getSourceFile();
        this.leftMember = leftMember.getMember();
    }

    private void initializeRightMember(RSEMember rightMember) {
        this.rightConnection = rightMember.getRSEConnection();
        this.rightLibrary = rightMember.getLibrary();
        this.rightFile = rightMember.getSourceFile();
        this.rightMember = rightMember.getMember();
    }

    @Override
    protected void createEditableLeftArea(Composite parent) {

        Group leftGroup = new Group(parent, SWT.NONE);
        leftGroup.setText(Messages.Right);
        GridLayout rightLayout = new GridLayout();
        rightLayout.numColumns = 1;
        leftGroup.setLayout(rightLayout);
        leftGroup.setLayoutData(getGridData());

        leftConnectionCombo = new ISeriesConnectionCombo(leftGroup, getLeftConnection(), false);
        leftConnectionCombo.setLayoutData(getGridData());
        leftConnectionCombo.getCombo().setLayoutData(getGridData());

        leftConnectionCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getOkButton().setEnabled(canFinish());
                leftMemberPrompt.setSystemConnection(leftConnectionCombo.getSystemConnection());
            }
        });

        leftMemberPrompt = new ISeriesMemberPrompt(leftGroup, SWT.NONE, false, true, ISeriesMemberPrompt.FILETYPE_SRC);
        leftMemberPrompt.setSystemConnection(getLeftConnection().getSystemConnection());
        leftMemberPrompt.setLibraryName(getLeftLibrary());
        leftMemberPrompt.setFileName(getLeftFile());
        leftMemberPrompt.setMemberName(getLeftMember());

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getOkButton().setEnabled(canFinish());
            }
        };

        leftMemberPrompt.getMemberCombo().addModifyListener(modifyListener);
        leftMemberPrompt.getFileCombo().addModifyListener(modifyListener);
        leftMemberPrompt.getLibraryCombo().addModifyListener(modifyListener);
        leftMemberPrompt.getLibraryCombo().setFocus();
    }

    @Override
    protected void createEditableRightArea(Composite parent) {

        Group rightGroup = new Group(parent, SWT.NONE);
        rightGroup.setText(Messages.Right);
        GridLayout rightLayout = new GridLayout();
        rightLayout.numColumns = 1;
        rightGroup.setLayout(rightLayout);
        rightGroup.setLayoutData(getGridData());

        rightConnectionCombo = new ISeriesConnectionCombo(rightGroup, getLeftConnection(), false);
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
        rightMemberPrompt.setLibraryName(getLeftLibrary());
        rightMemberPrompt.setFileName(getLeftFile());

        if (hasMultipleRightMembers()) {
            rightMemberPrompt.setMemberName(SPECIAL_MEMBER_NAME_LEFT);
        } else {
            rightMemberPrompt.setMemberName(getLeftMember());
        }

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getOkButton().setEnabled(canFinish());
            }
        };

        rightMemberPrompt.getMemberCombo().addModifyListener(modifyListener);
        rightMemberPrompt.getFileCombo().addModifyListener(modifyListener);
        rightMemberPrompt.getLibraryCombo().addModifyListener(modifyListener);
        rightMemberPrompt.getLibraryCombo().setFocus();

        setRightMemberPromptEnablement(!hasMultipleRightMembers());
    }

    @Override
    protected void createEditableAncestorArea(Composite parent) {

        ancestorGroup = new Group(parent, SWT.NONE);
        ancestorGroup.setText(Messages.Ancestor);
        GridLayout ancestorLayout = new GridLayout();
        ancestorLayout.numColumns = 1;
        ancestorGroup.setLayout(ancestorLayout);
        ancestorGroup.setLayoutData(getGridData());

        ancestorConnectionCombo = new ISeriesConnectionCombo(ancestorGroup, getLeftConnection(), false);
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
        ancestorMemberPrompt.setLibraryName(getLeftLibrary());
        ancestorMemberPrompt.setFileName(getLeftFile());
        ancestorMemberPrompt.setMemberName(getLeftMember());

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getOkButton().setEnabled(canFinish());
            }
        };

        ancestorMemberPrompt.getMemberCombo().addModifyListener(modifyListener);
        ancestorMemberPrompt.getFileCombo().addModifyListener(modifyListener);
        ancestorMemberPrompt.getLibraryCombo().addModifyListener(modifyListener);

    }

    @Override
    protected void setAncestorVisible(boolean visible) {
        ancestorGroup.setVisible(visible);
        setFocus();
    }

    @Override
    public void setFocus() {

        if (leftMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentLeftLibraryName())) {
            leftMemberPrompt.getLibraryCombo().setFocus();
            return;
        }
        if (leftMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentLeftFileName())) {
            leftMemberPrompt.getFileCombo().setFocus();
            return;
        }
        if (leftMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentLeftMemberName())) {
            leftMemberPrompt.getMemberCombo().setFocus();
            return;
        }

        if (rightMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentRightLibraryName())) {
            rightMemberPrompt.getLibraryCombo().setFocus();
            return;
        }
        if (rightMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentRightFileName())) {
            rightMemberPrompt.getFileCombo().setFocus();
            return;
        }
        if (rightMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentRightMemberName())) {
            rightMemberPrompt.getMemberCombo().setFocus();
            return;
        }

        if (ancestorMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentAncestorLibraryName())) {
            ancestorMemberPrompt.getLibraryCombo().setFocus();
            return;
        }
        if (ancestorMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentAncestorFileName())) {
            ancestorMemberPrompt.getFileCombo().setFocus();
            return;
        }
        if (ancestorMemberPrompt != null && StringHelper.isNullOrEmpty(getCurrentAncestorMemberName())) {
            ancestorMemberPrompt.getMemberCombo().setFocus();
            return;
        }

    }

    @Override
    protected void okPressed() {

        if (!hasLeftMember()) {

            leftConnection = ISeriesConnection.getConnection(leftConnectionCombo.getSystemConnection());
            leftLibrary = getCurrentLeftLibraryName();
            leftFile = getCurrentLeftFileName();
            leftMember = getCurrentLeftMemberName();

            if (!validateMember(leftConnection, leftLibrary, leftFile, leftMember, leftMemberPrompt)) {
                return;
            }

        }

        if (!hasRightMember() || hasMultipleRightMembers()) {

            rightConnection = ISeriesConnection.getConnection(rightConnectionCombo.getSystemConnection());
            rightLibrary = getCurrentRightLibraryName();
            rightFile = getCurrentRightFileName();

            if (hasMultipleRightMembers()) {
                rightMember = null;
            } else {
                rightMember = getCurrentRightMemberName();
            }

            if (!validateMember(rightConnection, rightLibrary, rightFile, rightMember, rightMemberPrompt)) {
                return;
            }

        }

        if (isThreeWay()) {

            ancestorConnection = ISeriesConnection.getConnection(ancestorConnectionCombo.getSystemConnection());
            ancestorLibrary = getCurrentAncestorLibraryName();
            ancestorFile = getCurrentAncestorFileName();
            ancestorMember = getCurrentAncestorMemberName();

            if (!validateMember(ancestorConnection, ancestorLibrary, ancestorFile, ancestorMember, ancestorMemberPrompt)) {
                return;
            }

        }

        // Close dialog
        super.okPressed();
    }

    private boolean validateMember(ISeriesConnection connection, String library, String file, String member, ISeriesMemberPrompt memberPrompt) {

        if (!checkLibrary(connection, library)) {
            displayLibraryNotFoundMessage(library, memberPrompt);
            return false;
        }

        if (!checkFile(connection, library, file)) {
            displayFileNotFoundMessage(library, file, memberPrompt);
            return false;
        }

        if (member != null && !checkMember(connection, library, file, member)) {
            displayMemberNotFoundMessage(library, file, member, memberPrompt);
            return false;
        }

        return true;
    }

    private boolean checkLibrary(ISeriesConnection connection, String libraryName) {

        ISeriesLibrary qsysLibrary = null;
        try {
            qsysLibrary = connection.getISeriesLibrary(getShell(), libraryName);
        } catch (Exception e) {
        }

        if (qsysLibrary != null) {
            return true;
        }

        return false;
    }

    private void displayLibraryNotFoundMessage(String libraryName, ISeriesMemberPrompt qsysMemberPrompt) {

        String message = biz.isphere.core.Messages.bind(Messages.Library_A_not_found, new Object[] { libraryName });
        MessageDialog.openError(getShell(), biz.isphere.core.Messages.Error, message);
        qsysMemberPrompt.getLibraryCombo().setFocus();
    }

    private boolean checkFile(ISeriesConnection connection, String libraryName, String fileName) {

        ISeriesFile qsysFile = null;
        try {
            qsysFile = connection.getISeriesFile(getShell(), libraryName, fileName);
        } catch (Exception e) {
        }

        if (qsysFile != null) {
            return true;
        }

        return false;
    }

    private void displayFileNotFoundMessage(String libraryName, String fileName, ISeriesMemberPrompt qsysMemberPrompt) {

        String message = biz.isphere.core.Messages.bind(Messages.File_A_in_library_B_not_found, new Object[] { fileName, libraryName });
        MessageDialog.openError(getShell(), biz.isphere.core.Messages.Error, message);
        qsysMemberPrompt.getFileCombo().setFocus();
    }

    private boolean checkMember(ISeriesConnection connection, String libraryName, String fileName, String memberName) {

        RSEMember _rightMember = getRightRSEMember();
        if (_rightMember == null) {
            return false;
        }

        if (_rightMember.exists()) {
            return true;
        }

        return false;
    }

    private void displayMemberNotFoundMessage(String libraryName, String fileName, String memberName, ISeriesMemberPrompt qsysMemberPrompt) {

        String message = biz.isphere.core.Messages.bind(biz.isphere.core.Messages.Member_2_of_file_1_in_library_0_not_found, new Object[] {
            libraryName, fileName, memberName });
        MessageDialog.openError(getShell(), biz.isphere.core.Messages.Error, message);
        qsysMemberPrompt.getMemberCombo().setFocus();

    }

    @Override
    protected boolean canFinish() {

        if (getCurrentLeftConnectionName() == null || getCurrentLeftLibraryName() == null || getCurrentLeftFileName() == null
            || getCurrentLeftMemberName() == null) {
            return false;
        }

        if (isThreeWay()) {
            if (getCurrentRightMemberName() == null || getCurrentRightMemberName().length() == 0 || getCurrentRightFileName() == null
                || getCurrentRightFileName().length() == 0 || getCurrentRightLibraryName() == null || getCurrentRightLibraryName().length() == 0
                || getCurrentAncestorMemberName() == null || getCurrentAncestorMemberName().length() == 0 || getCurrentAncestorFileName() == null
                || getCurrentAncestorFileName().length() == 0 || getCurrentAncestorLibraryName() == null
                || getCurrentAncestorLibraryName().length() == 0) {
                return false;
            }
            if (getCurrentRightMemberName().equalsIgnoreCase(getCurrentAncestorMemberName())
                && getCurrentRightFileName().equalsIgnoreCase(getCurrentAncestorFileName())
                && getCurrentRightLibraryName().equalsIgnoreCase(getCurrentAncestorLibraryName())
                && getCurrentRightConnectionName().equals(getCurrentAncestorConnectionName())) {
                return false;
            }
            if (getCurrentRightLibraryName().equalsIgnoreCase(getCurrentLeftLibraryName())
                && getCurrentRightFileName().equalsIgnoreCase(getCurrentLeftFileName())
                && getCurrentRightMemberName().equalsIgnoreCase(getCurrentLeftMemberName())
                && getCurrentRightConnectionName().equals(getCurrentLeftConnectionName())) {
                return false;
            }
            if (getCurrentAncestorLibraryName().equalsIgnoreCase(getCurrentLeftLibraryName())
                && getCurrentAncestorFileName().equalsIgnoreCase(getCurrentLeftFileName())
                && getCurrentAncestorMemberName().equalsIgnoreCase(getCurrentLeftMemberName())
                && getCurrentAncestorConnectionName().equals(getCurrentLeftConnectionName())) {
                return false;
            }
        } else {

            String rightMember = getCurrentRightMemberName();
            if (rightMember == null || rightMember.length() == 0) {
                return false;
            }

            String leftMember = getCurrentLeftMemberName();
            if (leftMember == null || leftMember.length() == 0) {
                return false;
            }

            if (getCurrentRightMemberName().equalsIgnoreCase(getCurrentLeftMemberName())
                && getCurrentRightFileName().equalsIgnoreCase(getCurrentLeftFileName())
                && getCurrentRightLibraryName().equalsIgnoreCase(getCurrentLeftLibraryName())
                && getCurrentRightConnectionName().equalsIgnoreCase(getCurrentLeftConnectionName())) {
                return false;
            }
        }
        return true;
    }

    private String getCurrentLeftConnectionName() {
        if (leftConnectionCombo == null) {
            // return value for read-only left member
            return getLeftConnection().getHostName();
        }
        if (leftConnectionCombo.getSystemConnection().getHostName().trim().length() == 0) {
            return null;
        }
        return leftConnectionCombo.getSystemConnection().getHostName().trim();
    }

    private String getCurrentLeftLibraryName() {
        if (leftMemberPrompt == null) {
            // return value for read-only left member
            return getLeftLibrary();
        }
        if (leftMemberPrompt.getLibraryName() == null) {
            return null;
        }
        return leftMemberPrompt.getLibraryName().trim();
    }

    private String getCurrentLeftFileName() {
        if (leftMemberPrompt == null) {
            // return value for read-only left member
            return getLeftFile();
        }
        if (leftMemberPrompt.getFileName() == null) {
            return null;
        }
        return leftMemberPrompt.getFileName().trim();
    }

    private String getCurrentLeftMemberName() {
        if (leftMemberPrompt == null) {
            // return value for read-only left member
            return getLeftMember();
        }
        if (leftMemberPrompt.getMemberName() == null) {
            return null;
        }
        return leftMemberPrompt.getMemberName().trim();
    }

    private void setRightMemberPromptEnablement(boolean enabled) {
        rightMemberPrompt.getMemberCombo().setEnabled(enabled);
        rightMemberPrompt.getMemberBrowseButton().setEnabled(enabled);
    }

    private String getCurrentRightConnectionName() {
        if (rightConnectionCombo.getSystemConnection().getHostName().trim().length() == 0) {
            return null;
        }
        return rightConnectionCombo.getSystemConnection().getHostName().trim();
    }

    private String getCurrentRightLibraryName() {
        if (rightMemberPrompt.getLibraryName() == null) {
            return null;
        }
        return rightMemberPrompt.getLibraryName().trim();
    }

    private String getCurrentRightFileName() {
        if (rightMemberPrompt.getFileName() == null) {
            return null;
        }
        return rightMemberPrompt.getFileName().trim();
    }

    private String getCurrentRightMemberName() {
        if (rightMemberPrompt.getMemberName() == null) {
            return null;
        }
        String memberName = rightMemberPrompt.getMemberName().trim();
        if (SPECIAL_MEMBER_NAME_LEFT.equalsIgnoreCase(memberName)) {
            memberName = getLeftMember();
        }
        return memberName;
    }

    private String getCurrentAncestorConnectionName() {
        return ancestorConnectionCombo.getSystemConnection().getHostName();
    }

    private String getCurrentAncestorLibraryName() {
        if (ancestorMemberPrompt == null) {
            return null;
        }
        if (ancestorMemberPrompt.getLibraryName() == null) {
            return null;
        }
        return ancestorMemberPrompt.getLibraryName().trim();
    }

    private String getCurrentAncestorFileName() {
        if (ancestorMemberPrompt == null) {
            return null;
        }
        if (ancestorMemberPrompt.getFileName() == null) {
            return null;
        }
        return ancestorMemberPrompt.getFileName().trim();
    }

    private String getCurrentAncestorMemberName() {
        if (ancestorMemberPrompt == null) {
            return null;
        }
        if (ancestorMemberPrompt.getMemberName() == null) {
            return null;
        }
        return ancestorMemberPrompt.getMemberName().trim();
    }

    public RSEMember getRightRSEMember() {
        return getRSEMember(rightConnection, rightLibrary, rightFile, rightMember);
    }

    public RSEMember getLeftRSEMember() {
        return getRSEMember(leftConnection, leftLibrary, leftFile, leftMember);
    }

    public RSEMember getAncestorRSEMember() {
        return getRSEMember(ancestorConnection, ancestorLibrary, ancestorFile, ancestorMember);
    }

    private RSEMember getRSEMember(ISeriesConnection connection, String library, String file, String member) {

        try {
            return new RSEMember(connection.getISeriesMember(getShell(), library, file, member));
        } catch (Exception e) {
            MessageDialog.openError(getShell(), biz.isphere.core.Messages.Error, e.getMessage());
            return null;
        }
    }

    @Override
    protected void switchLeftAndRightMember(Member leftMember, Member rightMember) {
        super.switchLeftAndRightMember(leftMember, rightMember);
        initializeRightMember((RSEMember)leftMember);
        initializeLeftMember((RSEMember)rightMember);
    }

    private ISeriesConnection getLeftConnection() {
        return leftConnection;
    }

    private String getLeftLibrary() {
        if (leftLibrary == null) {
            return ""; //$NON-NLS-1$
        }
        return leftLibrary;
    }

    private String getLeftFile() {
        if (leftFile == null) {
            return ""; //$NON-NLS-1$
        }
        return leftFile;
    }

    private String getLeftMember() {
        if (leftMember == null) {
            return ""; //$NON-NLS-1$
        }
        return leftMember;
    }
}
