package biz.isphere.core.internal;

import biz.isphere.core.Messages;

public abstract class AbstractMessageFormatter {

    private int width = 79;

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

    public String format(String aMessage) {
        if (aMessage == null) {
            return "";
        }

        String tIndent = INDENT_DEFAULT;
        StringBuilder tFormatted = new StringBuilder();
        int tLineLength = tFormatted.length();

        String[] tWords = aMessage.split("(?<=[\\s])");
        for (String tWord : tWords) {
            if (FMT_CTRL_CHAR_N.equals(tWord)) {
                tFormatted.append("\n");
                tFormatted.append(INDENT_N);
                tLineLength = tIndent.length();
                tIndent = POSITION_4;
            } else if (FMT_CTRL_CHAR_P.equals(tWord)) {
                tFormatted.append("\n");
                tFormatted.append(INDENT_P);
                tLineLength = tIndent.length();
                tIndent = POSITION_4;
            } else if (FMT_CTRL_CHAR_B.equals(tWord)) {
                tFormatted.append("\n");
                tFormatted.append(INDENT_B);
                tLineLength = tIndent.length();
                tIndent = POSITION_6;
            } else {
                if (tLineLength + tWord.length() > width) {
                    tFormatted.append("\n");
                    tFormatted.append(tIndent);
                    tLineLength = tIndent.length();
                }

                if (tWord.length() > 0) {
                    if (tFormatted.length() == 0) {
                        tFormatted.append(tIndent);
                        tLineLength += tIndent.length();
                        tIndent = POSITION_4;
                    }
                    tFormatted.append(tWord);
                }
                tLineLength += tWord.length();
            }

        }

        return tFormatted.toString();
    }

    protected String format(String aText, String aHelp) {
        String tHelp = format(aHelp);
        String tNewLine;
        if (!tHelp.startsWith("\n")) {
            tNewLine = "\n\n";
        } else {
            tNewLine = "\n";
        }
        return format(Messages.MessageText_Message_Colon + "   " + aText) + tNewLine + tHelp;
    }

    public static void main(String[] args) {
        AbstractMessageFormatter main = new AbstractMessageFormatter() {};
        String tMessage = "&N Cause . . . . . :   The application running requires a later version of CSP/AE. &N Recovery  . . . :   Contact the application developer to generate the application for the level of CSP/AE installed. Or, contact your system administrator to determine the correct level of CSP/AE required to run the generated application. &N Technical description . . . . . . . . :   Consult the Program Directory of your current CSP/AD product to determine the level of CSP/AE required to run the generated application.";
        System.out.println(main.format(tMessage));
    }

}
