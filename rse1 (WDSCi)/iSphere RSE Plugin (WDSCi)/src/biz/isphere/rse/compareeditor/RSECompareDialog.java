/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
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
import biz.isphere.core.annotations.CMOne;
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

    private static final String PREFIX_LEFT = "LEFT";
    private static final String PREFIX_RIGHT = "RIGHT";
    private static final String PREFIX_ANCESTOR = "ANCESTOR";
    private static final String CONNECTION = "_CONNECTION";
    private static final String LIBRARY = "_LIBRARY";
    private static final String FILE = "_FILE";
    private static final String MEMBER = "_MEMBER";

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

    private boolean rememberScreenValues;

    /**
     * Creates the compare dialog, for 2 selected member.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option
     *        "Open for browse/edit" is displayed
     * @param leftMember - the left selected member
     * @param rightMember - the right selected member
     */
    @CMOne(info = "Don`t change this constructor due to CMOne compatibility reasons")
    public RSECompareDialog(Shell parentShell, boolean selectEditable, RSEMember leftMember, RSEMember rightMember) {
        super(parentShell, selectEditable, leftMember, rightMember);
        initializeLeftMember(leftMember);
        initializeRightMember(rightMember);
        setSwitchMemberAllowed(false);
    }

    /**
     * Creates a three-way compare dialog.<br>
     * This constructor is used by CMOne.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option "Open for
     *        browse/edit" is displayed
     * @param leftMember - the left selected member
     * @param rightMember - the right selected member
     * @param ancestorMember - the ancestor member
     */
    @CMOne(info = "Don`t change this constructor due to CMOne compatibility reasons")
    public RSECompareDialog(Shell parentShell, boolean selectEditable, RSEMember leftMember, RSEMember rightMember, RSEMember ancestorMember) {
        super(parentShell, selectEditable, leftMember, rightMember, ancestorMember);
        initializeLeftMember(leftMember);
        initializeRightMember(rightMember);
        initializeAncestorMember(ancestorMember);
        setSwitchMemberAllowed(false);
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
    public RSECompareDialog(Shell parentShell, boolean selectEditable, Member[] selectedMembers) {
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
    public RSECompareDialog(Shell parentShell, boolean selectEditable, Member leftMember, Member rightMember) {
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
    public RSECompareDialog(Shell parentShell, boolean selectEditable, Member leftMember) {
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

        /*
         * Controls whether or not to store/load member values. For now members
         * are stored and loaded when the editor has been opened from the
         * iSphere main menu, so that no initial members has been passed to it.
         */
        rememberScreenValues = true;
    }

    private void initializeLeftMember(Member leftMember) {
        this.leftConnection = getConnection(leftMember);
        this.leftLibrary = leftMember.getLibrary();
        this.leftFile = leftMember.getSourceFile();
        this.leftMember = leftMember.getMember();
    }

    private void initializeRightMember(Member rightMember) {
        this.rightConnection = getConnection(rightMember);
        this.rightLibrary = rightMember.getLibrary();
        this.rightFile = rightMember.getSourceFile();
        this.rightMember = rightMember.getMember();
    }

    private void initializeAncestorMember(Member ancestorMember) {
        this.ancestorConnection = getConnection(ancestorMember);
        this.ancestorLibrary = ancestorMember.getLibrary();
        this.ancestorFile = ancestorMember.getSourceFile();
        this.ancestorMember = ancestorMember.getMember();
    }

    private ISeriesConnection getConnection(Member member) {

        if (member instanceof RSEMember) {
            return ((RSEMember)member).getRSEConnection();
        } else {
            return ISeriesConnection.getConnection(member.getConnection());
        }

    }

    @Override
    protected void createEditableLeftArea(Composite parent) {

        Group leftGroup = new Group(parent, SWT.NONE);
        leftGroup.setText(Messages.Left);
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

        // Initialize right connection with left connection
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

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getOkButton().setEnabled(canFinish());
            }
        };

        rightMemberPrompt.getMemberCombo().addModifyListener(modifyListener);
        rightMemberPrompt.getFileCombo().addModifyListener(modifyListener);
        rightMemberPrompt.getLibraryCombo().addModifyListener(modifyListener);
        rightMemberPrompt.getLibraryCombo().setFocus();

        rightMemberPrompt.getMemberCombo().setEnabled(!hasMultipleRightMembers());
        rightMemberPrompt.getMemberBrowseButton().setEnabled(!hasMultipleRightMembers());
    }

    @Override
    protected void createEditableAncestorArea(Composite parent) {

        ancestorGroup = new Group(parent, SWT.NONE);
        ancestorGroup.setText(Messages.Ancestor);
        GridLayout ancestorLayout = new GridLayout();
        ancestorLayout.numColumns = 1;
        ancestorGroup.setLayout(ancestorLayout);
        ancestorGroup.setLayoutData(getGridData());

        // Initialize ancestor connection with left connection
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

        if (hasEditableLeftMember()) {

            leftConnection = getCurrentLeftConnection();
            leftLibrary = getCurrentLeftLibraryName();
            leftFile = getCurrentLeftFileName();
            leftMember = getCurrentLeftMemberName();

            if (!validateMember(leftConnection, leftLibrary, leftFile, leftMember, leftMemberPrompt)) {
                return;
            }

            leftMemberPrompt.updateHistory(true);

        }

        if (hasEditableRightMember()) {

            rightConnection = getCurrentRightConnection();
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

            rightMemberPrompt.updateHistory(true);

        }

        if (isThreeWay() && hasEditableAncestorMember()) {

            ancestorConnection = getCurrentAncestorConnection();
            ancestorLibrary = getCurrentAncestorLibraryName();
            ancestorFile = getCurrentAncestorFileName();
            ancestorMember = getCurrentAncestorMemberName();

            if (!validateMember(ancestorConnection, ancestorLibrary, ancestorFile, ancestorMember, ancestorMemberPrompt)) {
                return;
            }

            ancestorMemberPrompt.updateHistory(true);

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

        RSEMember rseMember = getRSEMember(connection, libraryName, fileName, memberName);
        if (rseMember == null) {
            return false;
        }

        if (rseMember.exists()) {
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

        // Check left member is specified
        if (StringHelper.isNullOrEmpty(getCurrentLeftConnectionName()) || StringHelper.isNullOrEmpty(getCurrentLeftLibraryName())
            || StringHelper.isNullOrEmpty(getCurrentLeftFileName()) || StringHelper.isNullOrEmpty(getCurrentLeftMemberName())) {
            return false;
        }

        // Check right member is specified
        if (StringHelper.isNullOrEmpty(getCurrentRightConnectionName()) || StringHelper.isNullOrEmpty(getCurrentRightLibraryName())
            || StringHelper.isNullOrEmpty(getCurrentRightFileName()) || StringHelper.isNullOrEmpty(getCurrentRightMemberName())) {
            return false;
        }

        // Check ancestor member is specified
        if (isThreeWay()) {
            if (StringHelper.isNullOrEmpty(getCurrentAncestorConnectionName()) || StringHelper.isNullOrEmpty(getCurrentAncestorLibraryName())
                || StringHelper.isNullOrEmpty(getCurrentAncestorFileName()) || StringHelper.isNullOrEmpty(getCurrentAncestorMemberName())) {
                return false;
            }
        }

        // Ensure right and left members are different
        if (getCurrentRightConnectionName().equalsIgnoreCase(getCurrentLeftConnectionName())
            && getCurrentRightLibraryName().equalsIgnoreCase(getCurrentLeftLibraryName())
            && getCurrentRightFileName().equalsIgnoreCase(getCurrentLeftFileName())
            && (hasMultipleRightMembers() || getCurrentRightMemberName().equalsIgnoreCase(getCurrentLeftMemberName()))) {
            return false;
        }

        if (isThreeWay()) {
            // Ensure ancestor member is different from right member
            if (getCurrentAncestorConnectionName().equalsIgnoreCase(getCurrentRightConnectionName())
                && getCurrentAncestorLibraryName().equalsIgnoreCase(getCurrentRightLibraryName())
                && getCurrentAncestorFileName().equalsIgnoreCase(getCurrentRightFileName())
                && getCurrentAncestorMemberName().equalsIgnoreCase(getCurrentRightMemberName())) {
                return false;
            }
            // Ensure ancestor member is different from left member
            if (getCurrentAncestorConnectionName().equalsIgnoreCase(getCurrentLeftConnectionName())
                && getCurrentAncestorLibraryName().equalsIgnoreCase(getCurrentLeftLibraryName())
                && getCurrentAncestorFileName().equalsIgnoreCase(getCurrentLeftFileName())
                && getCurrentAncestorMemberName().equalsIgnoreCase(getCurrentLeftMemberName())) {
                return false;
            }
        }

        return true;
    }

    private ISeriesConnection getCurrentLeftConnection() {
        if (leftConnectionCombo == null) {
            // return value for read-only left member
            return leftConnection;
        }
        return ISeriesConnection.getConnection(getCurrentLeftConnectionName());
    }

    private String getCurrentLeftConnectionName() {
        if (leftConnectionCombo == null) {
            // return value for read-only left member
            return getLeftConnection().getConnectionName();
        }
        return leftConnectionCombo.getSystemConnection().getAliasName();
    }

    private String getCurrentLeftLibraryName() {
        if (leftMemberPrompt == null) {
            // return value for read-only left member
            return getLeftLibrary();
        }
        return leftMemberPrompt.getLibraryName();
    }

    private String getCurrentLeftFileName() {
        if (leftMemberPrompt == null) {
            // return value for read-only left member
            return getLeftFile();
        }
        return leftMemberPrompt.getFileName();
    }

    private String getCurrentLeftMemberName() {
        if (leftMemberPrompt == null) {
            // return value for read-only left member
            return getLeftMember();
        }
        return leftMemberPrompt.getMemberName();
    }

    private ISeriesConnection getCurrentRightConnection() {
        return ISeriesConnection.getConnection(getCurrentRightConnectionName());
    }

    private String getCurrentRightConnectionName() {
        return rightConnectionCombo.getSystemConnection().getAliasName();
    }

    private String getCurrentRightLibraryName() {
        return rightMemberPrompt.getLibraryName();
    }

    private String getCurrentRightFileName() {
        return rightMemberPrompt.getFileName();
    }

    private String getCurrentRightMemberName() {
        return rightMemberPrompt.getMemberName();
    }

    private ISeriesConnection getCurrentAncestorConnection() {
        return ISeriesConnection.getConnection(getCurrentAncestorConnectionName());
    }

    private String getCurrentAncestorConnectionName() {
        return ancestorConnectionCombo.getSystemConnection().getAliasName();
    }

    private String getCurrentAncestorLibraryName() {
        return ancestorMemberPrompt.getLibraryName();
    }

    private String getCurrentAncestorFileName() {
        return ancestorMemberPrompt.getFileName();
    }

    private String getCurrentAncestorMemberName() {
        return ancestorMemberPrompt.getMemberName();
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

    @Override
    protected void loadScreenValues() {
        super.loadScreenValues();

        if (hasEditableLeftMember()) {
            if (rememberScreenValues) {
                loadMemberValues(PREFIX_LEFT, leftConnectionCombo, leftMemberPrompt);
            }
        }

        if (hasEditableRightMember()) {
            if (hasMultipleRightMembers()) {
                rightMemberPrompt.setMemberName(SPECIAL_MEMBER_NAME_LEFT);
            } else if (rememberScreenValues) {
                loadMemberValues(PREFIX_RIGHT, rightConnectionCombo, rightMemberPrompt);
            } else {
                // Initialize right member with left member
                setMemberValues(rightConnectionCombo, rightMemberPrompt, getCurrentLeftConnectionName(), getCurrentLeftLibraryName(),
                    getCurrentLeftFileName(), getCurrentLeftMemberName());
            }
        }

        if (hasEditableAncestorMember()) {
            if (rememberScreenValues) {
                loadMemberValues(PREFIX_ANCESTOR, ancestorConnectionCombo, ancestorMemberPrompt);
            } else {
                // Initialize ancestor member with left member
                setMemberValues(ancestorConnectionCombo, ancestorMemberPrompt, getCurrentLeftConnectionName(), getCurrentLeftLibraryName(),
                    getCurrentLeftFileName(), getCurrentLeftMemberName());
            }
        }
    }

    private void loadMemberValues(String prefix, ISeriesConnectionCombo connectionCombo, ISeriesMemberPrompt memberPrompt) {

        memberPrompt.setSystemConnection(null);
        memberPrompt.setLibraryName(""); //$NON-NLS-1$
        memberPrompt.setFileName(""); //$NON-NLS-1$
        memberPrompt.setMemberName(""); //$NON-NLS-1$

        String connection = loadValue(prefix + CONNECTION, null);
        String library = loadValue(prefix + LIBRARY, null);
        String file = loadValue(prefix + FILE, null);
        String member = loadValue(prefix + MEMBER, null);

        setMemberValues(connectionCombo, memberPrompt, connection, library, file, member);
    }

    private void setMemberValues(ISeriesConnectionCombo connectionCombo, ISeriesMemberPrompt memberPrompt, String connection, String library,
        String file, String member) {

        if (haveMemberValues(connection, library, file, member)) {
            String[] connections = connectionCombo.getItems();
            for (int i = 0; i < connections.length; i++) {
                String connectionItem = connections[i];
                if (connectionItem.equals(connection)) {
                    connectionCombo.setSelectionIndex(i);
                    memberPrompt.getLibraryCombo().setText(library);
                    memberPrompt.getFileCombo().setText(file);
                    memberPrompt.getMemberCombo().setText(member);
                    memberPrompt.setSystemConnection(connectionCombo.getSystemConnection());
                }
            }
        }
    }

    @Override
    protected void storeScreenValues() {
        super.storeScreenValues();

        if (hasEditableLeftMember()) {
            if (rememberScreenValues) {
                storeMemberValues(PREFIX_LEFT, leftConnectionCombo, leftMemberPrompt);
            }
        }

        if (hasMultipleRightMembers()) {
            // do not store special value *LEFT.
        } else if (hasEditableRightMember()) {
            if (rememberScreenValues) {
                storeMemberValues(PREFIX_RIGHT, rightConnectionCombo, rightMemberPrompt);
            }
        }

        if (hasEditableAncestorMember()) {
            if (rememberScreenValues) {
                storeMemberValues(PREFIX_ANCESTOR, ancestorConnectionCombo, ancestorMemberPrompt);
            }
        }
    }

    private void storeMemberValues(String prefix, ISeriesConnectionCombo connectionCombo, ISeriesMemberPrompt memberPrompt) {

        String connection = connectionCombo.getText();
        String library = memberPrompt.getLibraryName();
        String file = memberPrompt.getFileName();
        String member = memberPrompt.getMemberName();

        if (haveMemberValues(connection, library, file, member)) {
            storeValue(prefix + CONNECTION, connection);
            storeValue(prefix + LIBRARY, library);
            storeValue(prefix + FILE, file);
            storeValue(prefix + MEMBER, member);
        }
    }

    private boolean haveMemberValues(String connection, String library, String file, String member) {

        if (connection != null && library != null & file != null && member != null) {
            return true;
        }

        return false;
    }

    private boolean hasEditableLeftMember() {
        return leftMemberPrompt != null;
    }

    private boolean hasEditableRightMember() {
        return rightMemberPrompt != null;
    }

    private boolean hasEditableAncestorMember() {
        return ancestorMemberPrompt != null;
    }
}
