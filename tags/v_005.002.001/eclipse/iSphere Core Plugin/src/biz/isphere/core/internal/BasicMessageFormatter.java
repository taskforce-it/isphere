/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import biz.isphere.core.Messages;
import biz.isphere.core.messagefileeditor.MessageDescription;

public class BasicMessageFormatter {

    private int width = 80;

    private static final String FMT_CTRL_CHAR_N = "&N ";
    private static final String FMT_CTRL_CHAR_P = "&P ";
    private static final String FMT_CTRL_CHAR_B = "&B ";

    private static final String POSITION_2 = " "; // 1 space
    private static final String POSITION_4 = "   "; // 3 spaces
    private static final String POSITION_6 = "     "; // 5 spaces

    private static final String INDENT_DEFAULT = POSITION_2;
    private static final String INDENT_N = POSITION_2;
    private static final String INDENT_P = POSITION_6;
    private static final String INDENT_B = POSITION_4;

    private int lineLength;
    private String indent;

    private StringBuilder formatted;

    public String format(MessageDescription aMessageDescription) {
        String messageHelp = aMessageDescription.getHelpText();
        if (MessageDescription.VALUE_NONE.equals(messageHelp)) {
            messageHelp = "";
        }
        return format(aMessageDescription.getMessage(), messageHelp);
    }

    public String formatHelpText(String aMessageHelpText) {
        return format(aMessageHelpText);
    }

    protected String format(String aMessage) {
        if (aMessage == null) {
            return "";
        }

        indent = INDENT_DEFAULT;
        formatted = new StringBuilder();

        int p = 0;
        String tToken;
        lineLength = formatted.length();
        while ((tToken = getNextToken(aMessage, p)) != null) {
            addToken(tToken);
            p = p + tToken.length();
        }

        return formatted.toString();
    }

    private String getNextToken(String aMessage, int p) {
        if (p >= aMessage.length()) {
            return null; // end of message reached
        }

        int i;

        // include leading spaces
        if (p == 0 && aMessage.startsWith(" ")) {
            i = includeSpaces(aMessage, p);
            return aMessage.substring(0, i);
        } else {
            i = p + 1;
        }

        // search for space or ampersand as delimiter
        while (i < aMessage.length() && (!(" ".equals(aMessage.substring(i, i + 1)) || "&".equals(aMessage.substring(i, i + 1))))) {
            i++;
        }

        // include trailing spaces
        i = includeSpaces(aMessage, i);

        if (i >= aMessage.length()) {
            // last token
            return aMessage.substring(p);
        }

        return aMessage.substring(p, i);
    }

    private int includeSpaces(String aMessage, int i) {
        while (i < aMessage.length() && " ".equals(aMessage.substring(i, i + 1))) {
            i++;
        }
        return i;
    }

    private void addToken(String tToken) {

        if (FMT_CTRL_CHAR_N.equals(tToken)) {
            formatted.append("\n");
            formatted.append(INDENT_N);
            lineLength = INDENT_N.length();
            indent = POSITION_4;
        } else if (FMT_CTRL_CHAR_P.equals(tToken)) {
            formatted.append("\n");
            formatted.append(INDENT_P);
            lineLength = INDENT_P.length();
            indent = POSITION_4;
        } else if (FMT_CTRL_CHAR_B.equals(tToken)) {
            formatted.append("\n");
            formatted.append(INDENT_B);
            lineLength = INDENT_B.length();
            indent = POSITION_6;
        } else {
            if (lineLength + tToken.trim().length() > width) {
                formatted.append("\n");
                formatted.append(indent);
                lineLength = indent.length();
            }

            if (tToken.length() > 0) {
                if (formatted.length() == 0) {
                    formatted.append(indent);
                    lineLength += indent.length();
                    indent = POSITION_4;
                }
                formatted.append(tToken);
            }
            lineLength += tToken.length();
        }

    }

    public String format(String aText, String aHelp) {
        String tHelp = format(aHelp);
        String tNewLine;
        if (!tHelp.startsWith("\n")) {
            tNewLine = "\n";
        } else {
            tNewLine = "";
        }
        return format(Messages.MessageText_Message_Colon.trim() + "   " + aText) + tNewLine + tHelp;
    }

    public static void main(String[] args) {

        BasicMessageFormatter main = new BasicMessageFormatter() {
        };

        String tMessage;

        tMessage = "&N Cause . . . . . :   The application running requires a later version of CSP/AE. &N Recovery  . . . :   Contact the application developer to generate the application for the level of CSP/AE installed. Or, contact your system administrator to determine the correct level of CSP/AE required to run the generated application. &N Technical description . . . . . . . . :   Consult the Program Directory of your current CSP/AD product to determine the level of CSP/AE required to run the generated application.";
        System.out.println("[" + main.format(tMessage) + "]");

        System.out.println();

        tMessage = "&N Cause . . . . . :   A significant part of a number (not the decimal positions) is lost because the field to receive the number is too short. Application processing ends. &N Message CAE0021, issued when the application ended, gives the number of the statement causing the error. &N Recovery  . . . :   Contact the application developer to correct this problem. &N Technical description . . . . . . . . :   Change the application by increasing the size of the target data item to avoid overflow.  Or, have the application handle overflow conditions using the EZEOVER and EZEOVERS special function words.*NONE";
        System.out.println("[" + main.format(tMessage) + "]");

        System.out.println();

        tMessage = "   &N Cause . . . . . :   A significant part of a number (not the decimal positions) is lost because the field to receive the number is too short. Application processing ends. &N Message CAE0021, issued when the application ended, gives the number of the statement causing the error. &N Recovery  . . . :   Contact the application developer to correct this problem. &N Technical description . . . . . . . . :   Change the application by increasing the size of the target data item to avoid overflow.  Or, have the application handle overflow conditions using the EZEOVER and EZEOVERS special function words.*NONE   ";
        System.out.println("[" + main.format(tMessage) + "]");
    }

}
