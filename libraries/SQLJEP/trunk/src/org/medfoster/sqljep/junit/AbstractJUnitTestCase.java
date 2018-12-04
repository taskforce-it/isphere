/*******************************************************************************
 * Copyright (c) project_year-2018 project_team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.junit;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.medfoster.sqljep.ASTFunNode;
import org.medfoster.sqljep.Node;

public abstract class AbstractJUnitTestCase {

	protected Date getDate(int year, int month, int day) {

		Calendar calendar = Calendar.getInstance();
		calendar.clear();

		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);

		return calendar.getTime();
	}

	protected Date getTime(int hour, int minute, int second) {

		Calendar calendar = Calendar.getInstance();
		calendar.clear();

		calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);

		return new Date(calendar.getTimeInMillis());
	}

	protected Date getTime(int hour, int minute, String am_pm) {

		Calendar calendar = Calendar.getInstance();
		calendar.clear();

		String[] ampm = new DateFormatSymbols().getAmPmStrings();
		for (int i = 0; i < ampm.length; i++) {
			if (ampm[i].equalsIgnoreCase(am_pm)) {
				calendar.set(Calendar.HOUR, hour);
				calendar.set(Calendar.MINUTE, minute);
				calendar.set(Calendar.AM_PM, i);
				return new Date(calendar.getTimeInMillis());
			}
		}

		throw new RuntimeException("AM/PM symbol not valid: " + ampm);
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
