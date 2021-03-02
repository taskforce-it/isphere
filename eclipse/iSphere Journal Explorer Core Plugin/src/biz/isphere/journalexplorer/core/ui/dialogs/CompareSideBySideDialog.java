/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.dialogs;

import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.JournalEntryComparator;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.ui.widgets.JournalEntryDetailsViewer;

public class CompareSideBySideDialog extends XDialog {

    private static final String COLUMN_WIDTH = "COLUMN_WIDTH_"; //$NON-NLS-1$
    private final static String DISPLAY_CHANGED_RECORDS_ONLY = "DISPLAY_CHANGED_RECORDS_ONLY"; //$NON-NLS-1$

    private Button chkDisplayChangedFieldsOnly;
    private JournalEntryDetailsViewer leftEntry;
    private JournalEntryDetailsViewer rightEntry;
    private Label lblLeftEntry;
    private Label lblRightEntry;
    private Composite sideBySideContainer;
    private ScrolledComposite sc;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public CompareSideBySideDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        Composite container = (Composite)super.createDialogArea(parent);

        createComparePanel(container);

        createOptionsPanel(parent);

        loadScreenValues();

        return container;
    }

    private void createComparePanel(Composite container) {

        Composite mainPanel = new Composite(container, SWT.BORDER);
        mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        FillLayout fl_container = new FillLayout(SWT.HORIZONTAL);
        mainPanel.setLayout(fl_container);

        sc = new ScrolledComposite(mainPanel, SWT.H_SCROLL | SWT.V_SCROLL);

        sideBySideContainer = new Composite(sc, SWT.NONE);
        GridLayout sideBySideLayout = new GridLayout(2, true);
        sideBySideLayout.marginHeight = 0;
        sideBySideLayout.marginWidth = 0;
        sideBySideContainer.setLayout(sideBySideLayout);

        Composite leftComposite = new Composite(sideBySideContainer, SWT.NONE);
        leftComposite.setLayout(new GridLayout(1, false));
        leftComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        lblLeftEntry = new Label(leftComposite, SWT.NONE);
        lblLeftEntry.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        leftEntry = new JournalEntryDetailsViewer(leftComposite);
        Tree leftTree = leftEntry.getTree();
        leftTree.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite rightComposite = new Composite(sideBySideContainer, SWT.NONE);
        rightComposite.setLayout(new GridLayout(1, false));
        rightComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        lblRightEntry = new Label(rightComposite, SWT.NONE);
        lblRightEntry.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        rightEntry = new JournalEntryDetailsViewer(rightComposite);
        Tree rightTree = rightEntry.getTree();
        rightTree.setLayoutData(new GridData(GridData.FILL_BOTH));

        // Set the child as the scrolled content of the ScrolledComposite
        sc.setContent(sideBySideContainer);

        // Expand both horizontally and vertically
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);

        addColumnResizeListener(leftEntry, rightEntry);
        addColumnResizeListener(rightEntry, leftEntry);
    }

    private ColumnResizeListener addColumnResizeListener(JournalEntryDetailsViewer viewer, JournalEntryDetailsViewer connectedViewer) {

        ColumnResizeListener columnResizeListener = new ColumnResizeListener(viewer, connectedViewer);
        viewer.setColumnResizeListener(columnResizeListener);

        return columnResizeListener;
    }

    private void createOptionsPanel(Composite parent) {

        Composite optionsPanel = new Composite(parent, SWT.NONE);
        GridLayout gl_optionsPanel = new GridLayout();
        gl_optionsPanel.marginBottom = 20;
        optionsPanel.setLayout(gl_optionsPanel);
        optionsPanel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        chkDisplayChangedFieldsOnly = WidgetFactory.createCheckbox(optionsPanel, Messages.CompareSideBySideDialog_Label_Changed_records_only);
        chkDisplayChangedFieldsOnly.setToolTipText(Messages.CompareSideBySideDialog_Tooltip_Changed_records_only);
        chkDisplayChangedFieldsOnly.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                setDisplayChangedFieldsOnly(leftEntry, chkDisplayChangedFieldsOnly.getSelection());
                setDisplayChangedFieldsOnly(rightEntry, chkDisplayChangedFieldsOnly.getSelection());
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }

            private void setDisplayChangedFieldsOnly(JournalEntryDetailsViewer viewer, boolean enabled) {
                if (viewer != null) {
                    viewer.setDisplayChangedFieldsOnly(enabled);
                }
            }
        });
    }

    public void setInput(JournalProperties leftEntryData, JournalProperties rightEntryData) {

        new JournalEntryComparator().compare(leftEntryData, rightEntryData);

        lblLeftEntry.setText(leftEntryData.toString());
        leftEntry.setInput(new Object[] { leftEntryData });
        leftEntry.expandAll();

        lblRightEntry.setText(rightEntryData.toString());
        rightEntry.setInput(new Object[] { rightEntryData });
        rightEntry.expandAll();

        // Compute size
        Point point = sideBySideContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT);

        // Set the minimum size
        sc.setMinSize(550, point.y);
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setImage(ISphereJournalExplorerCorePlugin.getDefault().getImage(ISphereJournalExplorerCorePlugin.IMAGE_HORIZONTAL_RESULTS_VIEW));
        newShell.setText(Messages.SideBySideCompareDialog_SideBySideComparison);
    }

    @Override
    public boolean close() {

        storeScreenValues();

        leftEntry.dispose();
        rightEntry.dispose();

        return super.close();
    }

    private void storeScreenValues() {

        storeValue(DISPLAY_CHANGED_RECORDS_ONLY, leftEntry.isDisplayChangedFieldsOnly());

        Set<String> columnNames = leftEntry.getColumnNames();
        for (String columnName : columnNames) {
            int width = leftEntry.getColumnWidth(columnName);
            storeValue(COLUMN_WIDTH + columnName, width);
        }
    }

    private void loadScreenValues() {

        chkDisplayChangedFieldsOnly.setSelection(loadBooleanValue(DISPLAY_CHANGED_RECORDS_ONLY, false));

        leftEntry.setDisplayChangedFieldsOnly(chkDisplayChangedFieldsOnly.getSelection());
        rightEntry.setDisplayChangedFieldsOnly(chkDisplayChangedFieldsOnly.getSelection());

        Set<String> columnNames = leftEntry.getColumnNames();
        for (String columnName : columnNames) {
            int width = loadIntValue(COLUMN_WIDTH + columnName, -1);
            if (width > 0) {
                leftEntry.setColumnWidth(columnName, width);
                rightEntry.setColumnWidth(columnName, width);
            }
        }
    }

    /**
     * Overridden make this dialog resizable {@link XDialog}.
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        return new Point(1020, 700);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISphereJournalExplorerCorePlugin.getDefault().getDialogSettings());
    }
}
