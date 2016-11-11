package biz.isphere.joblogexplorer.editor.detailsviewer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.core.internal.ColorHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.model.JobLogMessage;
import biz.isphere.joblogexplorer.preferences.Preferences;
import biz.isphere.joblogexplorer.preferences.SeverityColor;

public class JobLogExplorerDetailsViewer implements ISelectionChangedListener {

    private JobLogMessage jobLogMessage;

    private Preferences preferences;
    private boolean isColoring;
    private Color severityColor00;
    private Color severityColor10;
    private Color severityColor20;
    private Color severityColor30;
    private Color severityColor40;

    private UIJob updateDetailsViewerJob;

    private Object lock1 = new Object();

    private Text textID;
    private Text textType;
    private Text textSeverity;
    private Text textDate;
    private Text textTime;

    private Text textFromLibrary;
    private Text textFromProgram;
    private Text textFromModule;
    private Text textFromProcedure;
    private Text textFromStatement;

    private Text textToLibrary;
    private Text textToProgram;
    private Text textToModule;
    private Text textToProcedure;
    private Text textToStatement;

    private Text textMessage;

    public JobLogExplorerDetailsViewer() {

        this.preferences = Preferences.getInstance();

        initializeColors();
        registerPropertyChangeListener();
    }

    public void createViewer(Composite detailsArea) {

        // Create a composite to hold the children
        GridLayout layout = new GridLayout(1, false);
        layout.marginBottom = 0;
        layout.marginTop = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        detailsArea.setLayout(layout);
        detailsArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        detailsArea.setBackground(ColorHelper.getDefaultBackgroundColor());

        Composite messageDetailsArea = new Composite(detailsArea, SWT.NONE);
        GridLayout messageDetailsLayout = new GridLayout(2, false);
        messageDetailsLayout.horizontalSpacing=30;
        messageDetailsArea.setLayout(messageDetailsLayout);
        messageDetailsArea.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        textID = createDetailsField(messageDetailsArea, Messages.Label_ID);
        textSeverity = createDetailsField(messageDetailsArea, Messages.Label_Severity);
        textType = createDetailsField(messageDetailsArea, Messages.Label_Type);
        textDate = createDetailsField(messageDetailsArea, Messages.Label_Date_sent);
        textTime = createDetailsField(messageDetailsArea, Messages.Label_Time_sent);

        createSeparator(messageDetailsArea);

        textFromLibrary = createDetailsField(messageDetailsArea, Messages.Label_From_Library);
        textFromProgram = createDetailsField(messageDetailsArea, Messages.Label_From_Program);
        textFromModule = createDetailsField(messageDetailsArea, Messages.Label_From_Module);
        textFromProcedure = createDetailsField(messageDetailsArea, Messages.Label_From_Procedure);
        textFromStatement = createDetailsField(messageDetailsArea, Messages.Label_From_Stmt);

        textToLibrary = createDetailsField(messageDetailsArea, Messages.Label_To_Library);
        textToProgram = createDetailsField(messageDetailsArea, Messages.Label_To_Program);
        textToModule = createDetailsField(messageDetailsArea, Messages.Label_To_Module);
        textToProcedure = createDetailsField(messageDetailsArea, Messages.Label_To_Procedure);
        textToStatement = createDetailsField(messageDetailsArea, Messages.Label_To_Stmt);

        createSeparator(messageDetailsArea);

        Composite messageArea = new Composite(detailsArea, SWT.NONE);
        messageArea.setLayout(new GridLayout(2, false));
        messageArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        textMessage = createMultilineDetailsField(messageArea);
    }

    private void createSeparator(Composite detailsArea) {
        WidgetFactory.createSeparator(detailsArea).setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
    }

    private Text createDetailsField(Composite parent, String label) {

        Label labelField = new Label(parent, SWT.NONE);
        labelField.setText(label);
        labelField.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

        Text textField = WidgetFactory.createSelectableLabel(parent);
        textField.setBackground(ColorHelper.getDefaultBackgroundColor());
        textField.setLayoutData(new GridData(GridData.FILL_BOTH));

        return textField;
    }

    private Text createMultilineDetailsField(Composite parent) {

        Text textField = WidgetFactory.createSelectableMultilineLabel(parent);
        textField.setBackground(ColorHelper.getDefaultBackgroundColor());
        textField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        return textField;
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {

        if (event.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection)event.getSelection();
            Object element = selection.getFirstElement();
            if (element instanceof JobLogMessage) {

                jobLogMessage = (JobLogMessage)element;

                updateValue(textID, jobLogMessage.getId());
                updateValue(textType, jobLogMessage.getType());
                updateValue(textSeverity, jobLogMessage.getSeverity());
                updateValue(textDate, jobLogMessage.getDate());
                updateValue(textTime, jobLogMessage.getTime());

                updateValue(textFromLibrary, jobLogMessage.getFromLibrary());
                updateValue(textFromProgram, jobLogMessage.getFromProgram());
                updateValue(textFromModule, jobLogMessage.getFromModule());
                updateValue(textFromProcedure, jobLogMessage.getFromProcedure());
                updateValue(textFromStatement, jobLogMessage.getFromStatement());

                updateValue(textToLibrary, jobLogMessage.getToLibrary());
                updateValue(textToProgram, jobLogMessage.getToProgram());
                updateValue(textToModule, jobLogMessage.getToModule());
                updateValue(textToProcedure, jobLogMessage.getToProcedure());
                updateValue(textToStatement, jobLogMessage.getToStatement());

                updateValue(textMessage, jobLogMessage.getText() + "\n" + "\n" + jobLogMessage.getCause()); //$NON-NLS-1$ //$NON-NLS-2$

                updateColor(jobLogMessage);
            }
        }
    }

    private void updateColor(JobLogMessage jobLogMessage) {

        Color background = ColorHelper.getDefaultBackgroundColor();

        if (isColoring) {
            int severity = jobLogMessage.getSeverityInt();
            if (severity >= 40) {
                background = severityColor40;
            } else if (severity >= 30) {
                background = severityColor30;
            } else if (severity >= 20) {
                background = severityColor20;
            } else if (severity >= 10) {
                background = severityColor10;
            } else {
                background = severityColor00;
            }

            if (background == null) {
                background = ColorHelper.getDefaultBackgroundColor();
            }
        }

        textID.setBackground(background);
    }

    private void updateValue(Text textControl, String value) {

        if (value == null) {
            textControl.setText(""); //$NON-NLS-1$
        } else {
            textControl.setText(value);
        }
    }

    private void initializeColors() {

        synchronized (lock1) {
            isColoring = preferences.isColoringEnabled();

            if (isColoring) {
                severityColor00 = preferences.getColorSeverity(SeverityColor.SEVERITY_00);
                severityColor10 = preferences.getColorSeverity(SeverityColor.SEVERITY_10);
                severityColor20 = preferences.getColorSeverity(SeverityColor.SEVERITY_20);
                severityColor30 = preferences.getColorSeverity(SeverityColor.SEVERITY_30);
                severityColor40 = preferences.getColorSeverity(SeverityColor.SEVERITY_40);
            }
        }
    }

    private void registerPropertyChangeListener() {

        ISphereJobLogExplorerPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                String propertyName = event.getProperty();
                if (propertyName.startsWith("biz.isphere.joblogexplorer.COLORS.")) {
                    if (updateDetailsViewerJob != null) {
                        updateDetailsViewerJob.cancel();
                        updateDetailsViewerJob = null;
                    }
                    updateDetailsViewerJob = new UpdateDetailsViewerJob();
                    updateDetailsViewerJob.schedule(100);
                    /*
                     * Delay update for 100 mSecs to cancel updating the table
                     * viewer, when multiple colors have changed.
                     */
                }
            }
        });
    }

    private class UpdateDetailsViewerJob extends UIJob {

        public UpdateDetailsViewerJob() {
            super("");
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor arg0) {
            initializeColors();
            updateColor(jobLogMessage);
            return Status.OK_STATUS;
        }
    }

}
