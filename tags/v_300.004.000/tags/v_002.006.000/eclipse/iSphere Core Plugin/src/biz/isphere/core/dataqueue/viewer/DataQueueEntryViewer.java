/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.viewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.HexFormatter;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataqueue.retrieve.message.RDQM0200MessageEntry;
import biz.isphere.core.internal.FontHelper;
import biz.isphere.core.internal.Size;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class DataQueueEntryViewer extends XDialog {

    private final static int PREVIOUS_ID = IDialogConstants.OK_ID - 1;
    private final static int NEXT_ID = IDialogConstants.OK_ID - 2;

    private final static String SASH_WEIGHTS = "SASH_WEIGHTS_";
    private final int[] DEFAULT_SASH_WEIGHTS = new int[] { 1, 3 };

    protected Shell shell;

    private List<RDQM0200MessageEntry> messages;
    private int selectedMessage;

    private SashForm viewerArea;
    private Composite compositeKey;
    private Text textKey;
    private Text textMessage;
    private Label labelInfo;

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    public DataQueueEntryViewer(Shell parent) {
        super(parent);

        messages = new ArrayList<RDQM0200MessageEntry>();
        setSelectedItem(null);
    }

    public void setSelectedItem(RDQM0200MessageEntry message) {
        if (messages != null) {
            this.selectedMessage = getItemIndex(message);
        } else {
            this.selectedMessage = -1;
        }
    }

    public void setMessages(RDQM0200MessageEntry[] messages) {
        this.messages = Arrays.asList(messages);
        if (messages.length >= 1) {
            setSelectedItem(this.messages.get(0));
        }
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.Data_Queue_Entry);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        viewerArea = new SashForm(parent, SWT.VERTICAL);
        viewerArea.setLayout(new GridLayout());
        viewerArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        compositeKey = new Composite(viewerArea, SWT.NONE);
        compositeKey.setLayout(new GridLayout(1, false));
        compositeKey.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        Label labelKey = new Label(compositeKey, SWT.NONE);
        labelKey.setText(Messages.MessageKey_colon);

        textKey = WidgetFactory.createReadOnlyMultilineText(compositeKey, false, false);
        textKey.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        textKey.setFont(getFont());

        Composite compositeMessage = new Composite(viewerArea, SWT.NONE);
        compositeMessage.setLayout(new GridLayout(1, false));
        compositeMessage.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        Label labelMessage = new Label(compositeMessage, SWT.NONE);
        labelMessage.setText(Messages.Message_colon);

        textMessage = WidgetFactory.createReadOnlyMultilineText(compositeMessage, false, false);
        textMessage.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        textMessage.setFont(getFont());

        displayMessage(getSelectedItem());

        loadScreenValues();

        return viewerArea;
    }

    protected void createButtonsForButtonBar(Composite parent) {

        Composite compositeNavigation = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(0, false);
        compositeNavigation.setLayout(layout);
        compositeNavigation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        createButton(compositeNavigation, PREVIOUS_ID, Messages.Previous, false);
        createButton(compositeNavigation, NEXT_ID, Messages.Next, false);

        Composite compositeInfo = new Composite(compositeNavigation, SWT.NONE);
        GridLayout compositeInfoLayout = new GridLayout(1, true);
        compositeInfoLayout.marginWidth = 20;
        compositeInfo.setLayout(compositeInfoLayout);
        labelInfo = new Label(compositeInfo, SWT.NONE);
        layout.numColumns++;

        if (parent.getLayout() instanceof GridLayout) {
            GridLayout parentLayout = (GridLayout)parent.getLayout();
            parentLayout.numColumns++;
            parentLayout.makeColumnsEqualWidth = false;
        }

        if (parent.getLayoutData() instanceof GridData) {
            GridData parentLayoutData = (GridData)parent.getLayoutData();
            parentLayoutData.horizontalAlignment = GridData.FILL;
            parentLayoutData.grabExcessHorizontalSpace = true;
        }

        createButton(parent, IDialogConstants.OK_ID, Messages.btnLabel_Close, true);

        addButtonListeners();

        refreshButtonEnablement();
    }

    private RDQM0200MessageEntry getSelectedItem() {

        if (selectedMessage >= 0 && selectedMessage <= messages.size()) {
            return messages.get(selectedMessage);
        }

        return null;
    }

    private void addButtonListeners() {

        getButton(PREVIOUS_ID).addSelectionListener(new NavigationButtonSelectionListener());
        getButton(NEXT_ID).addSelectionListener(new NavigationButtonSelectionListener());
    }

    private int getItemIndex(RDQM0200MessageEntry message) {

        if (messages.size() == 0) {
            return -1;
        }

        return messages.indexOf(message);
    }

    private void displayMessage(RDQM0200MessageEntry message) {

        try {

            HexFormatter formatter = new HexFormatter();

            byte[] keyBytes;
            String keyText;
            byte[] messageBytes;
            String messageText;

            if (message == null) {
                keyBytes = null;
                keyText = null;
                messageBytes = null;
                messageText = null;
            } else {
                keyBytes = message.getKeyBytes();
                keyText = message.getKeyText();
                messageBytes = message.getMessageBytes(false);
                messageText = message.getMessageText(false);
            }

            if (keyBytes != null && keyBytes.length > 0) {
                displayKeyValue(formatter.createFormattedHexText(keyBytes, keyText));
            } else {
                displayKeyValue(null);
            }

            if (messageBytes != null && messageBytes.length > 0) {
                displayMessageData(formatter.createFormattedHexText(messageBytes, messageText));
            } else {
                displayMessageData(Messages.No_data_available);
            }

        } catch (Throwable e) {
            ISpherePlugin.logError("Failed to display data queue entry.", e); //$NON-NLS-1$
        }
    }

    private void refreshButtonEnablement() {
        if (messages == null || messages.size() == 0) {
            getButton(PREVIOUS_ID).setEnabled(false);
            getButton(NEXT_ID).setEnabled(false);
        } else if (selectedMessage <= 0) {
            getButton(PREVIOUS_ID).setEnabled(false);
            getButton(NEXT_ID).setEnabled(true);
        } else if (selectedMessage >= messages.size() - 1) {
            getButton(PREVIOUS_ID).setEnabled(true);
            getButton(NEXT_ID).setEnabled(false);
        } else {
            getButton(PREVIOUS_ID).setEnabled(true);
            getButton(NEXT_ID).setEnabled(true);
        }

        labelInfo.setText("Message " + (selectedMessage + 1) + " of " + messages.size() + ".");
    }

    private void displayKeyValue(String key) {
        if (key != null) {
            textKey.setText(key);
            compositeKey.setVisible(true);
        } else {
            textKey.setText(""); //$NON-NLS-1$
            compositeKey.setVisible(false);
        }
        layoutViewer();
    }

    private void displayMessageData(String message) {
        if (message != null) {
            textMessage.setText(message);
        } else {
            textMessage.setText(""); //$NON-NLS-1$
        }
    }

    private void layoutViewer() {
        if (viewerArea != null) {
            viewerArea.layout();
        }
    }

    private Font getFont() {
        return FontHelper.getFixedSizeFont();
    }

    private void loadScreenValues() {

        int[] weights = new int[DEFAULT_SASH_WEIGHTS.length];
        
        int i;
        for (i = 0 ; i < weights.length; i++) {
            if (getDialogBoundsSettings().get(SASH_WEIGHTS + i) == null) {
                break;
            }
            weights[i] = getDialogBoundsSettings().getInt(SASH_WEIGHTS + i);
        }

        if (i == DEFAULT_SASH_WEIGHTS.length) {
            viewerArea.setWeights(weights);
        } else {
            viewerArea.setWeights(DEFAULT_SASH_WEIGHTS);
        }
    }

    private void storeScreenValues() {

        int count = 0;
        for (int weight : viewerArea.getWeights()) {
            getDialogBoundsSettings().put(SASH_WEIGHTS + count, weight);
            count++;
        }
    }

    @Override
    public boolean close() {

        storeScreenValues();
        
        return super.close();
    }

    /**
     * Overridden to make this dialog resizable.
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        return getShell().computeSize(Size.getSize(500), Size.getSize(450), true);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

    private class NavigationButtonSelectionListener implements SelectionListener {

        public void widgetSelected(SelectionEvent event) {
            if (event.getSource() instanceof Button) {
                int id = (Integer)((Button)event.getSource()).getData();
                if (id == PREVIOUS_ID && selectedMessage > 0) {
                    selectedMessage--;
                    displayMessage(messages.get(selectedMessage));
                    refreshButtonEnablement();
                } else if (id == NEXT_ID && selectedMessage < messages.size() - 1) {
                    selectedMessage++;
                    displayMessage(messages.get(selectedMessage));
                    refreshButtonEnablement();
                }
            }
        }

        public void widgetDefaultSelected(SelectionEvent arg0) {
        }
    }
}
