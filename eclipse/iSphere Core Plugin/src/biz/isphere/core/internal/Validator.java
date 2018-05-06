/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;

import com.ibm.as400.access.AS400Text;

public class Validator {

    private static final String TYPE_NAME = "*NAME";
    private static final String TYPE_DEC = "*DEC";
    private static final String TYPE_CHAR = "*CHAR";
    private static final String TYPE_DATE = "*DATE";
    private static final String TYPE_TIME = "*TIME";

    private static final String EXTRA_CHARACTERS_CCSID_37 = "$@#";

    private Map<Integer, String> extraCharacters;

    private String type;
    private int length;
    private int precision;
    private boolean negativeValuesAllowed;
    private boolean mandatory;
    private boolean restricted;
    private boolean generic;
    private ArrayList<String> arrayListSpecialValues;
    private char[] charactersName1 = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
        'W', 'X', 'Y', 'Z' };
    private char[] charactersName2 = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
        'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '_' };
    private char[] charactersDec = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    private AS400Text as400Text37;
    private String extraCharactersCcsid;
    private SimpleDateFormat dateFormat1;
    private SimpleDateFormat dateFormat2;
    private SimpleDateFormat timeFormat1;
    private SimpleDateFormat timeFormat2;
    private int integerValue;
    private long longValue;
    private BigDecimal bigDecimal;
    private String date;
    private String time;

    public static Validator getMessageIdInstance() {
        Validator validator = new Validator(TYPE_NAME);
        validator.setLength(7);
        return validator;
    }

    public static Validator getNameInstance(Integer ccsid) {
        Validator validator = new Validator(TYPE_NAME, ccsid);
        validator.setLength(10);
        return validator;
    }

    public static Validator getLibraryNameInstance(Integer ccsid, String... specialValues) {
        Validator validator = new Validator(TYPE_NAME, ccsid);
        validator.setLength(10);
        validator.setRestricted(false);
        if (specialValues != null) {
            for (String specialValue : specialValues) {
                validator.addSpecialValue(specialValue);
            }
        }
        return validator;
    }

    public static Validator getIntegerInstance(int length) {
        Validator validator = new Validator(TYPE_DEC);
        validator.setLength(length);
        validator.setPrecision(0);
        return validator;
    }

    public static Validator getDecimalInstance(int length, int precision) {
        return getDecimalInstance(length, precision, false);
    }

    public static Validator getDecimalInstance(int length, int precision, boolean allowNegativeValues) {
        Validator validator = new Validator(TYPE_DEC);
        validator.setLength(length);
        validator.setPrecision(precision);
        validator.setAllowNegativeValues(allowNegativeValues);
        return validator;
    }

    public static Validator getCharInstance() {
        return new Validator(TYPE_CHAR);
    }

    public static Validator getDateInstance() {
        return new Validator(TYPE_DATE);
    }

    public static Validator getTimeInstance() {
        return new Validator(TYPE_TIME);
    }

    private Validator(String type) {
        this(type, null);
    }

    private Validator(String type, Integer ccsid) {
        checkAndSetType(type);
        length = -1;
        precision = -1;
        mandatory = true;
        restricted = false;
        generic = false;
        negativeValuesAllowed = false;
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
        extraCharactersCcsid = getExtraCharacters(ccsid);
    }

    private boolean canValidateNameCharacters() {

        if (extraCharactersCcsid != null) {
            return true;
        }

        return false;
    }

    private String getExtraCharacters(Integer ccsid) {

        if (ccsid == null) {
            return null;
        }

        if (ccsid.intValue() == 37) {
            return EXTRA_CHARACTERS_CCSID_37;
        }

        if (as400Text37 == null || ccsid.intValue() == 37) {
            as400Text37 = new AS400Text(EXTRA_CHARACTERS_CCSID_37.length(), 37);
        }

        if (extraCharacters == null) {
            extraCharacters = new HashMap<Integer, String>();
        }

        String extraCharactersCcsid = extraCharacters.get(ccsid);
        if (extraCharactersCcsid == null) {
            byte[] bytes = as400Text37.toBytes(EXTRA_CHARACTERS_CCSID_37);
            AS400Text textCcsid = new AS400Text(EXTRA_CHARACTERS_CCSID_37.length(), ccsid.intValue());
            extraCharactersCcsid = (String)textCcsid.toObject(bytes);
            extraCharacters.put(ccsid, extraCharactersCcsid);
        }

        return extraCharactersCcsid;
    }

    private void checkAndSetType(String type) {
        if (type.equals(TYPE_NAME) || type.equals(TYPE_DEC) || type.equals(TYPE_CHAR) || type.equals(TYPE_DATE) || type.equals(TYPE_TIME)) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type); //$NON-NLS-1$
        }
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
        if (type.equals(TYPE_DEC) && precision >= 0) {
            this.precision = precision;
            return true;
        }
        this.precision = -1;
        return false;
    }

    public boolean setAllowNegativeValues(boolean allowNegativeValues) {
        if (type.equals(TYPE_DEC)) {
            this.negativeValuesAllowed = allowNegativeValues;
            return true;
        }
        this.negativeValuesAllowed = false;
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
            if ((arrayListSpecialValues.get(idx)).equals(specialValue)) {
                return false;
            }
        }
        arrayListSpecialValues.add(specialValue);
        return true;
    }

    public boolean validate(String argument) {
        boolean isDecPos = false;
        int countLength = 0;
        int countDecPos = 0;
        int countComma = 0;

        integerValue = -1;
        longValue = -1;
        date = null;
        time = null;
        if (type == null || length == -1 || (type.equals(TYPE_DEC) && precision == -1) || (!type.equals(TYPE_CHAR) && restricted)
            || (!type.equals(TYPE_NAME) && generic)) {
            return false;
        }
        if (argument.equals("")) { //$NON-NLS-1$
            if (mandatory || restricted) {
                return false;
            } else if (type.equals(TYPE_DEC)) {
                integerValue = 0;
                longValue = 0;
                bigDecimal = new BigDecimal(0);
                return true;
            } else {
                return true;
            }
        }
        for (int idx = 0; idx < arrayListSpecialValues.size(); idx++) {
            if ((arrayListSpecialValues.get(idx)).equals(argument)) {
                return true;
            }
        }
        if (restricted) {
            return false;
        }
        if (type.equals(TYPE_NAME)) {
            if (generic && argument.endsWith("*")) { //$NON-NLS-1$
                argument = argument.substring(0, argument.length() - 1);
                if (argument.equals("")) {
                    return false;
                }
            }

            if (canValidateNameCharacters()) {
                char character;
                for (int idx = 0; idx < argument.length(); idx++) {
                    character = argument.charAt(idx);
                    if (idx == 0 && (Arrays.binarySearch(charactersName1, character) < 0 && !isExtraCharacter(argument.substring(idx, idx + 1)))) {
                        return false;
                    }
                    if (idx > 0 && (Arrays.binarySearch(charactersName2, character) < 0 && !isExtraCharacter(argument.substring(idx, idx + 1)))) {
                        return false;
                    }
                }
            }
        } else if (type.equals(TYPE_DEC)) {
            int maxLength = length;
            if (precision > 0) {
                maxLength++;
            }
            if (argument.length() > maxLength) {
                return false;
            }

            char character;
            for (int idx = 0; idx < argument.length(); idx++) {
                character = argument.charAt(idx);
                if (idx == 0 && (character == '+' || character == '-')) {
                    if (character == '-' && !negativeValuesAllowed) {
                        return false;
                    }
                    continue;
                }
                if (!(Character.isDigit(character) || character == '.')) {
                    return false;
                }
                if ((character == '.' && precision <= 0) && Arrays.binarySearch(charactersDec, character) < 0) {
                    return false;
                }
                if (character == '.') {
                    isDecPos = true;
                    countComma++;
                    if (countComma > 1 || countComma != 0 && precision == 0) {
                        return false;
                    }
                } else {
                    if (isDecPos) {
                        countDecPos++;
                        if (countDecPos > precision) {
                            return false;
                        }
                    } else {
                        countLength++;
                        if (countLength > length - precision) {
                            return false;
                        }
                    }
                }
            }
            if (precision == 0) {
                try {
                    integerValue = Integer.parseInt(argument);
                } catch (NumberFormatException e) {
                    integerValue = 0;
                }
                try {
                    longValue = Long.parseLong(argument);
                } catch (NumberFormatException e) {
                    longValue = 0;
                }
            } else {
                try {
                    bigDecimal = new BigDecimal(argument);
                } catch (NumberFormatException e) {
                    bigDecimal = null;
                }
            }
        } else if (type.equals(TYPE_DATE)) {
            try {
                dateFormat1.parse(argument);
                date = argument;
            } catch (ParseException e1) {
                try {
                    Date checkDate2 = dateFormat2.parse(argument);
                    date = dateFormat1.format(checkDate2);
                } catch (ParseException e2) {
                    return false;
                }
            }
        } else if (type.equals(TYPE_TIME)) {
            try {
                timeFormat1.parse(argument);
                time = argument;
            } catch (ParseException e1) {
                try {
                    Date checkTime2 = timeFormat2.parse(argument);
                    time = timeFormat1.format(checkTime2);
                } catch (ParseException e2) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isExtraCharacter(String character) {

        if (extraCharactersCcsid.indexOf(character) >= 0) {
            return true;
        }

        return false;
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

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public static boolean validateFile(String file) {
        IStatus status = ResourcesPlugin.getWorkspace().validateName(file, IResource.FILE);
        return status.isOK();
    }

}
