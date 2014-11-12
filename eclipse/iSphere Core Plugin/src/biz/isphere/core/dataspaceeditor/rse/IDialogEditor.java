/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.rse;

import biz.isphere.core.dataspaceeditor.model.AbstractDWidget;
import biz.isphere.core.dataspaceeditor.model.DEditor;
import biz.isphere.core.dataspaceeditor.model.DReferencedObject;
import biz.isphere.core.dataspaceeditor.model.DTemplateReferencedObject;

public interface IDialogEditor extends IDropObjectListener {

    public void setDataSpaceEditor(DEditor dialog);

    public void addDataSpaceEditor(DEditor dialog);

    public void deleteDataSpaceEditors();

    public void addWidget(DEditor dialog, AbstractDWidget widget);

    public void deleteWidget(DEditor dialog, AbstractDWidget widget);

    public void addReferencedObjectToSelectedEditors(DTemplateReferencedObject referencedObject);

    public void removeSelectedReferencedObject();

    public DEditor[] getSelectedDataSpaceEditors();

    public DReferencedObject[] getSelectedReferencedObjects();
}
