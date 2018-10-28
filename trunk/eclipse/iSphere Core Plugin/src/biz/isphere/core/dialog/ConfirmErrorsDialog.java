/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.base.swt.events.TableAutoSizeAdapter;
import biz.isphere.core.ISpherePlugin;

public class ConfirmErrorsDialog extends XDialog {

    private String dialogTitle;
    private Image dialogTitleImage;
    private String dialogMessage;
    private int kind;
    private String[] buttonLabels;
    private int defaultIndex;

    private Button[] buttons;

    private String[] errorMessages;
    private TableViewer tableViewer;

    private ConfirmErrorsDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, String[] dialogMessageItems,
        int kind, String[] dialogButtonLabels, int defaultIndex) {
        super(parentShell);

        this.dialogTitle = dialogTitle;
        this.dialogTitleImage = dialogTitleImage;
        this.dialogMessage = dialogMessage;
        this.errorMessages = dialogMessageItems;
        this.kind = kind;
        this.buttonLabels = dialogButtonLabels;
        this.defaultIndex = defaultIndex;
    }

    public static boolean openQuestion(Shell parent, String title, String message, String... messageItems) {
        return open(MessageDialog.QUESTION, parent, title, message, messageItems, SWT.NONE);
    }

    public static boolean openConfirm(Shell parent, String title, String message, String... messageItems) {
        return open(MessageDialog.CONFIRM, parent, title, message, messageItems, SWT.NONE);
    }

    private static boolean open(int kind, Shell parent, String title, String message, String[] messageItems, int style) {

        ConfirmErrorsDialog dialog = new ConfirmErrorsDialog(parent, title, null, message, messageItems, kind, getButtonLabels(kind), 0);

        style = style & 0x10000000;
        dialog.setShellStyle(dialog.getShellStyle() | style);

        return dialog.open() == 0;
    }

    static String[] getButtonLabels(int kind) {

        String[] dialogButtonLabels;

        switch (kind) {
        case MessageDialog.ERROR:
        case MessageDialog.INFORMATION:
        case MessageDialog.WARNING:
            dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL };
            break;

        case MessageDialog.CONFIRM:
            dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL };
            break;

        case MessageDialog.QUESTION:
            dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL };
            break;

        case MessageDialog.QUESTION_WITH_CANCEL:
            dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL };
            break;

        default:
            throw new IllegalArgumentException("Illegal value for kind in ConfirmErrorsDialog.getButtonLabels()");
        }

        return dialogButtonLabels;
    }

    /**
     * Overridden to set the window title.
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(dialogTitle);

        if (dialogTitleImage != null) {
            newShell.setImage(dialogTitleImage);
        }
    }

    @Override
    protected int getShellStyle() {
        return super.getShellStyle();
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite dialogArea = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        dialogArea.setLayout(gridLayout);
        dialogArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        createMessageArea(dialogArea);

        createViewerArea(dialogArea);

        return dialogArea;
    }

    private void createViewerArea(Composite parent) {

        if (!hasErrorMessages()) {
            return;
        }

        Composite viewerArea = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        viewerArea.setLayout(gridLayout);
        viewerArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        tableViewer = new TableViewer(viewerArea, SWT.FULL_SELECTION | SWT.BORDER);
        tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

        tableViewer.setContentProvider(new MessagesContentProvider());
        tableViewer.setLabelProvider(new MessagesLabelProvider());
        tableViewer.setInput(errorMessages);

        tableViewer.getTable().setLinesVisible(true);

        final TableColumn tblMessageItemText = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        TableAutoSizeAdapter tableAutoSizeAdapter = new TableAutoSizeAdapter(tableViewer);
        tableAutoSizeAdapter.addResizableColumn(tblMessageItemText, 1);
        viewerArea.addControlListener(tableAutoSizeAdapter);
    }

    private void createMessageArea(Composite parent) {

        Composite messageArea = new Composite(parent, SWT.NONE);
        messageArea.setLayout(new GridLayout(2, false));
        messageArea.setLayoutData(new GridData());

        Image image = getImage();
        if (image != null) {
            Label imageLabel = new Label(messageArea, SWT.NONE);
            imageLabel.setImage(image);
        }

        if (dialogMessage != null) {
            Label messageLabel = new Label(messageArea, SWT.WRAP);
            messageLabel.setText(dialogMessage);
        }
    }

    private Image getImage() {

        Image image = null;

        Display display = getShell().getDisplay();

        switch (kind) {
        case MessageDialog.ERROR:
            image = display.getSystemImage(SWT.ICON_ERROR);
            break;
        case MessageDialog.INFORMATION:
            image = display.getSystemImage(SWT.ICON_INFORMATION);
            break;
        case MessageDialog.WARNING:
            image = display.getSystemImage(SWT.ICON_WARNING);
            break;

        case MessageDialog.QUESTION:
        case MessageDialog.CONFIRM:
        case MessageDialog.QUESTION_WITH_CANCEL:
            image = display.getSystemImage(SWT.ICON_QUESTION);
            break;

        default:
            throw new IllegalArgumentException("Illegal value for kind in ConfirmErrorsDialog.getButtonLabels()");
        }

        return image;
    }

    protected void createButtonsForButtonBar(Composite parent) {

        buttons = new Button[buttonLabels.length];
        for (int i = 0; i < buttonLabels.length; i++) {
            String label = buttonLabels[i];
            Button button = createButton(parent, i, label, defaultIndex == i);
            buttons[i] = button;
        }
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    private boolean hasErrorMessages() {

        if (errorMessages != null && errorMessages.length > 0) {
            return true;
        }

        return false;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {

        if (isResizable()) {
            return new Point(450, 300);
        }

        return super.getDefaultSize();
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

    private class MessagesLabelProvider extends LabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            return (String)element;
        }
    }

    private class MessagesContentProvider implements IStructuredContentProvider {

        private String[] messages;

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            messages = (String[])newInput;
        }

        public void dispose() {
        }

        public Object[] getElements(Object paramObject) {
            return messages;
        }
    }
}
