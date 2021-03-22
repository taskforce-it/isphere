package biz.isphere.rcp.journalexplorer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.core.swt.widgets.HistoryCombo;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.connectioncombo.ConnectionCombo;
import biz.isphere.rcp.ISphereRCPExampleApplication;

public class OpenOutputFileDialog extends Dialog {

    private static final String CONNECTION = "CONNECTION";
    private static final String JOURNAL_OUTPUT_LIBRARY = "JOURNAL_OUTPUT_LIBRARY";
    private static final String JOURNAL_OUTPUT_FILE = "JOURNAL_OUTPUT_FILE";

    private DialogSettingsManager dialogSettingsManager;

    private ConnectionCombo cboConnection;
    private HistoryCombo txtFile;
    private HistoryCombo txtLibrary;

    private String connectionName;
    private String fileName;
    private String libraryName;

    public OpenOutputFileDialog(Shell parent) {
        super(parent);

        this.dialogSettingsManager = new DialogSettingsManager(getDialogBoundsSettings());
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite dialogArea = new Composite(parent, SWT.NONE);
        dialogArea.setLayout(new GridLayout(2, false));
        dialogArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label lblConnection = new Label(dialogArea, SWT.NONE);
        lblConnection.setText("Connection:");

        cboConnection = WidgetFactory.createConnectionCombo(dialogArea, SWT.NONE);
        cboConnection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label lblFile = new Label(dialogArea, SWT.NONE);
        lblFile.setText("File:");

        txtFile = WidgetFactory.createNameHistoryCombo(dialogArea);
        txtFile.setLayoutData(createNameFieldLayoutData());

        Label lblLibrary = new Label(dialogArea, SWT.NONE);
        lblLibrary.setText("Library:");

        txtLibrary = WidgetFactory.createNameHistoryCombo(dialogArea);
        txtLibrary.setLayoutData(createNameFieldLayoutData());

        configureControls();

        loadScreenValues();

        return dialogArea;
    }

    private void configureControls() {

        cboConnection.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                connectionName = cboConnection.getText();
            }
        });

        txtFile.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                fileName = txtFile.getText();
            }
        });

        txtLibrary.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                libraryName = txtLibrary.getText();
            }
        });

    }

    private Object createNameFieldLayoutData() {
        return new GridData(GridData.FILL_HORIZONTAL);
    }

    @Override
    protected void okPressed() {

        storeScreenValues();

        super.okPressed();
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getLibraryName() {
        return libraryName;
    }

    private void loadScreenValues() {

        txtLibrary.load(dialogSettingsManager, JOURNAL_OUTPUT_LIBRARY);
        txtFile.load(dialogSettingsManager, JOURNAL_OUTPUT_FILE);

        String connection = dialogSettingsManager.loadValue(CONNECTION, "");
        cboConnection.setText(connection);

        if (cboConnection.getSelectionIndex() < 0) {
            cboConnection.select(0);
        }

        txtLibrary.setText(dialogSettingsManager.loadValue(JOURNAL_OUTPUT_LIBRARY, ""));
        txtFile.setText(dialogSettingsManager.loadValue(JOURNAL_OUTPUT_FILE, ""));
    }

    private void storeScreenValues() {

        dialogSettingsManager.storeValue(CONNECTION, cboConnection.getText());
        dialogSettingsManager.storeValue(JOURNAL_OUTPUT_FILE, txtFile.getText());
        dialogSettingsManager.storeValue(JOURNAL_OUTPUT_LIBRARY, txtLibrary.getText());

        txtLibrary.updateHistory(txtLibrary.getText());
        txtLibrary.store();

        txtFile.updateHistory(txtFile.getText());
        txtFile.store();
    }

    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return ISphereRCPExampleApplication.getDefault().getDialogSettings();
    }
}
