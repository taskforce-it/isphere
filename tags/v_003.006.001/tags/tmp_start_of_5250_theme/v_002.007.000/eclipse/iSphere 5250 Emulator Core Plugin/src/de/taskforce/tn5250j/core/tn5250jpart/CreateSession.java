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

package de.taskforce.tn5250j.core.tn5250jpart;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.albireo.core.SwingControl;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.keys.IBindingService;

import de.taskforce.tn5250j.core.TN5250JCorePlugin;
import de.taskforce.tn5250j.core.session.Session;

public class CreateSession {

	private ITN5250JPart tn5250jPart;
	
	public TN5250JPanel run(Composite compositeControl, ArrayList<Composite> arrayListCompositeSession, ArrayList<TN5250JPanel> arrayListTabItemTN5250J, Session session, TN5250JInfo tn5250jInfo) {

		ITN5250JPart tn5250jPart = tn5250jInfo.getTN5250JPart();
		
		this.tn5250jPart = tn5250jPart;
		
		Composite compositeBorder = new Composite(compositeControl, SWT.NONE);
		compositeBorder.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		compositeBorder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		FillLayout fillLayoutBorder = new FillLayout();
		fillLayoutBorder.marginWidth = 3;
		fillLayoutBorder.marginHeight = 3;
		compositeBorder.setLayout(fillLayoutBorder);
		
		ScrolledComposite sc = new ScrolledComposite(compositeBorder, SWT.H_SCROLL | SWT.V_SCROLL);
		
		final Composite compositeSession = new Composite(sc, SWT.NONE);
		compositeSession.setLayout(new FillLayout());
		
	    // Set the child as the scrolled content of the ScrolledComposite
	    sc.setContent(compositeSession);

	    // Set the minimum size
		IPreferenceStore store = TN5250JCorePlugin.getDefault().getPreferenceStore();
		if (store.getString("DE.TASKFORCE.TN5250J.MSACTIVE").equals("Y")) {
			int hSize;
			try {
				hSize = Integer.parseInt(store.getString("DE.TASKFORCE.TN5250J.MSHSIZE"));
			} 
			catch (NumberFormatException e1) {
				hSize = 0;
			}
			int vSize;
			try {
				vSize = Integer.parseInt(store.getString("DE.TASKFORCE.TN5250J.MSVSIZE"));
			} 
			catch (NumberFormatException e1) {
				vSize = 0;
			}
		    sc.setMinSize(hSize, vSize);
		}
		else {
		    sc.setMinSize(0, 0);
		}

	    // Expand both horizontally and vertically
	    sc.setExpandHorizontal(true);
	    sc.setExpandVertical(true);
	        
     	final TN5250JPanel tn5250j = tn5250jInfo.getTN5250JPanel(session, tn5250jPart.getTabFolderSessions().getShell());
	    
		tn5250j.addScreenListener();
		
		tn5250j.getSessionGUI().addFocusListener(new java.awt.event.FocusListener() {
			public void focusGained(java.awt.event.FocusEvent event) {
				processFocusGained(event);
			}
			public void focusLost(java.awt.event.FocusEvent event) {
				processFocusLost(event);
			}
		});

		SwingControl swingControl = new SwingControl(compositeSession, SWT.NONE) {
			protected JComponent createSwingComponent() {
				
		        JPanel panel = new JPanel(new BorderLayout());
		        
			    panel.add(tn5250j.getSessionGUI());
			    
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						tn5250j.getSessionGUI().grabFocus();
					}
				});
			    
				return panel;
			
			}
			public Composite getLayoutAncestor() {
				return compositeSession;
			}
		};
		swingControl.addKeyListener(new KeyAdapter() {
			public void keyPressed(final KeyEvent keyEvent) {
				Composite sourceComposite = (Composite)keyEvent.getSource();
				final TN5250JPanel sourceTN5250J = (TN5250JPanel)sourceComposite.getData("CompositeTN5250J");
				if (keyEvent.keyCode == SWT.F10) {
					keyEvent.doit = false;
					if ((keyEvent.stateMask & SWT.SHIFT) == 0) {
						sourceTN5250J.getSession5250().getScreen().sendKeys("[pf10]");
					}
					else {
						sourceTN5250J.getSession5250().getScreen().sendKeys("[pf22]");
					}
				}
				else if (keyEvent.keyCode == SWT.TAB) {
					keyEvent.doit = false;
					if ((keyEvent.stateMask & SWT.SHIFT) == 0) {
						sourceTN5250J.getSession5250().getScreen().sendKeys("[tab]");
					}
					else {
						sourceTN5250J.getSession5250().getScreen().sendKeys("[backtab]");
					}
				}
				else if ((keyEvent.stateMask & SWT.CTRL) != 0 && keyEvent.keyCode == SWT.F12) {
					keyEvent.doit = false;
					BindingService bindingService = (BindingService)PlatformUI.getWorkbench().getService(IBindingService.class);
					if (!bindingService.isKeyFilterEnabled()) {
						HandleBindingService.setBindingService(true);
					}
					else {
						HandleBindingService.setBindingService(false);
					}
				}
			}
		});
		swingControl.setData("CompositeTN5250J", tn5250j);
		
		compositeBorder.setData("ScrolledComposite", sc);
		compositeBorder.setData("CompositeTN5250J", tn5250j);
		
		arrayListCompositeSession.add(compositeBorder);
		arrayListTabItemTN5250J.add(tn5250j);
		tn5250jPart.addTN5250JPanel(tn5250j);
		
		if (tn5250jPart.isMultiSession()) {
			tn5250jPart.setRemoveSession(true);
		}
		
		return tn5250j;
		
	}

	private void processFocusGained(java.awt.event.FocusEvent event) {
		new ProcessSessionFocus(tn5250jPart.getTabFolderSessions().getDisplay(), tn5250jPart.getTabFolderSessions(), "*GAINED", event).start();
	}

	private void processFocusLost(java.awt.event.FocusEvent event) {
		if (!tn5250jPart.getTabFolderSessions().isDisposed()) {
			new ProcessSessionFocus(tn5250jPart.getTabFolderSessions().getDisplay(), tn5250jPart.getTabFolderSessions(), "*LOST", event).start();
		}
	}
	
}
