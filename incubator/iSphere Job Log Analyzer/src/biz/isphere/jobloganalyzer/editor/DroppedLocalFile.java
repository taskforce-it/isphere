package biz.isphere.jobloganalyzer.editor;

public class DroppedLocalFile {

    private String pathName;

    public DroppedLocalFile(String dropString) {

        int c = 1;
        int s = 0;
        int e = dropString.indexOf(":");
        while (e > s && c <= 2) {
            dropString.substring(s, e);
            switch (c) {
            case 1:
                // profile and connection names
                break;

            case 2:
                // subsystem
                break;

            default:
                break;
            }
            c++;
            s = e + 1;
            e = dropString.indexOf(":", s);
        }

        if (s < dropString.length()) {
            pathName = dropString.substring(s);
        }
    }

    public String getPathName() {
        return pathName;
    }

    @Override
    public String toString() {
        return pathName;
    }
}
