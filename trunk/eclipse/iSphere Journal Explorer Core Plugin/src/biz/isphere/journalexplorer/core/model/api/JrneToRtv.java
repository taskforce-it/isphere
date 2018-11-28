/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.api;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.journalexplorer.core.internals.QualifiedName;
import biz.isphere.journalexplorer.core.model.JournalCode;
import biz.isphere.journalexplorer.core.model.JournalEntryType;
import biz.isphere.journalexplorer.core.model.shared.Journal;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Bin4;
import com.ibm.as400.access.AS400DataType;
import com.ibm.as400.access.AS400Structure;
import com.ibm.as400.access.AS400Text;

/**
 * Class representing 'Journal Entry to Retrieve' section of the
 * QjoRetrieveJournalEntries API. Mainly used to encode the following:
 * 
 * <pre>
 * a.  Binary(4) - Number of Variable Length Records
 * b1. Binary(4) - Length of Variable Length Record - length(b1+b2+b3+b4)
 * b2. Binary(4) - Key
 * b3. Binary(4) - Length of Data - length(b4)
 * b4. Char(*)   - Data
 * </pre>
 * 
 * To see an example using AS400Structure for a composite type of data types:
 * http://publib.boulder.ibm.com/html/as400/java/rzahh115.htm#HDRRZAHH-COMEX
 * <p>
 * This class has been inspired by the RJNE0100 example written by Stanley Vong.
 * See <a href="http://stanleyvong.blogspot.de/">RJNE0100</a> example from
 * February 19, 2013.
 */
public class JrneToRtv implements Serializable, Cloneable {

    private static final long serialVersionUID = 6666741116944210055L;

    public static final String FMTMINDTA_NO = "*NO";
    public static final String FMTMINDTA_YES = "*YES";
    public static final String ENTTYP_ALL = "*ALL";
    public static final String ENTTYP_RCD = "*RCD";
    public static final String JRNCDE_ALLSLT = "*ALLSLT";
    public static final String JRNCDE_CTL = "*CTL";
    public static final String JRNCDE_ALL = "*ALL";
    public static final String FRMENT_FIRST = "*FIRST";
    public static final String RCVRNG_CURRENT = "*CURRENT";
    public static final String RCVRNG_CURCHAIN = "*CURCHAIN";
    public static final String NULLINDLEN_VARLEN = "*VARLEN";

    private Journal journal;
    private HashMap<RetrieveKey, RetrieveCriterion> selectionCriteria;
    private ArrayList<AS400DataType> structure = null;
    private ArrayList<Object> data = null;
    private boolean isDirty = true;
    SimpleDateFormat dateFormatter = null;

    private LinkedList<FileCriterion> fileCriterions;

    public JrneToRtv(Journal aJournal) {
        journal = aJournal;
        selectionCriteria = new HashMap<RetrieveKey, RetrieveCriterion>();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
        fileCriterions = new LinkedList<FileCriterion>();

        setRcvRng(RCVRNG_CURCHAIN);
        setFormatMinimzedData(FMTMINDTA_YES);
    }

    public int getNullValueIndicatorsLength() {

        Object value = getSelectionValue(RetrieveKey.NULLINDLEN);
        if (value instanceof String) {
            String length = ((String)value).trim();
            if (NULLINDLEN_VARLEN.equals(length)) {
                return -1;
            } else {
                return Integer.parseInt(length);
            }
        }

        return -1; // default: *VARLEN
    }

    public String getJournalName() {
        return journal.getName();
    }

    public String getJournalLibraryName() {
        return journal.getLibrary();
    }

    public String getConnectionName() {
        return journal.getConnectionName();
    }

    public String getQualifiedJournalName() {
        return QualifiedName.getName(journal.getLibrary(), journal.getName());
    }

    public AS400 getSystem() {
        return IBMiHostContributionsHandler.getSystem(journal.getConnectionName());
    }

    public AS400DataType[] getStructure() {
        prepareStructureAndData();
        return structure.toArray(new AS400DataType[0]);
    }

    public Object[] getData() {
        prepareStructureAndData();
        return data.toArray(new Object[0]);
    }

    /**
     * Add retrieval criterion 01: Range of journal receivers.
     * 
     * @param aReceiverRange:<br>
     *        *CURRENT - The journal receiver that is attached when starting to
     *        retrieve journal entries is used. If *CURRENT is specified, the
     *        associated library name and ending journal receiver fields should
     *        be blank.<br>
     *        *CURCHAIN - The journal receiver chain that includes the journal
     *        receiver that is attached when starting to retrieve journal
     *        entries is used. This receiver chain does not cross a break in the
     *        chain. If there is a break in the chain, the receiver range is
     *        from the most recent break in the chain through the receiver that
     *        is attached when starting to retrieve journal entries. If
     *        *CURCHAIN is specified, the associated library name and ending
     *        journal receiver fields should be blank.
     */
    public void setRcvRng(String aReceiverRangeSpecialValue) {
        if (!RCVRNG_CURCHAIN.equals(aReceiverRangeSpecialValue) && !RCVRNG_CURRENT.equals(aReceiverRangeSpecialValue)) {
            throw new IllegalArgumentException(String.format("Value for '%s' must be either '*CURCHAIN' or '*CURRENT' if String; "
                + "or an instance of String[] with four elements (in the order of: starting receiver, staring library, "
                + "ending receiver, ending library).", RetrieveKey.RCVRNG.getDescription()));
        }
        String temp = padRight(aReceiverRangeSpecialValue, 40);
        addSelectionCriterion(RetrieveKey.RCVRNG, new AS400Text(40), temp);
    }

    /**
     * Add retrieval criterion 01: Range of journal receivers.
     * <p>
     * This can be used to indicate where to start when previous returned
     * continuation handle='1'.
     * 
     * @param aStartReceiver - Starting journal receiver name
     * @param aStartReceiverLibrary - Starting journal receiver library
     */
    public void setRcvRng(String aStartReceiver, String aStartReceiverLibrary) {
        setRcvRng(aStartReceiver, aStartReceiverLibrary, aStartReceiver, aStartReceiverLibrary);
    }

    /**
     * Add retrieval criterion 01: Range of journal receivers.
     * <p>
     * This can be used to indicate where to start when previous returned
     * continuation handle='1'.
     * 
     * @param aStartReceiver - Starting journal receiver name
     * @param aStartReceiverLibrary - Starting journal receiver library
     * @param anEndReceiver - Ending journal receiver name
     * @param anEndReceiverLibrary - Ending journal receiver library
     */
    public void setRcvRng(String aStartReceiver, String aStartReceiverLibrary, String anEndReceiver, String anEndReceiverLibrary) {
        String temp = padRight(aStartReceiver, 10) + padRight(aStartReceiverLibrary, 10) + padRight(anEndReceiver, 10)
            + padRight(anEndReceiverLibrary, 10);
        addSelectionCriterion(RetrieveKey.RCVRNG, new AS400Text(40), temp);
    }

    /**
     * Add retrieval criterion 02: starting sequence number.
     * 
     * @param aFromSequenceNumberSpecialValue - *FIRST - The first journal entry
     *        in the specified journal receiver range is the first entry
     *        considered for retrieval
     */
    public void setFromEnt(String aFromSequenceNumberSpecialValue) {
        if (FRMENT_FIRST.equals(aFromSequenceNumberSpecialValue)) {
            String temp = padRight(aFromSequenceNumberSpecialValue, 20);
            addSelectionCriterion(RetrieveKey.FROMENT, new AS400Text(20), temp.replace(' ', '0'));
            rmvSelectionCriterion(RetrieveKey.FROMTIME);
        } else {
            int fromSequenceNumber = IntHelper.tryParseInt(aFromSequenceNumberSpecialValue, -1);
            if (fromSequenceNumber < 0) {
                throw new IllegalArgumentException(String.format("Value for '%s' must be either '*FIRST' or an instance of Integer.",
                    RetrieveKey.FROMENT.getDescription()));
            } else {
                String temp = padLeft(Integer.toString(fromSequenceNumber), 20);
                addSelectionCriterion(RetrieveKey.FROMENT, new AS400Text(20), temp.replace(' ', '0'));
                rmvSelectionCriterion(RetrieveKey.FROMTIME);
            }
        }
    }

    /**
     * Add retrieval criterion 02: Starting sequence number.
     * <p>
     * This can be used to indicate where to start when previous returned
     * continuation handle='1'.
     * <p>
     * <b>Note:</b> If this key is specified, Key 3 (starting time stamp) cannot
     * also be specified.
     * 
     * @param aFromSequenceNumber - The first journal entry considered for
     *        retrieval
     */
    public void setFromEnt(Long aFromSequenceNumber) {
        String temp = aFromSequenceNumber.toString();
        // integer will be passed as String, need to padLeft()
        temp = padLeft(temp, 20).replace(' ', '0');
        addSelectionCriterion(RetrieveKey.FROMENT, new AS400Text(20), temp);
        rmvSelectionCriterion(RetrieveKey.FROMTIME);
    }

    /**
     * Add retrieval criterion 03: Starting time stamp
     * <p>
     * <b>Note:</b> If this key is specified, Key 2 (starting sequence number)
     * cannot also be specified.
     * 
     * @param aStartingTimestamp - The time stamp of the first journal entry
     *        considered for retrieval
     */
    public void setFromTime(Date aStartingTimestamp) {
        // TimeZone temp = aStartingTimestamp.getTimeZone();
        // Timestamp temp2 = new
        // Timestamp(aStartingTimestamp.getTime().getTime());
        addSelectionCriterion(RetrieveKey.FROMTIME, new AS400Text(26), dateFormatter.format(aStartingTimestamp) + ".000000");
        rmvSelectionCriterion(RetrieveKey.FROMENT);
    }

    /**
     * Add retrieval criterion 03: Starting time stamp
     * <p>
     * <b>Note:</b> If this key is specified, Key 2 (starting sequence number)
     * cannot also be specified.
     * 
     * @param aStartingTimestamp - The time stamp of the first journal entry
     *        considered for retrieval
     * @throws ParseException
     */
    public void setFromTime(String aStartingTimestamp) throws ParseException {
        setFromTime(getTime(aStartingTimestamp));
    }

    /**
     * Add retrieval criterion 04: Ending sequence number.
     * <p>
     * <b>Note:</b> If this key is specified, Key 5 (ending time stamp) cannot
     * also be specified.
     * 
     * @param aToSequenceNumber - The last journal entry considered for
     *        retrieval.
     */
    public void setToEnt(Long aToSequenceNumber) {
        String temp = aToSequenceNumber.toString();
        // integer will be passed as String, need to padLeft()
        temp = padLeft(temp, 20).replace(' ', '0');
        addSelectionCriterion(RetrieveKey.TOENT, new AS400Text(20), temp);
        rmvSelectionCriterion(RetrieveKey.TOTIME);
    }

    /**
     * Add retrieval criterion 05: Ending time stamp
     * <p>
     * <b>Note:</b> If this key is specified, Key 4 (ending sequence number)
     * cannot also be specified.
     * 
     * @param anEndingTimestamp - The time stamp of the last journal entry
     *        considered for retrieval
     */
    public void setToTime(Date anEndingTimestamp) {
        // TimeZone temp = anEndingTimestamp.getTimeZone();
        // Timestamp temp2 = new
        // Timestamp(anEndingTimestamp.getTime().getTime());
        addSelectionCriterion(RetrieveKey.TOTIME, new AS400Text(26), dateFormatter.format(anEndingTimestamp) + ".000000");
        rmvSelectionCriterion(RetrieveKey.TOENT);
    }

    /**
     * Add retrieval criterion 05: Ending time stamp
     * <p>
     * <b>Note:</b> If this key is specified, Key 4 (ending sequence number)
     * cannot also be specified.
     * 
     * @param anEndingTimestamp - The time stamp of the last journal entry
     *        considered for retrieval
     * @throws ParseException
     */
    public void setToTime(String anEndingTimestamp) throws ParseException {
        setToTime(getTime(anEndingTimestamp));
    }

    /**
     * Add retrieval criterion 06: Number of entries to retrieve.
     * <p>
     * This indicates the 'max' number of entries to retrieve.
     * 
     * @param aNumberOfJournalEntries - The maximum number of journal entries
     *        that are requested to be retrieved
     */
    public void setNbrEnt(Integer aNumberOfJournalEntries) {
        addSelectionCriterion(RetrieveKey.NBRENT, new AS400Bin4(), aNumberOfJournalEntries);
    }

    /**
     * Add retrieval criterion 07: Journal codes.
     * 
     * @param aJournalCodeSpecialValue:<br>
     *        *ALL - The retrieval of journal entries is not limited to entries
     *        with a particular journal code.<br>
     *        *CTL - Only journal entries deposited to control the journal
     *        functions are to be retrieved (journal codes = J and F).
     */
    public void setJrnCde(String aJournalCodeSpecialValue) {
        RetrieveKey rtvKey = RetrieveKey.JRNCDE;

        if (!JRNCDE_ALL.equals(aJournalCodeSpecialValue) && !JRNCDE_CTL.equals(aJournalCodeSpecialValue)) {
            throw new IllegalArgumentException(String.format("Value for '%s' must be either '*ALL' or '*CTL' if String; "
                + "or an instance of JournalCode[] containing the desired journal codes to retrieve.", rtvKey.getDescription()));
        }

        String temp = padRight(aJournalCodeSpecialValue, 20);
        int count = 1;

        Object[] temp2 = new Object[2];
        temp2[0] = new Integer(count);
        temp2[1] = temp;

        AS400DataType type[] = new AS400DataType[2];
        type[0] = new AS400Bin4();
        type[1] = new AS400Text(temp.length());
        AS400Structure temp2Structure = new AS400Structure(type);

        addSelectionCriterion(rtvKey, temp2Structure, temp2);
    }

    /**
     * Add retrieval criterion 07: Journal codes.
     * <p>
     * Currently only '*ALLSLT' is implemented if JournalCode[] is passed in.
     * 
     * @param aJournalCodes - Array of the 1-character journal code for which
     *        journal entries are to be retrieved
     */
    public void setJrnCde(JournalCode... aJournalCodes) {
        RetrieveKey rtvKey = RetrieveKey.JRNCDE;

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < aJournalCodes.length; i++) {
            code.append(padRight(aJournalCodes[i].label(), 10));
            code.append(padRight(JRNCDE_ALLSLT, 10));
        }

        String temp = code.toString();
        int count = aJournalCodes.length;

        Object[] temp2 = new Object[2];
        temp2[0] = new Integer(count);
        temp2[1] = temp;

        AS400DataType type[] = new AS400DataType[2];
        type[0] = new AS400Bin4();
        type[1] = new AS400Text(temp.length());
        AS400Structure temp2Structure = new AS400Structure(type);

        addSelectionCriterion(rtvKey, temp2Structure, temp2);
    }

    /**
     * Add retrieval criterion 08: Journal entry types.
     * 
     * @param aJournalEntryTypesSpecialValue - One of:<br>
     *        *ALL - The retrieval of journal entries is not limited to entries
     *        with a particular journal entry type.<br>
     *        *RCD - Only journal entries that have an entry type for record
     *        level operations are retrieved. The following entry types are
     *        valid: BR, DL, DR, IL, PT, PX, UB, UP, and UR.
     */
    public void setEntTyp(String aJournalEntryTypesSpecialValue) {
        RetrieveKey rtvKey = RetrieveKey.ENTTYP;

        if (!ENTTYP_ALL.equals(aJournalEntryTypesSpecialValue) && !ENTTYP_RCD.equals(aJournalEntryTypesSpecialValue)) {
            throw new IllegalArgumentException(String.format("Value for '%s' must be either '*ALL' or '*RCD' if String; "
                + "or an instance of JournalEntryType[] containing the desired entry types to retrieve.", rtvKey.getDescription()));
        }

        String temp = padRight(aJournalEntryTypesSpecialValue, 10);
        int count = 1;

        Object[] temp2 = new Object[2];
        temp2[0] = new Integer(count);
        temp2[1] = temp;

        AS400DataType type[] = new AS400DataType[2];
        type[0] = new AS400Bin4();
        type[1] = new AS400Text(temp.length());
        AS400Structure temp2Structure = new AS400Structure(type);

        addSelectionCriterion(rtvKey, temp2Structure, temp2);
    }

    /**
     * Add retrieval criterion 08: Journal entry types.
     * 
     * @param aJournalEntryTypes
     */
    public void setEntTyp(JournalEntryType... aJournalEntryTypes) {
        RetrieveKey rtvKey = RetrieveKey.ENTTYP;

        StringBuilder entry = new StringBuilder();
        for (int i = 0; i < aJournalEntryTypes.length; i++) {
            entry.append(padRight(aJournalEntryTypes[i].label(), 10));
        }

        String temp = entry.toString();
        int count = aJournalEntryTypes.length;

        Object[] temp2 = new Object[2];
        temp2[0] = new Integer(count);
        temp2[1] = temp;

        AS400DataType type[] = new AS400DataType[2];
        type[0] = new AS400Bin4();
        type[1] = new AS400Text(temp.length());
        AS400Structure temp2Structure = new AS400Structure(type);

        addSelectionCriterion(rtvKey, temp2Structure, temp2);
    }

    /**
     * Add retrieval criterion 15: Null value indicators length.
     * 
     * @param nullIndLen - null indicators length
     */
    public void setNullIndLen(String nullIndLen) {
        RetrieveKey rtvKey = RetrieveKey.NULLINDLEN;
        if (!NULLINDLEN_VARLEN.equals(nullIndLen) && !isPositiveNumericValue(nullIndLen)) {
            throw new IllegalArgumentException(String.format("Value for '%s' must be either '" + NULLINDLEN_VARLEN + " or a positive integer value.",
                rtvKey.getDescription()));
        }

        if (NULLINDLEN_VARLEN.equals(nullIndLen)) {
            addSelectionCriterion(rtvKey, new AS400Text(10), padRight(nullIndLen, 10));
        } else {
            addSelectionCriterion(rtvKey, new AS400Text(10), padLeftZero(nullIndLen, 10));
        }
    }

    /**
     * Add retrieval criterion 15: Null value indicators length.
     * 
     * @param nullIndLen - null indicators length
     */
    public void setNullIndLen(int nullIndLen) {
        setNullIndLen(Integer.toString(nullIndLen));
    }

    /**
     * Set retrieval criterion 16: FileCriterion.
     * 
     * @param file - Specifies the name of the file that contains the member.
     * @param library - Specifies the library where the file is stored.
     * @param member - Specifies the name of the member, whose journal entries
     *        are retrieved.
     */
    public void setFile(String library, String file, String member) {
        FileCriterion fileCriterion = new FileCriterion(file, library, member);

        fileCriterions.clear();
        fileCriterions.add(fileCriterion);
    }

    /**
     * Add retrieval criterion 16: FileCriterion.
     * 
     * @param file - Specifies the name of the file that contains the member.
     * @param library - Specifies the library where the file is stored.
     * @param member - Specifies the name of the member, whose journal entries
     *        are retrieved.
     */
    public void addFile(String library, String file, String member) {
        FileCriterion fileCriterion = new FileCriterion(file, library, member);

        fileCriterions.add(fileCriterion);
    }

    public String[] getFiles() {

        List<String> files = new LinkedList<String>();

        for (FileCriterion fileCriterion : fileCriterions) {
            files.add(fileCriterion.getQualifiedName());
        }

        return files.toArray(new String[files.size()]);
    }

    /**
     * Add retrieval criterion 22: Format minimized data.
     * 
     * @param aFormatMinimizedData - Specifies whether entry specific data which
     *        has been minimized on field boundaries will be returned in a
     *        readable format.<br>
     *        *NO - The journal entries which have entry specific data that has
     *        been minimized on field boundaries will not be returned in a
     *        readable format. Therefore, the entry specific data may not be
     *        viewable.<br>
     *        *YES - The journal entries which have entry specific data that has
     *        been minimized on field boundaries will be returned in a readable
     *        format. Therefore, the entry specific data is viewable and may be
     *        used for auditing purposes. The fields that were changed are
     *        accurately reflected. The fields that were not changed and were
     *        not recorded return default data and are indicated by a value of
     *        '9' in the null value indicators field.
     */
    public void setFormatMinimzedData(String aFormatMinimizedData) {
        RetrieveKey rtvKey = RetrieveKey.FMTMINDTA;
        if (!FMTMINDTA_YES.equals(aFormatMinimizedData) && !FMTMINDTA_NO.equals(aFormatMinimizedData)) {
            throw new IllegalArgumentException(String.format("Value for '%s' must be either '*YES' or '*NO' if String; "
                + "or 'Boolean.TRUE' or 'Boolean.FALSE' if Boolean.", rtvKey.getDescription()));
        }

        String temp = padRight(aFormatMinimizedData, 10);
        addSelectionCriterion(rtvKey, new AS400Text(10), temp);
    }

    /**
     * Add retrieval criterion 22: Format minimized data.
     * 
     * @param aFormatMinimizedData - Specifies whether entry specific data which
     *        has been minimized on field boundaries will be returned in a
     *        readable format.<br>
     *        <code>false</code> - The journal entries which have entry specific
     *        data that has been minimized on field boundaries will not be
     *        returned in a readable format. Therefore, the entry specific data
     *        may not be viewable.<br>
     *        <code>true</code> - The journal entries which have entry specific
     *        data that has been minimized on field boundaries will be returned
     *        in a readable format. Therefore, the entry specific data is
     *        viewable and may be used for auditing purposes. The fields that
     *        were changed are accurately reflected. The fields that were not
     *        changed and were not recorded return default data and are
     *        indicated by a value of '9' in the null value indicators field.
     */
    public void setFormatMinimzedData(Boolean aFormatMinimizedData) {
        if (aFormatMinimizedData) {
            setFormatMinimzedData(FMTMINDTA_YES);
        } else {
            setFormatMinimzedData(FMTMINDTA_NO);
        }
    }

    /**
     * Returns the maximum number of entries to retrieve.
     * 
     * @return maximum number of entries to be retrieved or -1
     */
    public int getNbrEnt() {
        Object nbrEnt = getSelectionValue(RetrieveKey.NBRENT);
        if (nbrEnt == null) {
            return -1;
        }
        return ((Integer)nbrEnt).intValue();
    }

    /**
     * Adds a given selection criterion to the list of selection criteria.
     * 
     * @param aRtvKey - retrieve key
     * @param aValue - value
     */
    private void addSelectionCriterion(RetrieveKey aRtvKey, AS400DataType aDataType, Object aValue) {
        selectionCriteria.put(aRtvKey, new RetrieveCriterion(aRtvKey, aDataType, aValue));
        isDirty = true;
    }

    /**
     * Returns the value that is associated to a given selection criterion.
     * 
     * @return value
     */
    private Object getSelectionValue(RetrieveKey aRtvKey) {
        RetrieveCriterion tCriterion = selectionCriteria.get(aRtvKey);
        if (tCriterion == null) {
            return null;
        }
        return tCriterion.getValue();
    }

    /**
     * Removes a given selection criterion from the list of selection criteria.
     * 
     * @param aRtvKey - retrieve key
     */
    private void rmvSelectionCriterion(RetrieveKey aRtvKey) {
        selectionCriteria.remove(aRtvKey);
        isDirty = true;
    }

    /**
     * Prepares the internal used <code>structure</code> and <code>data</code>
     * arrays.
     */
    private void prepareStructureAndData() {
        if (!isDirty) {
            return;
        }

        addFileCriterions();

        structure = new ArrayList<AS400DataType>();
        structure.add(new AS400Bin4());
        // first element is the number of variable length records
        data = new ArrayList<Object>();
        data.add(new Integer(0));

        for (RetrieveCriterion item : selectionCriteria.values()) {
            addStructureData(item);
        }

        isDirty = false;
    }

    private void addFileCriterions() {

        if (fileCriterions.size() == 0) {
            return;
        }

        RetrieveKey rtvKey = RetrieveKey.FILE;

        int arraySize = fileCriterions.size() * 3 + 1;
        Object[] temp2 = new Object[arraySize];
        AS400DataType type[] = new AS400DataType[fileCriterions.size() * 3 + 1];

        int x = 0;
        temp2[x] = new Integer(fileCriterions.size());
        type[x] = new AS400Bin4();

        x++;
        for (int i = 0; i < fileCriterions.size(); i++) {

            FileCriterion fileCriterion = fileCriterions.get(i);

            temp2[x] = fileCriterion.getFile();
            temp2[x + 1] = fileCriterion.getLibrary();
            temp2[x + 2] = fileCriterion.getMember();

            type[x] = new AS400Text(10);
            type[x + 1] = new AS400Text(10);
            type[x + 2] = new AS400Text(10);

            x = x + 3;
        }

        AS400Structure temp2Structure = new AS400Structure(type);
        addSelectionCriterion(rtvKey, temp2Structure, temp2);
    }

    /**
     * Add additional selection entry to two ArrayList: structure and data.
     * 
     * @param aRetrieveCriterion
     * @param dataType
     * @param value
     */
    private void addStructureData(RetrieveCriterion aRetrieveCriterion) {

        AS400Bin4 parm1Type = new AS400Bin4();
        AS400Bin4 parm2Type = new AS400Bin4();
        AS400Bin4 parm3Type = new AS400Bin4();
        AS400DataType parm4Type = aRetrieveCriterion.getDataType();

        Integer parm1Value = new Integer(parm1Type.getByteLength() + parm2Type.getByteLength() + parm3Type.getByteLength()
            + parm4Type.getByteLength());
        Integer parm2Value = new Integer(aRetrieveCriterion.getKey().getKey());
        Integer parm3Value = new Integer(parm4Type.getByteLength());
        Object parm4Value = aRetrieveCriterion.getValue();

        structure.add(parm1Type);
        structure.add(parm2Type);
        structure.add(parm3Type);
        structure.add(parm4Type);

        data.add(parm1Value);
        data.add(parm2Value);
        data.add(parm3Value);
        data.add(parm4Value);

        // pump up "Number of Variable Length Records" by 1
        data.set(0, (Integer)data.get(0) + 1);
    }

    private boolean isPositiveNumericValue(String value) {
        if (IntHelper.tryParseInt(value, -1) >= 0) {
            return true;
        }
        return false;
    }

    private String padRight(String aFormatMinimizedData, int byteLength) {
        return StringHelper.getFixLength(aFormatMinimizedData, byteLength);
    }

    private String padLeft(String aFormatMinimizedData, int byteLength) {
        return StringHelper.getFixLengthLeading(aFormatMinimizedData, byteLength);
    }

    private String padLeftZero(String aFormatMinimizedData, int byteLength) {
        return StringHelper.getFixLengthLeading(aFormatMinimizedData, byteLength).replaceAll(" ", "0");
    }

    private Date getTime(String aTimestamp) throws ParseException {
        Date date = dateFormatter.parse(aTimestamp);
        return date;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JrneToRtv clone() throws CloneNotSupportedException {

        JrneToRtv clone = (JrneToRtv)super.clone();

        if (this.data != null) {
            clone.data = (ArrayList<Object>)this.data.clone();
        }

        if (this.structure != null) {
            clone.structure = (ArrayList<AS400DataType>)this.structure.clone();
        }

        clone.dateFormatter = (SimpleDateFormat)this.dateFormatter.clone();
        clone.fileCriterions = (LinkedList<FileCriterion>)this.fileCriterions.clone();
        clone.isDirty = this.isDirty;
        clone.journal = this.journal.clone();
        clone.selectionCriteria = (HashMap<RetrieveKey, RetrieveCriterion>)this.selectionCriteria.clone();

        return clone;
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        Collection<RetrieveCriterion> values = selectionCriteria.values();
        for (Iterator<RetrieveCriterion> iterator = values.iterator(); iterator.hasNext();) {
            RetrieveCriterion retrieveCriterion = iterator.next();

            if (buffer.length() != 0) {
                buffer.append(", ");
            }

            buffer.append(retrieveCriterion.toString());
        }

        return buffer.toString();
    }
}
