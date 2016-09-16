/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.wb.swt.SWTResourceManager;

import biz.isphere.core.ISpherePlugin;

public class ColorHelper {

    public static final String EDITOR_PROTECTED_AREAS_BACKGROUND = "biz.isphere.fonts.editors.protectedbackground";
    public static final String EDITOR_SELECTED_CONTROL_BACKGROUND = "biz.isphere.fonts.editors.selectedcontrol";
    public static final String WATCHED_ITEM_VALUE_CHANGED = "biz.isphere.fonts.watcheditem.valuechanged";

    private static Color COLOR_BACKGROUND_DEFAULT = null;

    public static Color getDefaultBackgroundColor() {
        if (COLOR_BACKGROUND_DEFAULT == null) {
            COLOR_BACKGROUND_DEFAULT = SWTResourceManager.getColor(255, 255, 255);
        }
        return COLOR_BACKGROUND_DEFAULT;
    }

    public static Color getBackgroundColorOfProtectedAreas() {
        ColorRegistry registry = ISpherePlugin.getDefault().getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
        return registry.get(EDITOR_PROTECTED_AREAS_BACKGROUND);
    }

    public static Color getBackgroundColorOfSelectedControls() {
        ColorRegistry registry = ISpherePlugin.getDefault().getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
        return registry.get(EDITOR_SELECTED_CONTROL_BACKGROUND);
    }

    public static Color getBackgroundColorOfChangedValues() {
        ColorRegistry registry = ISpherePlugin.getDefault().getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
        return registry.get(WATCHED_ITEM_VALUE_CHANGED);
    }
}
