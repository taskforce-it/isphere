/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.model.JobLogMessage;
import biz.isphere.joblogexplorer.preferences.Preferences;
import biz.isphere.joblogexplorer.preferences.SeverityColor;

public class JobLogExplorerDetailsViewer implements ISelectionChangedListener {

    private static final String EMPTY = ""; //$NON-NLS-1$
    private static final String NEW_LINE = "\n"; //$NON-NLS-1$ 

    private JobLogMessage jobLogMessage;

    private Preferences preferences;
    private boolean isColoring;
    private Color severityColorBL;
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

    public void createViewer(Composite parent) {

        Composite messageDetailsArea = new Composite(parent, SWT.BORDER);
        GridLayout messageDetailsLayout = new GridLayout(2, false);
        messageDetailsLayout.horizontalSpacing = 30;
        messageDetailsArea.setLayout(messageDetailsLayout);
        messageDetailsArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        messageDetailsArea.setBackground(getBackgroundColor());

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

        // createSeparator(messageDetailsArea);

        Composite messageArea = new Composite(parent, SWT.BORDER);
        messageArea.setLayout(new GridLayout(1, false));
        messageArea.setLayoutData(new GridData(GridData.FILL_BOTH));
        messageArea.setBackground(getBackgroundColor());

        textMessage = createMultilineDetailsField(messageArea);
    }

    /**
     * Create the Details
     */
    private void createDetails(Composite parent) {

    }

    private void createSeparator(Composite detailsArea) {
        WidgetFactory.createSeparator(detailsArea).setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
    }

    private Text createDetailsField(Composite parent, String label) {

        Label labelField = new Label(parent, SWT.NONE);
        labelField.setText(label);
        labelField.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
        labelField.setBackground(getBackgroundColor());

        Text textField = WidgetFactory.createSelectableLabel(parent);
        textField.setBackground(getBackgroundColor());
        textField.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        return textField;
    }

    private Text createMultilineDetailsField(Composite parent) {

        Text textField = WidgetFactory.createSelectableMultilineLabel(parent);
        textField.setBackground(getBackgroundColor());
        textField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        return textField;
    }

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

                updateValue(textMessage, getCompleteMessageText());

                updateColor(jobLogMessage);
            }
        }
    }

    private String getCompleteMessageText() {

        if (jobLogMessage.getText() == null && jobLogMessage.getHelp() == null) {
            return EMPTY;
        } else if (jobLogMessage.getText() != null && jobLogMessage.getHelp() != null) {
            return jobLogMessage.getText() + NEW_LINE + NEW_LINE + jobLogMessage.getHelp();
        } else if (jobLogMessage.getText() != null) {
            return jobLogMessage.getText();
        } else {
            return jobLogMessage.getHelp();
        }
    }

    private void updateColor(JobLogMessage jobLogMessage) {

        Color background = getBackgroundColor();

        if (isColoring) {
            int severity = jobLogMessage.getSeverityInt();
            if (severity == JobLogMessage.SEVERITY_BLANK) {
                background = severityColorBL;
            } else if (severity >= 40) {
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
                background = getBackgroundColor();
            }
        }

        textID.setBackground(background);
    }

    private void updateValue(Text textControl, String value) {

        if (value == null) {
            textControl.setText(EMPTY);
        } else {
            textControl.setText(value);
        }
    }

    private void initializeColors() {

        synchronized (lock1) {
            isColoring = preferences.isColoringEnabled();

            if (isColoring) {
                severityColorBL = preferences.getColorSeverity(SeverityColor.SEVERITY_BL);
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
            public void propertyChange(PropertyChangeEvent event) {
                String propertyName = event.getProperty();
                if (propertyName.startsWith("biz.isphere.joblogexplorer.COLORS.")) { //$NON-NLS-1$
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
            super(EMPTY);
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor arg0) {
            initializeColors();
            updateColor(jobLogMessage);
            return Status.OK_STATUS;
        }
    }

    private Color getBackgroundColor() {
        return Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    }

}
