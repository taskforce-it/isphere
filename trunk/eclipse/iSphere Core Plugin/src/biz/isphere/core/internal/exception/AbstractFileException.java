package biz.isphere.core.internal.exception;

import java.io.File;

public abstract class AbstractFileException extends AbstractException {

    private static final long serialVersionUID = -6122103145415365894L;

    private File file;

    public AbstractFileException() {
        this(null, null, null, null);
    }

    public AbstractFileException(String text, String localizedText) {
        this(text, localizedText, null, null);
    }

    public AbstractFileException(String text, String localizedText, File file) {
        this(text, localizedText, file, null);
    }

    public AbstractFileException(String text, String localizedText, Throwable aCause) {
        this(text, localizedText, null, aCause);
    }

    public AbstractFileException(String text, String localizedText, File file, Throwable aCause) {
        super(getText(text, file), getText(localizedText, file), aCause);
    }

    public File getFile() {
        return file;
    }

    private static String getText(String text, File file) {
        return text + ": " + file.getAbsolutePath();
    }
}
