/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.point.IDateEdit;
import biz.isphere.core.swt.widgets.extension.point.ITimeEdit;

public class SpooledFileBaseFilterStringEditPane {

    private static final String[] SPECIAL_VALUES_USER = { "*", "*ALL", "*CURRENT" };
    private static final String[] SPECIAL_VALUES_OUTPUT_QUEUE = { "*", "*ALL" };
    private static final String[] SPECIAL_VALUES_OUTPUT_QUEUE_LIBRARY = { "*", "*CURLIB", "*LIBL" };
    private static final String[] SPECIAL_VALUES_USER_DATA = { "*", "*ALL" };
    private static final String[] SPECIAL_VALUES_FORM_TYPE = { "*", "*ALL", "*STD" };

    private Combo userText;
    private Combo outqText;
    private Combo outqLibText;
    private Combo userDataText;
    private Combo formTypeText;
    private Text nameText;
    private Combo startingDateCombo;
    private IDateEdit startingDateDateTime;
    private Combo startingTimeCombo;
    private ITimeEdit startingTimeDateTime;
    private Combo endingDateCombo;
    private IDateEdit endingDateDateTime;
    private Combo endingTimeCombo;
    private ITimeEdit endingTimeDateTime;

    public SpooledFileBaseFilterStringEditPane() {
    }

    public void createContents(Composite composite_prompts, final ModifyListener modifyListener, String inputFilterString) {

        new Label(composite_prompts, SWT.NONE).setText(Messages.User + ":");
        userText = WidgetFactory.createUpperCaseCombo(composite_prompts);
        userText.setItems(SPECIAL_VALUES_USER);
        userText.setLayoutData(createLayoutData(2));
        userText.setTextLimit(10);

        new Label(composite_prompts, SWT.NONE).setText(Messages.Output_queue + ":");
        outqText = WidgetFactory.createUpperCaseCombo(composite_prompts);
        outqText.setItems(SPECIAL_VALUES_OUTPUT_QUEUE);
        outqText.setLayoutData(createLayoutData(2));
        outqText.setTextLimit(10);
        outqText.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if ("*".equals(outqText.getText()) || "*ALL".equals(outqText.getText())) {
                    outqLibText.setText("*");
                }
            }
        });

        new Label(composite_prompts, SWT.NONE).setText(Messages.___Library + ":");
        outqLibText = WidgetFactory.createUpperCaseCombo(composite_prompts);
        outqLibText.setItems(SPECIAL_VALUES_OUTPUT_QUEUE_LIBRARY);
        outqLibText.setLayoutData(createLayoutData(2));
        outqLibText.setTextLimit(10);

        new Label(composite_prompts, SWT.NONE).setText(Messages.Spooled_file_name + ":");
        nameText = WidgetFactory.createUpperCaseText(composite_prompts);
        nameText.setLayoutData(createLayoutData(1));
        nameText.setTextLimit(10);
        new Label(composite_prompts, SWT.NONE).setText("*GENERIC*");

        new Label(composite_prompts, SWT.NONE).setText(Messages.User_data + ":");
        userDataText = WidgetFactory.createUpperCaseCombo(composite_prompts);
        userDataText.setItems(SPECIAL_VALUES_USER_DATA);
        userDataText.setLayoutData(createLayoutData(2));
        userDataText.setTextLimit(10);

        new Label(composite_prompts, SWT.NONE).setText(Messages.Form_type + ":");
        formTypeText = WidgetFactory.createUpperCaseCombo(composite_prompts);
        formTypeText.setItems(SPECIAL_VALUES_FORM_TYPE);
        formTypeText.setLayoutData(createLayoutData(2));
        formTypeText.setTextLimit(10);

        // From date and time

        new Label(composite_prompts, SWT.NONE).setText(Messages.From_date + ":");
        startingDateCombo = WidgetFactory.createReadOnlyCombo(composite_prompts);
        startingDateCombo.setItems(new String[] { "*", ISpooledFileFilter.EXACTLY, ISpooledFileFilter.TODAY, ISpooledFileFilter.YESTERDAY,
            ISpooledFileFilter.LASTWEEK, ISpooledFileFilter.LASTMONTH });
        startingDateCombo.setText(ISpooledFileFilter.EXACTLY);
        startingDateCombo.setLayoutData(createLayoutData(1));
        startingDateCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setControlEnablement();
            }
        });

        startingDateDateTime = WidgetFactory.createDateEdit(composite_prompts);
        startingDateDateTime.setLayoutData(createLayoutData(1));

        new Label(composite_prompts, SWT.NONE).setText(Messages.From_time + ":");
        startingTimeCombo = WidgetFactory.createReadOnlyCombo(composite_prompts);
        startingTimeCombo.setItems(new String[] { "*", ISpooledFileFilter.EXACTLY });
        startingTimeCombo.setText(ISpooledFileFilter.EXACTLY);
        startingTimeCombo.setLayoutData(createLayoutData(1));
        startingTimeCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setControlEnablement();
            }
        });

        startingTimeDateTime = WidgetFactory.createTimeEdit(composite_prompts);
        startingTimeDateTime.setLayoutData(createLayoutData(1));

        // To date and time

        new Label(composite_prompts, SWT.NONE).setText(Messages.To_date + ":");
        endingDateCombo = WidgetFactory.createReadOnlyCombo(composite_prompts);
        endingDateCombo.setItems(new String[] { "*", ISpooledFileFilter.EXACTLY, ISpooledFileFilter.TODAY });
        endingDateCombo.setText("*");
        endingDateCombo.setLayoutData(createLayoutData(1));
        endingDateCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setControlEnablement();
            }
        });

        endingDateDateTime = WidgetFactory.createDateEdit(composite_prompts);
        endingDateDateTime.setLayoutData(createLayoutData(1));

        new Label(composite_prompts, SWT.NONE).setText(Messages.To_time + ":");
        endingTimeCombo = WidgetFactory.createReadOnlyCombo(composite_prompts);
        endingTimeCombo.setItems(new String[] { "*", ISpooledFileFilter.EXACTLY });
        endingTimeCombo.setText("*");
        endingTimeCombo.setLayoutData(createLayoutData(1));
        endingTimeCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setControlEnablement();
            }
        });

        endingTimeDateTime = WidgetFactory.createTimeEdit(composite_prompts);
        endingTimeDateTime.setLayoutData(createLayoutData(1));

        resetFields();
        doInitializeFields(inputFilterString);

        userText.addModifyListener(modifyListener);
        outqText.addModifyListener(modifyListener);
        outqLibText.addModifyListener(modifyListener);
        userDataText.addModifyListener(modifyListener);
        formTypeText.addModifyListener(modifyListener);
        nameText.addModifyListener(modifyListener);
        startingDateCombo.addModifyListener(modifyListener);
        startingDateDateTime.addModifyListener(modifyListener);
        startingTimeCombo.addModifyListener(modifyListener);
        startingTimeDateTime.addModifyListener(modifyListener);
        endingDateCombo.addModifyListener(modifyListener);
        endingDateDateTime.addModifyListener(modifyListener);
        endingTimeCombo.addModifyListener(modifyListener);
        endingTimeDateTime.addModifyListener(modifyListener);
    }

    private void setControlEnablement() {

        /*
         * Enable/disable combo boxes
         */

        if (startingDateCombo.getText().equals(ISpooledFileFilter.ALL)) {
            startingTimeCombo.setEnabled(false);
            startingTimeCombo.setText(ISpooledFileFilter.ALL);
        } else if (startingDateCombo.getText().equals(ISpooledFileFilter.LASTWEEK)) {
            startingTimeCombo.setEnabled(false);
            startingTimeCombo.setText(ISpooledFileFilter.ALL);
        } else if (startingDateCombo.getText().equals(ISpooledFileFilter.LASTMONTH)) {
            startingTimeCombo.setEnabled(false);
            startingTimeCombo.setText(ISpooledFileFilter.ALL);
        } else {
            startingTimeCombo.setEnabled(true);
        }

        if (endingDateCombo.getText().equals(ISpooledFileFilter.ALL)) {
            endingTimeCombo.setEnabled(false);
            endingTimeCombo.setText(ISpooledFileFilter.ALL);
        } else {
            endingTimeCombo.setEnabled(true);
        }

        /*
         * Enable/disable date/time selectors
         */

        if (startingDateCombo.getText().equals(ISpooledFileFilter.EXACTLY)) {
            startingDateDateTime.setEnabled(true);
        } else {
            startingDateDateTime.setEnabled(false);
        }

        if (startingTimeCombo.getText().equals(ISpooledFileFilter.EXACTLY)) {
            startingTimeDateTime.setEnabled(true);
        } else {
            startingTimeDateTime.setEnabled(false);
        }

        if (endingDateCombo.getText().equals(ISpooledFileFilter.EXACTLY)) {
            endingDateDateTime.setEnabled(true);
        } else {
            endingDateDateTime.setEnabled(false);
        }

        if (endingTimeCombo.getText().equals(ISpooledFileFilter.EXACTLY)) {
            endingTimeDateTime.setEnabled(true);
        } else {
            endingTimeDateTime.setEnabled(false);
        }

        updateDateAndTimeValues();
    }

    private void updateDateAndTimeValues() {

        if (!ISpooledFileFilter.EXACTLY.equals(startingDateCombo.getText())) {
            setStartingDate(SpooledFileFilter.getStartingDateValue(startingDateCombo.getText()));
        }

        if (!ISpooledFileFilter.EXACTLY.equals(startingTimeCombo.getText())) {
            setStartingTime(SpooledFileFilter.getStartingTimeValue(startingTimeCombo.getText()));
        }

        if (!ISpooledFileFilter.EXACTLY.equals(endingDateCombo.getText())) {
            setEndingDate(SpooledFileFilter.getEndingDateValue(endingDateCombo.getText()));
        }

        if (!ISpooledFileFilter.EXACTLY.equals(endingTimeCombo.getText())) {
            setEndingTime(SpooledFileFilter.getEndingTimeValue(endingTimeCombo.getText()));
        }
    }

    private void setStartingDate(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        startingDateDateTime.setDate(year, month, day);
    }

    private void setStartingTime(Calendar calendar) {
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        startingTimeDateTime.setTime(hours, minutes, seconds);
    }

    private void setEndingDate(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        endingDateDateTime.setDate(year, month, day);
    }

    private void setEndingTime(Calendar calendar) {
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        endingTimeDateTime.setTime(hours, minutes, seconds);
    }

    private GridData createLayoutData(int numColumns) {
        GridData gd = new GridData();
        gd.widthHint = 75;
        gd.horizontalSpan = numColumns;
        return gd;
    }

    public Control getInitialFocusControl() {
        return userText;
    }

    public void doInitializeFields(String inputFilterString) {

        if (inputFilterString != null) {
            SpooledFileFilter filter = new SpooledFileFilter(inputFilterString);

            if (filter.getUser() != null) {
                userText.setText(filter.getUser());
            } else {
                userText.setText("*");
            }

            if (filter.getOutputQueue() != null) {
                outqText.setText(filter.getOutputQueue());
            } else {
                outqText.setText("*");
            }

            if (filter.getOutputQueueLibrary() != null) {
                outqLibText.setText(filter.getOutputQueueLibrary());
            } else {
                outqLibText.setText("*");
            }

            if (filter.getUserData() != null) {
                userDataText.setText(filter.getUserData());
            } else {
                userDataText.setText("*");
            }

            if (filter.getFormType() != null) {
                formTypeText.setText(filter.getFormType());
            } else {
                formTypeText.setText("*");
            }

            if (filter.getName() != null) {
                nameText.setText(filter.getName());
            } else {
                nameText.setText("*");
            }

            if (filter.getStartingDate() != null) {
                setDateControlValue(startingDateCombo, startingDateDateTime, filter.getStartingDate());
            } else {
                startingDateCombo.setText(ISpooledFileFilter.ALL);
            }

            if (filter.getStartingTime() != null) {
                setTimeControlValue(startingTimeCombo, startingTimeDateTime, filter.getStartingTime());
            } else {
                startingTimeCombo.setText(ISpooledFileFilter.ALL);
            }

            if (filter.getEndingDate() != null) {
                setDateControlValue(endingDateCombo, endingDateDateTime, filter.getEndingDate());
            } else {
                endingDateCombo.setText(ISpooledFileFilter.ALL);
            }

            if (filter.getEndingTime() != null) {
                setTimeControlValue(endingTimeCombo, endingTimeDateTime, filter.getEndingTime());
            } else {
                endingTimeCombo.setText(ISpooledFileFilter.ALL);
            }
        }

        setControlEnablement();
    }

    private void setDateControlValue(Combo combo, IDateEdit control, String value) {

        if (value != null) {
            if (!value.startsWith("*")) {
                combo.setText(ISpooledFileFilter.EXACTLY);
                setDate(control, value);
            } else {
                combo.setText(value);
            }
        } else {
            combo.setText(ISpooledFileFilter.ALL);
        }
    }

    private void setTimeControlValue(Combo combo, ITimeEdit control, String value) {

        if (value != null) {
            if (!value.startsWith("*")) {
                combo.setText(ISpooledFileFilter.EXACTLY);
                setTime(control, value);
            } else {
                combo.setText(value);
            }
        } else {
            combo.setText(ISpooledFileFilter.ALL);
        }
    }

    private void setDate(IDateEdit dateTime, String dateValue) {

        int year;
        int month;
        int day;

        try {
            year = IntHelper.tryParseInt(dateValue.substring(0, 4));
            month = IntHelper.tryParseInt(dateValue.substring(4, 6)) - 1;
            day = IntHelper.tryParseInt(dateValue.substring(6, 8));
            dateTime.setDate(year, month, day);
        } catch (Exception e) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            dateTime.setDate(year, month, day);
        }
    }

    private void setTime(ITimeEdit dateTime, String timeValue) {

        int hours = IntHelper.tryParseInt(timeValue.substring(0, 2));
        int minutes = IntHelper.tryParseInt(timeValue.substring(2, 4));
        int seconds = IntHelper.tryParseInt(timeValue.substring(4, 6));

        try {
            hours = IntHelper.tryParseInt(timeValue.substring(0, 2));
            minutes = IntHelper.tryParseInt(timeValue.substring(2, 4));
            seconds = IntHelper.tryParseInt(timeValue.substring(4, 6));
            dateTime.setTime(hours, minutes, seconds);
        } catch (Exception e) {
            hours = 12;
            minutes = 0;
            seconds = 0;
            dateTime.setTime(hours, minutes, seconds);
        }
    }

    public void resetFields() {
        userText.setText("*CURRENT");
        outqText.setText("*");
        outqLibText.setText("*");
        userDataText.setText("*");
        formTypeText.setText("*");
        nameText.setText("*");
        startingDateCombo.setText(ISpooledFileFilter.ALL);
        startingTimeCombo.setText(ISpooledFileFilter.ALL);
        endingDateCombo.setText(ISpooledFileFilter.ALL);
        endingTimeCombo.setText(ISpooledFileFilter.ALL);
    }

    public boolean areFieldsComplete() {
        String error = validateInput();
        if (error == null) {
            return true;
        }
        return false;
    }

    public String validateInput() {

        String error;

        error = validateSimple(Messages.User, userText.getText(), SPECIAL_VALUES_USER);
        if (error != null) {
            return error;
        }

        error = validateOutputQueue();
        if (error != null) {
            return error;
        }

        error = validateSpooledFileName();
        if (error != null) {
            return error;
        }

        error = validateSimple(Messages.User_data, userDataText.getText(), SPECIAL_VALUES_USER_DATA);
        if (error != null) {
            return error;
        }

        error = validateSimple(Messages.Form_type, formTypeText.getText(), SPECIAL_VALUES_FORM_TYPE);
        if (error != null) {
            return error;
        }

        error = validateStartAndEndDates();
        if (error != null) {
            return error;
        }

        return null;
    }

    private String validateSimple(String fieldName, String text, String... specialValues) {

        if (text.trim().length() == 0) {
            return Messages.bind(Messages.The_value_in_field_A_is_not_valid, fieldName);
        }

        for (String specialValue : specialValues) {
            if (text.equalsIgnoreCase(specialValue)) {
                return null;
            }
        }

        if (text.indexOf("*") >= 0) {
            return Messages.bind(Messages.The_value_in_field_A_is_not_valid, fieldName);
        }

        return null;
    }

    private String validateOutputQueue() {

        String error = validateSimple(Messages.Output_queue, outqText.getText(), SPECIAL_VALUES_OUTPUT_QUEUE);
        if (error != null) {
            return error;
        }

        error = validateSimple(Messages.___Library, outqLibText.getText(), SPECIAL_VALUES_OUTPUT_QUEUE_LIBRARY);
        if (error != null) {
            return error;
        }

        if ("*".equals(outqText.getText()) || "*ALL".equals(outqText.getText())) {
            if (!"*".equals(outqLibText.getText())) {
                return Messages.bind(Messages.The_value_in_field_A_is_not_valid, Messages.___Library);
            }
        }

        return null;
    }

    private String validateSpooledFileName() {

        if (nameText.getText().trim().length() == 0) {
            return Messages.bind(Messages.The_value_in_field_A_is_not_valid, Messages.Spooled_file_name);
        }

        String spooledFileName = nameText.getText();
        if (spooledFileName.equals("*")) {
            return null;
        }

        if (spooledFileName.startsWith("*") && spooledFileName.endsWith("*")) {
            if (spooledFileName.length() < 3) {
                nameText.setFocus();
                return Messages.bind(Messages.The_value_in_field_A_is_not_valid, Messages.Spooled_file_name);
            }
            spooledFileName = spooledFileName.substring(1, spooledFileName.length() - 1);
            if (StringHelper.isNullOrEmpty(spooledFileName.trim())) {
                nameText.setFocus();
                return Messages.bind(Messages.The_value_in_field_A_is_not_valid, Messages.Spooled_file_name);
            }
        }

        return null;
    }

    private String validateStartAndEndDates() {

        int startDate = IntHelper.tryParseInt(getDate(startingDateDateTime), -1);
        int startTime = IntHelper.tryParseInt(getTime(startingTimeDateTime), -1);

        int endDate = IntHelper.tryParseInt(getDate(endingDateDateTime), -1);
        int endTime = IntHelper.tryParseInt(getTime(endingTimeDateTime), -1);

        if (startDate < 0) {
            return Messages.bind(Messages.Valid_of_field_a_is_invalid, "" + Messages.From_date);
        }

        if (startTime < 0) {
            return Messages.bind(Messages.Valid_of_field_a_is_invalid, "" + Messages.From_time);
        }

        if (endDate < 0) {
            return Messages.bind(Messages.Valid_of_field_a_is_invalid, "" + Messages.To_date);
        }

        if (endTime < 0) {
            return Messages.bind(Messages.Valid_of_field_a_is_invalid, "" + Messages.To_time);
        }

        if (startDate > endDate) {
            return Messages.End_date_and_time_must_be_greater_than_from_date_and_time;
        } else {
            if (startTime > endTime) {
                return Messages.End_date_and_time_must_be_greater_than_from_date_and_time;
            }
        }

        return null;
    }

    public String getFilterString() {

        SpooledFileFilter filter = new SpooledFileFilter();

        if (isValidFilterValue(userText.getText())) {
            filter.setUser(userText.getText().toUpperCase());
        }

        if (isValidFilterValue(outqText.getText())) {
            filter.setOutputQueue(outqText.getText().toUpperCase());
        }

        if (isValidFilterValue(outqLibText.getText())) {
            filter.setOutputQueueLibrary(outqLibText.getText().toUpperCase());
        }

        if (isValidFilterValue(userDataText.getText())) {
            filter.setUserData(userDataText.getText().toUpperCase());
        }

        if (isValidFilterValue(formTypeText.getText())) {
            filter.setFormType(formTypeText.getText().toUpperCase());
        }

        if (isValidFilterValue(nameText.getText())) {
            filter.setName(nameText.getText().toUpperCase());
        }

        if (isValidFilterValue(startingDateCombo.getText())) {
            if (ISpooledFileFilter.EXACTLY.equals(startingDateCombo.getText())) {
                filter.setStartingDate(getDate(startingDateDateTime));
            } else {
                filter.setStartingDate(startingDateCombo.getText());
            }
        }

        if (isValidFilterValue(startingTimeCombo.getText())) {
            if (ISpooledFileFilter.EXACTLY.equals(startingTimeCombo.getText())) {
                filter.setStartingTime(getTime(startingTimeDateTime));
            } else {
                filter.setStartingTime(startingTimeCombo.getText());
            }
        }

        if (isValidFilterValue(endingDateCombo.getText())) {
            if (ISpooledFileFilter.EXACTLY.equals(endingDateCombo.getText())) {
                filter.setEndingDate(getDate(endingDateDateTime));
            } else {
                filter.setEndingDate(endingDateCombo.getText());
            }
        }

        if (isValidFilterValue(endingTimeCombo.getText())) {
            if (ISpooledFileFilter.EXACTLY.equals(endingTimeCombo.getText())) {
                filter.setEndingTime(getTime(endingTimeDateTime));
            } else {
                filter.setEndingTime(endingTimeCombo.getText());
            }
        }

        return filter.getFilterString();
    }

    private String getDate(IDateEdit dateTime) {

        StringBuilder buffer = new StringBuilder();
        buffer.append(StringHelper.getFixLengthLeading(Integer.toString(dateTime.getYear()), 4).replaceAll(" ", "0"));
        buffer.append(StringHelper.getFixLengthLeading(Integer.toString(dateTime.getMonth() + 1), 2).replaceAll(" ", "0"));
        buffer.append(StringHelper.getFixLengthLeading(Integer.toString(dateTime.getDay()), 2).replaceAll(" ", "0"));

        return buffer.toString();
    }

    private String getTime(ITimeEdit dateTime) {

        StringBuilder buffer = new StringBuilder();
        buffer.append(StringHelper.getFixLengthLeading(Integer.toString(dateTime.getHours()), 2).replaceAll(" ", "0"));
        buffer.append(StringHelper.getFixLengthLeading(Integer.toString(dateTime.getMinutes()), 2).replaceAll(" ", "0"));
        buffer.append(StringHelper.getFixLengthLeading(Integer.toString(dateTime.getSeconds()), 2).replaceAll(" ", "0"));

        return buffer.toString();
    }

    private boolean isValidFilterValue(String text) {
        if ((text != null) && (text.length() > 0) && (!text.equals("*"))) {
            return true;
        }
        return false;
    }
}
