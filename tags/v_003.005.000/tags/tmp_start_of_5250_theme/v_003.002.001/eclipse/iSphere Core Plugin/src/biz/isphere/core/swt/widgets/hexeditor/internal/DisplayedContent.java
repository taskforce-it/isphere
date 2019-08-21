/*******************************************************************************
 * javahexeditor, a java hex editor
 * Copyright (C) 2006-2015 Jordi Bergenthal, pestatije(-at_)users.sourceforge.net
 * The official javahexeditor site is sourceforge.net/projects/javahexeditor
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 * Contributors:
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/

package biz.isphere.core.swt.widgets.hexeditor.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;
import org.eclipse.swt.custom.TextChangingEvent;

/**
 * StyledTextContent customised for content that fills up to one page of the
 * StyledText widget. No line delimiters, content wraps lines.
 * 
 * @author Jordi
 */
public final class DisplayedContent implements StyledTextContent {

    private StringBuilder myData;
    private Set<TextChangeListener> myTextListeners;
    private int numberOfColumns = -1;
    private int linesTimesColumns = -1;

    /**
     * Create empty content for a StyledText of the specified size
     * 
     * @param numberOfLines
     * @param numberOfColumns
     */
    public DisplayedContent(int numberOfColumns, int numberOfLines) {
        // reserve space and account for replacements
        myData = new StringBuilder(numberOfColumns * numberOfLines * 2);
        myTextListeners = new HashSet<TextChangeListener>();
        setDimensions(numberOfColumns, numberOfLines);
    }

    public void addTextChangeListener(TextChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Parameter 'listener' must not be null."); //$NON-NLS-1$
        }
        myTextListeners.add(listener);
    }

    public int getCharCount() {
        return myData.length();
    }

    public String getLine(int lineIndex) {
        return getTextRange(lineIndex * numberOfColumns, numberOfColumns);
    }

    public int getLineAtOffset(int offset) {
        int result = offset / numberOfColumns;
        if (result >= getLineCount()){
            return getLineCount() - 1;
        }

        return result;
    }

    public int getLineCount() {
        return (myData.length() - 1) / numberOfColumns + 1;
    }

    public String getLineDelimiter() {
        return ""; //$NON-NLS-1$
    }

    public int getOffsetAtLine(int lineIndex) {
        return lineIndex * numberOfColumns;
    }

    public String getTextRange(int start, int length) {
        int dataLength = myData.length();
        if (start > dataLength) {
            return ""; //$NON-NLS-1$
        }

        return myData.substring(start, Math.min(dataLength, start + length));
    }

    public void removeTextChangeListener(TextChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Cannot remove a null listener"); //$NON-NLS-1$
        }

        myTextListeners.remove(listener);
    }

    /**
     * Replaces part of the content with new text. Works only when the new text
     * length is the same as replaceLength (when the content's size won't
     * change). For other cases use <code>setText()</code> or
     * <code>shiftLines()</code> instead.
     * 
     * @see org.eclipse.swt.custom.StyledTextContent#replaceTextRange(int, int,
     *      java.lang.String)
     */
    public void replaceTextRange(int start, int replaceLength, String text) {
        int length = text.length();
        if (length != replaceLength || start + length > myData.length()) {
            return;
        }

        myData.replace(start, start + length, text);
    }

    public void setDimensions(int columns, int lines) {
        numberOfColumns = columns;
        // numberOfLines = lines;
        linesTimesColumns = lines * columns;
        setText(myData.toString());
    }

    public void setText(String text) {
        myData.setLength(0);
        myData.append(text.substring(0, Math.min(text.length(), linesTimesColumns)));

        TextChangedEvent changedEvent = new TextChangedEvent(this);
        for (TextChangeListener listener : myTextListeners) {
            listener.textSet(changedEvent);
        }
    }

    /**
     * Shifts full lines of text and fills the new empty space with text
     * 
     * @param text to replace new empty lines. Its size determines the number of
     *        lines to shift
     * @param forward shifts lines either forward or backward
     */
    @SuppressWarnings("boxing")
    public void shiftLines(String text, boolean forward) {
        if (text.length() == 0) {
            return;
        }

        int linesInText = (text.length() - 1) / numberOfColumns + 1;
        int currentLimit = Math.min(myData.length(), linesTimesColumns);
        TextChangingEvent event = new TextChangingEvent(this);
        event.start = forward ? 0 : currentLimit;
        event.newText = text;
        event.replaceCharCount = 0;
        event.newCharCount = text.length();
        event.replaceLineCount = 0;
        event.newLineCount = linesInText;

        for (TextChangeListener listener : myTextListeners) {
            listener.textChanging(event);
        }
        myData.insert(event.start, text);
        // Log.log(this,
        // "Event 1: start={0}, newCharCount={1}, newLineCount={2}",
        // event.start, event.newCharCount, event.newLineCount);

        TextChangedEvent changedEvent = new TextChangedEvent(this);
        for (TextChangeListener listener : myTextListeners) {
            listener.textChanged(changedEvent);
        }

        event = new TextChangingEvent(this);
        event.start = forward ? linesTimesColumns - 1 : 0;
        event.newText = "";
        event.replaceCharCount = linesInText * numberOfColumns - linesTimesColumns + currentLimit;
        event.newCharCount = 0;
        event.replaceLineCount = linesInText;
        event.newLineCount = 0;
        for (TextChangeListener listener : myTextListeners) {
            listener.textChanging(event);
        }

        if (forward) {
            myData.delete(linesTimesColumns, linesTimesColumns + event.replaceCharCount);
        } else {
            myData.delete(0, event.replaceCharCount);
        }

        changedEvent = new TextChangedEvent(this);
        for (TextChangeListener listener : myTextListeners) {
            listener.textChanged(changedEvent);
        }
    }
}
