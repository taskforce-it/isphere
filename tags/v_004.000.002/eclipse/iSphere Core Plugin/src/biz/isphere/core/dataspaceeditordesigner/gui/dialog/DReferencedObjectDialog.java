/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.gui.dialog;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.swt.widgets.UpperCaseOnlyVerifier;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditordesigner.model.DTemplateReferencedObject;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.preferences.Preferences;

public class DReferencedObjectDialog extends AbstractDialog {

    private String type;
    private DTemplateReferencedObject referencedObject;

    private Text textName;
    private Text textLibrary;

    private Validator nameValidator;
    private Validator libraryValidator;

    public DReferencedObjectDialog(Shell parentShell, String type) {
        super(parentShell);
        this.type = type;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        if (ISeries.DTAARA.equals(type)) {
            newShell.setText(Messages.Assign_data_area);
        } else if (ISeries.USRSPC.equals(type)) {
            newShell.setText(Messages.Assign_user_space);
        } else {
            throw new IllegalArgumentException("Illegal 'type' value: " + type); //$NON-NLS-1$
        }
    }

    @Override
    protected void createContent(Composite parent) {

        // Name
        textName = createNameField(parent, Messages.Name_colon);
        textName.addVerifyListener(new UpperCaseOnlyVerifier());
        textName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                validateName();
            }
        });

        // TODO: fix name validator (pass CCSID) - DONE
        nameValidator = Validator.getNameInstance(getDefaultSystemCcsid());

        // Library
        textLibrary = createNameField(parent, Messages.Library_colon);
        textLibrary.setText(ISeries.SPCVAL_LIBL);
        textLibrary.addVerifyListener(new UpperCaseOnlyVerifier());
        textLibrary.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                validateLibrary();
            }
        });

        // TODO: fix library name validator (pass CCSID) - DONE
        libraryValidator = Validator.getLibraryNameInstance(getDefaultSystemCcsid(), ISeries.SPCVAL_LIBL);
    }

    public DTemplateReferencedObject getReferencedObject() {
        return referencedObject;
    }

    @Override
    protected boolean validate() {

        // Name
        if (!validateName()) {
            return false;
        }

        // Columns
        if (!validateLibrary()) {
            return false;
        }

        return true;
    }

    private boolean validateName() {

        if (!nameValidator.validate(textName.getText())) {
            setErrorMessage(textName, "Object name is invalid or missing.");
            return false;
        }

        clearErrorMessage(textName);
        return true;
    }

    private boolean validateLibrary() {

        if (!libraryValidator.validate(textLibrary.getText())) {
            setErrorMessage(textLibrary, "Library name is invalid or missing.");
            return false;
        }

        clearErrorMessage(textLibrary);
        return true;
    }

    @Override
    protected void performOKPressed() {

        String name = textName.getText();
        String library = textLibrary.getText();
        referencedObject = new DTemplateReferencedObject(name, library, type);
    }

    private int getDefaultSystemCcsid() {
        return Preferences.getInstance().getSystemCcsid();
    }
}
