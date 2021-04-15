/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.objectselector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.objectselector.model.AbstractListItem;
import biz.isphere.core.swt.widgets.objectselector.model.LibraryItem;
import biz.isphere.core.swt.widgets.objectselector.model.LibraryListItem;
import biz.isphere.core.swt.widgets.objectselector.model.ListItemsFactory;
import biz.isphere.core.swt.widgets.objectselector.model.ObjectItem;
import biz.isphere.core.swt.widgets.objectselector.model.QSYSObjectTypes;
import biz.isphere.core.swt.widgets.objectselector.model.SelectedObject;
import biz.isphere.core.swt.widgets.objectselector.model.SystemItem;

import com.ibm.as400.access.AS400;

public class SelectQSYSObjectDialog extends XDialog implements ISelectQSYSObjectDialog {

    private String connectionName;
    private String objectType;
    private String objectLabel;

    private boolean expandLibraryList;

    private Label toDoLabel;
    private Composite frame;
    private Label objectDescription;
    private TreeViewer viewer;

    private boolean isCurrentLibraryEnabled;
    private boolean isLibraryListEnabled;
    private ObjectItem selectedObject;

    private Set<String> libraryNames;

    public static ISelectQSYSObjectDialog createSelectLibraryDialog(Shell shell, String connectionName) {
        return new SelectQSYSObjectDialog(shell, connectionName, QSYSObjectTypes.LIB);
    }

    public static ISelectQSYSObjectDialog createSelectMessageFileDialog(Shell shell, String connectionName) {
        return new SelectQSYSObjectDialog(shell, connectionName, QSYSObjectTypes.MSGF);
    }

    private SelectQSYSObjectDialog(Shell parentShell, String connectionName, QSYSObjectTypes objectTypeFilter) {
        this(parentShell, connectionName, objectTypeFilter.type(), objectTypeFilter.label());
    }

    private SelectQSYSObjectDialog(Shell parentShell, String connectionName, String objectType, String objectLabel) {
        super(parentShell);

        this.connectionName = connectionName;
        this.objectType = objectType;
        this.objectLabel = objectLabel;

        this.expandLibraryList = false;
        this.libraryNames = new HashSet<String>();
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);

        if (ISeries.LIB.equals(objectType)) {
            newShell.setText(Messages.bind("Browse for {0}", objectLabel));
        } else if (ISeries.MSGF.equals(objectType)) {
            newShell.setText(Messages.bind("Browse for {0}", objectLabel));
        } else {
            newShell.setText(Messages.bind("Browse for {0}", "Object"));
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite container = (Composite)super.createDialogArea(parent);
        GridLayout containerLayout = new GridLayout();
        containerLayout.marginWidth = 10;
        container.setLayout(containerLayout);

        toDoLabel = new Label(container, SWT.NONE);
        toDoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toDoLabel.setText(NLS.bind("Select {0}:", objectLabel));

        WidgetFactory.createLineFiller(container);

        frame = new Composite(container, SWT.BORDER);
        GridLayout frameLayout = new GridLayout();
        frameLayout.marginWidth = 2;
        frameLayout.marginHeight = 2;
        frame.setLayout(frameLayout);
        frame.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        objectDescription = new Label(frame, SWT.NONE);
        objectDescription.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        viewer = new TreeViewer(container);
        viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        viewer.setContentProvider(new TreeContentProvider());
        viewer.setLabelProvider(new TreeLabelProvider());
        viewer.getTree().setHeaderVisible(false);
        viewer.getTree().setLinesVisible(false);

        TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
        viewerColumn.getColumn().setWidth(300);
        viewerColumn.setLabelProvider(new TreeLabelProvider());

        setListItems();

        configureControls();

        return dialogArea;
    }

    private void setListItems() {

        SystemItem systemItem = ListItemsFactory.createSystem(getSystem(), connectionName, objectType);

        Set<AbstractListItem> expandedItems = new HashSet<AbstractListItem>();

        if (isCurrentLibraryEnabled) {
            systemItem.addCurrentLibrary();
        }

        if (isLibraryListEnabled) {
            LibraryListItem libraryListItem = systemItem.addLibraryList();
            if (libraryListItem != null) {
                expandedItems.add(libraryListItem);
            }
        }

        if (libraryNames != null) {
            String[] sortedLibraryNames = libraryNames.toArray(new String[libraryNames.size()]);
            Arrays.sort(sortedLibraryNames);
            for (String libraryName : sortedLibraryNames) {
                LibraryItem libraryItem = systemItem.addLibrary(libraryName);
                if (libraryItem != null) {
                    expandedItems.add(libraryItem);
                }
            }
        }

        viewer.setInput(new SystemItem[] { systemItem });

        viewer.setExpandedState(systemItem, true);

        if (expandLibraryList) {
            Iterator<AbstractListItem> it = expandedItems.iterator();
            while (it.hasNext()) {
                viewer.setExpandedState(it.next(), true);
            }
        }
    }

    private void configureControls() {

        viewer.addSelectionChangedListener(new TreeSelectionChangedListener());
        viewer.addDoubleClickListener(new TreeDoubleClickListener());
    }

    public void setExpandLibraryListsEnabled(boolean enabled) {
        expandLibraryList = enabled;
    }

    public void setCurrentLibraryEnabled(boolean enabled) {
        isCurrentLibraryEnabled = enabled;
    }

    public void setLibraryListEnabled(boolean enabled) {
        isLibraryListEnabled = enabled;
    }

    public void addLibrary(String libraryName) {
        libraryNames.add(libraryName);
    }

    @Override
    protected void okPressed() {

        if (!isValidated()) {
            return;
        }

        super.okPressed();
    }

    private boolean isValidated() {

        if (selectedObject == null) {
            getButton(Dialog.OK).setEnabled(false);
            return false;
        }

        if (!isValidObjectType(selectedObject)) {
            getButton(Dialog.OK).setEnabled(false);
            return false;
        }

        getButton(Dialog.OK).setEnabled(true);

        return true;
    }

    private boolean isValidObjectType(AbstractListItem listItem) {

        if (!(listItem instanceof ObjectItem)) {
            throw new IllegalArgumentException("Unexpected object type: " + listItem.getClass().getName());
        }

        Set<String> types = new HashSet<String>(Arrays.asList(objectType));
        boolean isFound = types.contains(listItem.getObjectType());

        return isFound;
    }

    public ISelectedObject getSelectedItem() {

        ObjectItem objectItem = (ObjectItem)selectedObject;

        String libraryName = objectItem.getLibrary();
        String objectName = objectItem.getName();
        String objectType = objectItem.getObjectType();

        return new SelectedObject(connectionName, libraryName, objectName, objectType);
    }

    public AS400 getSystem() {
        return IBMiHostContributionsHandler.getSystem(connectionName);
    }

    /**
     * Overridden make this dialog resizable {@link XDialog}.
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Overridden to return a default size.
     */
    @Override
    protected Point getDefaultSize() {
        return new Point(300, 500);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

    private class TreeLabelProvider extends CellLabelProvider {
        public TreeLabelProvider() {
            return;
        }

        @Override
        public boolean isLabelProperty(Object element, String property) {
            return super.isLabelProperty(element, property);
        }

        @Override
        public void update(ViewerCell cell) {
            Object element = cell.getElement();
            if (element instanceof AbstractListItem) {
                AbstractListItem listItem = (AbstractListItem)element;
                cell.setText(listItem.getLabel());
                cell.setImage(listItem.getImage());
            }
        }

    }

    private class TreeContentProvider implements ITreeContentProvider {

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        public boolean hasChildren(Object element) {

            if (element instanceof AbstractListItem) {
                return ((AbstractListItem)element).hasChildren();
            }

            return false;
        }

        public Object getParent(Object element) {
            return null;
        }

        public Object[] getElements(Object inputElement) {
            return ArrayContentProvider.getInstance().getElements(inputElement);
        }

        public AbstractListItem[] getChildren(Object element) {

            if (element instanceof AbstractListItem) {
                AbstractListItem abstractListItem = (AbstractListItem)element;
                return abstractListItem.getChildren();
            }

            return null;
        }
    }

    private class TreeSelectionChangedListener implements ISelectionChangedListener {
        public void selectionChanged(SelectionChangedEvent event) {
            ISelection selection = event.getSelection();
            if (selection instanceof ITreeSelection) {
                ITreeSelection treeSelection = (ITreeSelection)selection;
                Object firstElement = treeSelection.getFirstElement();
                showObjectDescription(firstElement);
            }
        }

        private void showObjectDescription(Object element) {
            objectDescription.setText("");
            if (element instanceof ObjectItem) {
                ObjectItem objectItem = (ObjectItem)element;
                selectedObject = (ObjectItem)objectItem;
                if (isValidated()) {
                    objectDescription.setText(String.format("%s - %s", selectedObject.getName(), selectedObject.getDescription())); //$NON-NLS-1$
                }
            }
        }
    }

    private class TreeDoubleClickListener implements IDoubleClickListener {
        public void doubleClick(DoubleClickEvent event) {
            ISelection selection = event.getSelection();
            if (selection instanceof ITreeSelection) {
                ITreeSelection treeSelection = (ITreeSelection)selection;
                Object firstElement = treeSelection.getFirstElement();
                selectAndReturnElement(firstElement);
                TreeViewer treeViewer = (TreeViewer)event.getViewer();
                toggleExpandedStateOfElement(treeViewer, firstElement);
            }
        }

        private void selectAndReturnElement(Object element) {
            if (element instanceof ObjectItem) {
                ObjectItem objectItem = (ObjectItem)element;
                selectedObject = (ObjectItem)objectItem;
                if (isValidated()) {
                    okPressed(); // Closes the dialog.
                }
            }
        }

        private void toggleExpandedStateOfElement(TreeViewer treeViewer, Object firstElement) {
            if (treeViewer.getExpandedState(firstElement)) {
                treeViewer.setExpandedState(firstElement, false);
            } else {
                treeViewer.setExpandedState(firstElement, true);
            }
        }
    }
}
