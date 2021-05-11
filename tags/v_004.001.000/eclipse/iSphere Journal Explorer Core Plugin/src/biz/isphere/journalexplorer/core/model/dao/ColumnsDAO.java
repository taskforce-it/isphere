/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.dao;

import biz.isphere.journalexplorer.core.Messages;

public enum ColumnsDAO {
    ID ("DECIMAL(15, 0)", Messages.LongFieldName_OutputFile_Rrn),
    JOENTL ("NUMERIC(5, 0)", Messages.LongFieldName_JOENTL),
    JOSEQN ("NUMERIC(10, 0)", Messages.LongFieldName_JOSEQN),
    JOCODE ("CHAR(1)", Messages.LongFieldName_JOCODE),
    JOENTT ("CHAR(2)", Messages.LongFieldName_JOENTT),
    JODATE ("CHAR(6)", Messages.LongFieldName_JODATE),
    JOTIME ("NUMERIC(6)", Messages.LongFieldName_JOTIME),
    JOTSTP ("TIMESTAMP", Messages.LongFieldName_JOTSTP),
    JOJOB ("CHAR(10)", Messages.LongFieldName_JOJOB),
    JOUSER ("CHAR(10)", Messages.LongFieldName_JOUSER),
    JONBR ("NUMERIC(6, 0)", Messages.LongFieldName_JONBR),
    JOPGM ("CHAR(10)", Messages.LongFieldName_JOPGM),
    JOPGMLIB ("CHAR(10)", Messages.LongFieldName_JOPGMLIB),
    JOPGMDEV ("CHAR(10)", Messages.LongFieldName_JOPGMDEV),
    JOPGMASP ("NUMERIC(5, 0)", Messages.LongFieldName_JOPGMASP),
    JOOBJ ("CHAR(10)", Messages.LongFieldName_JOOBJ),
    JOLIB ("CHAR(10)", Messages.LongFieldName_JOLIB),
    JOMBR ("CHAR(10)", Messages.LongFieldName_JOMBR),
    JOCTRR ("NUMERIC(10, 0)", Messages.LongFieldName_JOCTRR),
    JOFLAG ("CHAR(1)", Messages.LongFieldName_JOFLAG),
    JOCCID ("NUMERIC(10, 0)", Messages.LongFieldName_JOCCID),
    JOUSPF ("CHAR(10)", Messages.LongFieldName_JOUSPF),
    JOSYNM ("CHAR(8)", Messages.LongFieldName_JOSYNM),
    JOJID ("CHAR(10)", Messages.LongFieldName_JOJID),
    JORCST ("CHAR(1)", Messages.LongFieldName_JORCST),
    JOTGR ("CHAR(1)", Messages.LongFieldName_JOTGR),
    JOINCDAT ("CHAR(1)", Messages.LongFieldName_JOINCDAT),
    JOIGNAPY ("CHAR(1)", Messages.LongFieldName_JOIGNAPY),
    JOMINESD ("CHAR(1)", Messages.LongFieldName_JOMINESD),
    JOOBJIND ("CHAR(1)", Messages.LongFieldName_JOOBJIND),
    JOSYSSEQ ("CHAR(20)", Messages.LongFieldName_JOSYSSEQ),
    JORCV ("CHAR(10)", Messages.LongFieldName_JORCV),
    JORCVLIB ("CHAR(10)", Messages.LongFieldName_JORCVLIB),
    JORCVDEV ("CHAR(10)", Messages.LongFieldName_JORCVDEV),
    JORCVASP ("NUMERIC(5, 0)", Messages.LongFieldName_JORCVASP),
    JOARM ("NUMERIC(5, 0)", Messages.LongFieldName_JOARM),
    JOTHDX ("CHAR(8)", Messages.LongFieldName_JOTHDX),
    JOADF ("CHAR(1)", Messages.LongFieldName_JOADF),
    JORPORT ("NUMERIC(5, 0)", Messages.LongFieldName_JORPORT),
    JORADR ("CHAR(46)", Messages.LongFieldName_JORADR),
    JOLUW ("CHAR(39)", Messages.LongFieldName_JOLUW),
    JOXID ("CHAR(140)", Messages.LongFieldName_JOXID),
    JOOBJTYP ("CHAR(7)", Messages.LongFieldName_JOOBJTYP),
    JOFILTYP ("CHAR(1)", Messages.LongFieldName_JOFILTYP),
    JOCMTLVL ("CHAR(7)", Messages.LongFieldName_JOCMTLVL),
    JOESD ("CHAR(N)", Messages.LongFieldName_JOESD),
    JONVI ("CHAR(50)", Messages.LongFieldName_JONVI);

    private String sqlType;
    private String description;

    private ColumnsDAO(String sqlType, String description) {
        this.sqlType = sqlType;
        this.description = description;
    }

    public String sqlType() {
        return sqlType;
    }

    public String description() {
        return description;
    }
}
