package biz.isphere.antcontrib.utils;

public final class FileUtil {

    public static String trimDirectory(String directory) {

        if (directory.endsWith("/") || directory.endsWith("\\")) {
            if (directory.length() >= 2) {
                return directory.substring(0, directory.length() - 1);
            } else {
                return "";
            }
        }

        return directory;
    }

}
