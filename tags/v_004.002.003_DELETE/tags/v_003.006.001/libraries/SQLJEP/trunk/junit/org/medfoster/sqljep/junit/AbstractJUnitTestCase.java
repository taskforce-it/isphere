/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.junit;

import java.sql.Time;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.medfoster.sqljep.ASTFunNode;
import org.medfoster.sqljep.Node;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

public abstract class AbstractJUnitTestCase {

    private static final int JOCODE = 0;
    private static final int JOENTT = 1;
    private static final int JOJOB = 2;
    private static final int JOUSER = 3;
    private static final int JONBR = 4;
    private static final int JOLIB = 5;
    private static final int JOOBJ = 6;
    private static final int JOMBR = 7;
    private static final int JODATE = 8;
    private static final int JOTIME = 9;
    private static final int JOTIME2 = 10;
    private static final int JOTIME3 = 11;
    private static final int JOTSTP = 12;
    
    private static HashMap<String, Integer> columnMappings;
    static {
        columnMappings = new HashMap<String, Integer>();
        columnMappings.put("JOCODE", JOCODE);
        columnMappings.put("JOENTT", JOENTT);
        columnMappings.put("JOJOB", JOJOB);
        columnMappings.put("JOUSER", JOUSER);
        columnMappings.put("JONBR", JONBR);
        columnMappings.put("JOLIB", JOLIB);
        columnMappings.put("JOOBJ", JOOBJ);
        columnMappings.put("JOMBR", JOMBR);
        columnMappings.put("JODATE", JODATE);
        columnMappings.put("JOTIME", JOTIME);
        columnMappings.put("JOTIME2", JOTIME2);
        columnMappings.put("JOTIME3", JOTIME3);
        columnMappings.put("JOTSTP", JOTSTP);
    }

    private static HashMap<String, Integer> emptyColumnMappings;
    static {
        emptyColumnMappings = new HashMap<String, Integer>();
    }

    protected java.sql.Date getDate(int year, int month, int day) {

        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        return new java.sql.Date(calendar.getTimeInMillis());
    }

    protected java.sql.Timestamp getTimestamp(int year, int month, int day, int hour, int minute, int second) {
        return getTimestamp(year, month, day, hour, minute, second, -1);
    }

    protected java.sql.Timestamp getTimestamp(int year, int month, int day, int hour, int minute, int second, int millisecond) {

        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        if (millisecond >= 0) {
            calendar.set(Calendar.MILLISECOND, millisecond);
        }

        return new java.sql.Timestamp(calendar.getTimeInMillis());
    }

    protected static HashMap<String, Integer> getColumnMapping() {
        return columnMappings;
    }

    protected static HashMap<String, Integer> getEmptyColumnMapping() {
        return emptyColumnMappings;
    }

    protected Comparable<?>[] getRow() throws ParseException {

        Comparable<?>[] row = new Comparable[columnMappings.size()];

        row[JOCODE] = "R";
        row[JOENTT] = "PT";
        row[JOJOB] = "QZDASOINIT";
        row[JOUSER] = "DONALD";
        row[JONBR] = "123456";
        row[JOLIB] = "QGPL";
        row[JOOBJ] = "ITEMS";
        row[JOMBR] = "ITEMS";
        row[JODATE] = getDate(2018, 10, 22);
        row[JOTIME] = getTime(3, 5, "pm");
        row[JOTSTP] = getTimestamp(2018, 10, 22, 15, 5, 0);

        try {
            // Different mSecs
            row[JOTIME2] = getTime(15, 5, 30);
            Thread.sleep(15);
            row[JOTIME3] = getTime(15, 5, 30);
        } catch (InterruptedException e) {
        }

        return row;
    }

    protected java.sql.Time getTime(int hour, int minute, int second) {

        Calendar calendar = Calendar.getInstance();
        int mSecs = calendar.get(Calendar.MILLISECOND);
        calendar.clear();

        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, mSecs);

        return new Time(calendar.getTimeInMillis());
    }

    protected java.sql.Time getTime(int hour, int minute, String am_pm) {

        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        String[] ampm = new DateFormatSymbols().getAmPmStrings();
        for (int i = 0; i < ampm.length; i++) {
            if (ampm[i].equalsIgnoreCase(am_pm)) {
                calendar.set(Calendar.HOUR, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.AM_PM, i);
                return new Time(calendar.getTimeInMillis());
            }
        }

        throw new RuntimeException("AM/PM symbol not valid: " + ampm);
    }

    protected Date stripMilliSeconds(Date timeOrTime) {

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(timeOrTime);
        calendar.set(Calendar.MILLISECOND, 0);

        return new Time(calendar.getTimeInMillis());
    }

    protected static void printTree(Node topNode, int level) {
        System.out.println(spaces(level) + topNode.toString() + " (" + getClassName(topNode) + ")");

        for (int i = 0; i < topNode.jjtGetNumChildren(); i++) {
            printTree(topNode.jjtGetChild(i), level + 1);
        }
    }

    private static String getClassName(Node topNode) {

        if (topNode instanceof ASTFunNode) {
            return ((ASTFunNode)topNode).getPFMC().getClass().getSimpleName();
        } else {
            return topNode.getClass().getSimpleName();
        }
    }

    private static String spaces(int level) {

        StringBuilder buffer = new StringBuilder();

        while (level > 0) {
            buffer.append(" ");
            level--;
        }

        return buffer.toString();
    }

    protected boolean parseExpression(String expression) throws ParseException {
        return parseExpression(expression, getEmptyColumnMapping(), null);
    }

    protected boolean parseExpression(String expression, HashMap<String, Integer> columnMapping) throws ParseException {
        return parseExpression(expression, columnMapping, getRow());
    }

    protected boolean parseExpression(String expression, HashMap<String, Integer> columnMapping, Comparable<?>[] row) throws ParseException {

        RowJEP sqljep = new RowJEP(expression);
        sqljep.parseExpression(columnMapping);

        if (row == null) {
            return (Boolean)sqljep.getValue();
        } else {
            return (Boolean)sqljep.getValue(row);
        }
    }

}
