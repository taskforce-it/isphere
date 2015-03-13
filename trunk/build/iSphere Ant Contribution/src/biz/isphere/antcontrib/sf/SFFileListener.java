package biz.isphere.antcontrib.sf;

public interface SFFileListener {

    public static final String CREATE = "CREATE";
    public static final String DELETE = "DELETE";
    public static final String IGNORED = "IGNORED";

    public void executingFileCommand(String command, String filename, String info);

}
