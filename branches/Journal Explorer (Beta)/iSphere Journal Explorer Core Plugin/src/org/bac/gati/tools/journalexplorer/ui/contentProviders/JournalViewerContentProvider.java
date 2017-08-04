package org.bac.gati.tools.journalexplorer.ui.contentProviders;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import biz.isphere.journalexplorer.core.model.JournalEntry;

public class JournalViewerContentProvider implements ILazyContentProvider {

    private ArrayList<JournalEntry> elements;
    private TableViewer viewer;

    public JournalViewerContentProvider(TableViewer viewer) {
        this.viewer = viewer;
    }

    public void dispose() {
    }

    @SuppressWarnings("unchecked")
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        if (newInput != null) {
            this.elements = (ArrayList<JournalEntry>)newInput;
        } else {
            this.elements = null;
        }
    }

    public void updateElement(int index) {

        if (getInput() == null || getInput().length < index + 1) {
            return;
        }

        this.viewer.replace(elements.get(index), index);
    }

    public Object[] getInput() {

        if (this.elements != null) {
            return this.elements.toArray();
        } else {
            return null;
        }
    }

}
