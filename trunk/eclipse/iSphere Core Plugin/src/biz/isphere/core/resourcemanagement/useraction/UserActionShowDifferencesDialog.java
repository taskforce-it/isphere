/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.useraction;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.Size;

public class UserActionShowDifferencesDialog extends XDialog {

    private static final String COLUMN_WIDTH = "COLUMN_WIDTH_";

    private RSEUserActionBoth input;
    private TableViewer tableViewer;

    protected UserActionShowDifferencesDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);

        newShell.setText(Messages.User_Action_Compare_Result);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        createButton(parent, XDialog.OK, IDialogConstants.CLOSE_LABEL, true);
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        Composite container = (Composite)super.createDialogArea(parent);
        FillLayout fl_container = new FillLayout(SWT.HORIZONTAL);
        fl_container.marginHeight = 10;
        fl_container.marginWidth = 10;
        container.setLayout(fl_container);

        createTableViewer(container);

        return container;
    }

    private void createTableViewer(Composite container) {

        tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);

        Table table = tableViewer.getTable();

        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        // /
        // / Property name
        // /
        TableColumn columnPropertyName = new TableColumn(table, SWT.NONE);
        columnPropertyName.setWidth(Size.getSize(120));
        columnPropertyName.setResizable(true);
        columnPropertyName.setText(Messages.Property);

        // /
        // / repository value
        // /
        TableColumn columnRepositoryValue = new TableColumn(table, SWT.NONE);
        columnRepositoryValue.setWidth(Size.getSize(230));
        columnRepositoryValue.setResizable(true);
        columnRepositoryValue.setText(Messages.Repository_value);

        // /
        // / compare result icon
        // /
        TableColumn columnCompareResult = new TableColumn(table, SWT.CENTER);
        columnCompareResult.setWidth(16);
        columnCompareResult.setResizable(false);

        // /
        // / workspace value
        // /
        TableColumn columnWorkspaceValue = new TableColumn(table, SWT.NONE);
        columnWorkspaceValue.setWidth(Size.getSize(230));
        columnWorkspaceValue.setResizable(true);
        columnWorkspaceValue.setText(Messages.Workspace_value);

        tableViewer.setLabelProvider(new UserActionDifferencesLabelProvider());
        tableViewer.setContentProvider(new UserActionDifferencesContentProvider());
        tableViewer.setInput(input);

        loadScreenValues();
    }

    @Override
    protected void okPressed() {

        saveScreenValues();

        super.okPressed();
    }

    private void loadScreenValues() {

        TableColumn[] columns = tableViewer.getTable().getColumns();
        for (int i = 0; i < columns.length; i++) {
            int width = loadIntValue(getColumnKey(i), columns[i].getWidth());
            columns[i].setWidth(width);
        }
    }

    private void saveScreenValues() {

        TableColumn[] columns = tableViewer.getTable().getColumns();
        for (int i = 0; i < columns.length; i++) {
            storeValue(getColumnKey(i), columns[i].getWidth());
        }
    }

    private String getColumnKey(int i) {
        return COLUMN_WIDTH + i;
    }

    public void setInput(RSEUserActionBoth input) {
        this.input = input;
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
        return new Point(800, 400);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

    private class UserActionDifferencesLabelProvider extends LabelProvider implements ITableLabelProvider, IColorProvider {

        private Image compareNotEqual = ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_COPY_NOT_EQUAL);
        private Image compareEqual = ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_COPY_EQUAL);

        private boolean setBackgroundColor;

        public Image getColumnImage(Object object, int index) {

            if (index == 2) {
                CompareItem compareItem = (CompareItem)object;
                if (compareItem.isEqual()) {
                    return compareEqual;
                } else {
                    return compareNotEqual;
                }
            }

            return null;
        }

        public String getColumnText(Object object, int index) {

            CompareItem compareItem = (CompareItem)object;

            setBackgroundColor = true;

            switch (index) {
            case 0: // Property name
                return compareItem.getProperty();
            case 1: // Repository
                return compareItem.getRepositoryValue();
            case 2: // Compare image
                setBackgroundColor = false;
                return null;
            case 3: // Workspace
                return compareItem.getWorkspaceValue();
            default:
                break;
            }

            return null;
        }

        public Color getForeground(Object object) {
            return null;
        }

        public Color getBackground(Object object) {

            CompareItem compareItem = (CompareItem)object;
            if (setBackgroundColor && !compareItem.isEqual()) {
                return Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
            }

            return null;
        }
    }

    private class UserActionDifferencesContentProvider implements IStructuredContentProvider {

        private List<CompareItem> compareItems;

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

            compareItems = new LinkedList<CompareItem>();

            RSEUserActionBoth rseUserActionBoth = (RSEUserActionBoth)newInput;
            if (rseUserActionBoth != null) {
                RSEUserAction resourceRepository = rseUserActionBoth.getResourceRepository();
                RSEUserAction resourceWorkspace = rseUserActionBoth.getResourceWorkspace();

                compareItems.add(new CompareItem(Messages.Property_Comment, resourceRepository.getComment(), resourceWorkspace.getComment()));
                compareItems.add(new CompareItem(Messages.Property_Command, resourceRepository.getCommandString(), resourceWorkspace
                    .getCommandString()));
                compareItems.add(new CompareItem(Messages.Property_Run_environment, resourceRepository.getRunEnvironment(), resourceWorkspace
                    .getRunEnvironment()));
                compareItems.add(new CompareItem(Messages.Property_Prompt_first, resourceRepository.isPromptFirst(), resourceWorkspace
                    .isPromptFirst()));
                compareItems.add(new CompareItem(Messages.Property_Refresh_after, resourceRepository.isRefreshAfter(), resourceWorkspace
                    .isRefreshAfter()));
                compareItems.add(new CompareItem(Messages.Property_Show_action, resourceRepository.isShowAction(), resourceWorkspace.isShowAction()));
                compareItems.add(new CompareItem(Messages.Property_Single_selection_only, resourceRepository.isSingleSelection(), resourceWorkspace
                    .isSingleSelection()));
                compareItems.add(new CompareItem(Messages.Property_Invoke_once, resourceRepository.isInvokeOnce(), resourceWorkspace.isInvokeOnce()));
                compareItems.add(new CompareItem(Messages.Property_File_types, resourceRepository.getFileTypes(), resourceWorkspace.getFileTypes()));
            }
        }

        public Object[] getElements(Object paramObject) {
            return compareItems.toArray(new CompareItem[compareItems.size()]);
        }

        public void dispose() {
        }
    }
}
