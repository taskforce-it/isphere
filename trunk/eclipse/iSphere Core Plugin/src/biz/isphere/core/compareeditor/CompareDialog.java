/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.compareeditor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.Member;

public abstract class CompareDialog extends Dialog {

    private boolean selectEditable;
    private Member leftMember;
    private Member rightMember;
    private Member ancestorMember;
    private Button editButton;
    private Button browseButton;
    private Button dontConsiderDateButton;
    private Button considerDateButton;
    private Button twoWayButton;
    private Button threeWayButton;
    private boolean editable;
    private boolean considerDate;
    private boolean threeWay;
    private Button okButton;
    private boolean defined;
    private boolean hasRight;
    private boolean hasAncestor;
    private Image switchImage;
    private Text leftConnectionText;
    private Text leftLibraryText;
    private Text leftFileText;
    private Text leftMemberText;
    private Text rightConnectionText;
    private Text rightLibraryText;
    private Text rightFileText;
    private Text rightMemberText;

    public CompareDialog(Shell parentShell, boolean selectEditable, Member leftMember) {
        super(parentShell);
        initialize(parentShell, selectEditable, leftMember, null, null);
    }

    public CompareDialog(Shell parentShell, boolean selectEditable, Member leftMember, Member rightMember) {
        super(parentShell);
        initialize(parentShell, selectEditable, leftMember, rightMember, null);
    }

    public CompareDialog(Shell parentShell, boolean selectEditable, Member leftMember, Member rightMember, Member ancestorMember) {
        super(parentShell);
        initialize(parentShell, selectEditable, leftMember, rightMember, ancestorMember);
    }

    private void initialize(Shell parentShell, boolean selectEditable, Member leftMember, Member rightMember, Member ancestorMember) {
        this.selectEditable = selectEditable;
        this.leftMember = leftMember;
        this.rightMember = rightMember;
        this.ancestorMember = ancestorMember;

        editable = false;
        considerDate = false;

        if (this.rightMember == null) {
            defined = false;
            hasRight = false;
        } else {
            defined = true;
            hasRight = true;
        }

        if (this.ancestorMember == null) {
            hasAncestor = false;
            threeWay = false;
        } else {
            hasAncestor = true;
            threeWay = true;
        }

        addImageDisposeListener(parentShell);
    }

    private void addImageDisposeListener(Shell parentShell) {
        parentShell.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent arg0) {
                if (switchImage != null) {
                    switchImage.dispose();
                }
            }
        });
    }

    @Override
    public Control createDialogArea(Composite parent) {
        Composite rtnGroup = (Composite)super.createDialogArea(parent);
        parent.getShell().setText(Messages.Compare_Source_Members);

        GridLayout rtnLayout = new GridLayout();
        rtnLayout.numColumns = 1;
        rtnGroup.setLayout(rtnLayout);
        rtnGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        Group modeGroup = new Group(rtnGroup, SWT.NONE);
        modeGroup.setText(Messages.Mode);
        GridLayout modeLayout = new GridLayout();
        modeLayout.numColumns = 1;
        modeGroup.setLayout(modeLayout);
        modeGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        if (selectEditable) {

            Composite editableGroup = new Composite(modeGroup, SWT.NONE);
            GridLayout editableLayout = new GridLayout();
            editableLayout.numColumns = 2;
            editableGroup.setLayout(editableLayout);
            editableGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

            browseButton = new Button(editableGroup, SWT.RADIO);
            browseButton.setText(Messages.Open_for_browse);
            if (!editable) {
                browseButton.setSelection(true);
            }

            editButton = new Button(editableGroup, SWT.RADIO);
            editButton.setText(Messages.Open_for_edit);
            if (editable) {
                editButton.setSelection(true);
            }

        }

        Composite considerDateGroup = new Composite(modeGroup, SWT.NONE);
        GridLayout considerDateLayout = new GridLayout();
        considerDateLayout.numColumns = 2;
        considerDateGroup.setLayout(considerDateLayout);
        considerDateGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        dontConsiderDateButton = new Button(considerDateGroup, SWT.RADIO);
        dontConsiderDateButton.setText(Messages.Don_t_consider_date);
        if (!considerDate) {
            dontConsiderDateButton.setSelection(true);
        }

        considerDateButton = new Button(considerDateGroup, SWT.RADIO);
        considerDateButton.setText(Messages.Consider_date);
        if (considerDate) {
            considerDateButton.setSelection(true);
        }

        if (!defined) {

            Composite threeWayGroup = new Composite(modeGroup, SWT.NONE);
            GridLayout threeWayLayout = new GridLayout();
            threeWayLayout.numColumns = 2;
            threeWayGroup.setLayout(threeWayLayout);
            threeWayGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

            twoWayButton = new Button(threeWayGroup, SWT.RADIO);
            twoWayButton.setText(Messages.Two_way_compare);
            if (!threeWay) {
                twoWayButton.setSelection(true);
            }
            twoWayButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    threeWay = false;
                    setAncestorVisible(false);
                    okButton.setEnabled(canFinish());
                }
            });

            threeWayButton = new Button(threeWayGroup, SWT.RADIO);
            threeWayButton.setText(Messages.Three_way_compare);
            if (threeWay) {
                threeWayButton.setSelection(true);
            }
            threeWayButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    threeWay = true;
                    setAncestorVisible(true);
                    okButton.setEnabled(canFinish());
                }
            });

        }

        Group leftGroup = new Group(rtnGroup, SWT.NONE);
        leftGroup.setText(Messages.Left);
        GridLayout leftLayout = new GridLayout();
        leftLayout.numColumns = 2;
        leftGroup.setLayout(leftLayout);
        leftGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        Label leftConnectionLabel = new Label(leftGroup, SWT.NONE);
        leftConnectionLabel.setText(Messages.Connection_colon);

        leftConnectionText = new Text(leftGroup, SWT.BORDER);
        leftConnectionText.setEditable(false);
        leftConnectionText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        leftConnectionText.setText(leftMember.getConnection());

        Label leftLibraryLabel = new Label(leftGroup, SWT.NONE);
        leftLibraryLabel.setText(Messages.Library_colon);
        leftLibraryText = new Text(leftGroup, SWT.BORDER);
        leftLibraryText.setEditable(false);
        leftLibraryText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        if (leftMember.isArchive()) {
            leftLibraryText.setText(leftMember.getArchiveLibrary());
        } else {
            leftLibraryText.setText(leftMember.getLibrary());
        }

        Label leftFileLabel = new Label(leftGroup, SWT.NONE);
        leftFileLabel.setText(Messages.File_colon);
        leftFileText = new Text(leftGroup, SWT.BORDER);
        leftFileText.setEditable(false);
        leftFileText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        if (leftMember.isArchive()) {
            leftFileText.setText(leftMember.getArchiveFile());
        } else {
            leftFileText.setText(leftMember.getSourceFile());
        }

        Label leftMemberLabel = new Label(leftGroup, SWT.NONE);
        leftMemberLabel.setText(Messages.Member_colon);
        leftMemberText = new Text(leftGroup, SWT.BORDER);
        leftMemberText.setEditable(false);
        leftMemberText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        if (leftMember.isArchive()) {
            leftMemberText.setText(leftMember.getArchiveMember());
        } else {
            leftMemberText.setText(leftMember.getMember());
        }

        if (leftMember.isArchive()) {
            Label leftTimeLabel = new Label(leftGroup, SWT.NONE);
            leftTimeLabel.setText(Messages.Archive_colon);
            Text leftTimeText = new Text(leftGroup, SWT.BORDER);
            leftTimeText.setEditable(false);
            leftTimeText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
            leftTimeText.setText(leftMember.getArchiveDate() + " - " + leftMember.getArchiveTime());
        }

        if (!defined) {

            createRightArea(rtnGroup);

            createAncestorArea(rtnGroup);

        } else {

            if (hasRight) {

                Composite switchPanel = new Composite(rtnGroup, SWT.NONE);
                GridLayout middleLayout = new GridLayout();
                middleLayout.numColumns = 1;
                switchPanel.setLayout(middleLayout);
                switchPanel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

                Button switchMemberButton = new Button(switchPanel, SWT.PUSH);
                switchMemberButton.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, false));
                switchMemberButton.setImage(getSwitchImage());
                switchMemberButton.addListener(SWT.Selection, new Listener() {
                    public void handleEvent(Event arg0) {
                        switchLeftAndRightMember(leftMember, rightMember);
                    }
                });

                Group rightGroup = new Group(rtnGroup, SWT.NONE);
                rightGroup.setText(Messages.Right);
                GridLayout rightLayout = new GridLayout();
                rightLayout.numColumns = 2;
                rightGroup.setLayout(rightLayout);
                rightGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

                Label rightConnectionLabel = new Label(rightGroup, SWT.NONE);
                rightConnectionLabel.setText(Messages.Connection_colon);

                rightConnectionText = new Text(rightGroup, SWT.BORDER);
                rightConnectionText.setEditable(false);
                rightConnectionText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
                rightConnectionText.setText(rightMember.getConnection());

                Label rightLibraryLabel = new Label(rightGroup, SWT.NONE);
                rightLibraryLabel.setText(Messages.Library_colon);
                rightLibraryText = new Text(rightGroup, SWT.BORDER);
                rightLibraryText.setEditable(false);
                rightLibraryText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
                if (rightMember.isArchive()) {
                    rightLibraryText.setText(rightMember.getArchiveLibrary());
                } else {
                    rightLibraryText.setText(rightMember.getLibrary());
                }

                Label rightFileLabel = new Label(rightGroup, SWT.NONE);
                rightFileLabel.setText(Messages.File_colon);
                rightFileText = new Text(rightGroup, SWT.BORDER);
                rightFileText.setEditable(false);
                rightFileText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
                if (rightMember.isArchive()) {
                    rightFileText.setText(rightMember.getArchiveFile());
                } else {
                    rightFileText.setText(rightMember.getSourceFile());
                }

                Label rightMemberLabel = new Label(rightGroup, SWT.NONE);
                rightMemberLabel.setText(Messages.Member_colon);
                rightMemberText = new Text(rightGroup, SWT.BORDER);
                rightMemberText.setEditable(false);
                rightMemberText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
                if (rightMember.isArchive()) {
                    rightMemberText.setText(rightMember.getArchiveMember());
                } else {
                    rightMemberText.setText(rightMember.getMember());
                }

                if (rightMember.isArchive()) {
                    Label rightTimeLabel = new Label(rightGroup, SWT.NONE);
                    rightTimeLabel.setText(Messages.Archive_colon);
                    Text rightTimeText = new Text(rightGroup, SWT.BORDER);
                    rightTimeText.setEditable(false);
                    rightTimeText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
                    rightTimeText.setText(rightMember.getArchiveDate() + " - " + rightMember.getArchiveTime());
                }

            }

            if (hasAncestor) {

                Group ancestorGroup = new Group(rtnGroup, SWT.NONE);
                ancestorGroup.setText(Messages.Ancestor);
                GridLayout ancestorLayout = new GridLayout();
                ancestorLayout.numColumns = 2;
                ancestorGroup.setLayout(ancestorLayout);
                ancestorGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

                Label ancestorConnectionLabel = new Label(ancestorGroup, SWT.NONE);
                ancestorConnectionLabel.setText(Messages.Connection_colon);

                Text ancestorConnectionText = new Text(ancestorGroup, SWT.BORDER);
                ancestorConnectionText.setEditable(false);
                ancestorConnectionText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
                ancestorConnectionText.setText(ancestorMember.getConnection());

                Label ancestorLibraryLabel = new Label(ancestorGroup, SWT.NONE);
                ancestorLibraryLabel.setText(Messages.Library_colon);
                Text ancestorLibraryText = new Text(ancestorGroup, SWT.BORDER);
                ancestorLibraryText.setEditable(false);
                ancestorLibraryText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
                if (ancestorMember.isArchive()) {
                    ancestorLibraryText.setText(ancestorMember.getArchiveLibrary());
                } else {
                    ancestorLibraryText.setText(ancestorMember.getLibrary());
                }

                Label ancestorFileLabel = new Label(ancestorGroup, SWT.NONE);
                ancestorFileLabel.setText(Messages.File_colon);
                Text ancestorFileText = new Text(ancestorGroup, SWT.BORDER);
                ancestorFileText.setEditable(false);
                ancestorFileText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
                if (ancestorMember.isArchive()) {
                    ancestorFileText.setText(ancestorMember.getArchiveFile());
                } else {
                    ancestorFileText.setText(ancestorMember.getSourceFile());
                }

                Label ancestorMemberLabel = new Label(ancestorGroup, SWT.NONE);
                ancestorMemberLabel.setText(Messages.Member_colon);
                Text ancestorMemberText = new Text(ancestorGroup, SWT.BORDER);
                ancestorMemberText.setEditable(false);
                ancestorMemberText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
                if (ancestorMember.isArchive()) {
                    ancestorMemberText.setText(ancestorMember.getArchiveMember());
                } else {
                    ancestorMemberText.setText(ancestorMember.getMember());
                }

                if (ancestorMember.isArchive()) {
                    Label ancestorTimeLabel = new Label(ancestorGroup, SWT.NONE);
                    ancestorTimeLabel.setText(Messages.Archive_colon);
                    Text ancestorTimeText = new Text(ancestorGroup, SWT.BORDER);
                    ancestorTimeText.setEditable(false);
                    ancestorTimeText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
                    ancestorTimeText.setText(ancestorMember.getArchiveDate() + " - " + ancestorMember.getArchiveTime());
                }

            }

        }

        if (!defined) {
            if (!threeWay) {
                setAncestorVisible(false);
            } else {
                setAncestorVisible(true);
            }
        }

        return rtnGroup;
    }

    private Image getSwitchImage() {
        if (switchImage == null) {
            switchImage = ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_SWITCH_MEMBER).createImage();
        }
        return switchImage;
    }

    protected void switchLeftAndRightMember(Member leftMember, Member rightMember) {
        Member tempMember = leftMember;
        this.leftMember = rightMember;

        leftConnectionText.setText(this.leftMember.getConnection());
        leftLibraryText.setText(this.leftMember.getLibrary());
        leftFileText.setText(this.leftMember.getSourceFile());
        leftMemberText.setText(this.leftMember.getMember());

        this.rightMember = tempMember;

        rightConnectionText.setText(this.rightMember.getConnection());
        rightLibraryText.setText(this.rightMember.getLibrary());
        rightFileText.setText(this.rightMember.getSourceFile());
        rightMemberText.setText(this.rightMember.getMember());
    }

    @Override
    public Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        Button button = super.createButton(parent, id, label, defaultButton);
        if (id == OK) {
            okButton = button;
            if (defined) {
                okButton.setEnabled(true);
            } else {
                okButton.setEnabled(false);
            }
        }
        return button;
    }

    @Override
    protected void okPressed() {
        if (selectEditable) {
            editable = editButton.getSelection();
        }
        considerDate = considerDateButton.getSelection();
        if (!defined) {
            threeWay = threeWayButton.getSelection();
        }
        super.okPressed();
    }

    public Button getOkButton() {
        return okButton;
    }

    protected void createRightArea(Composite parent) {
    }

    protected void createAncestorArea(Composite parent) {
    }

    protected void setAncestorVisible(boolean visible) {
    }

    protected boolean canFinish() {
        return true;
    }

    public boolean isDefined() {
        return defined;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isConsiderDate() {
        return considerDate;
    }

    public boolean isThreeWay() {
        return threeWay;
    }

}
