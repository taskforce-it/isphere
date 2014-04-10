/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;

public class Validator {

	private String type;
	private int length;
	private int precision;
	private boolean mandatory;
	private boolean restricted;
	private boolean generic;
	private ArrayList<String> arrayListSpecialValues;
	private char[] charactersName1 = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
									  'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '$', '§', '#'};
	private char[] charactersName2 = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
			  						  'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5',
									  '6', '7', '8', '9', '.', '_', '$', '§', '#'};
	private char[] charactersDec = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	private SimpleDateFormat dateFormat1;
	private SimpleDateFormat dateFormat2;
	private SimpleDateFormat timeFormat1;
	private SimpleDateFormat timeFormat2;
	private int integerValue;
	private long longValue;
	private String date;
	private String time;
	
	public Validator() {
		type = null;
		length = -1;
		precision = -1;
		mandatory = true;
		restricted = false;
		generic = false;
		arrayListSpecialValues = new ArrayList<String>();
		Arrays.sort(charactersName1);
		Arrays.sort(charactersName2);
		Arrays.sort(charactersDec);
		dateFormat1 = new SimpleDateFormat();
		dateFormat1.applyPattern("dd'.'MM'.'yyyy");
		dateFormat1.setLenient(false);
		dateFormat2 = new SimpleDateFormat();
		dateFormat2.applyPattern("ddMMyyyy");
		dateFormat2.setLenient(false);
		timeFormat1 = new SimpleDateFormat();
		timeFormat1.applyPattern("HH':'mm':'ss");
		timeFormat1.setLenient(false);
		timeFormat2 = new SimpleDateFormat();
		timeFormat2.applyPattern("HHmmss");
		timeFormat2.setLenient(false);
		integerValue = -1;
		longValue = -1;
		date = null;
		time = null;
	}
	
	public boolean setType(String type) {
		if (type.equals("*NAME") ||
			type.equals("*DEC") ||
			type.equals("*CHAR") ||
			type.equals("*DATE") ||
			type.equals("*TIME")) {
			this.type = type;
			return true;
		}
		this.type = null;
		return false;
	}

	public boolean setLength(int length) {
		if (length > 0) {
			this.length = length;
			return true;
		}
		this.length = -1;
		return false;
	}

	public boolean setPrecision(int precision) {
		if (type.equals("*DEC") && precision >= 0) {
			this.precision = precision;
			return true;
		}
		this.precision = -1;
		return false;
	}
	
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	
	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}
	
	public void setGeneric(boolean generic) {
		this.generic = generic;
	}
	
	public boolean addSpecialValue(String specialValue) {
		if (specialValue.equals("")) {
			return false;
		}
		for (int idx = 0; idx < arrayListSpecialValues.size(); idx++) {
			if (((String)arrayListSpecialValues.get(idx)).equals(specialValue)) {
				return false;
			}
		}
		arrayListSpecialValues.add(specialValue);
		return true;
	}
	
	public boolean validate(String argument) {
		integerValue = -1;
		longValue = -1;
		date = null;
		time = null;
		if (type == null || length == -1 || (type.equals("*DEC") && precision == -1) || (!type.equals("*CHAR") && restricted) || (!type.equals("*NAME") && generic)) {
			return false;
		}
		if (argument.equals("")) {
			if (mandatory || restricted) {
				return false;
			}
			else if (type.equals("*DEC")) {
				integerValue = 0;
				longValue = 0;
				return true;
			}
			else {
				return true;
			}
		}
		for (int idx = 0; idx < arrayListSpecialValues.size(); idx++) {
			if (((String)arrayListSpecialValues.get(idx)).equals(argument)) {
				return true;
			}
		}
		if (restricted) {
			return false;
		}
		if (type.equals("*NAME")) {
			if (generic && argument.endsWith("*")) {
				argument = argument.substring(0, argument.length() - 1);
				if (argument.equals("")) {
					return false;
				}
			}
			char character;
			for (int idx = 0; idx < argument.length(); idx++) {
				character = argument.charAt(idx);
				if (idx == 0 && Arrays.binarySearch(charactersName1, character) < 0) {
					return false;
				}
				if (idx > 0 && Arrays.binarySearch(charactersName2, character) < 0) {
					return false;
				}
			}
		}
		else if (type.equals("*DEC")) {
			char character;
			for (int idx = 0; idx < argument.length(); idx++) {
				character = argument.charAt(idx);
				if (Arrays.binarySearch(charactersDec, character) < 0) {
					return false;
				}
			}
			if (precision == 0) {
				try {
					integerValue = Integer.parseInt(argument);
				}
				catch (NumberFormatException e) {
					integerValue = 0;
				}
				try {
					longValue = Long.parseLong(argument);
				}
				catch (NumberFormatException e) {
					longValue = 0;
				}
			}
		}
		else if (type.equals("*DATE")) {
			try {
				dateFormat1.parse(argument);
				date = argument;
			} 
			catch (ParseException e1) {
				try {
					Date checkDate2 = dateFormat2.parse(argument);
					date = dateFormat1.format(checkDate2);
				} 
				catch (ParseException e2) {
					return false;
				}			
			}
		}
		else if (type.equals("*TIME")) {
			try {
				timeFormat1.parse(argument);
				time = argument;
			} 
			catch (ParseException e1) {
				try {
					Date checkTime2 = timeFormat2.parse(argument);
					time = timeFormat1.format(checkTime2);
				} 
				catch (ParseException e2) {
					return false;
				}			
			}
		}
		return true;
	}

	public int getIntegerValue() {
		return integerValue;
	}

	public long getLongValue() {
		return longValue;
	}
	
	public String getDateValue() {
		return date;
	}

	public String getTimeValue() {
		return time;
	}
	
	public static boolean validateFile(String file) {
        IStatus status = ResourcesPlugin.getWorkspace().validateName(file, IResource.FILE);
		return status.isOK();
	}
	
}
