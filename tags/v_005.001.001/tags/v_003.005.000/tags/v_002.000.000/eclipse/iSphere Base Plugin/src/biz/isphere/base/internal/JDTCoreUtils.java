package biz.isphere.base.internal;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.JavaCore;

public final class JDTCoreUtils {

    public static String getJDTCoreKey(String key) {
        return getKey(JavaCore.PLUGIN_ID, key);
    }

    private static String getKey(String plugin, String key) {
        String pref = Platform.getPreferencesService().getString(plugin, key, "", null);
        return pref;
    }

}
