package org.tn5250j.tools;

import java.awt.Toolkit;
import java.text.Format;
import java.text.ParseException;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class FormattedDocument extends PlainDocument {
    private static final long serialVersionUID = 1L;
    private Format format;

    public FormattedDocument(Format f) {
        format = f;
    }

    public Format getFormat() {
        return format;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

        String currentText = getText(0, getLength());
        String beforeOffset = currentText.substring(0, offs);
        String afterOffset = currentText.substring(offs, currentText.length());
        String proposedResult = beforeOffset + str + afterOffset;

        try {
            format.parseObject(proposedResult);
            super.insertString(offs, str, a);
        } catch (ParseException e) {
            Toolkit.getDefaultToolkit().beep();
            System.err.println("insertString: could not parse: " + proposedResult);
        }
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        String currentText = getText(0, getLength());
        String beforeOffset = currentText.substring(0, offs);
        String afterOffset = currentText.substring(len + offs, currentText.length());
        String proposedResult = beforeOffset + afterOffset;

        try {
            if (proposedResult.length() != 0) format.parseObject(proposedResult);
            super.remove(offs, len);
        } catch (ParseException e) {
            Toolkit.getDefaultToolkit().beep();
            System.err.println("remove: could not parse: " + proposedResult);
        }
    }
}
