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

package biz.isphere.journalexplorer.core.internals;

import biz.isphere.journalexplorer.base.interfaces.IJoesdParserDelegate;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaColumn;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.rse.shared.model.JoesdParserDelegate;

import com.ibm.as400.access.AS400Bin2;
import com.ibm.as400.access.AS400Bin4;
import com.ibm.as400.access.AS400Bin8;
import com.ibm.as400.access.AS400Float4;
import com.ibm.as400.access.AS400Float8;
import com.ibm.as400.access.AS400PackedDecimal;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.AS400ZonedDecimal;
import com.ibm.as400.access.BinaryFieldDescription;
import com.ibm.as400.access.CharacterFieldDescription;
import com.ibm.as400.access.FloatFieldDescription;
import com.ibm.as400.access.PackedDecimalFieldDescription;
import com.ibm.as400.access.Record;
import com.ibm.as400.access.RecordFormat;
import com.ibm.as400.access.ZonedDecimalFieldDescription;

public class JoesdParser {
    private static final int AJUSTE_VARCHAR = 2;

    private MetaTable metadata;

    private RecordFormat joesdRecordFormat;

    private IJoesdParserDelegate joesdParserDelegate = new JoesdParserDelegate();

    public JoesdParser(MetaTable metadata) throws Exception {
        this.metadata = metadata;
        this.initialize();
    }

    private void initialize() throws Exception {
        joesdRecordFormat = new RecordFormat();

        for (MetaColumn column : metadata.getColumns()) {
            switch (column.getDataType()) {
            case BIGINT:
                joesdRecordFormat.addFieldDescription(new BinaryFieldDescription(new AS400Bin8(), column.getName()));
                break;

            case CHAR:
                joesdRecordFormat.addFieldDescription(new CharacterFieldDescription(new AS400Text(column.getSize()), column.getName()));
                break;

            case CLOB:
                throw new Exception(Messages.JoesdParser_CLOBNotSupported);

            case DATE:
                joesdRecordFormat.addFieldDescription(joesdParserDelegate.getDateFieldDescription(column.getName()));
                break;

            case DECIMAL:
                joesdRecordFormat.addFieldDescription(new PackedDecimalFieldDescription(new AS400PackedDecimal(column.getSize(), column
                    .getPrecision()), column.getName()));
                break;

            case DOUBLE:
                joesdRecordFormat.addFieldDescription(new FloatFieldDescription(new AS400Float8(), column.getName()));
                break;

            case INTEGER:
                joesdRecordFormat.addFieldDescription(new BinaryFieldDescription(new AS400Bin4(), column.getName()));
                break;

            case NUMERIC:
                joesdRecordFormat.addFieldDescription(new ZonedDecimalFieldDescription(
                    new AS400ZonedDecimal(column.getSize(), column.getPrecision()), column.getName()));
                break;

            case REAL:
                joesdRecordFormat.addFieldDescription(new FloatFieldDescription(new AS400Float4(), column.getName()));
                break;

            case SMALLINT:
                joesdRecordFormat.addFieldDescription(new BinaryFieldDescription(new AS400Bin2(), column.getName()));
                break;

            case TIME:
                joesdRecordFormat.addFieldDescription(joesdParserDelegate.getTimeFieldDescription(column.getName()));
                break;

            case TIMESTMP:
                joesdRecordFormat.addFieldDescription(joesdParserDelegate.getTimestampFieldDescription(column.getName()));
                break;

            case VARCHAR:
                joesdRecordFormat.addFieldDescription(new CharacterFieldDescription(new AS400Text(column.getSize() + AJUSTE_VARCHAR), column
                    .getName()));
                break;
            }
        }
    }

    public Record execute(JournalEntry journal) throws Exception {
        if (verifyJournalEntry(journal))
            return getFormatoJoesd().getNewRecord(journal.getSpecificData(), metadata.getParsingOffset());
        else
            throw new Exception(Messages.JoesdParser_TableMetadataDontMatchEntry);
    }

    private boolean verifyJournalEntry(JournalEntry journalEntry) {
        return metadata.getName().equals(journalEntry.getObjectName()) && metadata.getLibrary().equals(journalEntry.getObjectLibrary());
    }

    /**
     * @return the formatoJoesd
     */
    public RecordFormat getFormatoJoesd() {
        return joesdRecordFormat;
    }
}
