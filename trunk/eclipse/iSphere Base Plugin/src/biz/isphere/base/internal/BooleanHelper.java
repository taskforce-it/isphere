package biz.isphere.base.internal;

public final class BooleanHelper {

    /**
     * Parses a Boolean value from a given text.
     * 
     * @param aSomeText Text representing a boolean value.
     * @return Boolean on success, else null.
     */
    public static Boolean tryParseBoolean(String aSomeText) {
        try {
            return Boolean.parseBoolean(aSomeText);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Parses a Boolean value from a given text.
     * 
     * @param aSomeText Text representing a boolean value.
     * @param aDefaultValue Default value that is returned on invalid input
     *        values.
     * @return Boolean on success, else the specified default value.
     */
    public static Boolean tryParseBoolean(String aSomeText, Boolean aDefaultValue) {
        Boolean tBoolean = tryParseBoolean(aSomeText);
        if (tBoolean == null) {
            return aDefaultValue;
        }
        return tBoolean;
    }

    /**
     * Parses a <code>boolean</code> value from a given text.
     * 
     * @param aSomeText Text representing a <code>boolean</code> value.
     * @param aDefaultValue Default value that is returned on invalid input
     *        values.
     * @return <code>boolean</code> value on success, else the specified default
     *         value.
     */
    public static Boolean tryParseBoolean(String aSomeText, boolean aDefaultValue) {
        Boolean tBoolean = tryParseBoolean(aSomeText);
        if (tBoolean == null) {
            return new Boolean(aDefaultValue);
        }
        return tBoolean;
    }

}
