package biz.isphere.core.internal.api.retrievemessagedescription;

import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

public class RTVM0300 extends APIFormat {

    private static final String BYTES_RETURNED = "bytesReturned"; //$NON-NLS-1$
    private static final String BYTES_AVAILABLE = "bytesAvailable"; //$NON-NLS-1$
    private static final String MESSAGE_SEVERITY = "messageSeverity"; //$NON-NLS-1$
    private static final String MESSAGE_ID = "messageID"; //$NON-NLS-1$
    private static final String NUMBER_OF_SUBSTITUTION_VARIABLE_FORMATS = "numberOfSubstitutionVariableFormats"; //$NON-NLS-1$
    private static final String CCSID_OF_TEXT_RETURNED = "ccsIdOfTextReturned"; //$NON-NLS-1$
    private static final String OFFSET_MESSAGE = "offsetMessage"; //$NON-NLS-1$
    private static final String LENGTH_MESSAGE_RETURNED = "lengthMessageReturned"; //$NON-NLS-1$
    private static final String OFFSET_MESSAGE_HELP = "offsetMessageHelp"; //$NON-NLS-1$
    private static final String LENGTH_MESSAGE_HELP_RETURNED = "lengthMessageHelpReturned"; //$NON-NLS-1$
    private static final String OFFSET_OF_SUBSTITUTION_VARIABLE_FORMATS = "offsetOfSubstitutionVariableFormats"; //$NON-NLS-1$
    private static final String LENGTH_OF_SUBSTITUTION_VARIABLE_FORMATS_RETURNED = "lengthOfSubstitutionVariableFormatsReturned"; //$NON-NLS-1$

    public RTVM0300(AS400 system, byte[] bytes) throws UnsupportedEncodingException {
        super(system, "RTVM0300");

        createStructure();

        setBytes(bytes);
    }

    public void setOffset(int offset) {
        super.setOffset(offset);
    }

    public int getBytesReturned() {
        return getInt4Value(BYTES_RETURNED);
    }

    public int getBytesAvailable() {
        return getInt4Value(BYTES_AVAILABLE);
    }

    public int getMessageSeverity() {
        return getInt4Value(MESSAGE_SEVERITY);
    }

    public String getMessageId() throws UnsupportedEncodingException {
        return getCharValue(MESSAGE_ID);
    }

    public int getNumberOfSubstitutionVariables() {
        return getInt4Value(NUMBER_OF_SUBSTITUTION_VARIABLE_FORMATS);
    }

    public String getMessage() throws UnsupportedEncodingException {

        int offset = getInt4Value(OFFSET_MESSAGE);
        int length = getInt4Value(LENGTH_MESSAGE_RETURNED);
        if (length > 0) {
            return convertToText(getBytesAt(offset, length));
        }

        return "";
    }

    public String getMessageHelp() throws UnsupportedEncodingException {

        int offset = getInt4Value(OFFSET_MESSAGE_HELP);
        int length = getInt4Value(LENGTH_MESSAGE_HELP_RETURNED);
        if (length > 0) {
            return convertToText(getBytesAt(offset, length));
        }

        return "";
    }

    public int getCcsid() {
        return getInt4Value(CCSID_OF_TEXT_RETURNED);
    }

    public int getOffsetSubstitutionVariables() {
        return getInt4Value(OFFSET_OF_SUBSTITUTION_VARIABLE_FORMATS);
    }
    
    private void createStructure() {

        addInt4Field(BYTES_RETURNED, 0);
        addInt4Field(BYTES_AVAILABLE, 4);
        addInt4Field(MESSAGE_SEVERITY, 8);
        addCharField(MESSAGE_ID, 26, 7);
        addInt4Field(NUMBER_OF_SUBSTITUTION_VARIABLE_FORMATS, 36);
        addInt4Field(CCSID_OF_TEXT_RETURNED, 48);
        addInt4Field(OFFSET_MESSAGE, 64);
        addInt4Field(LENGTH_MESSAGE_RETURNED, 68);
        addInt4Field(OFFSET_MESSAGE_HELP, 76);
        addInt4Field(LENGTH_MESSAGE_HELP_RETURNED, 80);
        addInt4Field(OFFSET_OF_SUBSTITUTION_VARIABLE_FORMATS, 88);
        addInt4Field(LENGTH_OF_SUBSTITUTION_VARIABLE_FORMATS_RETURNED, 92);

    }

}
