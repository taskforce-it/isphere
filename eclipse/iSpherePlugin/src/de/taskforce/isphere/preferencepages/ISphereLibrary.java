package de.taskforce.isphere.preferencepages;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import de.taskforce.isphere.ISpherePlugin;
import de.taskforce.isphere.Messages;
import de.taskforce.isphere.internal.TransferISphereLibrary;
import de.taskforce.isphere.internal.Validator;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ISphereLibrary extends PreferencePage implements IWorkbenchPreferencePage {
	
	private Text textISphereLibrary;
	private IPreferenceStore store;
	private String iSphereLibrary;
	private Validator validatorLibrary;

	public ISphereLibrary() {
		super();
		setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
		store = getPreferenceStore();
	}
	
	public Control createContents(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
			
		final Label labelWorksheetHistoryEntries = new Label(container, SWT.NONE);
		labelWorksheetHistoryEntries.setText(Messages.getString("iSphere_library_colon"));

		textISphereLibrary = new Text(container, SWT.BORDER);
		textISphereLibrary.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				iSphereLibrary = textISphereLibrary.getText().toUpperCase().trim();
				if (iSphereLibrary.equals("") || !validatorLibrary.validate(iSphereLibrary)) {
					setErrorMessage(Messages.getString("The_value_in_field_'iSphere_library'_is_not_valid."));
					setValid(false);
				}
				else {
					setErrorMessage(null);
					setValid(true);
				}
			}
		});
		textISphereLibrary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textISphereLibrary.setTextLimit(10);
		
		Button buttonTransfer = new Button(container, SWT.NONE);
		buttonTransfer.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TransferISphereLibrary statusDialog = new TransferISphereLibrary(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay(), SWT.APPLICATION_MODAL | SWT.SHELL_TRIM);
				if (statusDialog.connect()) {
					statusDialog.open();
				}
			}
		});
		buttonTransfer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		buttonTransfer.setText(Messages.getString("Transfer_iSphere_library"));
		
		validatorLibrary = new Validator();
		validatorLibrary.setType("*NAME");
		validatorLibrary.setLength(10);
		validatorLibrary.setRestricted(false);

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
		store.setValue("DE.TASKFORCE.ISPHERE.LIBRARY", iSphereLibrary);
	}
	
	protected void setStoreToDefaults() {
		store.setToDefault("DE.TASKFORCE.ISPHERE.LIBRARY");
	}
	
	protected void setScreenToValues() {
		iSphereLibrary = store.getString("DE.TASKFORCE.ISPHERE.LIBRARY");
		textISphereLibrary.setText(iSphereLibrary);
	}
	
	public void init(IWorkbench workbench) {
	}

}