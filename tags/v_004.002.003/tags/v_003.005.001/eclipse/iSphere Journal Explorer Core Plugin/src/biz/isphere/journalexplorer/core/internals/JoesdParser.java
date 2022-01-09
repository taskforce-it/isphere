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
import biz.isphere.journalexplorer.core.as400.access.AS400DataLink;
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
import com.ibm.as400.access.DBCSGraphicFieldDescription;
import com.ibm.as400.access.FieldDescription;
import com.ibm.as400.access.FloatFieldDescription;
import com.ibm.as400.access.HexFieldDescription;
import com.ibm.as400.access.PackedDecimalFieldDescription;
import com.ibm.as400.access.Record;
import com.ibm.as400.access.RecordFormat;
import com.ibm.as400.access.ZonedDecimalFieldDescription;

public class JoesdParser {

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
            switch (column.getType()) {
            /*
             * Numeric field types:
             */
            case INTEGER: {
                joesdRecordFormat.addFieldDescription(new BinaryFieldDescription(new AS400Bin4(), column.getName()));
                break;
            }

            case SMALLINT: {
                joesdRecordFormat.addFieldDescription(new BinaryFieldDescription(new AS400Bin2(), column.getName()));
                break;
            }

            case BIGINT: {
                joesdRecordFormat.addFieldDescription(new BinaryFieldDescription(new AS400Bin8(), column.getName()));
                break;
            }

            case REAL: {
                joesdRecordFormat.addFieldDescription(new FloatFieldDescription(new AS400Float4(), column.getName()));
                break;
            }

            case DOUBLE: {
                joesdRecordFormat.addFieldDescription(new FloatFieldDescription(new AS400Float8(), column.getName()));
                break;
            }

            case DECREAL:
                FieldDescription decFloatRealField = joesdParserDelegate.getDecRealFieldDescription(column.getName());
                joesdRecordFormat.addFieldDescription(decFloatRealField);
                break;

            case DECDOUBLE:
                FieldDescription decFloatDoubleField = joesdParserDelegate.getDecDoubleFieldDescription(column.getName());
                joesdRecordFormat.addFieldDescription(decFloatDoubleField);
                break;

            case NUMERIC: {
                joesdRecordFormat.addFieldDescription(new ZonedDecimalFieldDescription(new AS400ZonedDecimal(column.getLength(), column
                    .getDecimalPositions()), column.getName()));
                break;
            }

            case DECIMAL: {
                joesdRecordFormat.addFieldDescription(new PackedDecimalFieldDescription(new AS400PackedDecimal(column.getLength(), column
                    .getDecimalPositions()), column.getName()));
                break;
            }
                /*
                 * Character field types:
                 */
            case CHAR: {
                CharacterFieldDescription charField = new CharacterFieldDescription(new AS400Text(column.getLength(), column.getCcsid()),
                    column.getName());
                joesdRecordFormat.addFieldDescription(charField);
                break;
            }

            case VARCHAR: {
                CharacterFieldDescription varCharField = new CharacterFieldDescription(new AS400Text(column.getLength(), column.getCcsid()),
                    column.getName());
                varCharField.setVARLEN(column.getLength());
                joesdRecordFormat.addFieldDescription(varCharField);
                break;
            }

            case BINARY: {
                CharacterFieldDescription binaryField = new CharacterFieldDescription(new AS400Text(column.getLength(), column.getCcsid()),
                    column.getName());
                joesdRecordFormat.addFieldDescription(binaryField);
                break;
            }

            case VARBINARY: {
                CharacterFieldDescription varBinaryField = new CharacterFieldDescription(new AS400Text(column.getLength(), column.getCcsid()),
                    column.getName());
                varBinaryField.setVARLEN(column.getLength());
                joesdRecordFormat.addFieldDescription(varBinaryField);
                break;
            }
                /*
                 * Graphic field types:
                 */
            case GRAPHIC: {
                DBCSGraphicFieldDescription graphicField = new DBCSGraphicFieldDescription(new AS400Text(column.getLength() * 2, column.getCcsid()),
                    column.getName());
                joesdRecordFormat.addFieldDescription(graphicField);
                break;
            }

            case VARGRAPHIC: {
                DBCSGraphicFieldDescription varGraphicField = new DBCSGraphicFieldDescription(
                    new AS400Text(column.getLength() * 2, column.getCcsid()), column.getName());
                varGraphicField.setVARLEN(varGraphicField.getLength());
                joesdRecordFormat.addFieldDescription(varGraphicField);
                break;
            }
                /*
                 * Date/time field types:
                 */
            case DATE: {
                // joesdRecordFormat.addFieldDescription(joesdParserDelegate.getDateFieldDescription(column.getName(),
                // column.getDateTimeFormat(),
                // column.getDateTimeSeparator()));
                FieldDescription field = new CharacterFieldDescription(new AS400Text(column.getLength(), column.getCcsid()), column.getName());
                joesdRecordFormat.addFieldDescription(field);
                break;
            }

            case TIME: {
                // joesdRecordFormat.addFieldDescription(joesdParserDelegate.getTimeFieldDescription(column.getName(),
                // column.getDateTimeFormat(),
                // column.getDateTimeSeparator()));
                FieldDescription field = new CharacterFieldDescription(new AS400Text(column.getLength(), column.getCcsid()), column.getName());
                joesdRecordFormat.addFieldDescription(field);
                break;
            }

            case TIMESTAMP: {
                // FieldDescription field =
                // joesdParserDelegate.getTimestampFieldDescription(column.getName());
                FieldDescription field = new CharacterFieldDescription(new AS400Text(column.getLength(), column.getCcsid()), column.getName());
                joesdRecordFormat.addFieldDescription(field);
                break;
            }
                /*
                 * LOB (CLOB & BLOB) field types:
                 */
            case LOB: {
                CharacterFieldDescription lobField = new CharacterFieldDescription(new AS400Text(column.getLength(), column.getCcsid()),
                    column.getName());
                if (column.isVaryingLength()) {
                    lobField.setVARLEN(column.getLength());
                }
                joesdRecordFormat.addFieldDescription(lobField);
                break;
            }

                /*
                 * DataLink field type:
                 */
            case DATALINK: {
                HexFieldDescription charField = new HexFieldDescription(new AS400DataLink(column.getLength(), column.getCcsid()), column.getName());
                joesdRecordFormat.addFieldDescription(charField);
                break;
            }

            case UNKNOWN: {
                UnknownFieldDescription unknownField = new UnknownFieldDescription(new AS400Text(column.getBufferLength(), 65535), column.getName());
                joesdRecordFormat.addFieldDescription(unknownField);
                break;
            }

            default:
                /*
                 * Should never be reached. All unsupported data types should
                 * come by as UNKNOWN.
                 */
                throw new Exception(Messages.bind(Messages.JoesdParser_Data_type_not_supported_A, column.getType()));
            }
        }

        return;
    }

    public Record execute(JournalEntry journal) throws Exception {

        if (verifyJournalEntry(journal)) {
            byte[] recordData = journal.getSpecificData(metadata.getRecordLength());
            return getJoesdRecordFormat().getNewRecord(recordData, metadata.getParsingOffset());
        } else {
            throw new Exception(Messages.JoesdParser_TableMetadataDontMatchEntry);
        }
    }

    private boolean verifyJournalEntry(JournalEntry journalEntry) {
        return metadata.getName().equals(journalEntry.getObjectName()) && metadata.getLibrary().equals(journalEntry.getObjectLibrary());
    }

    /**
     * @return the formatoJoesd
     */
    public RecordFormat getJoesdRecordFormat() {
        return joesdRecordFormat;
    }
}
