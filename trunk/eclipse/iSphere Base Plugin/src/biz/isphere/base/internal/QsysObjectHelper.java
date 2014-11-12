package biz.isphere.base.internal;

public final class QsysObjectHelper {

    public static String getAPIObjectType(String objectType) {
        if (objectType.startsWith("*")) { //$NON-NLS-1$
            return objectType.substring(1);
        }
        return objectType;
    }

}
