/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcefilesearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.search.SearchArgument;

public class SearchDialog extends XDialog {

    private HashMap<String, SearchElement> searchElements;
    private String searchString;
    private Text textString;
    private Text textFromColumn;
    private Text textToColumn;
    private Button buttonCaseNo;
    private Button buttonCaseYes;
    private Button okButton;
    private String _string;
    private int _fromColumn = 1;
    private int _toColumn = 228;
    private String _case;

    public SearchDialog(Shell parentShell, HashMap<String, SearchElement> searchElements) {
        super(parentShell);
        this.searchElements = searchElements;
        ISpherePlugin.getDefault().getPreferenceStore();
        // TODO: Remove disabled statements 'DE.TASKFORCE'
        // searchString =
        // store.getString("DE.TASKFORCE.ISPHERE.SOURCEFILESEARCH.SEARCHSTRING");
        searchString = Preferences.getInstance().getSourceFileSearchString();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite)super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        Group groupAttributes = new Group(container, SWT.NONE);
        groupAttributes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        groupAttributes.setText(Messages.Attributes);
        groupAttributes.setLayout(new GridLayout(2, false));

        Label labelString = new Label(groupAttributes, SWT.NONE);
        labelString.setText(Messages.String_colon);

        textString = new Text(groupAttributes, SWT.BORDER);
        textString.setText(searchString);
        textString.setTextLimit(40);
        textString.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        textString.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                if (textString.getText().trim().equals("")) {
                    okButton.setEnabled(false);
                } else {
                    okButton.setEnabled(true);
                }
            }
        });

        Label labelFromColumn = new Label(groupAttributes, SWT.NONE);
        labelFromColumn.setText(Messages.From_column_colon);

        textFromColumn = new Text(groupAttributes, SWT.BORDER);
        textFromColumn.setText("*START");
        textFromColumn.setTextLimit(10);
        textFromColumn.setLayoutData(new GridData(50, SWT.DEFAULT));
        textFromColumn.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                _fromColumn = 0;
                if (textFromColumn.getText().toUpperCase().trim().equals("*START")) {
                    _fromColumn = 1;
                } else {
                    try {
                        _fromColumn = Integer.parseInt(textFromColumn.getText().trim());
                    } catch (NumberFormatException e1) {
                    }
                }
                if (_fromColumn != 0 && _toColumn != 0 && _fromColumn <= _toColumn) {
                    okButton.setEnabled(true);
                } else {
                    okButton.setEnabled(false);
                }
            }
        });

        Label labelToColumn = new Label(groupAttributes, SWT.NONE);
        labelToColumn.setText(Messages.To_column_colon);

        textToColumn = new Text(groupAttributes, SWT.BORDER);
        textToColumn.setText("*END");
        textToColumn.setTextLimit(10);
        textToColumn.setLayoutData(new GridData(50, SWT.DEFAULT));
        textToColumn.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                _toColumn = 0;
                if (textToColumn.getText().toUpperCase().trim().equals("*END")) {
                    _toColumn = 228;
                } else {
                    try {
                        _toColumn = Integer.parseInt(textToColumn.getText().trim());
                        if (_toColumn > 228) {
                            _toColumn = 228;
                        }
                    } catch (NumberFormatException e1) {
                    }
                }
                if (_fromColumn != 0 && _toColumn != 0 && _fromColumn <= _toColumn) {
                    okButton.setEnabled(true);
                } else {
                    okButton.setEnabled(false);
                }
            }
        });

        Label labelCaseSensitive = new Label(groupAttributes, SWT.NONE);
        labelCaseSensitive.setText(Messages.Case_sensitive_colon);

        Composite groupCaseSensitive = new Composite(groupAttributes, SWT.NONE);
        GridLayout editableLayout = new GridLayout();
        editableLayout.numColumns = 2;
        groupCaseSensitive.setLayout(editableLayout);

        buttonCaseNo = new Button(groupCaseSensitive, SWT.RADIO);
        buttonCaseNo.setText(Messages.No);
        buttonCaseNo.setSelection(true);

        buttonCaseYes = new Button(groupCaseSensitive, SWT.RADIO);
        buttonCaseYes.setText(Messages.Yes);
        buttonCaseYes.setSelection(false);

        Group groupArea = new Group(container, SWT.NONE);
        groupArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        groupArea.setText(Messages.Area);
        groupArea.setLayout(new FillLayout(SWT.HORIZONTAL));

        List listArea = new List(groupArea, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

        ArrayList<String> items = new ArrayList<String>();
        SortedSet<String> keys = new TreeSet<String>(searchElements.keySet());
        Iterator<String> _iterator = keys.iterator();
        while (_iterator.hasNext()) {
            String key = _iterator.next();
            SearchElement value = searchElements.get(key);
            String item = value.getLibrary() + "/" + value.getFile() + "(" + value.getMember() + ")" + " - \"" + value.getDescription() + "\"";
            items.add(item);
        }
        String[] _items = new String[items.size()];
        items.toArray(_items);
        listArea.setItems(_items);

        return container;
    }

    @Override
    protected void okPressed() {
        // TODO: Remove disabled statements 'DE.TASKFORCE'
        // store.setValue("DE.TASKFORCE.ISPHERE.SOURCEFILESEARCH.SEARCHSTRING",
        // textString.getText().trim());
        Preferences.getInstance().setSourceFileSearchString(textString.getText());
        _string = textString.getText().trim();
        if (buttonCaseNo.getSelection()) {
            _case = SearchArgument.CASE_IGNORE;
        } else {
            _case = SearchArgument.CASE_MATCH;
        }
        super.okPressed();
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        okButton = createButton(parent, IDialogConstants.OK_ID, Messages.OK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.Cancel, false);
        if (searchString.equals("")) {
            okButton.setEnabled(false);
        } else {
            okButton.setEnabled(true);
        }
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.iSphere_Source_File_Search);
    }

    public String getString() {
        return _string;
    }

    public int getFromColumn() {
        return _fromColumn;
    }

    public int getToColumn() {
        return _toColumn;
    }

    public String getCase() {
        return _case;
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
        return getShell().computeSize(400, 600, true);
    }

    /**
     * Overriden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

}
