/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.tn5250j.TN5250jConstants;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.preferences.DoNotAskMeAgain;
import biz.isphere.core.preferences.DoNotAskMeAgainDialog;
import biz.isphere.tn5250j.core.Messages;
import biz.isphere.tn5250j.core.TN5250JCorePlugin;
import biz.isphere.tn5250j.core.preferences.Preferences;
import biz.isphere.tn5250j.core.session.ISession;

/**
 * 5250 preferences page: Session defaults
 */
public class PreferencePage2 extends PreferencePage implements IWorkbenchPreferencePage {

    private Preferences preferences;
    private Text textPort;
    private CCombo comboCodePage;
    private CCombo comboSSLType;
    private Button buttonScreenSize24_80;
    private Button buttonScreenSize27_132;
    // private Button buttonEnhancedMode;
    private Button buttonView;
    private Button buttonEditor;
    private Label labelView;
    private Group groupView;
    private Label labelGroupSessionsBy;
    private Button checkboxMultiSession;
    private CCombo comboGroupSessionsBy;
    private Button checkboxActivateViewsOnStartup;

    private String[] codePages = { "37", "37PT", "273", "280", "284", "285", "277-dk", "277-no", "278", "297", "424", "500-ch", "870-pl", "870-sk",
        "871", "875", "1025-r", "1026", "1112", "1141", "1140", "1147", "1148" };

    public PreferencePage2() {
        super();
        setPreferenceStore(TN5250JCorePlugin.getDefault().getPreferenceStore());
        preferences = Preferences.getInstance();
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayoutx = new GridLayout();
        gridLayoutx.numColumns = 2;
        container.setLayout(gridLayoutx);

        // SSL Type

        final Label labelSSLType = new Label(container, SWT.NONE);
        labelSSLType.setText(Messages.SSL_type_colon);

        comboSSLType = new CCombo(container, SWT.BORDER | SWT.READ_ONLY);
        comboSSLType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboSSLType.setTextLimit(10);
        comboSSLType.setItems(preferences.getSSLTypeOptions());
        comboSSLType.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                if (TN5250jConstants.SSL_TYPE_NONE.equals(comboSSLType.getText())) {
                    textPort.setText(TN5250jConstants.PORT_NUMBER);
                } else {
                    textPort.setText(TN5250jConstants.SSL_PORT_NUMBER);
                }
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        // Port

        final Label labelPort = new Label(container, SWT.NONE);
        labelPort.setText(Messages.Port_colon);

        textPort = new Text(container, SWT.BORDER);
        textPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textPort.setTextLimit(5);

        // Codepage

        final Label labelCodePage = new Label(container, SWT.NONE);
        labelCodePage.setText(Messages.Codepage_colon);

        comboCodePage = new CCombo(container, SWT.BORDER);
        comboCodePage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboCodePage.setTextLimit(10);
        for (int idx = 0; idx < codePages.length; idx++) {
            comboCodePage.add(codePages[idx]);
        }

        // Screen size

        final Label labelScreenSize = new Label(container, SWT.NONE);
        labelScreenSize.setText(Messages.Screensize_colon);

        final Group groupScreenSize = new Group(container, SWT.NONE);
        final GridLayout gridLayoutScreenSize = new GridLayout();
        gridLayoutScreenSize.numColumns = 2;
        groupScreenSize.setLayout(gridLayoutScreenSize);
        groupScreenSize.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        buttonScreenSize24_80 = new Button(groupScreenSize, SWT.RADIO);
        buttonScreenSize24_80.setText("24*80"); //$NON-NLS-1$

        buttonScreenSize27_132 = new Button(groupScreenSize, SWT.RADIO);
        buttonScreenSize27_132.setText("27*132"); //$NON-NLS-1$

        // Enhanced mode
        /*
         * final Label labelEnhancedMode = new Label(container, SWT.NONE);
         * labelEnhancedMode.setText(Messages.getString("Enhanced_mode") + ":");
         * buttonEnhancedMode = new Button(container, SWT.CHECK);
         */
        // Area

        final Label labelArea = new Label(container, SWT.NONE);
        labelArea.setText(Messages.Area_colon);

        final Group groupArea = new Group(container, SWT.NONE);
        final GridLayout gridLayoutArea = new GridLayout();
        gridLayoutArea.numColumns = 2;
        groupArea.setLayout(gridLayoutArea);
        groupArea.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        buttonView = new Button(groupArea, SWT.RADIO);
        buttonView.setText(Messages.View);
        buttonView.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                setControlEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        buttonEditor = new Button(groupArea, SWT.RADIO);
        buttonEditor.setText(Messages.Editor);
        buttonEditor.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                setControlEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        labelView = new Label(container, SWT.NONE);
        labelView.setText(Messages.View);

        groupView = new Group(container, SWT.NONE);
        final GridLayout gridLayoutView = new GridLayout();
        gridLayoutView.numColumns = 2;
        groupView.setLayout(gridLayoutView);
        groupView.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        checkboxMultiSession = new Button(groupView, SWT.CHECK);
        checkboxMultiSession.setText(Messages.Enable_multiple_sessions);
        checkboxMultiSession.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        checkboxActivateViewsOnStartup = new Button(groupView, SWT.CHECK);
        checkboxActivateViewsOnStartup.setText(Messages.Activate_pinned_views_on_startup);
        checkboxActivateViewsOnStartup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        labelGroupSessionsBy = new Label(groupView, SWT.NONE);
        labelGroupSessionsBy.setText(Messages.Group_sessions_by);

        comboGroupSessionsBy = new CCombo(groupView, SWT.BORDER + SWT.READ_ONLY);
        comboGroupSessionsBy.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        String[] labelsGrouping = Preferences.getInstance().getSessionGroupingLables();
        for (int idx = 0; idx < labelsGrouping.length; idx++) {
            comboGroupSessionsBy.add(labelsGrouping[idx]);
        }

        // Miscellaneous

        setScreenToValues();

        return container;
    }

    @Override
    protected void performApply() {
        setStoreToValues();
        setScreenToValues();
        super.performApply();
    }

    @Override
    protected void performDefaults() {
        setScreenToDefaultValues();
        super.performDefaults();
    }

    @Override
    public boolean performOk() {

        String oldGrouping = preferences.getSessionGrouping();
        setStoreToValues();
        String newGrouping = preferences.getSessionGrouping();

        if (!oldGrouping.equals(newGrouping) && is5250ViewVisible()) {
            DoNotAskMeAgainDialog.openInformation(getShell(), DoNotAskMeAgain.TN5250_SESSION_GROUPING_CHANGED,
                Messages.The_Group_sessions_by_attribute_has_been_changed_Please_close_all_5250_views_to_let_the_new_value_take_effect);
        }

        return super.performOk();
    }

    private boolean is5250ViewVisible() {

        IViewReference[] viewReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
        for (IViewReference iViewReference : viewReferences) {
            if ("biz.isphere.tn5250j.rse.sessionsview.SessionsView".equals(iViewReference.getId())) {
                return true;
            }
        }

        return false;
    }

    protected void setStoreToValues() {

        preferences.setSessionPortNumber(IntHelper.tryParseInt(textPort.getText(), preferences.getDefaultSessionPortNumber()));
        preferences.setSSLType(comboSSLType.getText());
        preferences.setSessionCodepage(comboCodePage.getText());

        if (buttonScreenSize27_132.getSelection()) {
            preferences.setSessionScreenSize(ISession.SIZE_132);
        } else {
            preferences.setSessionScreenSize(ISession.SIZE_80);
        }

        if (buttonView.getSelection()) {
            preferences.setSessionArea(ISession.AREA_VIEW);
        } else {
            preferences.setSessionArea(ISession.AREA_EDITOR);
        }

        if (checkboxMultiSession.getSelection()) {
            preferences.setIsMultiSessionEnabled(true);
        } else {
            preferences.setIsMultiSessionEnabled(false);
        }

        preferences.setSessionGroupingByLabel(comboGroupSessionsBy.getText());

        if (checkboxActivateViewsOnStartup.getSelection()) {
            preferences.setActivateViewsOnStartup(true);
        } else {
            preferences.setActivateViewsOnStartup(false);
        }
    }

    protected void setScreenToDefaultValues() {

        textPort.setText(Integer.toString(preferences.getDefaultSessionPortNumber()));
        comboSSLType.setText(preferences.getDefaultSSLType());
        comboCodePage.setText(preferences.getDefaultSessionCodepage());
        if (ISession.SIZE_132.equals(preferences.getDefaultSessionScreenSize())) {
            buttonScreenSize27_132.setSelection(true);
            buttonScreenSize24_80.setSelection(false);
        } else {
            buttonScreenSize27_132.setSelection(false);
            buttonScreenSize24_80.setSelection(true);
        }

        if (ISession.AREA_VIEW.equals(preferences.getDefaultSessionArea())) {
            buttonView.setSelection(true);
            buttonEditor.setSelection(false);
        } else {
            buttonView.setSelection(false);
            buttonEditor.setSelection(true);
        }

        checkboxMultiSession.setSelection(preferences.getDefaultIsMultiSessionEnabled());
        comboGroupSessionsBy.setText(preferences.getDefaultSessionGroupingLabel());
        checkboxActivateViewsOnStartup.setSelection(preferences.getDefaultActivateViewsOnStartup());

        setControlEnablement();
    }

    protected void setScreenToValues() {

        textPort.setText(Integer.toString(preferences.getSessionPortNumber()));
        comboSSLType.setText(preferences.getSSLType());
        comboCodePage.setText(preferences.getSessionCodepage());

        if (ISession.SIZE_132.equals(preferences.getSessionScreenSize())) {
            buttonScreenSize27_132.setSelection(true);
            buttonScreenSize24_80.setSelection(false);
        } else {
            buttonScreenSize27_132.setSelection(false);
            buttonScreenSize24_80.setSelection(true);
        }

        if (ISession.AREA_VIEW.equals(preferences.getSessionArea())) {
            buttonView.setSelection(true);
            buttonEditor.setSelection(false);
        } else {
            buttonView.setSelection(false);
            buttonEditor.setSelection(true);
        }

        checkboxMultiSession.setSelection(preferences.isMultiSessionEnabled());
        comboGroupSessionsBy.setText(preferences.getSessionGroupingLabel());
        checkboxActivateViewsOnStartup.setSelection(preferences.isActivateViewsOnStartup());

        setControlEnablement();
    }

    private void setControlEnablement() {

        if (buttonView.getSelection()) {
            // labelView.setEnabled(true);
            // groupView.setEnabled(true);
            // buttonMultiSession.setEnabled(true);
            // labelGroupSessionsBy.setEnabled(true);
            // comboGroupSessionsBy.setEnabled(true);
        } else {
            // labelView.setEnabled(false);
            // groupView.setEnabled(false);
            // buttonMultiSession.setEnabled(false);
            // labelGroupSessionsBy.setEnabled(false);
            // comboGroupSessionsBy.setEnabled(false);
        }
    }

    public void init(IWorkbench workbench) {
    }

}