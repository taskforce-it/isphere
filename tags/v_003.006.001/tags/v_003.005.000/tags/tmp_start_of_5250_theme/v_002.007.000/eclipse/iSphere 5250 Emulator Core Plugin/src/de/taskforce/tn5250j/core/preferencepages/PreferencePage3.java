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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.taskforce.tn5250j.core.Messages;
import de.taskforce.tn5250j.core.TN5250JCorePlugin;

public class PreferencePage3 extends PreferencePage implements IWorkbenchPreferencePage {

	private IPreferenceStore store;
	private Button buttonMSActive;
	private Text textMSHorizontalSize;
	private Text textMSVerticalSize;

	public PreferencePage3() {
		super();
		setPreferenceStore(TN5250JCorePlugin.getDefault().getPreferenceStore());
		store = getPreferenceStore();
	}
	
	public Control createContents(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		final GridLayout gridLayoutx = new GridLayout();
		gridLayoutx.numColumns = 2;
		container.setLayout(gridLayoutx);
		
		// Minimal size - Active
		
		final Label labelMSActive = new Label(container, SWT.NONE);
		labelMSActive.setText(Messages.getString("Active") + ":");
		
		buttonMSActive = new Button(container, SWT.CHECK);
		
		// Minimal size - Horizontal size
		
		final Label labelMSHorizontalSize = new Label(container, SWT.NONE);
		labelMSHorizontalSize.setText(Messages.getString("Horizontal_size") + ":");

		textMSHorizontalSize = new Text(container, SWT.BORDER);
		textMSHorizontalSize.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				try {
					Integer.parseInt(textMSHorizontalSize.getText());
					setErrorMessage(null);
					setValid(true);
				} 
				catch (NumberFormatException e1) {
					setErrorMessage(Messages.getString("The_value_in_field_'Horizontal_size'_is_not_valid."));
					setValid(false);
				}
			}
		});
		textMSHorizontalSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textMSHorizontalSize.setTextLimit(4);
		
		// Minimal size - Vertical size
		
		final Label labelMSVerticalSize = new Label(container, SWT.NONE);
		labelMSVerticalSize.setText(Messages.getString("Vertical_size") + ":");

		textMSVerticalSize = new Text(container, SWT.BORDER);
		textMSVerticalSize.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				try {
					Integer.parseInt(textMSVerticalSize.getText());
					setErrorMessage(null);
					setValid(true);
				} 
				catch (NumberFormatException e1) {
					setErrorMessage(Messages.getString("The_value_in_field_'Vertical_size'_is_not_valid."));
					setValid(false);
				}
			}
		});
		textMSVerticalSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textMSVerticalSize.setTextLimit(4);

		// Miscellaneous
		
		setScreenToValues();
		
		return container;
	}

	protected void performApply() {
		setStoreToValues();
		setScreenToValues();
		super.performApply();
	}

	protected void performDefaults() {
		setStoreToDefaults();
		setScreenToValues();
		super.performDefaults();
	}

	public boolean performOk() {
		setStoreToValues();
		return super.performOk();
	}
	
	protected void setStoreToValues() {
		if (buttonMSActive.getSelection()) {
			store.setValue("DE.TASKFORCE.TN5250J.MSACTIVE", "Y");
		}
		else {
			store.setValue("DE.TASKFORCE.TN5250J.MSACTIVE", "");
		}
		store.setValue("DE.TASKFORCE.TN5250J.MSHSIZE", textMSHorizontalSize.getText());
		store.setValue("DE.TASKFORCE.TN5250J.MSVSIZE", textMSVerticalSize.getText());
	}
	
	protected void setStoreToDefaults() {
		store.setToDefault("DE.TASKFORCE.TN5250J.MSACTIVE");
		store.setToDefault("DE.TASKFORCE.TN5250J.MSHSIZE");
		store.setToDefault("DE.TASKFORCE.TN5250J.MSVSIZE");
	}
	
	protected void setScreenToValues() {
		if (store.getString("DE.TASKFORCE.TN5250J.MSACTIVE").equals("Y")) {
			buttonMSActive.setSelection(true);
		}
		else {
			buttonMSActive.setSelection(false);
		}
		textMSHorizontalSize.setText(store.getString("DE.TASKFORCE.TN5250J.MSHSIZE"));
		textMSVerticalSize.setText(store.getString("DE.TASKFORCE.TN5250J.MSVSIZE"));
	}
	
	public void init(IWorkbench workbench) {
	}

}