/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.streamfilesearch;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.search.SearchArgument;
import biz.isphere.core.search.SearchOptions;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;

public class ZFNDSTR_search {

    public int run(AS400 _as400, int _handle, SearchOptions _searchOptions) {

        int errno = 0;

        try {

            // Debug options:
            // Trace.setTraceOn(true); // Turn on tracing function.
            // Trace.setTracePCMLOn(true); // Turn on PCML tracing.

            ProgramCallDocument pcml = new ProgramCallDocument(_as400, "biz.isphere.core.streamfilesearch.ZFNDSTR_search", this.getClass()
                .getClassLoader());

            pcml.setIntValue("ZFNDSTR_search.handle", _handle);

            int[] indices = new int[1];
            pcml.setIntValue("ZFNDSTR_search.size", _searchOptions.getSearchArguments().size());

            for (indices[0] = 0; indices[0] < _searchOptions.getSearchArguments().size(); indices[0]++) {
                SearchArgument searchArgument = _searchOptions.getSearchArguments().get(indices[0]);
                pcml.setIntValue("ZFNDSTR_search.arguments.operator", indices, searchArgument.getOperator());
                pcml.setValue("ZFNDSTR_search.arguments.string.length", indices, searchArgument.getString().length());
                pcml.setValue("ZFNDSTR_search.arguments.string.value", indices, searchArgument.getString());
                pcml.setIntValue("ZFNDSTR_search.arguments.fromColumn", indices, searchArgument.getFromColumn());
                pcml.setIntValue("ZFNDSTR_search.arguments.toColumn", indices, searchArgument.getToColumn());
                pcml.setValue("ZFNDSTR_search.arguments.case", indices, searchArgument.getCaseSensitive());
                pcml.setValue("ZFNDSTR_search.arguments.regex", indices, searchArgument.getRegularExpression());
            }

            if (_searchOptions.isShowAllItems()) {
                pcml.setValue("ZFNDSTR_search.showRecords", "1");
            } else {
                pcml.setValue("ZFNDSTR_search.showRecords", "0");
            }

            pcml.setValue("ZFNDSTR_search.matchOpt", _searchOptions.getMatchOption().getId());

            boolean rc = pcml.callProgram("ZFNDSTR_search");
            if (rc == false) {

                AS400Message[] msgs = pcml.getMessageList("ZFNDSTR_search");
                for (int idx = 0; idx < msgs.length; idx++) {
                    ISpherePlugin.logError(msgs[idx].getID() + " - " + msgs[idx].getText(), null);
                }
                ISpherePlugin.logError("*** Call to ZFNDSTR_search failed. See messages above ***", null);

                errno = -1;

            } else {

                errno = 1;

            }

        } catch (PcmlException e) {

            errno = -1;

            ISpherePlugin.logError("*** Call to ZFNDSTR_search failed due to an unexpected PcmlException ***", e);
        }

        return errno;

    }

}