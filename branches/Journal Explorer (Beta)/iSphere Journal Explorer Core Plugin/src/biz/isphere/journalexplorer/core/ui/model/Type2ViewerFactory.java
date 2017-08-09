/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.model;

import java.util.Arrays;
import java.util.HashSet;

public class Type2ViewerFactory extends AbstractTypeViewerFactory {

    private static ViewerColumn[] columnNames = { ViewerColumn.ID, ViewerColumn.JOENTT, ViewerColumn.JOSEQN, ViewerColumn.JOCODE, ViewerColumn.JOENTL,
        ViewerColumn.JODATE, ViewerColumn.JOTIME, ViewerColumn.JOSYNM, ViewerColumn.JOJOB, ViewerColumn.JOUSER, ViewerColumn.JONBR, ViewerColumn.JOUSPF,
        ViewerColumn.JOPGM, ViewerColumn.JOLIB, ViewerColumn.JOOBJ, ViewerColumn.JOMBR, ViewerColumn.JOMINESD, ViewerColumn.JOESD };

    public Type2ViewerFactory() {
        super(new HashSet<ViewerColumn>(Arrays.asList(columnNames)));
    }
}
