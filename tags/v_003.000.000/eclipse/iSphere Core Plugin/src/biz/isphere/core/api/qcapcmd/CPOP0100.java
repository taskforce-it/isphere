/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.api.qcapcmd;

import java.io.CharConversionException;
import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

public class CPOP0100 extends APIFormat {

    /**
     * <b>CCSID of command string.</b><br>
     * Input is in the job CCSID.
     */
    public static final int CCSID_JOB = 0;

    /**
     * <b>CCSID of command string.</b><br>
     * The command string is in UTF8.
     */
    public static final int CCSID_UTF8 = 1208;

    /**
     * <b>CCSID of command string.</b><br>
     * The command string is in UTF16.
     */
    public static final int CCSID_UTF16 = 1200;

    /**
     * <b>Command string syntax.</b><br>
     * Use system syntax. The specification of qualified objects is in the
     * format library/object.
     */
    public static final String SYNTAX_SYSTEM = "0";

    /**
     * <b>Command string syntax.</b><br>
     * Use System/38 syntax. The specification of qualified objects is in the
     * format object.library. The system searches the QUSER38 library (if it
     * exists) and the QSYS38 library for the command even though these
     * libraries are not in the library list.
     */
    public static final String SYNTAX_S38 = "1";

    /**
     * <b>DBCS data handling.</b><br>
     * Ignore DBCS data.
     */
    public static final String DBCS_IGNORE = "0";

    /**
     * <b>DBCS data handling.</b><br>
     * Handle DBCS data.
     */
    public static final String DBCS_HANDLE = "1";

    /**
     * <b>Prompter action.</b><br>
     * Never prompt the command. This will prevent a command prompt even if
     * selective prompting characters are present in the command string.
     * <p>
     * <b>Note:</b> When the type of command processing field is 2 or 3 and
     * there are missing required parameters, the command will be prompted, even
     * when the prompter action is set to 0.
     */
    public static final String PROMPT_NEVER = "0";

    /**
     * <b>Prompter action.</b><br>
     * Always prompt the command. This forces a command prompt even if selective
     * prompting characters are not present in the command string.
     */
    public static final String PROMPT_ALWAYS = "1";

    /**
     * <b>Prompter action.</b><br>
     * Prompt the command if selective prompting characters are present in the
     * command string. A CPF0008 exception is sent if this value is specified
     * with types of command processing values 4 through 8.
     */
    public static final String PROMPT_REQUESTED = "2";

    /**
     * <b>Prompter action.</b><br>
     * Show help. Provides help display for the command.
     */
    public static final String PROMPT_HELP = "3";

    /**
     * <b>Type of command processing.</b><br>
     * Command running. The processing for this type is the same as that
     * performed by the QCMDEXC API. Commands processed must have a value of
     * *EXEC on the ALLOW parameter of the Create Command (CRTCMD) or the Change
     * Command (CHGCMD) command.
     */
    public static final int CMD_EXECUTE = 0;

    /**
     * <b>Type of command processing.</b><br>
     * Command syntax check. The processing for this type is the same as that
     * performed by the QCMDCHK API. Command line running. This processing is
     * like that provided by the QCMDEXC API but with the following additions:
     * <ul>
     * <li>Limited user checking is performed.</li>
     * <li>Prompting for missing required parameters is performed.</li>
     * <li>If the System/36™ environment is active and the commands are
     * System/36 commands, the System/36 environment runs the commands.</li>
     * </ul>
     */
    public static final int CMD_CHECK = 1;

    /**
     * <b>Type of command processing.</b><br>
     * This type processes commands with value *INTERACT specified on the ALLOW
     * parameter of the CRTCMD or CHGCMD command. While this type is meant to
     * implement an interactive command line, it can be used in batch. When used
     * in a batch job, the command must have been created or changed to specify
     * value *BATCH on the ALLOW parameter of the CRTCMD or CHGCMD command.
     * Limited user checking and System/36 environment processing is done while
     * prompting options are ignored.
     */
    public static final int CMD_EXECUTE_CMDLINE = 2;

    /**
     * <b>Type of command processing.</b><br>
     * Command line syntax check. This processing provides the check only
     * complement of type 2 (command line running). The check option performs
     * all checks against CL rules. The System/36 environment is not called.
     */
    public static final int CMD_CHECK_CMDLINE = 3;

    /**
     * <b>Type of command processing.</b><br>
     * CL program statement. The command string is checked according to the
     * rules for CL programs (source entry utility (SEU) member type of CLP).
     * Commands may not be run with this type. Command prompts include a prompt
     * for a command label and comment. Variable names are allowed. Commands
     * processed for this type must be defined with values of *BPGM or *IPGM on
     * the ALLOW parameter of the CRTCMD or CHGCMD command.
     */
    public static final int CMD_CHECK_CL_STATEMENT = 4;

    /**
     * <b>Type of command processing.</b><br>
     * CL input stream. The command string is checked according to the rules for
     * CL batch jobs (SEU member type of CL). Commands may not be run. Command
     * prompts include a prompt for comment. Variable names are not allowed.
     */
    public static final int CMD_CHECK_CL_INPUT_STREAM = 5;

    /**
     * <b>Type of command processing.</b><br>
     * Command definition statements. The command string is checked according to
     * the rules for command definition (SEU member type of CMD). Commands may
     * not be run. The commands are restricted to CMD, PARM, ELEM, QUAL, DEP,
     * and PMTCTL.
     */
    public static final int CMD_CHECK_DEFINITION_STMT = 6;

    /**
     * <b>Type of command processing.</b><br>
     * Binder definition statements. The command string is checked according to
     * the rules for binder definition (SEU member type of BND). Commands may
     * not be run. The commands are restricted to STRPGMEXP, ENDPGMEXP, and
     * EXPORT.
     */
    public static final int CMD_CHECK_BINDER_STMT = 7;

    /**
     * <b>Type of command processing.</b><br>
     * User-defined option. This option allows a user to create user-defined
     * option command strings similar to those used by the programming
     * development manager (PDM). It allows checking and creating a command
     * string for future use with types 0 through 3 except that variables are
     * allowed. The command string produced may not be directly operable. That
     * is, if CL variables were specified in the command string, the user must
     * perform a substitution prior to using the API with types of 0 or 2. This
     * value is not allowed if the CCSID of the input command string is 1208
     * (UTF8) or 1200 (UTF16).
     */
    public static final int CMD_CHECK_USER_DEFINED_OPTION = 8;

    /**
     * <b>Type of command processing.</b><br>
     * ILE CL program source. The source is checked according to the rules for
     * ILE CL programs (source entry utility (SEU) member type of CLLE).
     * Commands may not be run with this type. Command prompts include a prompt
     * for a command label and comment. Variable names are allowed. Commands
     * processed for this type must be defined with values *IMOD or *IPGM or
     * *BMOD or *BPGM specified for the ALLOW parameter of the CRTCMD or CHGCMD
     * command.
     */
    public static final int CMD_CHECK_ILE_CL_SRC = 9;

    /**
     * <b>Type of command processing.</b><br>
     * Command prompt string. The command analyzer prepares the source command
     * for prompting and returns the command string to use for the initial
     * prompt display. If the command has an exit program registered for the
     * QIBM_QCA_CHG_COMMAND exit point, the exit program is called. If the exit
     * program replaces the original command, the changed command string
     * returned by QCAPCMD is the replacement command from the exit program. The
     * returned command string may not be syntactically correct because no
     * syntax checking is done on the replacement command. The length of changed
     * command string available to return is set to 0 and the changed command
     * string parameter is not changed if any of these conditions are true:
     * <ol>
     * <li>The exit program is not called.</li>
     * <li>The exit program ends in error.</li>
     * <li>The exit program does not replace the command.</li>
     * </ol>
     */
    public static final int CMD_PROMPT = 10;

    private static final String TYPE_OF_COMMAND_PROCESSING = "typeOfCmdPrc"; //$NON-NLS-1$
    private static final String DBCS_DATA_HANDLING = "DBCSDataHandling"; //$NON-NLS-1$
    private static final String PROMPTER_ACTION = "prompterAction"; //$NON-NLS-1$
    private static final String COMMAND_STRING_SYNTAX = "cmdStrSyntax"; //$NON-NLS-1$
    private static final String MESSAGE_RETRIEVE_KEY = "msgRtvKey"; //$NON-NLS-1$
    private static final String CCSID_OF_COMMAND_STRING = "CCSIDOfCmdString"; //$NON-NLS-1$
    private static final String RESERVED = "reserved"; //$NON-NLS-1$

    public CPOP0100(AS400 system, int typeOfCommandProcessing, String prompterAction) throws CharConversionException, UnsupportedEncodingException {
        this(system);

        setTypeOfCommandProcessing(typeOfCommandProcessing);
        setPrompterAction(prompterAction);
    }

    public CPOP0100(AS400 system, int typeOfCommandProcessing) throws CharConversionException, UnsupportedEncodingException {
        this(system);

        setTypeOfCommandProcessing(typeOfCommandProcessing);
    }

    public CPOP0100(AS400 system) throws CharConversionException, UnsupportedEncodingException {
        super(system, "CPOP0100");

        createStructure();

        setInt4Value(TYPE_OF_COMMAND_PROCESSING, CMD_CHECK);
        setCharValue(DBCS_DATA_HANDLING, DBCS_IGNORE);
        setCharValue(PROMPTER_ACTION, PROMPT_NEVER);
        setCharValue(COMMAND_STRING_SYNTAX, SYNTAX_SYSTEM);
        setByteValue(MESSAGE_RETRIEVE_KEY, new byte[] { 0x00 });
        setInt4Value(CCSID_OF_COMMAND_STRING, CCSID_JOB);
        setByteValue(RESERVED, new byte[] { 0x00 });
    }

    public static CPOP0100 executeCommand(AS400 system) throws CharConversionException, UnsupportedEncodingException {
        CPOP0100 cpop0100 = new CPOP0100(system, CMD_EXECUTE);
        return cpop0100;
    }

    public static CPOP0100 checkCLStatement(AS400 system) throws CharConversionException, UnsupportedEncodingException {
        CPOP0100 cpop0100 = new CPOP0100(system, CMD_CHECK_CL_STATEMENT);
        return cpop0100;
    }

    public void setTypeOfCommandProcessing(int typeOfCommandProcessing) {

        checkTypeOfCommandProcessing(typeOfCommandProcessing);
        setInt4Value(TYPE_OF_COMMAND_PROCESSING, typeOfCommandProcessing);
    }

    public void setPrompterAction(String prompterAction) throws CharConversionException, UnsupportedEncodingException {

        checkPrompterAction(prompterAction);
        setCharValue(PROMPTER_ACTION, prompterAction);
    }

    private void checkTypeOfCommandProcessing(int typeOfCommandProcessing) {
        if (typeOfCommandProcessing != CMD_EXECUTE && typeOfCommandProcessing != CMD_CHECK && typeOfCommandProcessing != CMD_EXECUTE_CMDLINE
            && typeOfCommandProcessing != CMD_CHECK_CMDLINE && typeOfCommandProcessing != CMD_CHECK_CL_STATEMENT
            && typeOfCommandProcessing != CMD_CHECK_CL_INPUT_STREAM && typeOfCommandProcessing != CMD_CHECK_DEFINITION_STMT
            && typeOfCommandProcessing != CMD_CHECK_BINDER_STMT && typeOfCommandProcessing != CMD_CHECK_USER_DEFINED_OPTION
            && typeOfCommandProcessing != CMD_CHECK_ILE_CL_SRC && typeOfCommandProcessing != CMD_PROMPT) {
            throw new IllegalArgumentException("Invalid value specified for parameter'typeOfCommandProcessing'");
        }
    }

    private void checkPrompterAction(String prompterAction) {
        if (!PROMPT_NEVER.equals(prompterAction) && !PROMPT_ALWAYS.equals(prompterAction) && !PROMPT_REQUESTED.equals(prompterAction)
            && !PROMPT_HELP.equals(prompterAction)) {
            throw new IllegalArgumentException("Invalid value specified for parameter'prompterAction'");
        }

    }

    /**
     * Creates the PRDI0100 structure.
     */
    private void createStructure() {

        addInt4Field(TYPE_OF_COMMAND_PROCESSING, 0);
        addCharField(DBCS_DATA_HANDLING, 4, 1);
        addCharField(PROMPTER_ACTION, 5, 1);
        addCharField(COMMAND_STRING_SYNTAX, 6, 1);
        addCharField(MESSAGE_RETRIEVE_KEY, 7, 4);
        addInt4Field(CCSID_OF_COMMAND_STRING, 11);
        addCharField(RESERVED, 15, 5);
    }
}
