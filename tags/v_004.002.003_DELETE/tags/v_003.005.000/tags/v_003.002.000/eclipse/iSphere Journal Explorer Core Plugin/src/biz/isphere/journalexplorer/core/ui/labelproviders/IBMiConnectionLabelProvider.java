/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.labelproviders;

import org.eclipse.jface.viewers.LabelProvider;

import biz.isphere.journalexplorer.rse.shared.model.ConnectionDelegate;

public class IBMiConnectionLabelProvider extends LabelProvider {

    @Override
    public String getText(Object element) {

        if (ConnectionDelegate.instanceOf(element)) {
            return ConnectionDelegate.getConnectionName(element);
        }

        return super.getText(element);
    }
}
