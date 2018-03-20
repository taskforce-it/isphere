/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.clcommands;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.api.qcapcmd.CPOP0100;
import biz.isphere.core.api.qcapcmd.QCAPCMD;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.data.PcmlException;
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
    private AS400Message[] errorMessages;

    /**
     * Produces a new CLFormatter object.
     * 
     * @param IBM i system with the iSphere library
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

            QCAPCMD qcapcmd = new QCAPCMD(system);
            if (!qcapcmd.execute(clCommand, CPOP0100.checkCLStatement(system))) {
                errorMessages = qcapcmd.getMessageList();
            } else {
                errorMessages = new AS400Message[0];
            }

            String formatted = qcapcmd.getChangedCommand();

            return formatted;

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Failed to format CL command: '" + clCommand + "' ***", e);
            return null;
        }

    }

    public AS400Message[] getErrorMEssages() {
        return errorMessages;
    }

    private int getLengthAvailableForChangedCommandString(ProgramCallDocument pcml, String clCommand) {

        int maxValue;
        try {
            maxValue = pcml.getIntValue("QCAPCMD.lenAvlChgCmdStr");
        } catch (PcmlException e) {
            maxValue = 32702;
        }

        return Math.min(clCommand.length() * 4, maxValue);
    }
}
