/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.bac.gati.tools.journalexplorer.rse.shared.ui.labelprovider2;

import org.bac.gati.tools.journalexplorer.rse.base.interfaces.IMetaTable;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ParserColumnLabel extends LabelProvider implements ITableLabelProvider {

    public Image getColumnImage(Object arg0, int arg1) {
        return null;
    }

    public String getColumnText(Object object, int index) {

        IMetaTable metaTable = (IMetaTable)object;

        switch (index) {
        case 0: // Journaled Object
            return metaTable.getQualifiedName();
        case 1: // Parser Library
            return metaTable.getDefinitionLibrary();
        case 2: // Parser Object
            return metaTable.getDefinitionName();
        case 3: // Parsing offset
            return Integer.toString(metaTable.getParsingOffset());
        default:
            break;
        }

        return null;
    }

}
