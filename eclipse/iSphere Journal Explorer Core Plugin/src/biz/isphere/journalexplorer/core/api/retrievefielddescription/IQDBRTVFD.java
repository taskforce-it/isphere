/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.api.retrievefielddescription;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.base.internal.Buffer;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.api.APIErrorCode;
import biz.isphere.core.internal.api.APIProgramCallDocument;
import biz.isphere.journalexplorer.core.model.MetaColumn;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ProgramParameter;

/**
 * This class retrieves the messages descriptions of a given message file.
 * 
 * @author Thomas Raddatz
 */
public class IQDBRTVFD extends APIProgramCallDocument {

    public static final String RVFD0100 = "RVFD0100";

    public static final String TYPE_API_BINARY = "B"; //$NON-NLS-1$
    public static final String TYPE_API_FLOAT = "F"; //$NON-NLS-1$
    public static final String TYPE_API_ZONED = "S"; //$NON-NLS-1$
    public static final String TYPE_API_PACKED = "P"; //$NON-NLS-1$
    public static final String TYPE_API_CHAR = "A"; //$NON-NLS-1$
    public static final String TYPE_API_GRAPHIC = "G"; //$NON-NLS-1$
    public static final String TYPE_API_DATE = "L"; //$NON-NLS-1$
    public static final String TYPE_API_TIME = "T"; //$NON-NLS-1$
    public static final String TYPE_API_TIMESTAMP = "Z"; //$NON-NLS-1$
    public static final String TYPE_API_LOB = "1"; //$NON-NLS-1$
    public static final String TYPE_API_DATALINK = "2"; //$NON-NLS-1$

    // 128 kB (= 131072 bytes) is large enough to return 1236 fields.
    // header size: 36 bytes
    // record size: 106 bytes
    // 1236 * 106 + 36 = 131052
    private static final int BUFFER_SIZE = Buffer.size("128 kByte"); //$NON-NLS-1$

    private String file;
    private String library;
    private String format;

    /**
     * Constructs a IQDBRTVFD object for a system and connection.
     * 
     * @param system - System that hosts the file.
     * @param connectionName - Name of the RDi connection.
     * @throws PropertyVetoException
     */
    public IQDBRTVFD(AS400 system, String connectionName) {
        this(system, connectionName, RVFD0100);
    }

    /**
     * Constructs a IQDBRTVFD object for a system and connection.
     * 
     * @param system - System that hosts the file.
     * @param connectionName - Name of the RDi connection.
     * @param format - Format of the retrieved data ({@link #RVFD0100}.
     * @throws PropertyVetoException
     */
    private IQDBRTVFD(AS400 system, String connectionName, String format) {
        super(system, "IQDBRTVFD", IBMiHostContributionsHandler.getISphereLibrary(connectionName)); //$NON-NLS-1$

        this.format = format;
    }

    /**
     * Sets the name of the file and library.
     * 
     * @param name - name of the message file
     * @param library - name of the message file library
     */
    public void setFile(String name, String library) {

        this.file = name;
        this.library = library;

    }

    /**
     * Retrieves all field descriptions of the file.
     * 
     * @param monitor - monitor
     * @return array of message descriptions
     */
    public MetaColumn[] retrieveFieldDescriptions() {
        return retrieveFieldDescriptions(null);
    }

    /**
     * Retrieves all field descriptions of the file.
     * 
     * @param monitor - monitor
     * @param bufferSize - buffer size
     * @return array of message descriptions
     */
    public MetaColumn[] retrieveFieldDescriptions(IProgressMonitor monitor) {

        int bufferSize = BUFFER_SIZE;

        return retrieveFieldDescriptions(monitor, bufferSize);
    }

    /**
     * Retrieves all field descriptions of the file.
     * 
     * @param monitor - monitor
     * @param bufferSize - buffer size
     * @return array of message descriptions
     */
    public MetaColumn[] retrieveFieldDescriptions(IProgressMonitor monitor, int bufferSize) {

        List<MetaColumn> fieldDescriptions = new ArrayList<MetaColumn>();

        try {

            IQDBRTVFDResult result = retrieveFieldDescriptions(bufferSize);

            if (result != null) {
                if (result.getBytesReturned() < result.getBytesAvailable()) {
                    bufferSize = bufferSize * 2;
                    return retrieveFieldDescriptions(monitor, bufferSize);
                }
                fieldDescriptions.addAll(result.getFieldDescriptions());
            }

        } catch (Exception e) {
            ISpherePlugin.logError("Failed calling the iSphere IQDBRTVFD API.", e); //$NON-NLS-1$
        }

        return fieldDescriptions.toArray(new MetaColumn[fieldDescriptions.size()]);
    }

    /**
     * Retrieves the field descriptions of the file.
     * 
     * @param bufferSize - buffer to return the field descriptions
     * @return result of the retrieve operation
     * @throws Exception
     */
    private IQDBRTVFDResult retrieveFieldDescriptions(int bufferSize) throws Exception {

        if (execute(createParameterList(bufferSize))) {
            return new IQDBRTVFDResult(getSystem(), getParameterList()[0].getOutputData(), format);
        }

        return null;
    }

    /**
     * Produces the parameter list for calling the IQDBRTVFD API.
     * 
     * @param bufferSize - buffer to return the message descriptions
     * @return parameter list
     * @throws Exception
     */
    protected ProgramParameter[] createParameterList(int bufferSize) throws Exception {

        ProgramParameter[] parameterList = new ProgramParameter[9];
        parameterList[0] = new ProgramParameter(bufferSize);
        parameterList[1] = produceIntegerParameter(bufferSize);
        parameterList[2] = produceStringParameter(format, 8);
        parameterList[3] = produceQualifiedObjectName(file, library);
        parameterList[4] = produceStringParameter("*FIRST", 10); //$NON-NLS-1$
        parameterList[5] = produceByteParameter(new APIErrorCode().getBytes());
        parameterList[6] = produceStringParameter("0", 1); //$NON-NLS-1$
        parameterList[7] = produceStringParameter("*LCL", 10); //$NON-NLS-1$
        parameterList[8] = produceStringParameter("*EXT", 10); //$NON-NLS-1$

        return parameterList;
    }
}
