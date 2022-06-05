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

package de.taskforce.isphere.rse.compareeditor;

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

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.ui.widgets.ISeriesConnectionCombo;
import com.ibm.etools.iseries.core.ui.widgets.ISeriesMemberPrompt;

import de.taskforce.isphere.compareeditor.CompareDialog;
import de.taskforce.isphere.rse.Messages;
import de.taskforce.isphere.rse.internal.RSEMember;

public class RSECompareDialog extends CompareDialog {

	private Group ancestorGroup;
	private RSEMember rseMember;
    private ISeriesConnectionCombo rightConnectionCombo;
    private ISeriesMemberPrompt rightMemberPrompt;
    private ISeriesConnection rightConnection;
    private ISeriesConnectionCombo ancestorConnectionCombo;
    private ISeriesMemberPrompt ancestorMemberPrompt;
    private ISeriesConnection ancestorConnection;
    private String rightLibrary;
    private String rightFile;
    private String rightMember;
    private String ancestorLibrary;
    private String ancestorFile;
    private String ancestorMember;
	
	public RSECompareDialog(
			Shell parentShell, 
			boolean selectEditable, 
			RSEMember leftMember, 
			RSEMember rightMember, 
			RSEMember ancestorMember) {
		super(parentShell, selectEditable, leftMember, rightMember, ancestorMember);
		this.rseMember = leftMember;
	}

	public RSECompareDialog(
			Shell parentShell, 
			boolean selectEditable, 
			RSEMember leftMember, 
			RSEMember rightMember) {
		super(parentShell, selectEditable, leftMember, rightMember);
		this.rseMember = leftMember;
	}

	public RSECompareDialog(
			Shell parentShell, 
			boolean selectEditable, 
			RSEMember leftMember) {
		super(parentShell, selectEditable, leftMember);
		this.rseMember = leftMember;
	}

	public void createRightArea(Composite parent) {
		
		Group rightGroup = new Group(parent, SWT.NONE);
		rightGroup.setText(Messages.getString("Right"));
		GridLayout rightLayout = new GridLayout();
		rightLayout.numColumns = 1;
		rightGroup.setLayout(rightLayout);
		rightGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL)); 
		
		rightConnectionCombo = new ISeriesConnectionCombo(rightGroup, rseMember.getRSEConnection(), false);
		rightConnectionCombo.setLayoutData(new GridData());
		GridData gd = new GridData();
		gd.widthHint = 200;
		rightConnectionCombo.getCombo().setLayoutData(gd);

		rightConnectionCombo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	getOkButton().setEnabled(canFinish());
                rightMemberPrompt.setSystemConnection(rightConnectionCombo.getSystemConnection());
            }
		});
		
		rightMemberPrompt = new ISeriesMemberPrompt(rightGroup, SWT.NONE, false, true, ISeriesMemberPrompt.FILETYPE_SRC);
		rightMemberPrompt.setSystemConnection(rightConnectionCombo.getSystemConnection());
		rightMemberPrompt.setLibraryName(rseMember.getLibrary());
		rightMemberPrompt.setFileName(rseMember.getSourceFile());
		rightMemberPrompt.setMemberName(rseMember.getMember());

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
	
	public void createAncestorArea(Composite parent) {
		
		ancestorGroup = new Group(parent, SWT.NONE);
		ancestorGroup.setText(Messages.getString("Ancestor"));
		GridLayout ancestorLayout = new GridLayout();
		ancestorLayout.numColumns = 1;
		ancestorGroup.setLayout(ancestorLayout);
		ancestorGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL)); 
		
		ancestorConnectionCombo = new ISeriesConnectionCombo(ancestorGroup, rseMember.getRSEConnection(), false);
		ancestorConnectionCombo.setLayoutData(new GridData());
		GridData gd = new GridData();
		gd.widthHint = 200;
		ancestorConnectionCombo.getCombo().setLayoutData(gd);

		ancestorConnectionCombo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	getOkButton().setEnabled(canFinish());
                ancestorMemberPrompt.setSystemConnection(ancestorConnectionCombo.getSystemConnection());
            }
		});
		
		ancestorMemberPrompt = new ISeriesMemberPrompt(ancestorGroup, SWT.NONE, false, true, ISeriesMemberPrompt.FILETYPE_SRC);
		ancestorMemberPrompt.setSystemConnection(ancestorConnectionCombo.getSystemConnection());
		ancestorMemberPrompt.setLibraryName(rseMember.getLibrary());
		ancestorMemberPrompt.setFileName(rseMember.getSourceFile());
		ancestorMemberPrompt.setMemberName(rseMember.getMember());

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

	protected void setAncestorVisible(boolean visible) {
		ancestorGroup.setVisible(visible);
	}
	
    protected void okPressed() {
    	if (!isDefined()) {
            rightConnection = ISeriesConnection.getConnection(rightConnectionCombo.getSystemConnection());        
            rightLibrary = rightMemberPrompt.getLibraryName();
            rightFile = rightMemberPrompt.getFileName();
            rightMember = rightMemberPrompt.getMemberName();
            if (isThreeWay()) {
                ancestorConnection = ISeriesConnection.getConnection(ancestorConnectionCombo.getSystemConnection());        
                ancestorLibrary = ancestorMemberPrompt.getLibraryName();
                ancestorFile = ancestorMemberPrompt.getFileName();
                ancestorMember = ancestorMemberPrompt.getMemberName();
            }
    	}
        super.okPressed();
    }
    
	public boolean canFinish() {
        if (isThreeWay()) {
            if (rightMemberPrompt.getMemberName() == null || rightMemberPrompt.getMemberName().trim().length() == 0 ||
                    rightMemberPrompt.getFileName() == null || rightMemberPrompt.getFileName().trim().length() == 0 ||
                    rightMemberPrompt.getLibraryName() == null || rightMemberPrompt.getLibraryName().trim().length() == 0 ||
                    ancestorMemberPrompt.getMemberName() == null || ancestorMemberPrompt.getMemberName().trim().length() == 0 ||
                    ancestorMemberPrompt.getFileName() == null || ancestorMemberPrompt.getFileName().trim().length() == 0 ||
                    ancestorMemberPrompt.getLibraryName() == null || ancestorMemberPrompt.getLibraryName().trim().length() == 0) return false;
            if (rightMemberPrompt.getMemberName().equalsIgnoreCase(ancestorMemberPrompt.getMemberName()) &&
                    rightMemberPrompt.getFileName().equalsIgnoreCase(ancestorMemberPrompt.getFileName()) &&
                    rightMemberPrompt.getLibraryName().equalsIgnoreCase(ancestorMemberPrompt.getLibraryName()) &&
                    rightConnectionCombo.getSystemConnection().getHostName().equals(ancestorConnectionCombo.getSystemConnection().getHostName())) return false;
            if (rightMemberPrompt.getLibraryName().equalsIgnoreCase(rseMember.getLibrary()) &&
                    rightMemberPrompt.getFileName().equalsIgnoreCase(rseMember.getSourceFile()) &&
                    rightMemberPrompt.getMemberName().equalsIgnoreCase(rseMember.getMember()) &&
                    rightConnectionCombo.getSystemConnection().getHostName().equals(rseMember.getRSEConnection().getHostName())) return false;        
            if (ancestorMemberPrompt.getLibraryName().equalsIgnoreCase(rseMember.getLibrary()) &&
                    ancestorMemberPrompt.getFileName().equalsIgnoreCase(rseMember.getSourceFile()) &&
                    ancestorMemberPrompt.getMemberName().equalsIgnoreCase(rseMember.getMember()) &&
                    ancestorConnectionCombo.getSystemConnection().getHostName().equals(rseMember.getRSEConnection().getHostName())) return false;                
        }
        else {
            if (rightMemberPrompt.getMemberName() == null || rightMemberPrompt.getMemberName().trim().length() == 0) return false;
            if (rightMemberPrompt.getMemberName().equalsIgnoreCase(rseMember.getMember()) &&
                    rightMemberPrompt.getFileName().equalsIgnoreCase(rseMember.getSourceFile()) &&
                    rightMemberPrompt.getLibraryName().equalsIgnoreCase(rseMember.getLibrary()) &&
                    rightConnectionCombo.getSystemConnection().getHostName().equalsIgnoreCase(rseMember.getRSEConnection().getHostName())) return false;
        }
        return true;    
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
    
}
