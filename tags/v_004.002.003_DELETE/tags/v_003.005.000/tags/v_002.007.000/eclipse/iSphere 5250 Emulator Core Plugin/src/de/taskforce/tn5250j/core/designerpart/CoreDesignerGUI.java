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

package de.taskforce.tn5250j.core.designerpart;

import java.awt.event.KeyEvent;

import de.taskforce.tn5250j.core.tn5250jpart.ITN5250JPart;
import de.taskforce.tn5250j.core.tn5250jpart.RemoveSession;
import de.taskforce.tn5250j.core.tn5250jpart.TN5250JGUI;
import de.taskforce.tn5250j.core.tn5250jpart.TN5250JInfo;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.tn5250j.Session5250;

public abstract class CoreDesignerGUI extends TN5250JGUI {

	private static final long serialVersionUID = 1L;
	
	private class CloseDesignerAsync extends Thread {

		private Object[] visibleObjects;
		private StructuredViewer structuredViewer;
		private Object objectToBeSelected;
		
		public CloseDesignerAsync(Object[] visibleObjects, StructuredViewer structuredViewer, Object objectToBeSelected) {
			this.visibleObjects = visibleObjects;
			this.structuredViewer = structuredViewer;
			this.objectToBeSelected = objectToBeSelected;
		}

		public void run() {
			((Shell)visibleObjects[1]).getDisplay().asyncExec(new Runnable() {
				public void run() {
					boolean disposed = false;
					int elements = ((Integer)visibleObjects[0]).intValue();
					for (int idx = 2; idx < elements; idx++) {
						if (visibleObjects[idx] instanceof String) {
							String myString = (String)visibleObjects[idx];
							if (myString.startsWith("*VIEW-")) {
								String view = myString.substring(6);
								try {
									PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(view);
								} 
								catch (PartInitException e) {
								}
							}
						}
						else if (visibleObjects[idx] instanceof TabItem) {
							TabItem myTabItem = (TabItem)visibleObjects[idx];
							if (myTabItem.isDisposed()) {
								disposed = true;
								break;
							}
							TabItem[] tabItemToBeSelected = new TabItem[1];
							tabItemToBeSelected[0] = myTabItem;
							myTabItem.getParent().setSelection(tabItemToBeSelected);
						}
						else if (visibleObjects[idx] instanceof CTabItem) {
							CTabItem myCTabItem = (CTabItem)visibleObjects[idx];
							if (myCTabItem.isDisposed()) {
								disposed = true;
								break;
							}
							myCTabItem.getParent().setSelection(myCTabItem);
						}
					}
					if (!disposed && structuredViewer != null & objectToBeSelected != null) {
						structuredViewer.getControl().setFocus();
						structuredViewer.setSelection(new StructuredSelection(objectToBeSelected),true); 
					}
				}
			});
		}
	}
	
	private class RemoveCurrentTabItemAsync extends Thread {

		private CTabFolder tabFolderSessions;
		private ITN5250JPart tn5250jPart;

		public RemoveCurrentTabItemAsync(CTabFolder tabFolderSessions, ITN5250JPart tn5250jPart) {
			this.tabFolderSessions = tabFolderSessions;
			this.tn5250jPart = tn5250jPart;
		}

		public void run() {
			tabFolderSessions.getDisplay().asyncExec(new Runnable() {
				public void run() {
					RemoveSession.run(tabFolderSessions.getSelection(), tn5250jPart);
				}
			});
		}
	}
	
	public CoreDesignerGUI(TN5250JInfo tn5250jInfo, Session5250 session5250) {
		super(tn5250jInfo, session5250);
	}

	public void processKeyEvent(KeyEvent keyEvent) {
		if (keyEvent.getKeyCode() == KeyEvent.VK_F3) {
			if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
				boolean signOnScreen = false;
				char[] chr = getSession5250().getScreen().getScreenAsChars();
				if (chr[1] == 'T' && chr[2] == 'N' && chr[3] == '5' && chr[4] == '2' && chr[5] == '5' && chr[6] == '0' && chr[7] == 'J' && chr[8] == '-' && chr[9] == 'D' && chr[10] == 'E' && chr[11] == 'S' && chr[12] == 'I' && chr[13] == 'G' && chr[14] == 'N' && chr[15] == 'E' && chr[16] == 'R') {
					signOnScreen = true;
				}
				getSession5250().getScreen().sendKeys("[pf3]");
				if (signOnScreen) {
					
					new RemoveCurrentTabItemAsync(getTN5250JInfo().getTN5250JPart().getTabFolderSessions(), getTN5250JInfo().getTN5250JPart()).start();
					
					CoreDesignerInfo designerInfo = (CoreDesignerInfo)getTN5250JInfo();
					if (designerInfo.getVisibleObject() != null) {
						new CloseDesignerAsync(designerInfo.getVisibleObject(), designerInfo.getStructuredViewer(), designerInfo.getObjectToBeSelected()).start();
					}
				}
			}
		}
		else {
			super.processKeyEvent(keyEvent);
		}
	}
 
}
