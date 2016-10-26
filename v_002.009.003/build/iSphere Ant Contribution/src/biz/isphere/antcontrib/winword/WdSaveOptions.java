package biz.isphere.antcontrib.winword;

/**
 * This class defines the WdSaveOptions enumeration of Microsoft Word.
 * 
 * @see <a
 *      href="http://msdn.microsoft.com/en-us/library/microsoft.office.interop.word.wdsaveoptions%28v=office.15%29.aspx"
 *      >WdSaveOptions enumeration</a>
 * @author Thomas Raddatz
 */
public enum WdSaveOptions {

    /**
     * Do not save pending changes.
     */
    DO_NOT_SAVE_CHANGES (0),

    /**
     * Save pending changes automatically without prompting the user.
     */
    SAVE_CHANGES (-1),

    /**
     * Prompt the user to save pending changes.
     */
    PROMPT_TO_SAVE_CHANGES (-2);

    private final int value;

    private WdSaveOptions(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}