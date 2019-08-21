/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.rse;

import biz.isphere.core.dataspaceeditordesigner.model.AbstractDWidget;
import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.model.DReferencedObject;
import biz.isphere.core.dataspaceeditordesigner.model.DTemplateReferencedObject;

public interface IDialogEditor extends IDropObjectListener {

    public void setDescription(String text);

    public void setDataSpaceEditor(DEditor dialog);

    public void addDataSpaceEditor(DEditor dialog);

    public void renameDataSpaceEditor(DEditor dialog, String name);

    public void changeDataSpaceEditorProperties(DEditor dialog, String description, int columns);

    public void deleteDataSpaceEditors();

    public void addWidget(DEditor dialog, Class<? extends AbstractDWidget> widget);

    public void changeWidget(AbstractDWidget widget);

    public void deleteWidget(DEditor dialog, AbstractDWidget widget);

    public void moveUpWidget(DEditor dialog, AbstractDWidget widget);

    public void moveDownWidget(DEditor dialog, AbstractDWidget widget);

    public void addReferencedObjectToSelectedEditors(DTemplateReferencedObject referencedObject);

    public void removeSelectedReferencedObject();

    public DEditor[] getSelectedDataSpaceEditors();

    public DReferencedObject[] getSelectedReferencedObjects();
}
