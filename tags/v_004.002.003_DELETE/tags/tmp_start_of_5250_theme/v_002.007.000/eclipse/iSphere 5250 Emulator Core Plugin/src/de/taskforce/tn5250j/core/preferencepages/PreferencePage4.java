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

package de.taskforce.tn5250j.core.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.taskforce.tn5250j.core.Messages;

public class PreferencePage4 extends PreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage4() {
		super();
	}
	
	public Control createContents(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		final GridLayout gridLayoutx = new GridLayout();
		container.setLayout(gridLayoutx);
		
		final Label labelKey1 = new Label(container, SWT.NONE);
		labelKey1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelKey1.setText(Messages.getString("Eclipse_Key_Bindings_Ctrl+F12"));
		
		final Label labelSeparator1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_labelSeparator1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelSeparator1.setLayoutData(gd_labelSeparator1);
		
		final Label labelKey2 = new Label(container, SWT.NONE);
		labelKey2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelKey2.setText(Messages.getString("Eclipse_Key_Bindings_Ctrl+Alt+Arrow_Up"));
		
		final Label labelKey3 = new Label(container, SWT.NONE);
		labelKey3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelKey3.setText(Messages.getString("Eclipse_Key_Bindings_Ctrl+Alt+Arrow_Down"));
		
		final Label labelKey4 = new Label(container, SWT.NONE);
		labelKey4.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelKey4.setText(Messages.getString("Eclipse_Key_Bindings_Ctrl+Alt+Arrow_Left"));
		
		final Label labelKey5 = new Label(container, SWT.NONE);
		labelKey5.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelKey5.setText(Messages.getString("Eclipse_Key_Bindings_Ctrl+Alt+Arrow_Right"));
		
		final Label labelSeparator2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_labelSeparator2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelSeparator2.setLayoutData(gd_labelSeparator2);
		
		final Label labelKey6 = new Label(container, SWT.NONE);
		labelKey6.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelKey6.setText(Messages.getString("Eclipse_Key_Bindings_Ctrl+Arrow_Up"));
		
		final Label labelKey7 = new Label(container, SWT.NONE);
		labelKey7.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelKey7.setText(Messages.getString("Eclipse_Key_Bindings_Ctrl+Arrow_Down"));
		
		final Label labelSeparator3 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_labelSeparator3 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelSeparator3.setLayoutData(gd_labelSeparator3);
		
		final Label labelKey8 = new Label(container, SWT.NONE);
		labelKey8.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelKey8.setText(Messages.getString("Eclipse_Key_Bindings_Alt+Arrow_Up"));
		
		final Label labelKey9 = new Label(container, SWT.NONE);
		labelKey9.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelKey9.setText(Messages.getString("Eclipse_Key_Bindings_Alt+Arrow_Down"));
		
		return container;
	}
	
	public void init(IWorkbench workbench) {
	}

}