/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.api;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.ProgramParameter;
import com.ibm.as400.access.ServiceProgramCall;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.journalexplorer.core.preferences.Preferences;

/**
 * Class, representing the QjoRetrieveJournalEntries API. Used to retrieve the
 * journal entries.
 * 
 * @author Thomas Raddatz
 */
public class QjoRetrieveJournalEntries {

    private AS400 system;
    private JrneToRtv jrneToRtv;
    private int maxNumEntries;

    private ServiceProgramCall serviceProgram;
    private List<AS400Message> messages;

    public QjoRetrieveJournalEntries(AS400 aSystem, JrneToRtv aJrneToRtv) throws Exception {
        system = aSystem;
        jrneToRtv = aJrneToRtv;
        maxNumEntries = aJrneToRtv.getNbrEnt();

        serviceProgram = new ServiceProgramCall(aSystem);
        serviceProgram.setProgram("/QSYS.LIB/QJOURNAL.SRVPGM");
        serviceProgram.setProcedureName("QjoRetrieveJournalEntries");
        serviceProgram.setAlignOn16Bytes(true);
        messages = new ArrayList<AS400Message>();
    }

    /**
     * Calls the API and returns the retrieved journal entries.<br>
     * Updates the selection criteria for the next call when there are more
     * journal entries available, but there is no room available in the return
     * structure.
     * 
     * @return retrieved journal entries
     */
    public RJNE0200 execute() throws Exception {

        int bufferSize = IntHelper.align16Bytes(Preferences.getInstance().getRetrieveJournalEntriesBufferSize());

        RJNE0200 tJournalEntries = new RJNE0200(system, jrneToRtv.getJournal(), jrneToRtv.getLibrary(), bufferSize);

        if (retrieveJournalEntries(tJournalEntries.getProgramParameters(jrneToRtv))) {
            if (tJournalEntries.moreEntriesAvailable()) {
                Long tFromSequenceNumber = tJournalEntries.getContinuatingSequenceNumber();
                String tStartReceiver = tJournalEntries.getContinuatingReceiver();
                String tStartReceiverLibrary = tJournalEntries.getContinuatingReceiverLibrary();

                jrneToRtv.setFromEnt(tFromSequenceNumber);
                jrneToRtv.setRcvRng(tStartReceiver, tStartReceiverLibrary);

                if (maxNumEntries != -1) {
                    jrneToRtv.setNbrEnt(maxNumEntries - tJournalEntries.getNbrOfEntriesRetrieved());
                }
            }
        } else {
            tJournalEntries = null;
        }

        return tJournalEntries;
    }

    /**
     * Returns the error messages on an API error.
     * 
     * @return list of API error messages
     */
    public List<AS400Message> getMessages() {
        return messages;
    }

    /**
     * Calls the QjoRetrieveJournalEntries API and retrieves error messages if
     * the API failed working.
     * 
     * @param parameters - parameters passed to the API
     * @return <code>true</code> on success, else <code>false</code>.
     * @throws PropertyVetoException
     * @throws ObjectDoesNotExistException
     * @throws InterruptedException
     * @throws IOException
     * @throws ErrorCompletingRequestException
     * @throws AS400SecurityException
     * @throws Exception
     */
    private boolean retrieveJournalEntries(ProgramParameter[] parameters) throws PropertyVetoException, AS400SecurityException,
        ErrorCompletingRequestException, IOException, InterruptedException, ObjectDoesNotExistException {
        messages.clear();
        serviceProgram.setParameterList(parameters);
        if (serviceProgram.run() != true) {
            messages.addAll(Arrays.asList(serviceProgram.getMessageList()));
            return false;
        } else {
            return true;
        }
    }
}
