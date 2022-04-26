package org.bac.gati.tools.journalexplorer.internals;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.bac.gati.tools.journalexplorer.ui.views.messages"; //$NON-NLS-1$
	public static String AddJournalDialog_AllDataRequired;
	public static String AddJournalDialog_Conection;
	public static String AddJournalDialog_FileName;
	public static String AddJournalDialog_Library;
	public static String AddJournalDialog_OpenJournal;
	public static String ConfigureParsersDialog_DefinitionLibrary;
	public static String ConfigureParsersDialog_DefinitionObject;
	public static String ConfigureParsersDialog_JournalObject;
	public static String ConfigureParsersDialog_ParsingOffset;
	public static String ConfigureParsersDialog_SetDefinitions;
	public static String JoesdParser_CLOBNotSupported;
	public static String JoesdParser_TableMetadataDontMatchEntry;
	public static String Journal_RecordNum;
	public static String JournalEntryView_CompareEntries;
	public static String JournalEntryView_ConfigureTableDefinitions;
	public static String JournalEntryView_ReloadEntries;
	public static String JournalEntryView_ShowSideBySide;
	public static String JournalEntryView_UncomparableEntries;
	public static String JournalEntryViewer_Property;
	public static String JournalEntryViewer_Value;
	public static String JournalExplorerView_OpenJournal;
	public static String JournalExplorerView_HighlightUserEntries;
	public static String JournalProperties_JOCODE;
	public static String JournalProperties_JOENTL;
	public static String JournalProperties_JOENTT;
	public static String JournalProperties_JOESD;
	public static String JournalProperties_JOSEQN;
	public static String JournalProperties_RRN;
	public static String DAOBase_ConnectionNotStablished;
	public static String DAOBase_InvalidConnectionObject;
	public static String MetaTableDAO_NullResultSet;
	public static String MetaTableDAO_TableDefinitionNotFound;
	public static String SelectEntriesToCompareDialog_3;
	public static String SelectEntriesToCompareDialog_ChooseBothRecordsToCompare;
	public static String SelectEntriesToCompareDialog_ChooseEntriesToCompare;
	public static String SelectEntriesToCompareDialog_ChooseLeftRecord;
	public static String SelectEntriesToCompareDialog_ChooseRightRecord;
	public static String SideBySideCompareDialog_SideBySideComparison;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
