/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.clcommands;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.data.ProgramCallDocument;

/**
 * This class formats a given CL command. It uses the <i>CL program statement
 * (4)</i> command processing type of the <i>Process Commands (QCAPCMD) API</i>
 * to do the formatting.
 * 
 * @author Thomas Raddatz
 */
public class CLFormatter {

    private AS400 system;

    /**
     * Produces a new CLFormatter object.
     */
    public CLFormatter(AS400 system) {
        this.system = system;
    }

    /**
     * Changes the system used by the formatter to call the QCAPCMD API.
     * 
     * @param system used for calling the QCAPCMD API.
     */
    public void setSystem(AS400 system) {

        if (system == null) {
            throw new IllegalArgumentException("Value of parameter 'system' must not be {null}."); //$NON-NLS-1$
        }

        this.system = system;
    }

    /**
     * Formats a given CL command.
     * 
     * @param clCommand - CL command that is formatted.
     * @return formatted CL command on success, else null.
     */
    public String format(String clCommand) {

        try {

            ProgramCallDocument pcml = new ProgramCallDocument(system, "biz.isphere.core.clcommands.QCAPCMD", this.getClass().getClassLoader());

            pcml.setStringValue("QCAPCMD.srcCmdStr", clCommand);
            pcml.setIntValue("QCAPCMD.srcCmdStrLen", clCommand.length());
            pcml.setIntValue("QCAPCMD.lenPrvChgCmdStr", 120);

            pcml.setStringValue("QCAPCMD.optCtrlBlock.typeOfCmdPrc", "4"); // CL-Syntax
            pcml.setStringValue("QCAPCMD.optCtrlBlock.DBCSDataHandling", "0"); // Ignore
            pcml.setStringValue("QCAPCMD.optCtrlBlock.prompterAction", "0"); // Never
            pcml.setStringValue("QCAPCMD.optCtrlBlock.cmdStrSyntax", "0"); // System
            pcml.setValue("QCAPCMD.optCtrlBlock.msgRetrieveKey", new byte[] { 0x00, 0x00, 0x00, 0x00 });
            pcml.setStringValue("QCAPCMD.optCtrlBlock.CCSIDOfCmdString", "0"); // Job
            pcml.setValue("QCAPCMD.optCtrlBlock.reserved", new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00 });

            if (!pcml.callProgram("QCAPCMD")) {
                AS400Message[] msgs = pcml.getMessageList("QCAPCMD");
                for (int i = 0; i < msgs.length; i++) {
                    // TODO: store error messages
                    System.out.println(msgs[i].getText());
                }
                return null;
            }

            String formatted = pcml.getStringValue("QCAPCMD.chgCmdString");

            return formatted;

        } catch (Throwable e) {
            // TODO: handle error
            e.printStackTrace();
            return null;
        }

    }
}
