package biz.isphere.antcontrib.sf;

public class SFException extends Exception {

    private static final long serialVersionUID = 6599141787050301995L;

    public SFException(String message) {
        super(message);
    }

    public SFException(String message, Throwable e) {
        super(message, e);
    }

}
