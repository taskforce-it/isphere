/*******************************************************************************
 * Copyright (c) project_year-2018 project_team
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

import org.medfoster.sqljep.ASTFunNode;
import org.medfoster.sqljep.Node;

public abstract class AbstractJUnitTestCase {

	protected java.sql.Date getDate(int year, int month, int day) {

		Calendar calendar = Calendar.getInstance();
		int mSecs = calendar.get(Calendar.MILLISECOND);
		calendar.clear();

		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MILLISECOND, mSecs);

		return new java.sql.Date(calendar.getTimeInMillis());
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
		calendar.setTime((Date)timeOrTime);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return new Time(calendar.getTimeInMillis());
	}

	protected static void printTree(Node topNode, int level) {
		System.out.println(spaces(level) + topNode.toString() + " ("
				+ getClassName(topNode) + ")");

		for (int i = 0; i < topNode.jjtGetNumChildren(); i++) {
			printTree(topNode.jjtGetChild(i), level + 1);
		}
	}

	private static String getClassName(Node topNode) {

		if (topNode instanceof ASTFunNode) {
			return ((ASTFunNode) topNode).getPFMC().getClass().getSimpleName();
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

}
