/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ccsid.CcsidUtil;

import com.ibm.as400.access.PrintObject;
import com.ibm.as400.access.PrintParameterList;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.SpooledFile;

public abstract class AbstractSpooledFileTransformer implements ISpooledFileTransformer {

    private static final String SPACE = " "; //$NON-NLS-1$

    private static final String UNDERSCORE = "_"; //$NON-NLS-1$

    protected static final String CR_LF = "\r\n"; //$NON-NLS-1$

    protected static final String FF = "\f"; //$NON-NLS-1$

    /**
     * Replacement value for carriage return (CR). The *WSCST objects transform
     * CR to DC1 in order to let the spooled file transformer detect CR.
     * Otherwise the CR gets lost due to the BufferedReader.
     * <p>
     * The spooled file transformer needs to know about CR to strip the
     * additional "bold" and "underline" lines of *SCS sppoled files.
     */
    protected static final byte DC1 = 0x11;

    private String connectionName;
    private SpooledFile spooledFile;
    private DecimalFormat jobNumberFormat;

    private Float cpi;
    private Integer lpi;
    private Float pageWidth;
    private Float pageHeight;

    public AbstractSpooledFileTransformer(String connectionName, SpooledFile spooledFile) {
        this.connectionName = connectionName;
        this.spooledFile = spooledFile;
        this.jobNumberFormat = new DecimalFormat("000000");
    }

    protected String getISphereLibrary() {
        return ISpherePlugin.getISphereLibrary(connectionName);
    }

    protected String getName() {
        return spooledFile.getName();
    }

    protected String getJob() {
        return jobNumberFormat.format(Integer.parseInt(spooledFile.getJobNumber())) + "/" + spooledFile.getJobUser() + "/" + spooledFile.getJobName();
    }

    protected String getUserData() {

        String userdata;
        try {
            userdata = spooledFile.getStringAttribute(PrintObject.ATTR_USERDATA);
        } catch (Exception e) {
            userdata = null;
        }

        if (userdata == null) {
            return "";
        }

        return userdata;
    }

    protected float getPageHeightInLines() {

        if (pageHeight != null) {
            return pageHeight;
        }

        try {
            pageHeight = spooledFile.getFloatAttribute(PrintObject.ATTR_PAGELEN);
        } catch (Exception e) {
            pageHeight = null;
        }

        if (pageHeight == null) {
            return 66;
        }

        return pageHeight.floatValue();
    }

    protected float getPageWidthInChars() {

        if (pageWidth != null) {
            return pageWidth;
        }

        try {
            pageWidth = spooledFile.getFloatAttribute(PrintObject.ATTR_PAGEWIDTH);
        } catch (Exception e) {
            pageWidth = null;
        }

        if (pageWidth == null) {
            return 132;
        }

        return pageWidth.floatValue();
    }

    protected int getLPI() {

        if (lpi != null) {
            return lpi;
        }

        try {
            lpi = spooledFile.getIntegerAttribute(PrintObject.ATTR_LPI);
        } catch (Exception e) {
            lpi = null;
        }

        if (lpi == null) {
            return 6;
        }

        return lpi.intValue();
    }

    protected float getCPI() {

        if (cpi != null) {
            return cpi;
        }

        try {
            cpi = spooledFile.getFloatAttribute(PrintObject.ATTR_CPI);
        } catch (Exception e) {
            cpi = null;
        }

        if (cpi == null) {
            return 10;
        }

        return cpi.floatValue();
    }

    public boolean transformSpooledFile(String target) throws Exception {

        BufferedReader reader = null;

        boolean cleanUp = false;

        try {

            QSYSObjectPathName wscst = getWorkstationCustomizationObject();

            PrintParameterList transformParameters = new PrintParameterList();
            transformParameters.setParameter(PrintObject.ATTR_WORKSTATION_CUST_OBJECT, wscst.getPath());
            transformParameters.setParameter(PrintObject.ATTR_MFGTYPE, "*WSCST");

            InputStream in = spooledFile.getTransformedInputStream(transformParameters);

            // TODO: check CcsidUtil and ebcdicAsciiMapping.txt
            int ccsid = spooledFile.getIntegerAttribute(PrintObject.ATTR_JOBCCSID).intValue();

            CcsidUtil util = new CcsidUtil();
            String ascii = util.getAsciiCodepage(ccsid);

            if (ascii != null) {
                reader = new BufferedReader(new InputStreamReader(in, ascii));
            } else {
                reader = new BufferedReader(new InputStreamReader(in));
            }

            openPrinter(target);
            initPrinter();

            String line;
            boolean isDelayedFormfeed = false;
            while ((line = reader.readLine()) != null) {
                if (isDelayedFormfeed) {
                    formfeed();
                    isDelayedFormfeed = false;
                }

                if (line.startsWith(FF)) {
                    line = line.substring(1);
                    formfeed();
                    line = handleDC1(line);
                    if (line.length() > 1) {
                        print(line);
                        newLine();
                    }
                } else if (line.endsWith(FF)) {
                    line = line.substring(0, line.length() - 1);
                    line = handleDC1(line);
                    if (line.length() > 1) {
                        print(line);
                        newLine();
                    }
                    // Delay FF until the next line printed.
                    isDelayedFormfeed = true;
                } else {
                    line = handleDC1(line);
                    print(line);
                    newLine();
                }
            }

            resetPrinter();

            cleanUp = true;

        } finally {
            if (reader != null) {
                reader.close();
            }

            closePrinter();
        }

        return cleanUp;
    }

    /**
     * Strips that special "fake" lines used by *SCS printer files for BOLD and
     * UNDERLINED printing.
     * 
     * @param line - current print data
     */
    protected String handleDC1(String line) {

        if (line.length() <= 0) {
            return line;
        }

        StringBuilder buffer = new StringBuilder();

        int start = 0;
        int end = line.indexOf(DC1, start);
        int linePartCount = 0;

        while ((end = line.indexOf(DC1, start)) >= 0) {

            linePartCount++;
            String linePart = StringHelper.trimR(line.substring(start, end));

            if (linePartCount == 1) {
                buffer.append(linePart);
            } else {

                if (linePart.length() == 0) {
                    // Ignore empty line parts
                } else if (isUnderlineDoublePrinting(linePart)) {
                    // Ignore underline printing
                } else {
                    // Perform double printing
                    int length = Math.min(buffer.length(), linePart.length());

                    // Overlay double printing characters
                    for (int offset = 0; offset < length; offset++) {
                        String linePartChar = linePart.substring(offset, offset + 1);
                        if (canReplaceLineChar(buffer, linePart, offset)) {
                            buffer.replace(offset, offset + 1, linePartChar);
                        }
                    }

                    // Append remaining double printing characters
                    if (length < linePart.length()) {
                        buffer.append(linePart.substring(length));
                    }
                }
            }

            start = start + linePart.length();
            start = start + 1; // DC1
            end = line.indexOf(DC1, start);
        }

        return buffer.toString();
    }

    /**
     * Checks, whether a given double printing line contains only underline
     * characters.
     * 
     * @param linePart - line that is checked for underline characters
     * @return <code>true</code> when the line contains only underline
     *         characters
     */
    private boolean isUnderlineDoublePrinting(String linePart) {

        if (linePart.trim().replaceAll(UNDERSCORE, "").length() == 0) {
            return true;
        }

        return false;
    }

    /**
     * Checks whether or not a character at a given position can be replaced or
     * not.
     * 
     * @param buffer - buffer that is checked
     * @param linePart - source of the new character
     * @param offset - offset of the old and new character
     * @return <code>true</code> when the character can be replaced else
     *         <code>false</code>.
     */
    private boolean canReplaceLineChar(StringBuilder buffer, String linePart, int offset) {

        // Replace spaces only
        if (!SPACE.equals(buffer.substring(offset, offset + 1))) {
            return false;
        }

        // Ignore spaces
        if (SPACE.equals(linePart.substring(offset, offset + 1))) {
            return false;
        }

        return true;
    }

    /**
     * This method returns the workstation customization object that is used by
     * this transformer for spooled file conversion.
     * 
     * @return workstation customization object
     */
    abstract protected QSYSObjectPathName getWorkstationCustomizationObject();

    /**
     * Opens the printer for writing.
     * <p>
     * This method should be used to open your virtual printer and to set up
     * basic requirements, such as creating an empty PDF document.
     * 
     * @param target - path name of the output file
     * @throws Exception
     */
    abstract protected void openPrinter(String target) throws Exception;

    /**
     * Closes the printer at the end of the task.
     * <p>
     * Used to close the printer and free resources.
     * 
     * @throws Exception
     */
    abstract protected void closePrinter() throws Exception;

    /**
     * Used to initialize the printer before the actual data stream is send to
     * it.
     * 
     * @throws Exception
     */
    abstract protected void initPrinter() throws Exception;

    /**
     * Resets the printer to its initial state.
     * 
     * @throws Exception
     */
    abstract protected void resetPrinter() throws Exception;

    /**
     * Starts a new page.
     * <p>
     * Is not used for the first page.
     * 
     * @throws Exception
     */
    abstract protected void formfeed() throws Exception;

    /**
     * Starts a new line.
     * 
     * @throws Exception
     */
    abstract protected void newLine() throws Exception;

    /**
     * Prints the actual print data.
     * 
     * @throws Exception
     */
    abstract protected void print(String text) throws Exception;
}
