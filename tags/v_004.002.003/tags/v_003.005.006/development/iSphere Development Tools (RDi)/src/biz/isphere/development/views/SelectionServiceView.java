/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.development.views;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IMarkSelection;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;

/**
 * This view simply mirrors the current selection in the workbench window. It
 * works for both, element and text selection.
 */
public class SelectionServiceView extends ViewPart {

    private PageBook pagebook;
    private TableViewer tableviewer;
    private TextViewer textviewer;

    // the listener we register with the selection service
    private ISelectionListener listener = new ISelectionListener() {
        public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
            // we ignore our own selections
            if (sourcepart != SelectionServiceView.this) {
                showSelection(sourcepart, selection);
            }
        }
    };

    /**
     * Shows the given selection in this view.
     */
    private void showSelection(IWorkbenchPart sourcepart, ISelection selection) {
        setContentDescription(sourcepart.getTitle() + " (" + selection.getClass().getName() + ")");
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection)selection;
            showText(getClassNames(ss.toArray()));
            // showItems(ss.toArray());
        } else if (selection instanceof ITextSelection) {
            ITextSelection ts = (ITextSelection)selection;
            showText(ts.getText());
        } else if (selection instanceof IMarkSelection) {
            IMarkSelection ms = (IMarkSelection)selection;
            try {
                showText(ms.getDocument().get(ms.getOffset(), ms.getLength()));
            } catch (BadLocationException ble) {
            }
        } else {
            showText("Unsupported object type: " + selection.getClass().getName());
        }
    }

    private String getClassNames(Object[] objects) {

        StringBuilder classNames = new StringBuilder();
        for (Object object : objects) {
            classNames.append(object.getClass().getName());
            classNames.append("\n");
        }

        return classNames.toString();
    }

    private void showItems(Object[] items) {
        tableviewer.setInput(items);
        pagebook.showPage(tableviewer.getControl());
    }

    private void showText(String text) {
        textviewer.setDocument(new Document(text));
        pagebook.showPage(textviewer.getControl());
    }

    public void createPartControl(Composite parent) {
        // the PageBook allows simple switching between two viewers
        pagebook = new PageBook(parent, SWT.NONE);

        tableviewer = new TableViewer(pagebook, SWT.NONE);
        tableviewer.setLabelProvider(new WorkbenchLabelProvider());
        tableviewer.setContentProvider(new ArrayContentProvider());

        // we're cooperative and also provide our selection
        // at least for the tableviewer
        getSite().setSelectionProvider(tableviewer);

        textviewer = new TextViewer(pagebook, SWT.H_SCROLL | SWT.V_SCROLL);
        textviewer.setEditable(false);

        getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(listener);
    }

    public void setFocus() {
        pagebook.setFocus();
    }

    public void dispose() {
        // important: We need do unregister our listener when the view is
        // disposed
        getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(listener);
        super.dispose();
    }

}
