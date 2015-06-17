/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class HexFormatter {

    private static final String OFFSET_DELIMITER = ":   "; //$NON-NLS-1$
    private static final String HEX_PACKAGE_DELIMITER = " "; //$NON-NLS-1$
    private static final String TEXT_DELIMITER = "   - "; //$NON-NLS-1$
    private static final String NEW_LINE = "\n"; //$NON-NLS-1$
    private static final String DECIMAL_NUMBER_FORMAT = "00000"; //$NON-NLS-1$

    private int lineLength;
    private int blockLength;
    
    private DecimalFormat formatter;

    public HexFormatter() {
        this.lineLength = 32;
        this.blockLength = 8;
    }
    
    public String createFormattedHexText(byte[] messageBytes, String messageText) {
        
        int hexOffset = 0;
        int textOffset = 0;
        int hexLength = lineLength;
        int textLength = hexLength / 2;

        String hexString = ByteHelper.getHexString(messageBytes);
        String textString = messageText;
        StringBuilder formattedHexString = new StringBuilder();

        while (hexOffset < hexString.length()) {

            textOffset = hexOffset / 2;

            String tmpHexString;
            String tmpTextString;
            if ((hexOffset + hexLength) <= hexString.length()) {
                tmpHexString = hexString.substring(hexOffset, hexOffset + hexLength);
                tmpTextString = textString.substring(textOffset, textOffset + textLength);
            } else {
                tmpHexString = hexString.substring(hexOffset);
                tmpTextString = textString.substring(hexOffset / 2);
            }

            if (tmpHexString.length() < hexLength) {
                tmpHexString = StringHelper.getFixLength(tmpHexString, hexLength);
                tmpTextString = StringHelper.getFixLength(tmpTextString, textLength);
            }

            appendLine(formattedHexString, hexOffset, tmpTextString, tmpHexString);
            hexOffset = hexOffset + hexLength;
        }
        return formattedHexString.toString();
    }

    private void appendLine(StringBuilder formattedHexString, int offset, String textString, String hexString) {

        formattedHexString.append(getFormatter().format(offset));
        formattedHexString.append(OFFSET_DELIMITER);

        String packageDelimiter = null;
        int packageWidth = blockLength;
        int count = hexString.length() / packageWidth;
        int tempOffset = 0;
        while (count > 0) {

            if (packageDelimiter != null) {
                formattedHexString.append(packageDelimiter);
            }

            formattedHexString.append(hexString.substring(tempOffset, tempOffset + packageWidth));
            packageDelimiter = HEX_PACKAGE_DELIMITER;

            tempOffset = tempOffset + packageWidth;
            count--;
        }

        formattedHexString.append(TEXT_DELIMITER);
        formattedHexString.append(textString);
        formattedHexString.append(NEW_LINE);
    }

    private NumberFormat getFormatter() {
        if (formatter == null) {
            formatter = new DecimalFormat(DECIMAL_NUMBER_FORMAT);
        }
        return formatter;
    }
}
