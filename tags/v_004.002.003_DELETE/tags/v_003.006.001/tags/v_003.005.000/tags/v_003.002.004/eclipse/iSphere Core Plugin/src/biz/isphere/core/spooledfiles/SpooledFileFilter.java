/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import biz.isphere.base.internal.IntHelper;

public class SpooledFileFilter {

    public static void main(String[] args) {

        // Xystem.out.println(getMaxEndingDate().getTime());

    }

    private static final int MIN_STARTING_DATE_YEAR = 1954;
    private static final int MIN_STARTING_DATE_MONTH = 1;
    private static final int MIN_STARTING_DATE_DAY = 1;
    private static final int MIN_STARTING_DATE = 19540101;

    private static final int MIN_STARTING_TIME_HOURS = 0;
    private static final int MIN_STARTING_TIME_MINUTES = 0;
    private static final int MIN_STARTING_TIME_SECONDS = 0;
    private static final int MIN_STARTING_TIME = 0;

    private static final int MAX_ENDING_DATE_YEAR = 2053;
    private static final int MAX_ENDING_DATE_MONTH = 12;
    private static final int MAX_ENDING_DATE_DAY = 31;
    private static final int MAX_ENDING_DATE = 20531231;

    private static final int MAX_ENDING_TIME_HOURS = 23;
    private static final int MAX_ENDING_TIME_MINUTES = 59;
    private static final int MAX_ENDING_TIME_SECONDS = 59;
    private static final int MAX_ENDING_TIME = 235959;

    private String user;
    private String name;
    private String outputQueue;
    private String outputQueueLibrary;
    private String userData;
    private String formType;
    private String startingDate;
    private String startingTime;
    private String endingDate;
    private String endingTime;

    public static Calendar getStartingDateValue(String dateTypeLiteral) {
        return getDateValue(dateTypeLiteral, true);
    }

    public static Calendar getEndingDateValue(String dateTypeLiteral) {
        return getDateValue(dateTypeLiteral, false);
    }

    public static Calendar getStartingTimeValue(String timeTypeLiteral) {
        return getTimeValue(timeTypeLiteral, true);
    }

    public static Calendar getEndingTimeValue(String timeTypeLiteral) {
        return getTimeValue(timeTypeLiteral, false);
    }

    public static Calendar getMinStartingDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(MIN_STARTING_DATE_YEAR, MIN_STARTING_DATE_MONTH - 1, MIN_STARTING_DATE_DAY, MIN_STARTING_TIME_HOURS, MIN_STARTING_TIME_MINUTES,
            MIN_STARTING_TIME_SECONDS);
        return calendar;
    }

    public static Calendar getMaxEndingDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(MAX_ENDING_DATE_YEAR, MAX_ENDING_DATE_MONTH - 1, MAX_ENDING_DATE_DAY, MAX_ENDING_TIME_HOURS, MAX_ENDING_TIME_MINUTES,
            MAX_ENDING_TIME_SECONDS);
        return calendar;
    }

    private static Calendar getDateValue(String dateTypeLiteral, boolean isStartingDate) {

        Calendar calendar = GregorianCalendar.getInstance();

        if (ISpooledFileFilter.ALL.equals(dateTypeLiteral)) {
            if (isStartingDate) {
                calendar = getMinStartingDate();
            } else {
                calendar = getMaxEndingDate();
            }
        } else if (ISpooledFileFilter.TODAY.equals(dateTypeLiteral)) {
            // already set
        } else if (ISpooledFileFilter.YESTERDAY.equals(dateTypeLiteral)) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        } else if (ISpooledFileFilter.LASTWEEK.equals(dateTypeLiteral)) {
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        } else if (ISpooledFileFilter.LASTMONTH.equals(dateTypeLiteral)) {
            calendar.add(Calendar.MONTH, -1);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (isStartingDate) {
            calendar.set(year, month, day, 0, 0, 0);
        } else {
            calendar.set(year, month, day, 0, 0, 0);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.SECOND, -1);
        }

        return calendar;
    }

    private static Calendar getTimeValue(String timeTypeLiteral, boolean isStartingDate) {

        Calendar calendar = null;

        if (ISpooledFileFilter.ALL.equals(timeTypeLiteral)) {
            if (isStartingDate) {
                calendar = getMinStartingDate();
            } else {
                calendar = getMaxEndingDate();
            }
        }

        return calendar;
    }

    public SpooledFileFilter() {
        super();
    }

    public SpooledFileFilter(String filterString) {
        this();
        setFilters(filterString);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutputQueue() {
        return outputQueue;
    }

    public void setOutputQueue(String outputQueue) {
        this.outputQueue = outputQueue;
    }

    public String getOutputQueueLibrary() {
        return outputQueueLibrary;
    }

    public void setOutputQueueLibrary(String outputQueueLibrary) {
        this.outputQueueLibrary = outputQueueLibrary;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(String endingTime) {
        this.endingTime = endingTime;
    }

    public int getStartingDateIntValue() {

        if (getStartingDate() == null || ISpooledFileFilter.ALL.equals(getStartingDate())) {
            return dateToInt(getMinStartingDate());
        }

        if (!startingDate.startsWith("*")) {
            return IntHelper.tryParseInt(startingDate, MIN_STARTING_DATE);
        }

        return dateToInt(getStartingDateValue(startingDate));
    }

    public int getStartingTimeIntValue() {

        if (getStartingTime() == null || ISpooledFileFilter.ALL.equals(getStartingTime())) {
            Calendar calendar = getMinStartingDate();
            return timeToInt(calendar);
        }

        if (!startingTime.startsWith("*")) {
            return IntHelper.tryParseInt(startingTime, MIN_STARTING_TIME);
        }

        return timeToInt(getStartingTimeValue(startingTime));
    }

    public int getEndingDateIntValue() {

        if (getEndingDate() == null || ISpooledFileFilter.ALL.equals(getEndingDate())) {
            return dateToInt(getMaxEndingDate());
        }

        if (!endingDate.startsWith("*")) {
            return IntHelper.tryParseInt(endingDate, MAX_ENDING_DATE);
        }

        return dateToInt(getEndingDateValue(endingDate));
    }

    public int getEndingTimeIntValue() {

        if (getEndingTime() == null || ISpooledFileFilter.ALL.equals(getEndingTime())) {
            Calendar calendar = getMaxEndingDate();
            return timeToInt(calendar);
        }

        if (!endingTime.startsWith("*")) {
            return IntHelper.tryParseInt(endingTime, MAX_ENDING_TIME);
        }

        return timeToInt(getEndingTimeValue(endingTime));
    }

    private int dateToInt(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return IntHelper.tryParseInt(formatter.format(calendar.getTime()), MIN_STARTING_DATE);
    }

    private int timeToInt(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
        return IntHelper.tryParseInt(formatter.format(calendar.getTime()), MIN_STARTING_DATE);
    }

    public String getFilterString() {

        StringBuffer filterString = new StringBuffer();

        if (user == null) {
            filterString.append("*/");
        } else {
            filterString.append(user + "/");
        }

        if (outputQueue == null) {
            filterString.append("*/");
        } else {
            filterString.append(outputQueue + "/");
        }

        if (outputQueueLibrary == null) {
            filterString.append("*/");
        } else {
            filterString.append(outputQueueLibrary + "/");
        }

        if (userData == null) {
            filterString.append("*/");
        } else {
            filterString.append(userData + "/");
        }

        if (formType == null) {
            filterString.append("*/");
        } else {
            filterString.append(formType + "/");
        }

        if (name == null) {
            filterString.append("*/");
        } else {
            filterString.append(name + "/");
        }

        if (startingDate == null) {
            filterString.append("*/");
        } else {
            filterString.append(startingDate + "/");
        }

        if (startingTime == null) {
            filterString.append("*/");
        } else {
            filterString.append(startingTime + "/");
        }

        if (endingDate == null) {
            filterString.append("*/");
        } else {
            filterString.append(endingDate + "/");
        }

        if (endingTime == null) {
            filterString.append("*/");
        } else {
            filterString.append(endingTime + "/");
        }

        return filterString.toString();
    }

    private void setFilters(String filterString) {

        int index;
        String[] values = filterString.split("/");

        index = 0; // User
        if (values.length > index && !values[index].equals("*")) {
            setUser(values[index]);
        }

        index++; // Output Queue
        if (values.length > index && !values[index].equals("*")) {
            setOutputQueue(values[index]);
        }

        index++; // Output Queue Library
        if (values.length > index && !values[index].equals("*")) {
            setOutputQueueLibrary(values[index]);
        }

        index++; // User Data
        if (values.length > index && !values[index].equals("*")) {
            setUserData(values[index]);
        }

        index++; // Form Type
        if (values.length > index && !values[index].equals("*")) {
            setFormType(values[index]);
        }

        index++; // Name
        if (values.length > index && !values[index].equals("*")) {
            setName(values[index]);
        }

        index++; // Starting date
        if (values.length > index && !values[index].equals("*")) {
            setStartingDate(values[index]);
        }

        index++; // Starting time
        if (values.length > index && !values[index].equals("*")) {
            setStartingTime(values[index]);
        }

        index++; // Ending date
        if (values.length > index && !values[index].equals("*")) {
            setEndingDate(values[index]);
        }

        index++; // Ending time
        if (values.length > index && !values[index].equals("*")) {
            setEndingTime(values[index]);
        }
    }
}
