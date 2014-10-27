package biz.isphere.core.internal;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.Color;

import biz.isphere.core.ISpherePlugin;

public class ColorHelper {
    
    public static final String EDITOR_PROTECTED_AREAS_BACKGROUND = "biz.isphere.fonts.editors.protectedbackground";
    
    public static Color getBackgroundColorOfProtectedAreas() {
        ColorRegistry registry = ISpherePlugin.getDefault().getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
        return registry.get(EDITOR_PROTECTED_AREAS_BACKGROUND);
    }

}
