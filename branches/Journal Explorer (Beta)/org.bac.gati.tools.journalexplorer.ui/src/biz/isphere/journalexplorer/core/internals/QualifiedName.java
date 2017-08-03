package biz.isphere.journalexplorer.core.internals;

public class QualifiedName {

    public static String getName(String library, String objectName) {
        return library.trim() + '/' + objectName.trim();
    }
}
