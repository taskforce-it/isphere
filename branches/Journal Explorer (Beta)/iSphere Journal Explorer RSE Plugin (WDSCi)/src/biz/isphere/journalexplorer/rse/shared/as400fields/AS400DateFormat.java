package biz.isphere.journalexplorer.rse.shared.as400fields;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public enum AS400DateFormat {
    MDY (0, "MDY", '/', "MM|dd|yy", 2),
    DMY (1, "DMY", '/', "dd|MM|yy", 2),
    YMD (2, "YMD", '/', "yy|MM|dd", 2),
    JUL (3, "JUL", '/', "yy|DDD", 2),
    ISO (4, "ISO", '-', "yyyy|MM|dd", 4),
    USA (5, "USA", '/', "MM|dd|yyyy", 4),
    EUR (6, "EUR", '.', "dd|MM|yyyy", 4),
    JIS (7, "JIS", '-', "yyyy|MM|dd", 4),
    CYMD (8, "CYMD", '/', "yy|MM|dd", 3),
    CMDY (9, "CMDY", '/', "MM|dd|yy", 3),
    CDMY (10, "CDMY", '/', "dd|MM|yy", 3),
    LONGJUL (11, "LONGJUL", '/', "yyyy|DDD", 4);

    public static final int FORMAT_MDY = MDY.index;
    public static final int FORMAT_DMY = DMY.index;
    public static final int FORMAT_YMD = YMD.index;
    public static final int FORMAT_JUL = JUL.index;
    public static final int FORMAT_ISO = ISO.index;
    public static final int FORMAT_USA = USA.index;
    public static final int FORMAT_EUR = EUR.index;
    public static final int FORMAT_JIS = JIS.index;
    public static final int FORMAT_CYMD = CYMD.index;
    public static final int FORMAT_CMDY = CMDY.index;
    public static final int FORMAT_CDMY = CDMY.index;
    public static final int FORMAT_LONGJUL = LONGJUL.index;

    public static final String LITERAL_MDY = MDY.rpgLiteral;
    public static final String LITERAL_DMY = DMY.rpgLiteral;
    public static final String LITERAL_YMD = YMD.rpgLiteral;
    public static final String LITERAL_JUL = JUL.rpgLiteral;
    public static final String LITERAL_ISO = ISO.rpgLiteral;
    public static final String LITERAL_USA = USA.rpgLiteral;
    public static final String LITERAL_EUR = EUR.rpgLiteral;
    public static final String LITERAL_JIS = JIS.rpgLiteral;
    public static final String LITERAL_CYMD = CYMD.rpgLiteral;
    public static final String LITERAL_CMDY = CMDY.rpgLiteral;
    public static final String LITERAL_CDMY = CDMY.rpgLiteral;
    public static final String LITERAL_LONGJUL = LONGJUL.rpgLiteral;

    private int index;
    private String rpgLiteral;
    private Character baseDelimiter;
    private String basePattern;
    private int numYearDigits;

    private AS400DateFormat(int index, String label, Character delimiter, String basePattern, int numYearDigits) {
        this.index = index;
        this.rpgLiteral = label;
        this.baseDelimiter = delimiter;
        this.basePattern = basePattern;
        this.numYearDigits = numYearDigits;
    }

    public int index() {
        return index;
    }

    public String rpgLiteral() {
        return rpgLiteral;
    }

    public Character delimiter() {
        return baseDelimiter;
    }

    public boolean is2DigitYearFormat() {
        return numYearDigits == 2;
    }

    public boolean is3DigitYearFormat() {
        return numYearDigits == 3;
    }

    public boolean is4DigitYearFormat() {
        return numYearDigits == 4;
    }

    public Date get2DigitYearFormatStartDate(String date, TimeZone timeZone, Character separator) {
        int century = get2DigitYearCentury(date, timeZone, separator);
        return getCalendar(timeZone, century);
    }

    private int get2DigitYearCentury(String date, TimeZone timeZone, Character separator) {

        int i = basePattern.indexOf("yy");
        if (i > 0) {
            if (separator == null) {
                i = i - 2;
            }
        }

        int year = Integer.parseInt(date.substring(i, i + 2));

        Calendar calendar = GregorianCalendar.getInstance(timeZone);
        int yyyy = calendar.get(Calendar.YEAR);

        if (yyyy < 1900) {
            throw new IllegalArgumentException("Date is out of range: " + date);
        }

        int century = (yyyy / 100) - 19;
        if (year >= 40) {
            century--;
        }

        return century;
    }

    public Date get3DigitYearFormatStartDate(String date, TimeZone timeZone) {
        int century = Integer.parseInt(date.substring(0, 1));
        return getCalendar(timeZone, century);
    }

    private Date getCalendar(TimeZone timeZone, int century) {

        Calendar calendar = GregorianCalendar.getInstance(timeZone);
        calendar.set((19 + century) * 100, 0, 1, 0, 0, 0);

        return calendar.getTime();
    }

    public SimpleDateFormat getFormatter(TimeZone timeZone, Character delimiter) {

        SimpleDateFormat formatter;

        if (delimiter != null) {
            formatter = new SimpleDateFormat(basePattern.replaceAll("\\|", delimiter.toString()));
        } else {
            formatter = new SimpleDateFormat(basePattern.replaceAll("\\|", ""));
        }

        formatter.setTimeZone(timeZone);

        return formatter;
    }
}
