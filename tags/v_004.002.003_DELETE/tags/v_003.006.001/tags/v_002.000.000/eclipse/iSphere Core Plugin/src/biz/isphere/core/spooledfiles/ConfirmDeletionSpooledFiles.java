/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.Size;


public class ConfirmDeletionSpooledFiles extends Dialog {

	private SpooledFile[] spooledFiles;
	private TableViewer tableViewerSpooledFiles;
	private Table tableSpooledFiles;
	private int tableHeight;
	
	private class LabelProviderSpooledFiles extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			SpooledFile spooledFile = (SpooledFile)element;
			if (columnIndex == 0) {
				return spooledFile.getFile();
			} 
			else if (columnIndex == 1) {
				return Integer.toString(spooledFile.getFileNumber());
			} 
			else if (columnIndex == 2) {
				return spooledFile.getJobName();
			} 
			else if (columnIndex == 3) {
				return spooledFile.getJobUser();
			} 
			else if (columnIndex == 4) {
				return spooledFile.getJobNumber();
			} 
			else if (columnIndex == 5) {
				return spooledFile.getJobSystem();
			} 
			else if (columnIndex == 6) {
				return spooledFile.getCreationDate();
			} 
			else if (columnIndex == 7) {
				return spooledFile.getCreationTime();
			} 
			return "*UNKNOWN";
		}
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	}
	
	private class ContentProviderSpooledFiles implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			return spooledFiles;
		}
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	public ConfirmDeletionSpooledFiles(Shell parentShell, SpooledFile[] spooledFiles) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.spooledFiles = spooledFiles;
	}
	
	protected Control createDialogArea(Composite parent) {
		
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout());
        			
		tableViewerSpooledFiles = new TableViewer(container, SWT.FULL_SELECTION | SWT.BORDER);
		tableViewerSpooledFiles.setLabelProvider(new LabelProviderSpooledFiles());
		tableViewerSpooledFiles.setContentProvider(new ContentProviderSpooledFiles());

		tableSpooledFiles = tableViewerSpooledFiles.getTable();
		tableSpooledFiles.setLinesVisible(true);
		tableSpooledFiles.setHeaderVisible(true);
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableSpooledFiles.setLayoutData(gridData);

		final TableColumn tableColumnFile = new TableColumn(tableSpooledFiles, SWT.NONE);
		tableColumnFile.setWidth(Size.getSize(100));
		tableColumnFile.setText(Messages.getString("File"));

		final TableColumn tableColumnFileNumber = new TableColumn(tableSpooledFiles, SWT.NONE);
		tableColumnFileNumber.setWidth(Size.getSize(100));
		tableColumnFileNumber.setText(Messages.getString("File_number"));

		final TableColumn tableColumnJobName = new TableColumn(tableSpooledFiles, SWT.NONE);
		tableColumnJobName.setWidth(Size.getSize(100));
		tableColumnJobName.setText(Messages.getString("Job_name"));

		final TableColumn tableJobUser = new TableColumn(tableSpooledFiles, SWT.NONE);
		tableJobUser.setWidth(Size.getSize(100));
		tableJobUser.setText(Messages.getString("Job_user"));

		final TableColumn tableColumnJobNumber = new TableColumn(tableSpooledFiles, SWT.NONE);
		tableColumnJobNumber.setWidth(Size.getSize(100));
		tableColumnJobNumber.setText(Messages.getString("Job_number"));

		final TableColumn tableColumnJobSystem = new TableColumn(tableSpooledFiles, SWT.NONE);
		tableColumnJobSystem.setWidth(Size.getSize(100));
		tableColumnJobSystem.setText(Messages.getString("Job_system"));

		final TableColumn tableColumnCreationDate = new TableColumn(tableSpooledFiles, SWT.NONE);
		tableColumnCreationDate.setWidth(Size.getSize(100));
		tableColumnCreationDate.setText(Messages.getString("Creation_date"));

		final TableColumn tableColumnCreationTime = new TableColumn(tableSpooledFiles, SWT.NONE);
		tableColumnCreationTime.setWidth(Size.getSize(100));
		tableColumnCreationTime.setText(Messages.getString("Creation_time"));
		
		tableHeight = tableSpooledFiles.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y;
		tableViewerSpooledFiles.setInput(new Object());
		
		return container;
	}
	
	public int getTableHeight(int visibleTableItems) {
		return -tableSpooledFiles.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y + tableHeight + (tableSpooledFiles.getItemHeight() * visibleTableItems);
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.getString("Delete_Spooled_Files"), true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("Cancel"), false);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("Confirm_Deletion_Spooled_Files"));
	}
	
	protected Point getInitialSize() {
		Point point = getShell().computeSize(Size.getSize(600), SWT.DEFAULT, true);	
		point.y = point.y + getTableHeight(10);
		return point;
	}
	
}