/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.api;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import biz.isphere.base.internal.ByteHelper;
import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.rse.as400.access.AS400UnsignedBin8;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Bin2;
import com.ibm.as400.access.AS400Bin4;
import com.ibm.as400.access.AS400ByteArray;
import com.ibm.as400.access.AS400DataType;
import com.ibm.as400.access.AS400Structure;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.AS400UnsignedBin2;
import com.ibm.as400.access.AS400UnsignedBin4;
import com.ibm.as400.access.DateTimeConverter;
import com.ibm.as400.access.FieldDescription;
import com.ibm.as400.access.ProgramParameter;

/**
 * Class representing the receiver variable. Mainly used to decode the following
 * of format RJNE0200:
 * 
 * <pre>
 * a. 1-time journal header
 * b. Repetitive occurrence of the entry sections
 *    b1. the entry header section
 *    b2. the entry null section
 *    b3. the entry detail section
 * </pre>
 * <p>
 * This class has been inspired by the RJNE0100 example written by Stanley Vong.
 * See <a href="http://stanleyvong.blogspot.de/">RJNE0100</a> example from
 * February 19, 2013.
 */
public class RJNE0200 {

    private static final String ADDRESS_FAMILY_IPV4 = "4"; //$NON-NLS-1$
    private static final String ADDRESS_FAMILY_IPV6 = "6"; //$NON-NLS-1$

    private static final int RECEIVER_LEN = 1024 * 32; // 32 KB
    private static final String FORMAT_NAME = "RJNE0200"; //$NON-NLS-1$
    private static final int ERROR_CODE = 0;

    private int bufferSize;
    private DateTimeConverter dateTimeConverter;
    private DynamicRecordFormatsStore store;
    private Calendar remoteCalendar;
    private int offsetMinutes;
    private ProgramParameter[] parameterList;

    // Cached data structures
    private AS400Structure headerStructure = null;
    private AS400Structure entryHeaderStructure = null;
    private AS400Structure nullValueIndicatorsStructure = null;
    private AS400Structure receiverInformationStructure = null;
    private AS400Structure logicalUnitOfWork = null;
    private AS400Structure transactionIdentifierStructure = null;
    private AS400Structure entrySpecificDataStructure = null;
    private List<AS400DataType> entrySpecificDataStructureHeader = null;

    // Cached data. Must be reset in resetReader().
    private int entryRRN;
    private int entryHeaderStartPos;
    private Object[] headerData;
    private Object[] entryHeaderData;
    private Object[] nullValueIndicatorsData;
    private Object[] receiverInformationData;
    private Object[] logicalUnitOfWorkData;
    private Object[] transactionIdentifierData;
    private String remoteAddress;
    private Boolean isRemoteIpv4Address;

    // Null value indicators length
    private int nullValueIndicatorsLength = -1; // *VARLEN

    // Current receiver information
    /*
     * Journal receiver information is returned only for the first entry in a
     * buffer and when the receiver information changes from one journal entry
     * to the next. If no journal receiver information is returned, it can be
     * assumed that the receiver information from the previous entry will apply
     * to the current journal entry.
     */
    private String currentReceiverName;
    private String currentReceiverLibraryName;
    private String currentReceiverLibraryASPDeviceName;
    private int currentReceiverLibraryASPNumber;

    public RJNE0200(AS400 aSystem) throws Exception {
        this(aSystem, RECEIVER_LEN);
    }

    public RJNE0200(AS400 aSystem, int aBufferSize) throws Exception {

        if ((aBufferSize % 16) != 0) {
            throw new IllegalArgumentException("Receiver Length not valid; value must be divisable by 16.");
        }

        bufferSize = aBufferSize;
        dateTimeConverter = new DateTimeConverter(aSystem);
        store = new DynamicRecordFormatsStore(aSystem);

        TimeZone timeZone = DateTimeConverter.timeZoneForSystem(aSystem);
        remoteCalendar = GregorianCalendar.getInstance(timeZone);
        Calendar localCalendar = GregorianCalendar.getInstance();
        int remoteOffset2GMT = (remoteCalendar.get(Calendar.ZONE_OFFSET) + remoteCalendar.get(Calendar.DST_OFFSET)) / (60 * 1000);
        int localOffset2GMT = (localCalendar.get(Calendar.ZONE_OFFSET) + localCalendar.get(Calendar.DST_OFFSET)) / (60 * 1000);
        offsetMinutes = localOffset2GMT - remoteOffset2GMT;

        resetReader();
    }

    /**
     * Return the 6-elements array of ProgramParameter to pass to the
     * ProgramCall/ServiceProgramCall.
     * 
     * @return parameter list of the QjoRetrieveJournalEntries API.
     */
    public ProgramParameter[] getProgramParameters(JrneToRtv aJrneToRtv) {

        nullValueIndicatorsLength = aJrneToRtv.getNullValueIndicatorsLength();

        parameterList = new ProgramParameter[6];

        setReceiverData(new byte[bufferSize]);
        setReceiverLength(bufferSize);
        setJournal(aJrneToRtv.getJournal(), aJrneToRtv.getLibrary());
        setFormatName(FORMAT_NAME);
        setJrneToRtv(aJrneToRtv);
        setErrorCode(ERROR_CODE);

        resetReader();

        int size = 0;
        for (ProgramParameter parameter : parameterList) {
            size += parameter.getOutputDataLength();
        }

        if (size % 16 != 0) {
            throw new IllegalArgumentException("*** Parameter list must be aligned to a 16-byte-boundary ***"); //$NON-NLS-1$
        }

        return parameterList;
    }

    /**
     * Return <i>true</i>, if there are more entries available, else
     * <i>false</i>.
     * 
     * @return <i>true</i>, if there are more entries available
     */
    public boolean hasNext() {

        if (entryRRN < getNbrOfEntriesRetrieved()) {
            return true;
        }

        return false;
    }

    /**
     * Point to the next journal entry (section b) in the receiver variable.
     * 
     * @return
     */
    public boolean nextEntry() {
        if (hasNext()) {
            entryRRN++;
            if (entryRRN == 1) {
                entryHeaderStartPos = getOffsetToFirstJrneHeader();
            } else {
                entryHeaderStartPos += getDspToNxtJrnEntHdr();
            }

            resetEntryData();

            Object[] receiverInformation = getReceiverInformationData();
            if (receiverInformation != null) {
                currentReceiverName = trimmed(receiverInformation[0]);
                currentReceiverLibraryName = trimmed(receiverInformation[1]);
                currentReceiverLibraryASPDeviceName = trimmed(receiverInformation[2]);
                currentReceiverLibraryASPNumber = (Short)receiverInformation[3];
            }

            return true;
        } else {
            return false;
        }
    }

    // ----------------------------------------------------
    // Header Information
    // ----------------------------------------------------

    /**
     * RJNE0200 Format, Header:<br>
     * Get number of bytes returned in the journal header.
     * 
     * @return
     */
    public int getBytesReturned() {
        Object[] tResult = getHeaderData();
        return (Integer)tResult[0];
    }

    /**
     * RJNE0200 Format, Header:<br>
     * Get offset to the first journal entry header in the journal header.
     * 
     * @return
     */
    private int getOffsetToFirstJrneHeader() {
        Object[] tResult = getHeaderData();
        return (Integer)tResult[1];
    }

    /**
     * RJNE0200 Format, Header:<br>
     * Get number of entries retrieved in the journal header.
     * 
     * @return
     */
    public int getNbrOfEntriesRetrieved() {
        Object[] tResult = getHeaderData();
        return (Integer)tResult[2];
    }

    /**
     * RJNE0200 Format, Header:<br>
     * Get continuation indicator in the journal header.
     * 
     * @return <code>true</code>, if there are more journal entries available in
     *         the specified receiver range that match the search criteria, but
     *         there is no room available in the return structure.
     *         <code>false</code>All the journal entries that match the search
     *         criteria are returned to this structure.
     */
    public boolean moreEntriesAvailable() {
        if (getOutputData() == null) {
            return false;
        }
        Object[] tResult = getHeaderData();
        return "1".equals(tResult[3]);
    }

    /**
     * RJNE0200 Format, Header:<br>
     * When more entries are available, then this value will identify the name
     * of the receiver that holds the next journal entry that could be retrieved
     * with the same selection criteria on a subsequent call to this API.
     * 
     * @return name of the receiver that holds the next journal entry
     */
    public String getContinuatingReceiver() {
        Object[] tResult = getHeaderData();
        return trimmed(tResult[4]);
    }

    /**
     * RJNE0200 Format, Header:<br>
     * When more entries are available, then this field will identify the name
     * of the library that contains the receiver that holds the next journal
     * entry that could be retrieved with the same selection criteria on a
     * subsequent call to this API.
     * 
     * @return name of the library that contains the receiver that holds the
     *         next journal entry
     */
    public String getContinuatingReceiverLibrary() {
        Object[] tResult = getHeaderData();
        return trimmed(tResult[5]);
    }

    /**
     * RJNE0200 Format, Header:<br>
     * When more entries are available, then this field will identify the
     * sequence number of the next journal entry that could be retrieved with
     * the same selection criteria on a subsequent call to this API.
     * 
     * @return
     */
    public Long getContinuatingSequenceNumber() {
        Object[] tResult = getHeaderData();
        return new Long((String)tResult[6]);
    }

    // ----------------------------------------------------
    // Journal entry's header
    // ----------------------------------------------------

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The displacement from the start of this journal entry's header section to
     * the start of the journal entry header section for the next journal entry.
     * 
     * @return displacement to next journal entry's header
     */
    public int getDspToNxtJrnEntHdr() {
        Object[] tResult = getEntryHeaderData();
        return ((Long)tResult[0]).intValue();
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The displacement from the start of this journal entry's header section to
     * the start of the null value indicators section for this journal entry.
     * 
     * @return displacement to this journal entry's null value indicators
     */
    public int getDspToThsJrnEntNullValInd() {
        Object[] tResult = getEntryHeaderData();
        return ((Long)tResult[1]).intValue();
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The displacement from the start of this journal entry's header section to
     * the start of the entry specific data section for this journal entry.
     * 
     * @return displacement to this journal entry's entry specific data
     */
    public int getDspToThsJrnEntData() {
        Object[] tResult = getEntryHeaderData();
        return ((Long)tResult[2]).intValue();
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The displacement from the start of this journal entry's header section to
     * the start of the transaction identifier section for this journal entry.
     * 
     * @return displacement to this journal entry's transaction identifier
     */
    public int getDspToThsJrnEntTransactionIdentifier() {
        Object[] tResult = getEntryHeaderData();
        return ((Long)tResult[3]).intValue();
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The displacement from the start of this journal entry's header section to
     * the start of the logical unit of work section for this journal entry.
     * 
     * @return displacement to this journal entry's logical unit of work
     */
    public int getDspToThsJrnEntLogicalUnitOfWork() {
        Object[] tResult = getEntryHeaderData();
        return ((Long)tResult[4]).intValue();
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The displacement from the start of this journal entry's header section to
     * the start of the receiver information section for this journal entry.
     * 
     * @return displacement to this journal entry's receiver information
     */
    public int getDspToThsJrnEntReceiver() {
        Object[] tResult = getEntryHeaderData();
        return ((Long)tResult[5]).intValue();
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Returns the sequence number of the journal entry.
     * <p>
     * Date type of field JOSYSSEQ in journal output file: CHAR(20)
     * 
     * @return sequence number
     */
    public BigInteger getSequenceNumber() {
        Object[] tResult = getEntryHeaderData();
        return (BigInteger)tResult[6];
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the system date and time when the journal entry was added to the
     * journal receiver.
     * <p>
     * *TYPE3+: JOTSTP, TIMESTAMP(26)<br>
     * *TYPE2-: JODATE, CHAR(6) and JOTIME, ZONED(6,0)
     * 
     * @return date and time the journal entry was added to the receiver
     */
    public Date getTimestamp() throws Exception {

        Object[] tResult = getEntryHeaderData();
        Date tTimestamp = dateTimeConverter.convert((byte[])tResult[7], "*DTS");

        tTimestamp = convertToLocalTimeZone(tTimestamp);

        return tTimestamp;
    }

    private Date convertToLocalTimeZone(Date timestamp) {

        remoteCalendar.setTime(timestamp);
        remoteCalendar.add(Calendar.MINUTE, offsetMinutes * -1);
        timestamp = remoteCalendar.getTime();

        return timestamp;
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the thread identifier of the process that added the journal entry.
     * <p>
     * JOTHD, HEX(8)<br>
     * JOTHDX, CHAR(16)
     * <p>
     * <b>Notes:</b><br>
     * <ol>
     * <li>If RCVSIZOPT(*MINFIXLEN) was in effect or FIXLENDTA(*THD) was not in
     * effect for the journal when the journal receiver that contains this
     * journal entry was attached, then hex 0 will be returned for the thread
     * identifier.</li>
     * </ol>
     * 
     * @return thread identifier
     */
    public String getThreadIdentifier() {
        Object[] tResult = getEntryHeaderData();
        byte[] threadId = new byte[8];
        Arrays.fill(threadId, (byte)0x0);
        BigInteger threadIdentifier = (BigInteger)tResult[8];
        // if (!threadIdentifier.equals(BigInteger.ZERO)) {
        byte[] threadBytes = threadIdentifier.toByteArray();
        int offset = threadId.length - threadBytes.length;
        System.arraycopy(threadBytes, 0, threadId, offset, threadBytes.length);
        return ByteHelper.getHexString(threadId);
        // } else {
        // return OMITTED_STRING;
        // }
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The system sequence number indicates the relative sequence of when this
     * journal entry was deposited into the journal. The system sequence number
     * could be used to sequentially order journal entries that are in separate
     * journal receivers.
     * <p>
     * JOSYSSEQ, CHAR(20)
     * <p>
     * <b>Notes:</b><br>
     * <ol>
     * <li>If RCVSIZOPT(*MINFIXLEN) was in effect or FIXLENDTA(*SYSSEQ) was not
     * in effect for the journal when the journal receiver that contains this
     * journal entry was attached, then Hex 0 will be returned for the system
     * sequence number.</li>
     * </ol>
     * 
     * @return system sequence number
     */
    public BigInteger getSystemSequenceNumber() {
        Object[] tResult = getEntryHeaderData();
        return (BigInteger)tResult[9];
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Contains either the relative record number (RRN) of the record that
     * caused the journal entry or a count that is pertinent to the specific
     * type of journal entry.
     * <p>
     * JOCTRR, CHAR(20)
     * 
     * @return count/relative record number
     */
    public BigInteger getRelativeRecordNumber() {
        Object[] tResult = getEntryHeaderData();
        return (BigInteger)tResult[10];
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the number that identifies the commit cycle.
     * <p>
     * JOCCID, CHAR(20)
     * 
     * @return commit cycle identifier
     */
    public BigInteger getCommitCycleId() {
        Object[] tResult = getEntryHeaderData();
        return (BigInteger)tResult[11];
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The logical unit of work identifies entries to be associated with a given
     * unit of work, usually within a commit cycle.
     * <p>
     * JOLUW, CHAR(39)
     * 
     * @return logical unit of work
     */
    public String getLogicalUnitOfWork() {
        Object[] tResult = getLogicalUnitOfWorkData();
        if (tResult != null) {
            return trimmed(tResult[0]);
        } else {
            // return OMITTED_STRING;
            return "";
        }
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The transaction identifier associated with this journal entry.
     * <p>
     * JOXID, CHAR(140)
     * <p>
     * <b>Notes:</b><br>
     * <ol>
     * <li>If RCVSIZOPT(*MINFIXLEN) was in effect or FIXLENDTA(*XID) was not in
     * effect for the journal when the journal receiver that contains the
     * journal entry was attached, then the displacement to transaction
     * identifier will be 0 and no transaction identifier will be returned.</li>
     * </ol>
     * 
     * @return logical unit of work
     */
    public String getTransactionIdentifier() {
        Object[] tResult = getTransactionIdentifierData();
        if (tResult != null) {
            // 0 = formatID
            // 1 = gtrid_length
            // 2 = bqual_length
            // 3 = data
            StringBuilder buffer = new StringBuilder();
            for (Object object : tResult) {
                buffer.append(ByteHelper.getHexString((byte[])object));
            }
            return buffer.toString();
        } else {
            // return OMITTED_STRING;
            return "";
        }
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * If the entry specific data returned for this journal entry returned any
     * pointers, this is the handle associated with those pointers.
     * <p>
     * ???, ???(?)
     * 
     * @return pointer handle
     */
    public int getPointerHandle() {
        Object[] tResult = getEntryHeaderData();
        return ((Long)tResult[12]).intValue();
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The port number of the remote address associate with this journal entry.
     * <p>
     * JORPORT, ZONED(5,0)
     * <p>
     * <b>Notes:</b><br>
     * <ol>
     * <li>If RCVSIZOPT(*MINFIXLEN) was in effect or FIXLENDTA(*RMTADR) was not
     * in effect for the journal when the journal receiver that contains this
     * journal entry was attached, then Hex 0 will be returned for remote port.</li>
     * </ol>
     * 
     * @return remote port
     */
    public Integer getRemotePort() {
        Object[] tResult = getEntryHeaderData();
        Integer remotePort = (Integer)tResult[13];
        return remotePort;
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The number of the disk arm that contains the journal entry.
     * <p>
     * JOARM, ZONED(5,0)
     * 
     * @return arm number
     */
    public Integer getArmNumber() {
        Object[] tResult = getEntryHeaderData();
        return (Integer)tResult[14];
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The number for the auxiliary storage pool that contains the program that
     * added the journal entry.
     * <p>
     * JOPGMASP, ZONED(5,0)
     * <p>
     * <b>Notes:</b>
     * <ol>
     * <li>If RCVSIZOPT(*MINFIXLEN) was in effect or FIXLENDTA(*PGMLIB) was not
     * in effect for the journal when the journal receiver that contains this
     * journal entry was attached, then Hex 0 will be returned for program ASP
     * number.</li>
     * </ol>
     * 
     * @return program library ASP number
     */
    public long getProgramLibraryASPNumber() {
        Object[] tResult = getEntryHeaderData();
        Integer programLibraryASP = (Integer)tResult[15];
        return programLibraryASP.longValue();
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The remote address associated with the journal entry. The format of the
     * address is dependent on the value of the address family for this journal
     * entry.
     * <p>
     * JORADR, CHAR(46)
     * <p>
     * <b>Notes:</b><br>
     * <ol>
     * <li>If RCVSIZOPT(*MINFIXLEN) was in effect or FIXLENDTA(*RMTADR) was not
     * in effect for the journal when the journal receiver that contains this
     * journal entry was attached, then Hex 0 will be returned for remote
     * address.</li>
     * </ol>
     * 
     * @return remote address
     */
    public String getRemoteAddress() {

        if (remoteAddress == null) {
            String addressFamily = getAddressFamilyIndicator();

            if (ADDRESS_FAMILY_IPV4.equals(addressFamily)) {
                remoteAddress = getIPv4Address(getRemoteAddressBytes());
            } else if (ADDRESS_FAMILY_IPV6.equals(addressFamily)) {
                remoteAddress = getIPv6Address(getRemoteAddressBytes());
            } else {
                // remoteAddress = OMITTED_STRING;
                remoteAddress = "";
            }
        }

        return remoteAddress;
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the journal code, which is the primary category of the journal entry.
     * <p>
     * JOCODE, CHAR(1)
     * 
     * @return journal code
     */
    public String getJournalCode() {
        Object[] tResult = getEntryHeaderData();
        return trimmed(tResult[17]);
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the entry type, which further identifies the type of user-created or
     * system-created entry.
     * <p>
     * JOENTT, CHAR(2)
     * 
     * @return entry type
     */
    public String getEntryType() {
        Object[] tResult = getEntryHeaderData();
        return trimmed(tResult[18]);
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The name of the job that added the entry.
     * <p>
     * JOJOB, CHAR(10)
     * <p>
     * <b>Notes:</b><br>
     * <ol>
     * <li>If RCVSIZOPT(*MINFIXLEN) was in effect or FIXLENDTA(*JOB) was not in
     * effect for the journal when the journal receiver that contains this
     * journal entry was attached, then <b>*OMITTED</b> is returned for the job
     * name.</li>
     * <li>If the journal entry was deposited by a system task that was not
     * associated with a job, then <b>*TDE</b> will be returned for the job
     * name.</li>
     * <li>If the job name was not available when the journal entry was
     * deposited, then <b>*NONE</b> is returned for the job name.</li>
     * </ol>
     * 
     * @return job name
     */
    public String getJobName() {
        Object[] tResult = getEntryHeaderData();
        return trimmed(tResult[19]);
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The user profile name of the user that started the job.
     * <p>
     * JOUSER, CHAR(10)
     * <p>
     * <b>Notes:</b><br>
     * <ol>
     * <li>If RCVSIZOPT(*MINFIXLEN) was in effect or FIXLENDTA(*JOB) was not in
     * effect for the journal when the journal receiver that contains the
     * journal entry was attached, then blanks are returned for the user name.</li>
     * <li>If the job name was not available when the journal entry was
     * deposited, then blanks are returned for the user name.</li>
     * </ol>
     * 
     * @return user name
     */
    public String getUserName() {
        Object[] tResult = getEntryHeaderData();
        return trimmed(tResult[20]);
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The job number of the job that added the entry.
     * <p>
     * JONBR, ZONED(6,0)
     * <p>
     * <b>Notes:</b><br>
     * <ol>
     * <li>If the RCVSIZOPT(*MINFIXLEN) was in effect or FIXLENDTA(*JOB) was not
     * was in effect for the journal when the journal receiver that contains the
     * journal entry was attached, then <b>zeros</b> are returned for the job
     * number.</li>
     * <li>If the journal entry was deposited by a system task that was not
     * associated with a job, then <b>zeros</b> will be returned for the job
     * number.</li>
     * <li>If the job name was not available when the journal entry was
     * deposited, then <b>zeros</b> are returned for the job number.</li>
     * </ol>
     * 
     * @return job number
     */
    public String getJobNumber() {
        Object[] tResult = getEntryHeaderData();
        return trimmed(tResult[21]);
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The name of the program that added the entry. If an application or CL
     * program did not add the entry, the field contains the name of a
     * system-supplied program such as QCMD or QPGMMENU.
     * <p>
     * JOPGM, CHAR(10)
     * <p>
     * <b>Notes:</b><br>
     * <ol>
     * <li>If RCVSIZOPT(*MINFIXLEN) was in effect or FIXLENDTA(*PGM) was not in
     * effect for the journal when the journal receiver that contains this
     * journal entry was attached, *OMITTED is returned as the program name.</li>
     * </ol>
     * 
     * @return program name
     */
    public String getProgramName() {
        Object[] tResult = getEntryHeaderData();
        return trimmed(tResult[22]);
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The name of the library that contains the program that added the journal
     * entry. journal entry.
     * <p>
     * JOPGMLIB, CHAR(10)
     * <p>
     * <b>Notes:</b><br>
     * <ol>
     * <li>If RCVSIZOPT(*MINFIXLEN) was in effect or FIXLENDTA(*PGMLIB) was not
     * in effect for the journal when the journal receiver that contains this
     * journal entry was attached, then *OMITTED will be returned for the
     * program library name.</li>
     * </ol>
     * 
     * @return program library name
     */
    public String getProgramLibraryName() {
        Object[] tResult = getEntryHeaderData();
        return trimmed(tResult[23]);
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the name of the ASP device that contains the program.
     * <p>
     * JOPGMASP, ZONED(5,0)
     * 
     * @return program library ASP device name
     */
    public String getProgramLibraryASPDeviceName() {
        Object[] tResult = getEntryHeaderData();
        return trimmed(tResult[24]);
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the name of the object for which the journal entry was added.
     * <p>
     * JOOBJ, CHAR(10)
     * 
     * @return object name
     */
    public String getObjectName() {
        String tObject = getQualifiedObjectName();
        if (isIFSObject()) {
            return trimmed(tObject.substring(0, 16));
        }
        return trimmed(tObject.substring(0, 10));
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the name of the library of the object for which the journal entry was
     * added.
     * <p>
     * JOLIB, CHAR(10)
     * 
     * @return library name
     */
    public String getObjectLibrary() {
        if (isIFSObject()) {
            return null;
        }
        String tObject = getQualifiedObjectName();
        return trimmed(tObject.substring(10, 20));
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the name of the file member for which the journal entry was added.
     * <p>
     * JOMBR, CHAR(10)
     * 
     * @return file member name
     */
    public String getFileMember() {
        if (!isFileObject()) {
            return null;
        }
        String tObject = getQualifiedObjectName();
        return trimmed(tObject.substring(20, 30));
    }

    /**
     * Returns <code>true</code>, if this journal entry was added for an
     * integrated file system object.
     * 
     * @return <code>true</code> for integrated file system objects, else
     *         <code>false</code>.
     */
    public Boolean isIFSObject() {
        String tType = getObjectType();
        if ("*DIR".equals(tType) || "*STMF".equals(tType) || "*SYMLNK".equals(tType)) {
            return true;
        }
        return false;
    }

    /**
     * Returns <code>true</code>, if this journal entry was added for a database
     * file object.
     * 
     * @return <code>true</code> for database file objects, else
     *         <code>false</code>.
     */
    public Boolean isFileObject() {
        String tType = getObjectType();
        if ("*FILE".equals(tType) || "*QDDS".equals(tType) || "*QDDSI".equals(tType)) {
            return true;
        }
        return false;
    }

    /**
     * Returns <code>true</code>, if this journal entry was added for other
     * objects, such as data areas and data queues.
     * 
     * @return <code>true</code> for other objects, else <code>false</code>.
     */
    public Boolean isOtherObject() {
        if (isFileObject() || isIFSObject()) {
            return false;
        }
        return true;
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The name of the effective user profile under which the job was running
     * when the entry was created. (JOUSPF)
     * <p>
     * JOUSPF, CHAR(10)
     * <p>
     * <b>Notes:</b><br>
     * <ol>
     * <li>If RCVSIZOPT(*MINFIXLEN) was in effect or FIXLENDTA(*JOB) was not in
     * effect for the journal when the journal receiver that contains this
     * journal entry was attached, *OMITTED is returned for the effective user
     * profile.</li>
     * <li>If the journal entry was deposited by a system task that was not
     * associated with a job, then a character representation of the task
     * description entry number will be returned for the user profile.</li>
     * </ol>
     * 
     * @return effective user profile
     */
    public String getUserProfile() {
        Object[] tResult = getEntryHeaderData();
        return trimmed(tResult[26]);
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the journal identifier (JID) for the object. When journaling is
     * started for an object, the system assigns a unique JID to that object.
     * The JID remains constant even if the object is renamed or moved.
     * <p>
     * JOJID, CHAR(10)
     * 
     * @return journal identifier
     */
    public String getJournalIdentifier() {
        Object[] tResult = getEntryHeaderData();
        // The first byte of the RCVJRNE API differs from the content of field
        // JOJID of a *TYPE4 output file. For example:
        // RCVJRNE API: x'59F00025247077BF0002'
        // DSPJRNE JOJID: x'A1F00025247077BF0002F'
        // PMR: 33254,031,724 (05.10.2018)
        return ByteHelper.getHexString((byte[])tResult[27]);
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the address family identifier. The address family identifies the
     * format of the remote address for this journal entry.
     * <p>
     * The possible address identifiers are:
     * <p>
     * 0 - This entry was not associated with any remote address.<br>
     * 4 - The format of the remote address is Internet protocol version 4. The
     * remote address is returned as a 16-byte character field.<br>
     * 6 - The format of the remote address is Internet protocol version 6. The
     * remote address is returned as a 128-bit binary number.
     * <p>
     * JOADF, CHAR(1)
     * 
     * @return address family
     */
    public String getAddressFamily() {

        String addressFamily = getAddressFamilyIndicator();

        if (ADDRESS_FAMILY_IPV6.equals(addressFamily)) {
            if (isEmbeddedIPv4Address()) {
                addressFamily = ADDRESS_FAMILY_IPV4;
            }
        }

        return addressFamily;
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the name of the system on which the entry is being retrieved, if the
     * journal receiver was attached prior to installing V4R2M0 on the system.
     * If the journal receiver was attached while the system was running V4R2M0
     * or a later release, the system name is the system where the journal entry
     * was actually deposited. (JOSYNM)
     * <p>
     * JOSYNM, CHAR(8)
     * 
     * @return system name
     */
    public String getSystemName() {
        Object[] tResult = getEntryHeaderData();
        return trimmed(tResult[29]);
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the indicator flag for the operation.
     * 
     * @return indicator flag
     */
    public String getIndicatorFlag() {
        Object[] tResult = getEntryHeaderData();
        return trimmed(tResult[30]);
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the indicator with respect to the information in the object field.
     * <p>
     * The possible object indicators are:
     * <p>
     * 0 - Either the journal entry has no object information or the object
     * information in the journal entry header does not necessarily reflect the
     * name of the object at the time the journal entry was deposited into the
     * journal.<br>
     * 1 - The object information in the journal entry header reflects the name
     * of the object at the time the journal entry was deposited into the
     * journal.<br>
     * 2 - The object information in the journal entry header does not
     * necessarily reflect the name of the object at the time the journal entry
     * was deposited into the journal.
     * <p>
     * JOOBJIND, CHAR(1)
     * 
     * @return object name indicator
     */
    public String getObjectNameIndicator() {
        Object[] tResult = getEntryHeaderData();
        return trimmed(tResult[31]);
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Whether this entry was recorded for actions that occurred on records that
     * are part of a referential constraint.
     * <p>
     * JORCST, CHAR(1)
     * 
     * @return <code>true</code> if this entry was added for a record that is
     *         part of a referential constraint, else <code>false</code>.
     */
    public Boolean isReferentialConstraint() {
        byte tByte = getJournalEntryFlags();
        boolean isRefCst = (tByte & 0x80) == 0x80;
        return isRefCst;
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Whether this entry was created as result of a trigger program.
     * <p>
     * JOTGR, CHAR(1)
     * 
     * @return <code>true</code> if this entry was added by a trigger program,
     *         else <code>false</code>.
     */
    public Boolean isTrigger() {
        byte tByte = getJournalEntryFlags();
        boolean isTrigger = (tByte & 0x40) == 0x40;
        return isTrigger;
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Whether this entry has data that must be additionally retrieved using a
     * pointer returned for the missing information.
     * <p>
     * JOINCDAT, CHAR(1)
     * 
     * @return <code>true</code> if this entry contains incomplete information,
     *         else <code>false</code>.
     */
    public Boolean isIncompleteData() {
        byte tByte = getJournalEntryFlags();
        boolean isIncompleteData = (tByte & 0x20) == 0x20;
        return isIncompleteData;
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Whether this entry is ignored during a Apply Journaled Changes
     * (APYJRNCHG) or Remove Journaled Changed (RMVJRNCHG) command.
     * <p>
     * JOIGNAPY, CHAR(1)
     * 
     * @return <code>true</code> if this entry is ignored, else
     *         <code>false</code>.
     */
    public Boolean isIgnoreApyRmvJrnChg() {
        byte tByte = getJournalEntryFlags();
        boolean isIgnored = (tByte & 0x10) == 0x10;
        return isIgnored;
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Whether this entry has minimized entry specific data as a result of the
     * journal having specified MINENTDTA for the object type of the entry.
     * <p>
     * JOMINESD, CHAR(1)
     * 
     * @return <code>true</code> if this entry has minimized data, else
     *         <code>false</code>.
     */
    public Boolean isMinimizedEntrySpecificData() {
        byte tByte = getJournalEntryFlags();
        boolean isMinimizedData = (tByte & 0x08) == 0x08;
        return isMinimizedData;
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Identifies whether or not this journal entry is associated with a logical
     * file.
     * 
     * @return <code>true</code> if this entry is associated with a logical
     *         file, else <code>false</code>.
     */
    public Boolean isLogicalFile() {
        byte tByte = getJournalEntryFlags();
        boolean isLogicalFile = (tByte & 0x04) == 0x04;
        return isLogicalFile;
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Whether this entry has minimized entry specific data on field boundaries
     * as a result of the journal having been specified with MINENTDTA(*FLDBDY).
     * 
     * @return <code>true</code> if this entry entry has minimized entry
     *         specific data on field boundaries, else <code>false</code>.
     */
    public Boolean isMinimizedOnFieldBoundaries() {
        byte tByte = getJournalEntryFlags();
        boolean isMinimizedOnBoundaries = (tByte & 0x02) == 0x02;
        return isMinimizedOnBoundaries;
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Identifies whether or not this journal entry is associated with a logical
     * file. The value will be 0 if the value for object type is not *FILE. The
     * possible values are:
     * <ul>
     * <li>0 - This entry is not associated with a logical file.</li>
     * <li>1 - This entry is associated with a logical file.</li>
     * </ul>
     * <p>
     * JOFILTYP, CHAR(1)
     * 
     * @return file type indicator
     */
    public String getFileTypeIndicator() {
        if (isLogicalFile()) {
            return "1";
        } else {
            // return "0";
            return "";
        }
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the type of object in the entry.
     * <p>
     * JOOBJTYP, CHAR(7)
     * <p>
     * The possible object types are:
     * 
     * <pre>
     * *DIR    - This entry is for an integrated file system directory.
     * *DTAARA - This entry is for a data area.
     * *DTAQ   - This entry is for a data queue.
     * *FILE   - This entry is for a database file.
     * *JRNRCV - This entry is for a journal receiver.
     * *LIB    - This entry is for a library.
     * *QDDS   - This entry is for the data portion of a database member.
     * *QDDSI  - This entry is for an access path of a database member.
     * *STMF   - This entry is for an integrated file system stream file.
     * *SYMLNK - This entry is for an integrated file system symbolic link.
     * </pre>
     * 
     * @return object type
     */
    public String getObjectType() {
        Object[] result = getEntryHeaderData();
        return (trimmed(result[33]));
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Indicates the nesting level of the commit cycle that was open when a
     * journal entry representing an object level change was deposited. The
     * primary commit cycle is considered the first level of nesting and
     * subsequent save point entries that were deposited prior to this entry
     * correspond to additional levels of nesting.<br>
     * This field will be zero if any of the following are true:<br>
     * <ul>
     * <li>This journal entry does not represent an object level change (object
     * level changes are the result of using commands like: CRTPF, CHGPF,
     * MOVOBJ, and RNMOBJ).</li>
     * <li>This journal entry was not deposited under commitment control.</li>
     * <li>This journal entry was deposited on a release prior to V5R4M0.</li>
     * </ul>
     * <p>
     * JOCMTLVL, CHAR(7)
     * 
     * @return nested commit level.
     */
    public long getNestedCommitLevel() {
        Object[] result = getEntryHeaderData();
        return ((Long)result[35]).longValue();
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * Get the null-value indicators of this journal entry.
     * <p>
     * JONVI, CHAR(50)
     * 
     * @return
     */
    public byte[] getNullValueIndicators() {

        Object[] tResult = getNullValueIndicatorsData();
        if (nullValueIndicatorsLength < 0) {
            int count = (Integer)tResult[0];
            if (count > 0) {
                byte[] nullValueIndicators = ByteHelper.copyOfRange((byte[])tResult[1], 0, count);
                return nullValueIndicators;
            }
        } else {
            byte[] nullValueIndicators = ByteHelper.copyOfRange((byte[])tResult[0], 0, nullValueIndicatorsLength);
            return nullValueIndicators;
        }

        return new byte[0];
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The name of the receiver holding the journal entry.
     * <p>
     * JORCV, CHAR(10)
     * 
     * @return receiver name
     */
    public String getReceiverName() {
        return currentReceiverName;
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The name of the library containing the receiver holding the journal
     * entry.
     * <p>
     * JORCVLIB, CHAR(10)
     * 
     * @return receiver library name
     */
    public String getReceiverLibraryName() {
        return currentReceiverLibraryName;
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The name of the ASP device that contains the receiver.
     * <p>
     * JORCVDEV, CHAR(10)
     * 
     * @return receiver library ASP device name
     */
    public String getReceiverLibraryASPDeviceName() {
        return currentReceiverLibraryASPDeviceName;
    }

    /**
     * RJNE0200 Format, Journal entry's header:<br>
     * The number for the auxiliary storage pool containing the receiver holding
     * the journal entry.
     * <p>
     * JORCVASP, ZONED(5,0)
     * 
     * @return receiver library ASP number
     */
    public int getReceiverLibraryASPNumber() {
        return currentReceiverLibraryASPNumber;
    }

    /**
     * Returns the entry specific data splitted into an array of objects using
     * the current record format of the journaled file.
     * <p>
     * The header fields 'Length of entry specific data' and 'Reserved' are
     * skipped. See section of section 'This journal entry's entry specific
     * data' of the QjoRetrieveJournalEntries API.
     * 
     * @return array of objects
     */
    public Object[] getEntrySpecificData() {

        DynamicRecordFormat recFormat = store.get(getObjectName(), getObjectLibrary());
        Object[] tmpEntrySpecificData = (Object[])getEntrySpecificDataStructure(recFormat).toObject(getOutputData(),
            entryHeaderStartPos + getDspToThsJrnEntData());

        Object[] entrySpecificData; // without header fields
        if (tmpEntrySpecificData != null && tmpEntrySpecificData.length > 2) {
            ArrayList<Object> list = new ArrayList<Object>();
            for (int i = 2; i < tmpEntrySpecificData.length; i++) {
                list.add(tmpEntrySpecificData[i]);
            }
            entrySpecificData = list.toArray();
        } else {
            entrySpecificData = new Object[0];
        }

        return entrySpecificData;
    }

    /**
     * Returns the the raw entry specific data.
     * <p>
     * The header fields 'Length of entry specific data' and 'Reserved' are
     * skipped. See section of section 'This journal entry's entry specific
     * data' of the QjoRetrieveJournalEntries API.
     * 
     * @return raw entry specific data as byte array
     */
    public byte[] getEntrySpecificDataRaw() {

        int tLength = getEntrySpecificDataLength();
        Object[] result = (Object[])getEntrySpecificDataStructureRaw(tLength)
            .toObject(getOutputData(), entryHeaderStartPos + getDspToThsJrnEntData());

        byte[] result2 = ((byte[])result[2]);

        return result2;
    }

    /**
     * Get length of entry specific data length in section b3
     * 
     * @return
     */
    public Integer getEntrySpecificDataLength() {
        Object[] result = (Object[])getEntrySpecificDataStructureHeader().toObject(getOutputData(), entryHeaderStartPos + getDspToThsJrnEntData());
        String temp = (String)result[0];
        return IntHelper.tryParseInt(temp, -1);
    }

    // ----------------------------------------------------
    // Private methods
    // ----------------------------------------------------

    private String getAddressFamilyIndicator() {

        Object[] tResult = getEntryHeaderData();
        String addressFamily = trimmed(tResult[28]);

        return addressFamily;
    }

    private byte[] getRemoteAddressBytes() {

        Object[] tResult = getEntryHeaderData();
        byte[] remoteAddressBytes = (byte[])tResult[16];

        return remoteAddressBytes;
    }

    /**
     * Checks, whether the IPv6 address is actually an embedded IPv4 address.
     * <p>
     * The format of embedded IPv4 addresses is:
     * 
     * <pre>
     * ::ffff:127.0.0.1
     * or
     * 0000:0000:0000:0000:0000:ffff:127.0.0.1
     * or
     * 0:0:0:0:0:ffff:7F00:1
     * </pre>
     * 
     * @return true, for embedded IPv4 addresses, else false.
     */
    private boolean isEmbeddedIPv4Address() {

        if (isRemoteIpv4Address == null) {
            byte[] remoteAddressBytes = getRemoteAddressBytes();

            // Check for leading bytes:
            // x'00000000000000000000FFFF'
            isRemoteIpv4Address = true;
            for (int i = 0; i < 12; i++) {
                byte cmpByte;
                if (i < 10) {
                    cmpByte = 0;
                } else {
                    cmpByte = -1;
                }
                if (remoteAddressBytes[i] != cmpByte) {
                    isRemoteIpv4Address = false;
                    break;
                }
            }
        }

        return isRemoteIpv4Address;
    }

    private String getIPv4Address(byte[] remoteAddressBytes) {

        String ipAddress = null;

        try {

            // Actually an IPv4 address should be stored as a string of
            // characters, such as: 127.0.0.1
            // Due to a bug in the RCVJRNE command, IPv4 addresses are returned
            // as a 4-byte binary value.
            // Hack: check for trailing x'00' bytes.
            // PMR: 33254,031,724 (05.10.2018)
            boolean isBinaryHack = true;
            for (int i = 4; i < remoteAddressBytes.length; i++) {
                if (remoteAddressBytes[i] != 0) {
                    isBinaryHack = false;
                    break;
                }
            }

            if (!isBinaryHack) {
                String ccsid = Preferences.getInstance().getJournalEntryCcsid();
                ipAddress = new String(remoteAddressBytes, ccsid);
            } else {

                StringBuilder buffer = new StringBuilder();

                for (int i = 0; i < 4; i++) {
                    if (buffer.length() > 0) {
                        buffer.append(".");
                    }
                    if (remoteAddressBytes[i] < 0) {
                        buffer.append(remoteAddressBytes[i] + 256);
                    } else {
                        buffer.append(remoteAddressBytes[i]);
                    }
                }

                ipAddress = buffer.toString();
            }

        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not get IPv4 address ***", e);
            return e.getLocalizedMessage();
        }

        return ipAddress;
    }

    private String getIPv6Address(byte[] remoteAddressBytes) {

        String ipAddress = null;

        if (isEmbeddedIPv4Address()) {
            ipAddress = getIPv4Address(ByteHelper.copyOfRange(remoteAddressBytes, 12, remoteAddressBytes.length - 12));
        } else {

            StringBuilder buffer = new StringBuilder();

            int i = 0;
            while (i < remoteAddressBytes.length) {
                byte[] bytes = ByteHelper.copyOfRange(remoteAddressBytes, i, 2);
                if (buffer.length() > 0) {
                    buffer.append(":");
                }
                buffer.append(ByteHelper.getHexString(bytes));
                i = i + 2;
            }

            ipAddress = buffer.toString();

        }

        return ipAddress;
    }

    private void setReceiverData(byte[] aBuffer) {
        parameterList[0] = new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, aBuffer, aBuffer.length);
    }

    private void setReceiverLength(int rcvLen) {
        parameterList[1] = new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, new AS400Bin4().toBytes(rcvLen));
    }

    private void setJournal(String journal, String library) {
        String jrnLib = StringHelper.getFixLength(journal, 10) + StringHelper.getFixLength(library, 10);
        parameterList[2] = new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, new AS400Text(20).toBytes(jrnLib));
    }

    private void setFormatName(String format) {
        parameterList[3] = new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, new AS400Text(8).toBytes(format));
    }

    private void setJrneToRtv(JrneToRtv aJrneToRtv) {
        parameterList[4] = new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, new AS400Structure(aJrneToRtv.getStructure()).toBytes(aJrneToRtv
            .getData()));
    }

    private void setErrorCode(int error) {
        parameterList[5] = new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, new AS400Bin4().toBytes(error));
    }

    private Object[] getHeaderData() {
        if (headerData == null) {
            headerData = (Object[])getHeaderStructure().toObject(getOutputData(), 0);
        }
        return headerData;
    }

    private Object[] getEntryHeaderData() {
        if (entryHeaderData == null) {
            entryHeaderData = (Object[])getEntryHeaderStructure().toObject(getOutputData(), entryHeaderStartPos);
        }
        return entryHeaderData;
    }

    private Object[] getNullValueIndicatorsData() {
        if (nullValueIndicatorsData == null && getDspToThsJrnEntNullValInd() > 0) {
            int nullValueIndicatorsStartPos = entryHeaderStartPos + getDspToThsJrnEntNullValInd();
            nullValueIndicatorsData = (Object[])getNullValueIndicatorsStructure().toObject(getOutputData(), nullValueIndicatorsStartPos);
        }
        return nullValueIndicatorsData;
    }

    private Object[] getReceiverInformationData() {
        if (receiverInformationData == null && getDspToThsJrnEntReceiver() > 0) {
            int receiverInformationStartPos = entryHeaderStartPos + getDspToThsJrnEntReceiver();
            receiverInformationData = (Object[])getReceiverInformationStructure().toObject(getOutputData(), receiverInformationStartPos);
        }
        return receiverInformationData;
    }

    private Object[] getLogicalUnitOfWorkData() {
        if (logicalUnitOfWorkData == null && getDspToThsJrnEntLogicalUnitOfWork() > 0) {
            int logicalUnitOfWorkStartPos = entryHeaderStartPos + getDspToThsJrnEntLogicalUnitOfWork();
            logicalUnitOfWorkData = (Object[])getLogicalUnitOfWorkStructure().toObject(getOutputData(), logicalUnitOfWorkStartPos);
        }
        return logicalUnitOfWorkData;
    }

    private Object[] getTransactionIdentifierData() {
        if (transactionIdentifierData == null && getDspToThsJrnEntTransactionIdentifier() > 0) {
            int transactionIdentifierStartPos = entryHeaderStartPos + getDspToThsJrnEntTransactionIdentifier();
            transactionIdentifierData = (Object[])getTransactionIdentifierStructure().toObject(getOutputData(), transactionIdentifierStartPos);
        }
        return transactionIdentifierData;
    }

    private byte[] getOutputData() {
        return parameterList[0].getOutputData();
    }

    private byte getJournalEntryFlags() {
        Object[] result = getEntryHeaderData();
        byte[] tBytes = (byte[])result[32];
        return tBytes[0];
    }

    private String getQualifiedObjectName() {
        Object[] result = getEntryHeaderData();
        String tObject = (String)result[25];
        return tObject;
    }

    private String trimmed(Object aValue) {
        return ((String)aValue).trim();
    }

    private void resetReader() {
        entryRRN = 0;
        entryHeaderStartPos = -1;
        headerData = null;
        resetEntryData();
    }

    private void resetEntryData() {

        entryHeaderData = null;
        nullValueIndicatorsData = null;
        receiverInformationData = null;
        logicalUnitOfWorkData = null;
        transactionIdentifierData = null;
        remoteAddress = null;
        isRemoteIpv4Address = null;
    }

    private AS400Structure getHeaderStructure() {
        if (this.headerStructure == null) {
            // @formatter:off formatter intentionally disabled
            AS400DataType[] structure = { 
                new AS400Bin4(), // 0 Bytes returned
                new AS400Bin4(), // 1 Offset to first journal entry header
                new AS400Bin4(), // 2 Number of entries retrieved
                new AS400Text(1), // 3 Continuation indicator
                new AS400Text(10), // 4 Continuation starting receiver
                new AS400Text(10), // 5 Continuation starting receiver library
                new AS400Text(20), // 6 Continuation starting sequence number
                new AS400Text(11) // 7 Reserved
            };
            // @formatter:on
            this.headerStructure = new AS400Structure(structure);
        }
        return this.headerStructure;
    }

    private AS400Structure getEntryHeaderStructure() {
        if (entryHeaderStructure == null) {
            // @formatter:off formatter intentionally disabled
            AS400DataType[] tStructure = { 
                new AS400UnsignedBin4(), // 0 Displacement to next journal entry's header
                new AS400UnsignedBin4(), // 1 Displacement to this journal entry's null value indicators
                new AS400UnsignedBin4(), // 2 Displacement to this journal entry's entry specific data
                new AS400UnsignedBin4(), // 3 Displacement to this journal entry's transaction identifier
                new AS400UnsignedBin4(), // 4 Displacement to this journal entry's logical unit of work
                new AS400UnsignedBin4(), // 5 Displacement to this journal entry's receiver information
                new AS400UnsignedBin8(), // 6 Sequence number
                new AS400ByteArray(8), // 7 Unformatted Time stamp
                new AS400UnsignedBin8(), // 8 Thread identifier
                new AS400UnsignedBin8(), // 9 System sequence number
                new AS400UnsignedBin8(), // 10 Count/relative record number
                new AS400UnsignedBin8(), // 11 Commit cycle identifier
                new AS400UnsignedBin4(), // 12 Pointer handle
                new AS400UnsignedBin2(), // 13 Remote port
                new AS400UnsignedBin2(), // 14 Arm number
                new AS400UnsignedBin2(), // 15 Program library ASP number
                new AS400ByteArray(16), // 16 Remote Address
                new AS400Text(1), // 17 Journal code
                new AS400Text(2), // 18 Entry type
                new AS400Text(10), // 19 Job name
                new AS400Text(10), // 20 User name
                new AS400Text(6), // 21 Job number
                new AS400Text(10), // 22 Program name
                new AS400Text(10), // 23 Program library name
                new AS400Text(10), // 24 Program library ASP device name
                new AS400Text(30), // 25 Object ()
                new AS400Text(10), // 26 User profile
                new AS400ByteArray(10), // 27 Journal identifier
                new AS400Text(1), // 28 Address family
                new AS400Text(8), // 29 System name
                new AS400Text(1), // 30 Indicator flag
                new AS400Text(1), // 31 Object name indicator
                new AS400ByteArray(1), // 32 Flags
                new AS400Text(10), // 33 Object type
                new AS400Text(3), // 34 Reserved
                new AS400UnsignedBin4() // 35 Nested commit level
            };
            // @formatter:on
            entryHeaderStructure = new AS400Structure(tStructure);
        }
        return entryHeaderStructure;
    }

    private AS400Structure getNullValueIndicatorsStructure() {
        if (this.nullValueIndicatorsStructure == null) {
            AS400DataType[] structure;
            if (nullValueIndicatorsLength < 0) {
                // @formatter:off formatter intentionally disabled
                structure = new AS400DataType[] { 
                    new AS400Bin4(), // 0 Length
                    new AS400ByteArray(8000) // 1 Null value indicators
                };
                // @formatter:on
            } else {
                // @formatter:off formatter intentionally disabled
                structure = new AS400DataType[] { 
                    new AS400ByteArray(nullValueIndicatorsLength) // 0 Null value indicators
                };
                // @formatter:on
            }
            this.nullValueIndicatorsStructure = new AS400Structure(structure);
        }
        return this.nullValueIndicatorsStructure;
    }

    private AS400Structure getReceiverInformationStructure() {
        if (this.receiverInformationStructure == null) {
            // @formatter:off formatter intentionally disabled
            AS400DataType[] structure = { 
                new AS400Text(10), // 0 Receiver name
                new AS400Text(10), // 1 Receiver library name
                new AS400Text(10), // 2 Receiver library ASP device name
                new AS400Bin2()    // 3 Receiver library ASP number
            };
            // @formatter:on
            this.receiverInformationStructure = new AS400Structure(structure);
        }
        return this.receiverInformationStructure;
    }

    private AS400Structure getLogicalUnitOfWorkStructure() {
        if (this.logicalUnitOfWork == null) {
            // @formatter:off formatter intentionally disabled
            AS400DataType[] structure = { 
                new AS400Text(39)  // 0 Logical unit of work
            };
            // @formatter:on
            this.logicalUnitOfWork = new AS400Structure(structure);
        }
        return this.logicalUnitOfWork;
    }

    // Transaction identifier (QSYSINC/H.XA: xid_t)
    private AS400Structure getTransactionIdentifierStructure() {
        if (this.transactionIdentifierStructure == null) {
            // @formatter:off formatter intentionally disabled
            AS400DataType[] structure = { 
                new AS400Bin4(),        // 0 formatID
                new AS400Bin4(),        // 1 gtrid_length
                new AS400Bin4(),        // 2 bqual_length
                new AS400ByteArray(128) // 3 data
            };
            // @formatter:on
            this.transactionIdentifierStructure = new AS400Structure(structure);
        }
        return this.transactionIdentifierStructure;
    }

    /**
     * Returns this journal entry's entry specific data structure header as
     * returned by the QjoRetrieveJournalEntries API.
     * 
     * @return entry specific data structure header
     */
    private AS400Structure getEntrySpecificDataStructureHeader() {
        if (entrySpecificDataStructure == null) {
            List<AS400DataType> tStructure = getEntrySpecificDataStructureHeaderEntries();
            entrySpecificDataStructure = new AS400Structure(tStructure.toArray(new AS400DataType[0]));
        }
        return entrySpecificDataStructure;
    }

    /**
     * Returns this journal entry's entry specific data structure.
     * <p>
     * Format:
     * <ul>
     * <li>Length of entry specific data</li>
     * <li>Reserved</li>
     * <li>Entry specific data splitted into individual fields</li>
     * </ul>
     * 
     * @return entry specific data structure
     */
    private AS400Structure getEntrySpecificDataStructure(DynamicRecordFormat aRecordFormat) {
        List<AS400DataType> tStructure = new ArrayList<AS400DataType>(getEntrySpecificDataStructureHeaderEntries());
        FieldDescription[] fds = aRecordFormat.getFieldDescriptions();
        for (int i = 0; i < fds.length; i++) {
            tStructure.add(fds[i].getDataType());
        }

        return new AS400Structure(tStructure.toArray(new AS400DataType[0]));
    }

    /**
     * Returns this journal entry's entry specific data structure.
     * <p>
     * Format:
     * <ul>
     * <li>Length of entry specific data</li>
     * <li>Reserved</li>
     * <li>Entry specific byte data</li>
     * </ul>
     * 
     * @return entry specific data structure
     */
    private AS400Structure getEntrySpecificDataStructureRaw(int aLength) {
        List<AS400DataType> tStructure = new ArrayList<AS400DataType>(getEntrySpecificDataStructureHeaderEntries());
        tStructure.add(new AS400ByteArray(aLength));

        return new AS400Structure(tStructure.toArray(new AS400DataType[0]));
    }

    /**
     * Returns this journal entry's entry specific data structure header.
     * <p>
     * The following fields of the 'This journal entry's entry specific data' of
     * format 'RJNE0200' are returned:
     * <table>
     * <tr>
     * <td>
     * <li/>CHAR(5)</td>
     * <td>-</td>
     * <td>Length of entry specific data</td>
     * </tr>
     * <tr>
     * <td>
     * <li/>CHAR(1)</td>
     * <td>-</td>
     * <td>Reserved</td>
     * </tr>
     * </table>
     * 
     * @return entry specific data structure header
     */
    private List<AS400DataType> getEntrySpecificDataStructureHeaderEntries() {
        if (entrySpecificDataStructureHeader == null) {
            // @formatter:off formatter intentionally disabled
            entrySpecificDataStructureHeader = new ArrayList<AS400DataType>();
            entrySpecificDataStructureHeader.add(new AS400Text(5)); // Length of entry specific data
            entrySpecificDataStructureHeader.add(new AS400Text(11)); // Reserved
            // @formatter:on
        }
        return entrySpecificDataStructureHeader;
    }
}
