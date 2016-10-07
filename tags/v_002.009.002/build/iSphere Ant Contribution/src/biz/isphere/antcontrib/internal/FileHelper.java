package biz.isphere.antcontrib.internal;

public final class FileHelper {

    public static boolean hasFileExtension(String fileName) {

        String extension = getFileExtension(fileName);
        if (extension == null) {
            return false;
        }

        return true;
    }

    public static String getBareFileName(String fileName) {
        
        String extension = getFileExtension(fileName);
        if (extension == null) {
            return fileName;
        }
        
        return fileName.substring(0, fileName.length() - extension.length() - 1);
    }

    public static String replaceFileExtension(String fileName, String extension) {
        return getBareFileName(fileName) + "." + extension;
    }

    public static String getFileExtension(String fileName) {
        String extension = null;
        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        if (extension != null && extension.length() == 0) {
            return null;
        }
        return extension;
    }

}
