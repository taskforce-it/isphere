/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.preferencepages;

import java.awt.Frame;

import org.eclipse.albireo.core.AwtEnvironment;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.tn5250j.TN5250jConstants;
import org.tn5250j.keyboard.KeyMapper;
import org.tn5250j.keyboard.configure.KeyConfigure;
import org.tn5250j.tools.Macronizer;

import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.tn5250j.core.Messages;
import biz.isphere.tn5250j.core.preferences.Preferences;

/**
 * 5250 preferences page: Key bindings information screen
 */
public class PreferencePage4 extends PreferencePage implements IWorkbenchPreferencePage {

    public PreferencePage4() {
        super();
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayoutx = new GridLayout();
        container.setLayout(gridLayoutx);

        final Label labelKey1 = new Label(container, SWT.NONE);
        labelKey1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey1.setText(Messages.Eclipse_Key_Bindings_Ctrl_F12);

        final Label labelSeparator1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        final GridData gd_labelSeparator1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        labelSeparator1.setLayoutData(gd_labelSeparator1);

        final Label labelKey2 = new Label(container, SWT.NONE);
        labelKey2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey2.setText(getKeyMappingInfo(Messages.Scroll_session_up, TN5250jConstants.MNEMONIC_SCROLL_SESSION_UP));

        final Label labelKey3 = new Label(container, SWT.NONE);
        labelKey3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey3.setText(getKeyMappingInfo(Messages.Scroll_session_down, TN5250jConstants.MNEMONIC_SCROLL_SESSION_DOWN));

        final Label labelKey4 = new Label(container, SWT.NONE);
        labelKey4.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey4.setText(getKeyMappingInfo(Messages.Scroll_session_left, TN5250jConstants.MNEMONIC_SCROLL_SESSION_LEFT));

        final Label labelKey5 = new Label(container, SWT.NONE);
        labelKey5.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey5.setText(getKeyMappingInfo(Messages.Scroll_session_right, TN5250jConstants.MNEMONIC_SCROLL_SESSION_RIGHT));

        final Label labelSeparator2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        final GridData gd_labelSeparator2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        labelSeparator2.setLayoutData(gd_labelSeparator2);

        final Label labelKey6 = new Label(container, SWT.NONE);
        labelKey6.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey6.setText(getKeyMappingInfo(Messages.Session_next_main, TN5250jConstants.MNEMONIC_NEXT_SESSION));

        final Label labelKey7 = new Label(container, SWT.NONE);
        labelKey7.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey7.setText(getKeyMappingInfo(Messages.Session_previous_main, TN5250jConstants.MNEMONIC_PREVIOUS_SESSION));

        final Label labelSeparator3 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        final GridData gd_labelSeparator3 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        labelSeparator3.setLayoutData(gd_labelSeparator3);

        final Label labelKey8 = new Label(container, SWT.NONE);
        labelKey8.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey8.setText(getKeyMappingInfo(Messages.Session_next_multiple, TN5250jConstants.MNEMONIC_NEXT_MULTIPLE_SESSION));

        final Label labelKey9 = new Label(container, SWT.NONE);
        labelKey9.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey9.setText(getKeyMappingInfo(Messages.Session_previous_multiple, TN5250jConstants.MNEMONIC_PREVIOUS_MULTIPLE_SESSION));

        final Button buttonKeyConfigure = WidgetFactory.createPushButton(container);
        buttonKeyConfigure.setText(Messages.Map_Keys);
        buttonKeyConfigure.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {

                final AwtEnvironment awtEnv = AwtEnvironment.getInstance(getShell().getDisplay());
                final Frame parent = awtEnv.createDialogParentFrame();
                awtEnv.invokeAndBlockSwt(new Runnable() {

                    public void run() {
                        String dftCodePage = Preferences.getInstance().getSessionCodepage();
                        if (Macronizer.isMacrosExist()) {
                            String[] macrosList = Macronizer.getMacroList();
                            new KeyConfigure(parent, macrosList, dftCodePage);
                        } else {
                            new KeyConfigure(parent, null, dftCodePage);
                        }
                    }
                });

            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        return container;
    }

    private String getKeyMappingInfo(String messageId, String keyMnemonic) {
        KeyMapper.init();
        return Messages.bind(messageId, KeyMapper.getKeyStrokeDesc(keyMnemonic));
    }

    public void init(IWorkbench workbench) {
    }

}