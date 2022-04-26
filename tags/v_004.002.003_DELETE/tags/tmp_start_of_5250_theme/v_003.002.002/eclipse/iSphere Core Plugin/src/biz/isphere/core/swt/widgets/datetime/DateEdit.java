/*******************************************************************************
 * Copyright (c) 2004, 2006 Plum Canary Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Plum Canary Corporation - initial API and implementation
 *     iSphere Project Team
 *******************************************************************************/

package biz.isphere.core.swt.widgets.datetime;

import java.text.AttributedCharacterIterator;
import java.text.DateFormat.Field;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.ColorHelper;
import biz.isphere.core.internal.FontHelper;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetHelper;
import biz.isphere.core.swt.widgets.extension.point.IDateEdit;

/**
 * is an editor for the date in a text format. The editor uses Apache's
 * {@link FastDateFormat} to obtain default system date format. So the editor is
 * locale sensible. During editing user may use up/down arrow keys to
 * increment/decrement the selected date field or enter date explicitly by
 * typing digits.
 * 
 * @author Alexey Afanasyev
 * @author Alexey Kharlamov
 */
public final class DateEdit extends Composite implements IDateEdit {

    private static final int MARGIN_BOTTOM = 2;

    private static final int MARGIN_LEFT = 2;

    private static final int MARGIN_RIGHT = 2;

    private static final int MARGIN_TOP = 2;

    private static final Calendar MAX_DATE_TIME;

    private static final Calendar MIN_DATE_TIME;

    static {
        MIN_DATE_TIME = Calendar.getInstance();
        MIN_DATE_TIME.set(Calendar.MILLISECOND, 0);
        MIN_DATE_TIME.set(Calendar.SECOND, 0);
        MIN_DATE_TIME.set(Calendar.MINUTE, 0);
        MIN_DATE_TIME.set(Calendar.HOUR_OF_DAY, 0);
        MIN_DATE_TIME.set(Calendar.DATE, 1);
        MIN_DATE_TIME.set(Calendar.MONTH, Calendar.JANUARY);
        MIN_DATE_TIME.set(Calendar.YEAR, 1752);

        MAX_DATE_TIME = Calendar.getInstance();
        MAX_DATE_TIME.set(Calendar.MILLISECOND, 0);
        MAX_DATE_TIME.set(Calendar.SECOND, 59);
        MAX_DATE_TIME.set(Calendar.MINUTE, 59);
        MAX_DATE_TIME.set(Calendar.HOUR_OF_DAY, 23);
        MAX_DATE_TIME.set(Calendar.DATE, 31);
        MAX_DATE_TIME.set(Calendar.MONTH, Calendar.DECEMBER);
        MAX_DATE_TIME.set(Calendar.YEAR, 9999);
    }

    private static int getMaximum(Calendar calendar, int field) {
        assert calendar != null : "Argument \"calendar\" cannot be null"; //$NON-NLS-1$

        if (field == Calendar.YEAR) {
            return MAX_DATE_TIME.get(field);
        }

        return calendar.getActualMaximum(field);
    }

    private static int getMinimum(Calendar calendar, int field) {
        assert calendar != null : "Argument \"calendar\" cannot be null"; //$NON-NLS-1$

        if (field == Calendar.YEAR) {
            return MIN_DATE_TIME.get(field);
        }

        return calendar.getActualMinimum(field);
    }

    private List<Field> fields = new ArrayList<Field>();

    private SimpleDateFormat dateTimeFormat;

    private Calendar selectedDateTime = Calendar.getInstance();

    private Field selectedField;

    private boolean typing;

    /**
     * creates an instance of the {@link DateEdit} for default {@link Locale}.
     * 
     * @param parent the parent composite.
     * @param style the style.
     */
    public DateEdit(Composite parent, int style) {
        this(parent, style, Preferences.getInstance().getDateFormatter());
    }

    /**
     * creates an instance of the {@link DateEdit}.
     * 
     * @param parent the parent composite.
     * @param style the style.
     * @param locale the locale used to obtain date format.
     */
    public DateEdit(Composite parent, int style, String pattern) {
        this(parent, style, new SimpleDateFormat(pattern));
    }

    public DateEdit(Composite parent, int style, SimpleDateFormat format) {
        super(parent, style | SWT.NO_BACKGROUND);
        initFields(format);

        setBackground(ColorHelper.getListBackground());
        setForeground(ColorHelper.getListForeground());

        WidgetHelper.hook(this, new int[] { SWT.FocusIn, SWT.FocusOut, SWT.KeyDown, SWT.MouseDown, SWT.MouseEnter, SWT.MouseExit, SWT.Paint,
            SWT.Traverse, }, new Listener() {
            /*
             * (non-Javadoc)
             * @see
             * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.
             * widgets.Event)
             */
            public void handleEvent(Event event) {
                switch (event.type) {
                case SWT.FocusIn:
                case SWT.FocusOut:
                    redraw();
                    break;
                case SWT.KeyDown:
                    onKeyDown(event);
                    break;
                case SWT.MouseDown:
                    onMouseDown(event);
                    break;
                case SWT.Paint:
                    onPaint(event);
                    break;
                case SWT.Traverse:
                    onTraverse(event);
                    break;
                }
            }
        });
    }

    /**
     * Adds the listener to the collection of listeners who will be notified
     * when the receiver's text is modified, by sending it one of the messages
     * defined in the <code>ModifyListener</code> interface.
     * 
     * @param listener the listener which should be notified
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *            thread that created the receiver</li>
     *            </ul>
     * @see ModifyListener
     * @see #removeModifyListener
     */
    public void addModifyListener(ModifyListener listener) {
        checkWidget();
        if (listener == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        TypedListener typedListener = new TypedListener(listener);
        addListener(SWT.Modify, typedListener);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
     */
    public Point computeSize(int wHint, int hHint, boolean changed) {
        if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
            return new Point(wHint, hHint);
        }

        Point dateSize = FontHelper.getTextExtent(this, dateTimeFormat.format(MAX_DATE_TIME.getTime()), getFont());
        Rectangle trim = computeTrim(0, 0, dateSize.x + MARGIN_LEFT + MARGIN_RIGHT, dateSize.y + MARGIN_TOP + MARGIN_BOTTOM);
        Point size = new Point(trim.width, trim.height);

        if (wHint != SWT.DEFAULT) {
            size.x = wHint;
        }
        if (hHint != SWT.DEFAULT) {
            size.y = hHint;
        }

        return size;
    }

    /**
     * the current date in the control.
     * 
     * @return the date or null.
     */
    public Calendar getDate() {
        if (selectedDateTime == null) {
            return null;
        }
        return (Calendar)selectedDateTime.clone();
    }

    /**
     * the current year in the control.
     * 
     * @return the year or -1.
     */
    public int getYear() {
        if (selectedDateTime == null) {
            return -1;
        }
        return selectedDateTime.get(Calendar.YEAR);
    }

    /**
     * the current month in the control, January = 0, December = 11.
     * 
     * @return the month or -1.
     */
    public int getMonth() {
        if (selectedDateTime == null) {
            return -1;
        }
        return selectedDateTime.get(Calendar.MONTH);
    }

    /**
     * the current day in the control.
     * 
     * @return the day or -1.
     */
    public int getDay() {
        if (selectedDateTime == null) {
            return -1;
        }
        return selectedDateTime.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Removes the listener from the collection of listeners who will be
     * notified when the receiver's text is modified.
     * 
     * @param listener the listener which should no longer be notified
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *            thread that created the receiver</li>
     *            </ul>
     * @see ModifyListener
     * @see #addModifyListener
     */
    public void removeModifyListener(ModifyListener listener) {
        checkWidget();
        if (listener == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        removeListener(SWT.Modify, listener);
    }

    /**
     * sets date into the calendar.
     * 
     * @param date the date value or null to reset editor.
     */
    public void setDate(Calendar date) {
        if (ObjectUtils.equals(date, selectedDateTime)) {
            return;
        }

        if (date != null) {
            selectedDateTime = (Calendar)date.clone();
        } else {
            selectedDateTime = null;
        }

        redraw();

        notifyListeners(SWT.Modify, new Event());
    }

    /**
     * Sets the values for the calendar fields YEAR, MONTH, and DAY_OF_MONTH.
     * Previous values of other calendar fields are retained. If this is not
     * desired, call clear() first.
     * 
     * @param year - the value used to set the YEAR calendar field.
     * @param month - the value used to set the MONTH calendar field. Month
     *        value is 0-based. e.g., 0 for January.
     * @param day - the value used to set the DAY_OF_MONTH calendar field.
     */
    public void setDate(int year, int month, int day) {
        Calendar date = (Calendar)MIN_DATE_TIME.clone();
        date.set(year, month, day);
        setDate(date);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        redraw();
    }

    private void decrementFieldValue() {

        setFocus();

        if (selectedDateTime != null) {
            selectedDateTime.roll(selectedField.getCalendarField(), false);
            if (selectedDateTime.before(MIN_DATE_TIME)) {
                selectedDateTime.setTimeInMillis(MIN_DATE_TIME.getTimeInMillis());
            }
        } else {
            selectedDateTime = Calendar.getInstance();
        }

        redraw();

        notifyListeners(SWT.Modify, new Event());
    }

    private Field getField(AttributedCharacterIterator it) {
        assert it != null : "Argument \"it\" cannot be null"; //$NON-NLS-1$

        Field field = (Field)it.getAttribute(Field.DAY_OF_MONTH);
        if (field == null) {
            field = (Field)it.getAttribute(Field.MONTH);
            if (field == null) {
                field = (Field)it.getAttribute(Field.YEAR);
            }
        }
        return field;
    }

    private void incrementFieldValue() {

        setFocus();

        if (selectedDateTime != null) {
            selectedDateTime.roll(selectedField.getCalendarField(), true);
            if (selectedDateTime.after(MAX_DATE_TIME)) {
                selectedDateTime.setTimeInMillis(MAX_DATE_TIME.getTimeInMillis());
            }
        } else {
            selectedDateTime = Calendar.getInstance();
        }

        redraw();

        notifyListeners(SWT.Modify, new Event());
    }

    private void initFields(SimpleDateFormat fmt) {
        this.dateTimeFormat = fmt;
        Calendar calendar = Calendar.getInstance();

        AttributedCharacterIterator it = dateTimeFormat.formatToCharacterIterator(calendar.getTime());

        StringBuffer pattern = new StringBuffer();

        for (int i = it.getBeginIndex(); i < it.getEndIndex(); i = it.getRunLimit()) {
            it.setIndex(i);

            Field field = (Field)getField(it);

            if (field == Field.DAY_OF_MONTH) {
                pattern.append("dd"); //$NON-NLS-1$
            } else if (field == Field.MONTH) {
                pattern.append("MM"); //$NON-NLS-1$
            } else if (field == Field.YEAR) {
                pattern.append("yyyy"); //$NON-NLS-1$
            } else if (field == null) {
                pattern.append(it.current());
                continue;
            } else {
                ISpherePlugin.logError(MessageFormat.format("*** The field \"{0}\" not supported ***", new Object[] { field.toString() }), null); //$NON-NLS-1$
            }

            fields.add(field);
        }

        dateTimeFormat.applyPattern(pattern.toString());
        selectField((Field)fields.get(0));
    }

    private void nextField() {
        int index = fields.indexOf(selectedField);
        if (index < fields.size() - 1) {
            index++;
        } else {
            index = 0;
        }

        selectField((Field)fields.get(index));
    }

    private void onDigitTyped(int digit) {
        if (selectedDateTime != null) {
            if (digit == 0 && !typing) {
                return;
            }

            int field = selectedField.getCalendarField();

            int oldValue = selectedDateTime.get(field);

            int newValue = digit;
            if (selectedField != Field.MONTH) {
                if (typing) {
                    newValue += oldValue * 10;
                }
            } else {
                if (typing) {
                    newValue += (oldValue + 1) * 10;
                }
                newValue--;
            }

            if (newValue < getMinimum(selectedDateTime, field) || newValue > getMaximum(selectedDateTime, field)) {
                typing = false;

                onDigitTyped(digit);

                return;
            }

            typing = true;

            selectedDateTime.roll(field, newValue - oldValue);
        } else {
            selectedDateTime = Calendar.getInstance();
        }

        redraw();

        notifyListeners(SWT.Modify, new Event());
    }

    private void onKeyDown(Event event) {
        int stateMask = event.stateMask;
        if (stateMask == SWT.ALT) {
            return;
        }

        if (event.character == SWT.ESC) {
            setDate(null);
        } else if (Character.isDigit(event.character)) {
            onDigitTyped(event.character - '0');
        } else {
            switch (event.character) {
            case '.':
            case '/':
            case ',':
                nextField();
                break;
            case '+':
                incrementFieldValue();
                break;
            case '-':
                decrementFieldValue();
                break;
            default:
                switch (event.keyCode) {
                case SWT.ARROW_LEFT:
                    previousField();
                    break;
                case SWT.ARROW_RIGHT:
                    nextField();
                    break;
                case SWT.ARROW_UP:
                    incrementFieldValue();
                    break;
                case SWT.ARROW_DOWN:
                    decrementFieldValue();
                    break;
                }
            }
        }
    }

    private void onMouseDown(Event event) {
        Rectangle clientArea = getClientArea();

        Point location = new Point(clientArea.x, clientArea.y);
        location.x += MARGIN_LEFT;
        location.y += MARGIN_TOP;

        GC gc = new GC(this);

        Calendar calendar;
        if (selectedDateTime != null) {
            calendar = selectedDateTime;
        } else {
            calendar = Calendar.getInstance();
        }

        AttributedCharacterIterator it = dateTimeFormat.formatToCharacterIterator(calendar.getTime());

        try {
            for (int i = it.getBeginIndex(); i < it.getEndIndex(); i = it.getRunLimit()) {
                it.setIndex(i);

                Field field = getField(it);

                for (int j = i; j < it.getRunLimit(); j++) {
                    it.setIndex(j);

                    Point extent = gc.textExtent("" + it.current());

                    Rectangle rect = new Rectangle(location.x, location.y, extent.x, extent.y);
                    if (field != null && rect.contains(event.x, event.y)) {
                        selectField(field);
                        return;
                    }

                    location.x += extent.x;
                }
            }
        } finally {
            gc.dispose();
        }
    }

    private void onPaint(Event event) {
        Rectangle clientArea = getClientArea();
        if (clientArea.width == 0 || clientArea.height == 0) {
            return;
        }

        Image image = new Image(getDisplay(), clientArea.width, clientArea.height);

        GC gc = new GC(image);

        Color background = getBackground();
        Color foreground = getForeground();

        if (!getEnabled() && SystemUtils.IS_OS_WINDOWS) {
            background = ColorHelper.getWidgetBackground();
            foreground = ColorHelper.getBlack();
        }

        gc.setBackground(background);
        gc.setForeground(foreground);

        gc.fillRectangle(image.getBounds());

        Font font = getFont();

        gc.setFont(font);

        boolean dateSelected = selectedDateTime != null;

        Calendar calendar;
        if (dateSelected) {
            calendar = selectedDateTime;
        } else {
            calendar = Calendar.getInstance();
        }

        int x = MARGIN_LEFT;
        int y = MARGIN_TOP;

        AttributedCharacterIterator it = dateTimeFormat.formatToCharacterIterator(calendar.getTime());

        for (int i = it.getBeginIndex(); i < it.getEndIndex(); i = it.getRunLimit()) {
            it.setIndex(i);

            Field field = getField(it);

            if (isFocusControl() && field == selectedField) {
                gc.setBackground(ColorHelper.getListSelection());
                gc.setForeground(ColorHelper.getListSelectionText());
            } else {
                gc.setBackground(background);
                gc.setForeground(foreground);
            }

            for (int j = i; j < it.getRunLimit(); j++) {
                it.setIndex(j);

                String character = "" + it.current();

                Point extent = gc.textExtent(character);

                if (dateSelected || field == null) {
                    gc.drawText(character, x, y);
                } else {
                    gc.fillRectangle(x, y, extent.x, extent.y);
                }

                x += extent.x;
            }
        }

        event.gc.drawImage(image, clientArea.x, clientArea.y);

        gc.dispose();
        image.dispose();
    }

    private void onTraverse(Event event) {
        switch (event.detail) {
        case SWT.TRAVERSE_ESCAPE:
        case SWT.TRAVERSE_RETURN:
        case SWT.TRAVERSE_TAB_NEXT:
        case SWT.TRAVERSE_TAB_PREVIOUS:
            event.doit = true;
            break;
        default:
            event.doit = false;
            break;
        }
    }

    private void previousField() {
        int index = fields.indexOf(selectedField);
        if (index > 0) {
            index--;
        } else {
            index = fields.size() - 1;
        }

        selectField((Field)fields.get(index));
    }

    private void selectField(Field field) {
        if (ObjectUtils.equals(field, selectedField)) {
            return;
        }

        selectedField = field;

        typing = false;

        redraw();
    }
}
