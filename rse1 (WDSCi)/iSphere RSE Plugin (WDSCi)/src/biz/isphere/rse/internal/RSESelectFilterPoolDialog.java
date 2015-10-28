/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.Size;

import com.ibm.etools.systems.filters.SystemFilterPool;

public class RSESelectFilterPoolDialog extends XDialog {

    private SystemFilterPool[] filterPools;

    private TableViewer tableViewer;
    private SystemFilterPool selectedFilterPool;

    public RSESelectFilterPoolDialog(Shell parentShell, SystemFilterPool[] filterPools) {
        super(parentShell);

        this.filterPools = filterPools;
        this.selectedFilterPool = null;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);

        newShell.setText("Select Filter Pool");
    }

    @Override
    public Control createDialogArea(Composite parent) {

        Composite dialogArea = new Composite(parent, SWT.NONE);
        dialogArea.setLayout(new GridLayout());
        dialogArea.setLayoutData(createGridDataFillAndGrab());

        tableViewer = new TableViewer(dialogArea, SWT.FULL_SELECTION | SWT.BORDER);
        tableViewer.setLabelProvider(new RSESelectFilterPoolLabelProvider());
        tableViewer.setContentProvider(new RSESelectFilterPoolContentProvider());
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                okPressed();
            }
        });
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                setButtonEnablement();
                if (event.getSelection() instanceof StructuredSelection) {
                    StructuredSelection selection = (StructuredSelection)event.getSelection();
                    selectedFilterPool = (SystemFilterPool)selection.getFirstElement();
                }
            }
        });

        Table table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setLayoutData(createGridDataFillAndGrab());

        TableColumn columnName = new TableColumn(table, SWT.NONE);
        columnName.setWidth(Size.getSize(300));
        columnName.setText(Messages.Name);

        // dDialogsList.setItems(getListItems());
        tableViewer.setInput(filterPools);

        if (selectedFilterPool != null) {
            tableViewer.setSelection(new StructuredSelection(selectedFilterPool));
        }

        setButtonEnablement();

        return dialogArea;
    }

    @Override
    protected void okPressed() {

        // Close dialog
        super.okPressed();
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    public SystemFilterPool getSelectedFilterPool() {

        return selectedFilterPool;
    }

    public void setSelectedFilterPool(SystemFilterPool filterPool) {

        selectedFilterPool = filterPool;

        if (tableViewer != null) {
            tableViewer.setSelection(new StructuredSelection(selectedFilterPool));
        }
    }

    private void setButtonEnablement() {

        Button okButton = getButton(IDialogConstants.OK_ID);
        if (okButton == null) {
            return;
        }

        if (getSelectedFilterPool() != null) {
            okButton.setEnabled(true);
        } else {
            okButton.setEnabled(false);
        }
    }

    private GridData createGridDataFillAndGrab() {

        GridData layoutData = new GridData();
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.verticalAlignment = SWT.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;

        return layoutData;
    }

    @Override
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {

        Button button = super.createButton(parent, id, label, defaultButton);

        if (id == OK) {
            setButtonEnablement();
        }

        return button;
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

    private class RSESelectFilterPoolContentProvider implements IStructuredContentProvider {

        private SystemFilterPool[] pools;

        public Object[] getElements(Object inputElement) {
            return pools;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            pools = (SystemFilterPool[])newInput;
        }
    }

    private class RSESelectFilterPoolLabelProvider extends LabelProvider implements ITableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {

            SystemFilterPool pool = (SystemFilterPool)element;
            if (columnIndex == 0) {
                return pool.getName();
            }
            return "*UNKNOWN"; //$NON-NLS-1$
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }
}
