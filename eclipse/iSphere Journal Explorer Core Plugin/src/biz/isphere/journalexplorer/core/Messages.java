/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.journalexplorer.core.messages"; //$NON-NLS-1$

    public static String E_R_R_O_R;
    public static String Warning;
    public static String Information;

    public static String AddJournalDialog_AllDataRequired;
    public static String AddJournalDialog_Conection;
    public static String AddJournalDialog_Conection_Tooltip;
    public static String AddJournalDialog_FileName;
    public static String AddJournalDialog_FileName_Tooltip;
    public static String AddJournalDialog_MemberName;
    public static String AddJournalDialog_MemberName_Tooltip;
    public static String AddJournalDialog_Library;
    public static String AddJournalDialog_Library_Tooltip;
    public static String AddJournalDialog_OpenJournal;

    public static String ConfigureParsersDialog_DefinitionLibrary;
    public static String ConfigureParsersDialog_DefinitionObject;
    public static String ConfigureParsersDialog_JournalObject;
    public static String ConfigureParsersDialog_ParsingOffset;
    public static String ConfigureParsersDialog_SetDefinitions;

    public static String JoesdParser_Data_type_not_supported_A;
    public static String JoesdParser_TableMetadataDontMatchEntry;
    public static String Journal_RecordNum;
    public static String JournalEntryView_CollapseAll;
    public static String JournalEntryView_CompareEntries;
    public static String JournalEntryView_ConfigureParsers;
    public static String JournalEntryView_ReloadEntries;
    public static String JournalEntryView_CompareSideBySide;
    public static String JournalEntryView_UncomparableEntries;
    public static String JournalEntryViewer_Property;
    public static String JournalEntryViewer_Value;
    public static String JournalExplorerView_OpenJournal;
    public static String JournalExplorerView_Export_to_Excel;
    public static String JournalExplorerView_Export_to_Excel_Tooltip;
    public static String JournalExplorerView_ResetColumnSize;
    public static String JournalExplorerView_ResetColumnSize_Tooltip;
    public static String JournalExplorerView_HighlightUserEntries;
    public static String LongFieldName_OutputFile_Rrn;
    public static String LongFieldName_JOENTL;
    public static String LongFieldName_JOSEQN;
    public static String LongFieldName_JOCODE;
    public static String LongFieldName_JOENTT;
    public static String LongFieldName_JODATE;
    public static String LongFieldName_JOTIME;
    public static String LongFieldName_JOJOB;
    public static String LongFieldName_JOUSER;
    public static String LongFieldName_JONBR;
    public static String LongFieldName_JOPGM;
    public static String LongFieldName_JOPGMLIB;
    public static String LongFieldName_JOPGMDEV;
    public static String LongFieldName_JOPGMASP;
    public static String LongFieldName_JOOBJ;
    public static String LongFieldName_JOLIB;
    public static String LongFieldName_JOMBR;
    public static String LongFieldName_JOCTRR;
    public static String LongFieldName_JOFLAG;
    public static String LongFieldName_JOCCID;
    public static String LongFieldName_JOUSPF;
    public static String LongFieldName_JOSYNM;
    public static String LongFieldName_JOJID;
    public static String LongFieldName_JORCST;
    public static String LongFieldName_JOTGR;
    public static String LongFieldName_JOINCDAT;
    public static String LongFieldName_JOIGNAPY;
    public static String LongFieldName_JOMINESD;
    public static String LongFieldName_JOOBJIND;
    public static String LongFieldName_JOSYSSEQ;
    public static String LongFieldName_JORCV;
    public static String LongFieldName_JORCVLIB;
    public static String LongFieldName_JORCVDEV;
    public static String LongFieldName_JORCVASP;
    public static String LongFieldName_JOARM;
    public static String LongFieldName_JOTHDX;
    public static String LongFieldName_JOADF;
    public static String LongFieldName_JORPORT;
    public static String LongFieldName_JORADR;
    public static String LongFieldName_JOLUW;
    public static String LongFieldName_JOXID;
    public static String LongFieldName_JOOBJTYP;
    public static String LongFieldName_JOFILTYP;
    public static String LongFieldName_JOCMTLVL;
    public static String LongFieldName_JONVI;

    public static String LongFieldName_JOESD;
    public static String MetaTableDAO_NullResultSet;
    public static String MetaTableDAO_TableDefinitionNotFound;
    public static String SelectEntriesToCompareDialog_3;
    public static String SelectEntriesToCompareDialog_ChooseBothRecordsToCompare;
    public static String SelectEntriesToCompareDialog_ChooseEntriesToCompare;
    public static String SelectEntriesToCompareDialog_ChooseLeftRecord;
    public static String SelectEntriesToCompareDialog_ChooseRightRecord;
    public static String SideBySideCompareDialog_SideBySideComparison;
    public static String Number_of_journal_entries_A;
    public static String Number_of_journal_entries_A_of_B;
    public static String Number_of_journal_entries_A_more_items_available;
    public static String subsetted_list;
    public static String SqlEditor_WHERE;
    public static String SqlEditor_WHERE_Tooltip;

    public static String DisplayJournalEntriesDialog_Title;
    public static String DisplayJournalEntriesDialog_From_date_colon;
    public static String DisplayJournalEntriesDialog_From_time_colon;
    public static String DisplayJournalEntriesDialog_To_date_colon;
    public static String DisplayJournalEntriesDialog_To_time_colon;
    public static String DisplayJournalEntriesDialog_Time_Last_used_values;
    public static String DisplayJournalEntriesDialog_Time_Today;
    public static String DisplayJournalEntriesDialog_Time_Yesterday;
    public static String DisplayJournalEntriesDialog_Show_Record_entries_only;
    public static String DisplayJournalEntriesDialog_Fast_date_presets;
    public static String DisplayJournalEntriesDialog_Label_cmd_None;
    public static String DisplayJournalEntriesDialog_Label_cmd_All;
    public static String DisplayJournalEntriesDialog_Label_cmd_Insert;
    public static String DisplayJournalEntriesDialog_Label_cmd_Update;
    public static String DisplayJournalEntriesDialog_Label_cmd_Delete;

    public static String DisplayJournalEntriesDialog_Tooltip_From_date_colon;
    public static String DisplayJournalEntriesDialog_Tooltip_From_time_colon;
    public static String DisplayJournalEntriesDialog_Tooltip_To_date_colon;
    public static String DisplayJournalEntriesDialog_Tooltip_To_time_colon;
    public static String DisplayJournalEntriesDialog_Tooltip_Show_Record_entries_only;
    public static String DisplayJournalEntriesDialog_Tooltip_Fast_date_presets;
    public static String DisplayJournalEntriesDialog_Tooltip_Selectable_Journal_entry_types;
    public static String DisplayJournalEntriesDialog_Tooltip_cmd_None;
    public static String DisplayJournalEntriesDialog_Tooltip_cmd_All;
    public static String DisplayJournalEntriesDialog_Tooltip_cmd_Insert;
    public static String DisplayJournalEntriesDialog_Tooltip_cmd_Update;
    public static String DisplayJournalEntriesDialog_Tooltip_cmd_Delete;

    public static String Error_Unknown_data_type;
    public static String Error_No_NULL_indicator_information_available;
    public static String Error_Field_JONVI_is_too_short_to_store_the_NULL_indicators_of_all_fields;
    public static String Error_Field_JOESD_is_too_short_A_to_hold_the_complete_record_data_B;
    public static String Error_Output_file_A_B_contains_records_that_are_not_a_result_of_a_record_level_operation;
    public static String Error_No_record_level_operation;
    public static String Error_Object_A_B_is_not_journaled;
    public static String Error_Cannot_perform_action_OS400_must_be_at_least_at_level_A;
    public static String Error_Meta_data_not_available_Check_file_A_B;
    public static String Error_The_following_objects_are_not_journaled_Continue_anyway;
    public static String Error_No_object_selected;
    public static String Error_in_SQL_WHERE_CLAUSE_A;

    public static String RJE0001_Retrieve_journal_entry_buffer_is_to_small_to_return_the_next_journal_entry;

    public static String Exception_No_entries_converted_or_received_from_journal_A_B;
    public static String Exception_Buffer_too_small_to_retrieve_next_journal_entry_Check_preferences;

    public static String Warning_Not_all_journal_entries_loaded;
    public static String Warning_Not_all_journal_entries_loaded_unknown_size;

    public static String ColLabel_OutputFile_Rrn;
    public static String ColLabel_JournalEntry_Table_A;

    public static String ButtonLabel_Reload_All;
    public static String ButtonTooltip_Reload_All;
    public static String ButtonLabel_Execute;
    public static String ButtonTooltip_Execute;
    public static String ButtonLabel_AddField;
    public static String ButtonTooltip_AddField;
    public static String ButtonLabel_Clear;
    public static String ButtonTooltip_Clear;

    public static String Tooltip_OutputFile_Rrn;
    public static String Tooltip_JOENTL;
    public static String Tooltip_JOSEQN;
    public static String Tooltip_JOCODE;
    public static String Tooltip_JOENTT;
    public static String Tooltip_JODATE;
    public static String Tooltip_JOTIME;
    public static String Tooltip_JOJOB;
    public static String Tooltip_JOUSER;
    public static String Tooltip_JONBR;
    public static String Tooltip_JOPGM;
    public static String Tooltip_JOPGMLIB;
    public static String Tooltip_JOPGMDEV;
    public static String Tooltip_JOPGMASP;
    public static String Tooltip_JOOBJ;
    public static String Tooltip_JOLIB;
    public static String Tooltip_JOMBR;
    public static String Tooltip_JOCTRR;
    public static String Tooltip_JOFLAG;
    public static String Tooltip_JOCCID;
    public static String Tooltip_JOUSPF;
    public static String Tooltip_JOSYNM;
    public static String Tooltip_JOJID;
    public static String Tooltip_JORCST;
    public static String Tooltip_JOTGR;
    public static String Tooltip_JOINCDAT;
    public static String Tooltip_JOIGNAPY;
    public static String Tooltip_JOMINESD;
    public static String Tooltip_JOOBJIND;
    public static String Tooltip_JOSYSSEQ;
    public static String Tooltip_JORCV;
    public static String Tooltip_JORCVLIB;
    public static String Tooltip_JORCVDEV;
    public static String Tooltip_JORCVASP;
    public static String Tooltip_JOARM;
    public static String Tooltip_JOTHDX;
    public static String Tooltip_JOADF;
    public static String Tooltip_JORPORT;
    public static String Tooltip_JORADR;
    public static String Tooltip_JOLUW;
    public static String Tooltip_JOXID;
    public static String Tooltip_JOOBJTYP;
    public static String Tooltip_JOFILTYP;
    public static String Tooltip_JOCMTLVL;
    public static String Tooltip_JONVI;
    public static String Tooltip_JOESD;
    public static String Tooltip_SqlEditor_Text;

    public static String Title_Connection_A;
    public static String Title_Journal_A;
    public static String Title_File_A;
    public static String Title_Files_A;

    public static String Colors;
    public static String Enable_coloring;
    public static String Enable_coloring_tooltip;
    public static String Clear_Colors;
    public static String Special_value_A_is_not_allowed;
    public static String Library_A_does_not_exist;
    public static String File_A_B_does_not_exist;
    public static String Member_C_does_not_exist_in_file_A_B;

    public static String Limitation_Properties;
    public static String Maximum_number_of_rows_to_fetch;
    public static String Buffer_size;

    public static String Excel_Export;
    public static String Export_Export_column_headings;
    public static String Export_Export_column_headings_tooltip;

    public static String ColumnHeading_ColumnName;
    public static String ColumnHeading_Description;
    public static String ColumnHeading_Color;

    public static String JournalPropertyValue_null;
    public static String JournalPropertyValue_not_available;

    public static String ExcelExport_Headline;
    public static String Finished_exporting_data_to_file_A;
    public static String Exporting_to_Excel;
    public static String Display_Export_Result;

    public static String Status_Loading_journal_entries;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
