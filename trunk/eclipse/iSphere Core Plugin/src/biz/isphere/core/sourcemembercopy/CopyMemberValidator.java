package biz.isphere.core.sourcemembercopy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import biz.isphere.core.Messages;
import biz.isphere.core.file.description.RecordFormatDescription;
import biz.isphere.core.file.description.RecordFormatDescriptionsStore;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.sourcemembercopy.rse.CopyMemberService;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.FieldDescription;

public class CopyMemberValidator implements Runnable {

    public static final int ERROR_TO_CONNECTION = 1;
    public static final int ERROR_TO_FILE = 2;
    public static final int ERROR_TO_LIBRARY = 3;
    public static final int ERROR_TO_MEMBER = 4;

    private CopyMemberService jobDescription;
    private boolean replace;
    private boolean ignoreDataLostError;

    private boolean isValidated;
    private int errorItem;
    private String errorMessage;

    public CopyMemberValidator(CopyMemberService jobDescription, boolean replace, boolean ignoreDataLostError) {
        this.jobDescription = jobDescription;
        this.replace = replace;
        this.ignoreDataLostError = ignoreDataLostError;
    }

    public boolean isValidated() {
        return isValidated;
    }

    public int getErrorItem() {
        return errorItem;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void run() {

        errorItem = -1;
        errorMessage = null;

        jobDescription.prepareValidation();

        if (!validateTargetFile(jobDescription.getToConnectionName(), jobDescription.getToLibrary(), jobDescription.getToFile())) {
            isValidated = false;
            return;
        }

        if (!validateMembers(jobDescription.getFromConnectionName(), jobDescription.getToConnectionName(), replace, ignoreDataLostError)) {
            isValidated = false;
            return;
        }

        isValidated = true;
    }

    private boolean validateTargetFile(String toConnectionName, String toLibrary, String toFile) {

        Validator nameValidator = Validator.getNameInstance(jobDescription.getToConnectionCcsid());

        boolean isError = false;

        if (!hasConnection(jobDescription.getToConnectionName())) {
            errorItem = ERROR_TO_CONNECTION;
            errorMessage = Messages.bind(Messages.Connection_A_not_found, jobDescription.getToConnectionName());
            isError = true;
        } else if (!nameValidator.validate(toLibrary)) {
            errorItem = ERROR_TO_LIBRARY;
            errorMessage = Messages.bind(Messages.Invalid_library_name, toLibrary);
            isError = true;
        } else if (!IBMiHostContributionsHandler.checkLibrary(toConnectionName, toLibrary)) {
            errorItem = ERROR_TO_LIBRARY;
            errorMessage = Messages.bind(Messages.Library_A_not_found, toLibrary);
            isError = true;
        } else if (!nameValidator.validate(toFile)) {
            errorItem = ERROR_TO_FILE;
            errorMessage = Messages.bind(Messages.Invalid_file_name, toFile);
            isError = true;
        } else if (!IBMiHostContributionsHandler.checkFile(toConnectionName, toLibrary, toFile)) {
            errorItem = ERROR_TO_FILE;
            errorMessage = Messages.bind(Messages.File_A_not_found, toFile);
            isError = true;
        }

        return !isError;
    }

    private boolean hasConnection(String connectionName) {

        Set<String> connectionNames = new HashSet<String>(Arrays.asList(IBMiHostContributionsHandler.getConnectionNames()));
        boolean hasConnection = connectionNames.contains(connectionName);

        return hasConnection;
    }

    private boolean validateMembers(String fromConnectionName, String toConnectionName, boolean replace, boolean ignoreDataLostError) {

        boolean isError = false;

        try {

            boolean isSeriousError = false;

            AS400 fromSystem = IBMiHostContributionsHandler.getSystem(fromConnectionName);
            AS400 toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);

            RecordFormatDescriptionsStore fromSourceFiles = new RecordFormatDescriptionsStore(fromSystem);
            RecordFormatDescriptionsStore toSourceFiles = new RecordFormatDescriptionsStore(toSystem);

            Set<String> targetMembers = new HashSet<String>();

            for (CopyMemberItem member : jobDescription.getItems()) {
                if (member.isCopied()) {
                    continue;
                }

                if (isSeriousError) {
                    member.setErrorMessage(Messages.Canceled_due_to_previous_error);
                    continue;
                }

                String from = member.getFromQSYSName();
                String to = member.getToQSYSName();

                if (from.equals(to) && fromConnectionName.equalsIgnoreCase(toConnectionName)) {
                    member.setErrorMessage(Messages.bind(Messages.Cannot_copy_A_to_the_same_name, from));
                    isError = true;
                } else if (targetMembers.contains(to)) {
                    member.setErrorMessage(Messages.Can_not_copy_member_twice_to_same_target_member);
                    isError = true;
                } else if (!IBMiHostContributionsHandler.checkMember(fromConnectionName, member.getFromLibrary(), member.getFromFile(),
                    member.getFromMember())) {
                    member.setErrorMessage(Messages.bind(Messages.From_member_A_not_found, from));
                    isError = true;
                } else if (!replace
                    && IBMiHostContributionsHandler.checkMember(jobDescription.getToConnectionName(), member.getToLibrary(), member.getToFile(),
                        member.getToMember())) {
                    member.setErrorMessage(Messages.bind(Messages.Target_member_A_already_exists, to));
                    isError = true;
                } else if (!ignoreDataLostError) {

                    RecordFormatDescription fromRecordFormatDescription = fromSourceFiles.get(member.getFromFile(), member.getFromLibrary());
                    RecordFormatDescription toRecordFormatDescription = toSourceFiles.get(member.getToFile(), member.getToLibrary());

                    FieldDescription fromSrcDta = fromRecordFormatDescription.getFieldDescription("SRCDTA");
                    if (fromSrcDta == null) {
                        member.setErrorMessage(Messages.bind(Messages.Could_not_retrieve_field_description_of_field_C_of_file_B_A, new String[] {
                            member.getFromFile(), member.getFromLibrary(), "SRCDTA" }));
                        isError = true;
                        isSeriousError = true;
                    } else {

                        FieldDescription toSrcDta = toRecordFormatDescription.getFieldDescription("SRCDTA");
                        if (toSrcDta == null) {
                            member.setErrorMessage(Messages.bind(Messages.Could_not_retrieve_field_description_of_field_C_of_file_B_A, new String[] {
                                member.getToFile(), member.getToLibrary(), "SRCDTA" }));
                            isError = true;
                            isSeriousError = true;
                        } else {

                            if (fromSrcDta.getLength() > toSrcDta.getLength()) {
                                member.setErrorMessage(Messages.Data_lost_error_From_source_line_is_longer_than_target_source_line);
                                isError = true;
                            }
                        }
                    }
                }

                if (!isError) {
                    member.setErrorMessage(Messages.EMPTY);
                } else {
                    errorItem = ERROR_TO_MEMBER;
                    errorMessage = Messages.Validation_ended_with_errors_Request_canceled;
                }

                targetMembers.add(to);
            }

        } finally {
            // endProcess();
        }

        return !isError;
    }
};
