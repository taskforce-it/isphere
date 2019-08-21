package org.bac.gati.tools.journalexplorer.ui.contentProviders;

import org.bac.gati.tools.journalexplorer.model.adapters.JOESDProperty;
import org.bac.gati.tools.journalexplorer.model.adapters.JournalProperties;
import org.bac.gati.tools.journalexplorer.model.adapters.JournalProperty;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class JournalPropertiesContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	private Object[] input;
	
	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		if (newInput instanceof Object[]) {
			Object[] inputArray = (Object[]) newInput;
			
			for (Object inputObject : inputArray) {
				if (!(inputObject instanceof JournalProperties)) {
					this.input = null;
					return;
				}
			}
 			
			if (this.input == null || !equalInput((Object[]) newInput, input)) {
				this.input = (Object[]) newInput;
			}
		} else {
			
			this.input = null;
		}
	}
 
	private boolean equalInput(Object[] newInput, Object[] currentInput) {
		
		int newInputLength = newInput.length;
		int currentInputLength = currentInput.length;
		JournalProperties newInputObject;
		JournalProperties oldInputObject;
		
		if (newInputLength != currentInputLength) {
			return false;
		} else {
			for (int i = 0; i < newInputLength; i++) {
				newInputObject = (JournalProperties) newInput[i];
				oldInputObject = (JournalProperties) currentInput[i];
				
				if (!newInputObject.getJournal().equals(oldInputObject.getJournal())) {
					return false;
				}
			}
			return true;
		}
	}

	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof JournalProperties) {
			return ((JournalProperties) parent).toArray();
		} else if (parent instanceof JOESDProperty) {
			return ((JOESDProperty) parent).toPropertyArray();
		} else {
			return new Object[0]; 
		}
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof JournalProperty) {
			return ((JournalProperty) element).parent;
		} else  {
			return null;
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		
		if (element instanceof JournalProperties) {
			return true;
		} 
		else if (element instanceof JOESDProperty) { 
			return true;
		} else {
		
			return false;
		}
	}

	@Override 
	public Object[] getElements(Object inputElement) {
		return this.input;
	}
}

