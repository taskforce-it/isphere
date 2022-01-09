package org.bac.gati.tools.journalexplorer.ui.contentProviders;

import java.util.ArrayList;

import org.bac.gati.tools.journalexplorer.model.Journal;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

public class JournalViewerContentProvider implements ILazyContentProvider  {

	private ArrayList<Journal> elements;
	private TableViewer viewer;
	
	public JournalViewerContentProvider(TableViewer viewer) {
		this.viewer = viewer;
	}
	
	@Override
	public void dispose() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput != null ) {
			this.elements = (ArrayList<Journal>) newInput;
		} else {
			this.elements = null;
		}
		
	}
	
	@Override
	public void updateElement(int index) {
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
