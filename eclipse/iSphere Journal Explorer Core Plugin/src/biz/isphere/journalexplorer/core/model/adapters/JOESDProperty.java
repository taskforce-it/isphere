/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.adapters;

import java.util.ArrayList;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.JoesdParser;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaColumn;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumnUI;

import com.ibm.as400.access.Record;

/**
 * The JOESDProperty groups the entry specific fields of a {@link JournalEntry}.
 * It is a child of {@link JournalProperties}.
 */
public class JOESDProperty extends JournalProperty {

    private JournalEntry journalEntry;

    private MetaTable metatable;

    private ArrayList<JournalProperty> specificProperties;

    private Record parsedJOESD;

    public JOESDProperty(JournalEntryColumnUI columnDef, JournalEntry journalEntry) {
        super(columnDef, null, null);

        initialize(journalEntry);
    }

    public JOESDProperty(JournalEntryColumnUI columnDef, Object parent, JournalEntry journalEntry) {
        super(columnDef, "", parent); //$NON-NLS-1$

        initialize(journalEntry);
    }

    private void initialize(JournalEntry journalEntry) {

        this.journalEntry = journalEntry;
        this.journalEntry.setJoesdProperty(this);

        setErrorParsing(false);
        this.executeParsing();
    }

    public void executeParsing() {
        try {
            initialize();
            parseJOESD();
        } catch (Exception exception) {
            value = ExceptionHelper.getLocalizedMessage(exception);
            setErrorParsing(true);
        }
    }

    private void initialize() throws Exception {

        setErrorParsing(false);

        metatable = null;

        parsedJOESD = null;

        if (specificProperties != null) {
            specificProperties.clear();
        } else {
            specificProperties = new ArrayList<JournalProperty>();
        }
    }

    @Override
    public void setErrorParsing(boolean error) {
        super.setErrorParsing(error);

        if (!isErrorParsing()) {
            this.value = "";
        }
    }

    private void parseJOESD() throws Exception {

        String columnName;
        String columnLabel;

        if (!journalEntry.isRecordEntryType()) {

            metatable = null;

            value = Messages.Error_No_record_level_operation;
            setErrorParsing(true);

        } else {

            metatable = MetaDataCache.getInstance().retrieveMetaData(journalEntry);

            parsedJOESD = new JoesdParser(metatable).execute(journalEntry);

            if (!metatable.hasColumns()) {
                value = Messages.bind(Messages.Error_Meta_data_not_available_Check_file_A_B, metatable.getLibrary(), metatable.getName());
                setErrorParsing(true);
                return;
            }

            for (MetaColumn column : metatable.getColumns()) {
                columnName = column.getName().trim();
                if (column.getText() != null && column.getText().trim().length() != 0) {
                    columnLabel = column.getText().trim();
                } else {
                    columnLabel = ""; //$NON-NLS-1$
                }

                // parsedJOESD.getRecordFormat().getFieldDescription(column.getName()).getDataType().TYPE_ARRAY;

                if (column.getOutputBufferOffset() + column.getBufferLength() > journalEntry.getSpecificDataLength()) {
                    JournalProperty journalProperty = new JournalProperty(columnName, columnLabel, Messages.JournalPropertyValue_not_available, this);
                    journalProperty.setErrorParsing(true);
                    specificProperties.add(journalProperty);
                } else if (column.isNullable() && journalEntry.isNull(column.getIndex())) {
                    JournalProperty journalProperty = new JournalProperty(columnName, columnLabel, Messages.JournalPropertyValue_null, this);
                    journalProperty.setNullValue(true);
                    specificProperties.add(journalProperty);
                } else if (MetaColumn.DataType.UNKNOWN.equals(column.getType())) {
                    JournalProperty journalProperty = new JournalProperty(columnName, columnLabel, Messages.Error_Unknown_data_type, this);
                    journalProperty.setErrorParsing(true);
                    specificProperties.add(journalProperty);
                } else if (MetaColumn.DataType.LOB.equals(column.getType())) {
                    JournalProperty journalProperty = new JournalProperty(columnName, columnLabel, parsedJOESD.getField(column.getName()).toString()
                        .trim(), getDataType(column.getName()), this);
                    specificProperties.add(journalProperty);
                } else {
                    JournalProperty journalProperty = new JournalProperty(columnName, columnLabel, parsedJOESD.getField(column.getName()).toString(),
                        getDataType(column.getName()), this);
                    specificProperties.add(journalProperty);
                }
            }

        }
    }

    private int getDataType(String name) {
        return parsedJOESD.getRecordFormat().getFieldDescription(name).getDataType().getInstanceType();
    }

    public JournalProperty[] toPropertyArray() {

        if (isErrorParsing()) {
            try {
                executeParsing();
            } catch (Exception e) {
                // Ignore errors
            }
        }

        if (specificProperties != null) {
            return specificProperties.toArray(new JournalProperty[specificProperties.size()]);
        } else {
            return null;
        }
    }

    @Override
    public int compareTo(JournalProperty comparable) {

        if (comparable instanceof JOESDProperty) {
            JOESDProperty joesdSpecificProperty = (JOESDProperty)comparable;

            if (joesdSpecificProperty.parsedJOESD == null || parsedJOESD == null) {
                highlighted = comparable.highlighted = true;
                return -1;

            } else if (joesdSpecificProperty.parsedJOESD.getNumberOfFields() != parsedJOESD.getNumberOfFields()) {
                highlighted = comparable.highlighted = true;
                return -1;

            } else {
                int status = 0;

                for (int i = 0; i < specificProperties.size(); i++) {

                    if (specificProperties.get(i).compareTo(joesdSpecificProperty.specificProperties.get(i)) != 0) {
                        status = -1;
                    }
                }
                return status;
            }
        } else {
            return -1;
        }
    }

    public ContentAssistProposal[] getContentAssistProposals() {

        try {

            MetaTable metaTable = MetaDataCache.getInstance().retrieveMetaData(journalEntry);
            return metaTable.getContentAssistProposals();

        } catch (Exception e) {
            // Ignore errors
            return new ContentAssistProposal[0];
        }
    }
}
