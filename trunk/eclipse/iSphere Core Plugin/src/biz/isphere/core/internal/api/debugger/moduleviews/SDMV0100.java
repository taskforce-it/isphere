/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.debugger.moduleviews;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import biz.isphere.base.internal.IBMiHelper;
import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

public class SDMV0100 extends APIFormat {

    public static final String LISTING_VIEW = "*LISTING";
    public static final String TEXT_VIEW = "*TEXT";
    public static final String STATEMENT_VIEW = "*STATEMENT";

    public static final String MAIN_VIEW = "*MAIN";
    public static final String NOMAIN_VIEW = "*NOMAIN";

    private static final String OFFSET_NEXT_VIEW = "offsetNextView"; //$NON-NLS-1$
    private static final String MODULE = "module"; //$NON-NLS-1$
    private static final String VIEW_TYPE = "viewType"; //$NON-NLS-1$
    private static final String COMPILER_ID = "compilerId"; //$NON-NLS-1$
    private static final String MAIN_INDICATOR = "mainInd"; //$NON-NLS-1$
    private static final String VIEW_TIMESTAMP = "viewTmstmp"; //$NON-NLS-1$
    private static final String VIEW_DESCRIPTION = "viewDesc"; //$NON-NLS-1$
    private static final String RESERVED_1 = "reserved_1"; //$NON-NLS-1$
    private static final String VIEW_NUMBER = "viewNumber"; //$NON-NLS-1$
    private static final String NUMBER_OF_VIEWS = "numOfViews"; //$NON-NLS-1$

    /**
     * Constructs a SDMV0100 object.
     * 
     * @param system - System that calls the API
     * @param bytes - buffer that contains the retrieved debug views
     * @throws UnsupportedEncodingException
     */
    public SDMV0100(AS400 system, byte[] bytes) throws UnsupportedEncodingException {
        super(system, "SDMV0100");

        createStructure();

        setBytes(bytes);
    }

    /**
     * Returns the name of the module this view belongs to.
     * 
     * @return module name
     * @throws UnsupportedEncodingException
     */
    public String getModule() throws UnsupportedEncodingException {
        return getCharValue(MODULE).trim();
    }

    /**
     * Returns the type of this view.
     * 
     * @return view type
     * @throws UnsupportedEncodingException
     */
    public String getViewType() throws UnsupportedEncodingException {
        return getCharValue(VIEW_TYPE).trim();
    }

    /**
     * Returns the main indicator of this view.
     * 
     * @return main indicator
     * @throws UnsupportedEncodingException
     */
    public String getMainIndicator() throws UnsupportedEncodingException {
        return getCharValue(MAIN_INDICATOR).trim();
    }

    /**
     * Return the date the view was created.
     * 
     * @return date created
     * @throws Exception
     */
    public Date getViewTimestamp() throws UnsupportedEncodingException {

        String timestamp = getCharValue(VIEW_TIMESTAMP);
        Date date = IBMiHelper.cyymmddToDate(timestamp);

        return date;
    }

    /**
     * Returns the description of this view.
     * 
     * @return view description
     * @throws UnsupportedEncodingException
     */
    public String getViewDescription() throws UnsupportedEncodingException {
        return getCharValue(VIEW_DESCRIPTION).trim();
    }

    /**
     * Returns the sequence number of this view.
     * 
     * @return view number
     * @throws UnsupportedEncodingException
     */
    public int getViewNumber() {
        return getInt4Value(VIEW_NUMBER);
    }

    /**
     * Factory methods to create a debugger view.
     * 
     * @param messageFile - message file
     * @param library - message file library name
     * @return message description
     * @throws UnsupportedEncodingException
     */
    public DebuggerView createDebuggerView(String object, String library, String objectType) throws UnsupportedEncodingException {

        DebuggerView debuggerView = new DebuggerView(object, library, objectType, this);

        return debuggerView;
    }

    /**
     * Creates the SDMV0100 structure.
     */
    protected void createStructure() {

        addInt4Field(OFFSET_NEXT_VIEW, 0);
        addCharField(MODULE, 4, 10);
        addCharField(VIEW_TYPE, 14, 10);
        addCharField(COMPILER_ID, 24, 20);
        addCharField(MAIN_INDICATOR, 44, 10);
        addCharField(VIEW_TIMESTAMP, 54, 13);
        addCharField(VIEW_DESCRIPTION, 67, 50);
        addCharField(RESERVED_1, 117, 3);
        addInt4Field(VIEW_NUMBER, 120);
        addInt4Field(NUMBER_OF_VIEWS, 124);
    }

}
