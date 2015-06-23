// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this software; see the file COPYING.  If not, write to
// the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
// Boston, MA 02111-1307 USA

package de.taskforce.tn5250j.core.tn5250jeditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.part.EditorPart;

import de.taskforce.tn5250j.core.tn5250jpart.ITN5250JPart;
import de.taskforce.tn5250j.core.tn5250jpart.TN5250JPanel;
import de.taskforce.tn5250j.core.tn5250jpart.TN5250JPart;

public abstract class TN5250JEditor extends EditorPart implements ITN5250JPart, ISaveablePart2 {
 
	private TN5250JPart tn5250jPart;

	public void createPartControl(Composite parent) {
		
		tn5250jPart = 
			new TN5250JPart(
					this, 
					getEditorSite().getActionBars().getToolBarManager(),
					this,
					isMultiSession());
		
		tn5250jPart.createPartControl(parent);
		
	}
	
	public void init(IEditorSite site, IEditorInput input) {
       setSite(site);
       setInput(input);
       setPartName(input.getName());
       setTitleImage(((TN5250JEditorInput)input).getImage());
    }
    
	public void setFocus() {
    }
	
	public void dispose() {
		tn5250jPart.dispose();
		super.dispose();
	}
	
	public CTabFolder getTabFolderSessions() {
		return tn5250jPart.getTabFolderSessions();
	}

	public void addTN5250JPanel(TN5250JPanel tn5250jPanel) {
		tn5250jPart.addTN5250JPanel(tn5250jPanel);
	}

	public void removeTN5250JPanel(TN5250JPanel tn5250jPanel) {
		tn5250jPart.removeTN5250JPanel(tn5250jPanel);
	}
	
	public boolean isMultiSession() {
		return true;
	}

	public void setAddSession(boolean value) {
		tn5250jPart.setAddSession(value);
	}

	public void setRemoveSession(boolean value) {
		tn5250jPart.setRemoveSession(value);
	}

	public void setBindingService(boolean value) {
		tn5250jPart.setBindingService(value);
	}

	public void doSave(IProgressMonitor monitor) {
	}
	
	public void doSaveAs() {
	}

	public boolean isDirty() {
		return true;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public boolean isSaveOnCloseNeeded() {
		return true;
	}

	public int promptToSaveOnClose() {
		
		int result = tn5250jPart.closePart();
		
		if (result == TN5250JPart.CLOSE_PART_YES) {
			return ISaveablePart2.YES;
		}
		else {
			return ISaveablePart2.CANCEL;
		}
		
	}
	
 }
