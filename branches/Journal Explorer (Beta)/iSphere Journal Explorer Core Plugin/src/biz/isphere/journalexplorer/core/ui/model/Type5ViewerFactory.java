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

public class Type5ViewerFactory extends AbstractTypeViewerFactory {

    // @formatter:off
    private static ViewerColumn[] columnNames = { 
        ViewerColumn.ID, 
        ViewerColumn.JOENTT, 
        ViewerColumn.JOSEQN, 
        ViewerColumn.JOCODE, 
        ViewerColumn.JOENTL,
        ViewerColumn.JODATE, 
        ViewerColumn.JOTIME,
        ViewerColumn.JOCCID,
        ViewerColumn.JOLUW,
        ViewerColumn.JOXID,
        ViewerColumn.JOCMTLVL,
        ViewerColumn.JOSYNM, 
        ViewerColumn.JOSYSSEQ, 
        ViewerColumn.JOJOB, 
        ViewerColumn.JOUSER, 
        ViewerColumn.JONBR, 
        ViewerColumn.JOTHDX, 
        ViewerColumn.JOUSPF,
        ViewerColumn.JOPGM, 
        ViewerColumn.JOPGMLIB, 
        ViewerColumn.JOPGMDEV, 
        ViewerColumn.JOPGMASP, 
        ViewerColumn.JOLIB, 
        ViewerColumn.JOOBJ,
        ViewerColumn.JOOBJIND,
        ViewerColumn.JOOBJTYP,
        ViewerColumn.JOFILTYP,
        ViewerColumn.JOMBR, 
        ViewerColumn.JOJID, 
        ViewerColumn.JORCV, 
        ViewerColumn.JORCVLIB, 
        ViewerColumn.JORCVDEV, 
        ViewerColumn.JORCVASP, 
        ViewerColumn.JOARM, 
        ViewerColumn.JOADF, 
        ViewerColumn.JORPORT, 
        ViewerColumn.JORADR, 
        ViewerColumn.JORCST, 
        ViewerColumn.JOTGR,
        ViewerColumn.JOIGNAPY, 
        ViewerColumn.JOMINESD, 
        ViewerColumn.JOINCDAT,
        ViewerColumn.JOESD };
    // @formatter:on

    public Type5ViewerFactory() {
        super(new HashSet<ViewerColumn>(Arrays.asList(columnNames)));
    }
}
