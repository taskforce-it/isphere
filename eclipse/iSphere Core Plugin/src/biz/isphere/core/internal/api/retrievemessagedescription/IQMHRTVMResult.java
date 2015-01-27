package biz.isphere.core.internal.api.retrievemessagedescription;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import biz.isphere.core.internal.api.APIFormat;
import biz.isphere.core.messagefileeditor.FieldFormat;
import biz.isphere.core.messagefileeditor.MessageDescription;

import com.ibm.as400.access.AS400;

public class IQMHRTVMResult extends APIFormat {

    private static final String BYTES_RETURNED = "bytesReturned"; //$NON-NLS-1$
    private static final String BYTES_AVAILABLE = "bytesAvailable"; //$NON-NLS-1$
    private static final String NUMBER_OF_MESSAGES_RETURNED = "numberOfMessagesReturned"; //$NON-NLS-1$
    private static final String FIRST_MESSAGE_ID_RETURNED = "firstMessageIdReturned"; //$NON-NLS-1$
    private static final String LAST_MESSAGE_ID_RETURNED = "lastMessageIdReturned"; //$NON-NLS-1$
    private static final String RESERVED = "reserved"; //$NON-NLS-1$
    private static final String OFFSET_TO_FIRST_MESSAGE = "offsetToFirstMessage"; //$NON-NLS-1$

    private String messageFile;
    private String library;

    public IQMHRTVMResult(AS400 system, String messageFile, String library, byte[] bytes) throws UnsupportedEncodingException {
        super(system, "IQMHRTVMHeader");

        this.messageFile = messageFile;
        this.library = library;

        createStructure();

        setBytes(bytes);
    }

    public int getBytesReturned() {
        return getInt4Value(BYTES_RETURNED);
    }

    public int getBytesAvailable() {
        return getInt4Value(BYTES_AVAILABLE);
    }

    public int getNumberOfMessagesReturned() {
        return getInt4Value(NUMBER_OF_MESSAGES_RETURNED);
    }

    public String getFirstMessageIdReturned() throws UnsupportedEncodingException {
        return getCharValue(FIRST_MESSAGE_ID_RETURNED);
    }

    public String getLastMessageIdReturned() throws UnsupportedEncodingException {
        return getCharValue(LAST_MESSAGE_ID_RETURNED);
    }

    public List<MessageDescription> getMessages() throws UnsupportedEncodingException {

        ArrayList<MessageDescription> messages = new ArrayList<MessageDescription>();

        RTVM0300 rtvm0300 = new RTVM0300(getSystem(), getBytes());
        int offset = getOffsetFirstMessage();

        for (int i = 0; i < getNumberOfMessagesReturned(); i++) {

            rtvm0300.setOffset(offset);

            ArrayList<FieldFormat> fieldFormats = new ArrayList<FieldFormat>();

            SubstitutionVariable variable = new SubstitutionVariable(getSystem(), getBytes());
            int offsetVariable = rtvm0300.getOffsetSubstitutionVariables();

            for (int f = 0; f < rtvm0300.getNumberOfSubstitutionVariables(); f++) {

                variable.setOffset(offsetVariable);

                FieldFormat fieldFormat = new FieldFormat();

                fieldFormat.setType(variable.getType());
                if (variable.getLength() == -1) {
                    fieldFormat.setVary(true);
                    fieldFormat.setBytes(variable.getDecimalPositions());
                } else {
                    fieldFormat.setVary(false);
                    fieldFormat.setLength(variable.getLength());
                    fieldFormat.setDecimalPositions(variable.getDecimalPositions());
                }

                fieldFormats.add(fieldFormat);
            }

            MessageDescription messageDescription = new MessageDescription();
            messageDescription.setConnection(getSystem().getSystemName());
            messageDescription.setLibrary(library);
            messageDescription.setMessageFile(messageFile);
            messageDescription.setMessageId(rtvm0300.getMessageId());
            messageDescription.setMessage(rtvm0300.getMessage());
            messageDescription.setHelpText(rtvm0300.getMessageHelp());
            messageDescription.setFieldFormats(fieldFormats);
            messageDescription.setSeverity(rtvm0300.getMessageSeverity());
            messageDescription.setCcsid(rtvm0300.getCcsid());
            messages.add(messageDescription);

            offset += rtvm0300.getBytesReturned();
        }

        return messages;
    }

    private int getOffsetFirstMessage() {
        return getInt4Value(OFFSET_TO_FIRST_MESSAGE);
    }

    /**
     * Creates the sub fields of the API format.
     */
    private void createStructure() {

        addInt4Field(BYTES_RETURNED, 0);
        addInt4Field(BYTES_AVAILABLE, 4);
        addInt4Field(NUMBER_OF_MESSAGES_RETURNED, 8);
        addCharField(FIRST_MESSAGE_ID_RETURNED, 12, 7);
        addCharField(LAST_MESSAGE_ID_RETURNED, 19, 7);
        addCharField(RESERVED, 26, 2);
        addInt4Field(OFFSET_TO_FIRST_MESSAGE, 28);
    }
}
