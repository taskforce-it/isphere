/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;

import biz.isphere.core.ISpherePlugin;

public final class FontHelper {

    public static final String EDITOR_FIXED_SIZE = "biz.isphere.fonts.editors.fixedsize";

    public static Font getFixedSizeFont() {
        FontRegistry registry = ISpherePlugin.getDefault().getWorkbench().getThemeManager().getCurrentTheme().getFontRegistry();
        return registry.get(EDITOR_FIXED_SIZE);
    }

    /*
     * http://www.java2s.com/Tutorial/Java/0300__SWT-2D-Graphics/
     * UsingFontMetricstogetcharwidth.htm (FontMetricsCharWidth)
     */
    public static int getFontCharWidth(Drawable aDrawable) {
        GC gc = new GC(aDrawable);
        FontMetrics fm = gc.getFontMetrics();
        int charWidth = fm.getAverageCharWidth();
        gc.dispose();
        return charWidth;
    }

    /*
     * http://www.java2s.com/Tutorial/Java/0300__SWT-2D-Graphics/
     * UsingFontMetricstogetcharwidth.htm (FontMetricsCharWidth)
     */
    public static int getFontCharHeight(Drawable aDrawable) {
        GC gc = new GC(aDrawable);
        FontMetrics fm = gc.getFontMetrics();
        int charHeight = fm.getHeight();
        gc.dispose();
        return charHeight;
    }

}
