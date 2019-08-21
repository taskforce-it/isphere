/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspacemonitor.internal;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import biz.isphere.core.internal.ColorHelper;
import biz.isphere.core.internal.IControlDecoration;

public class WatchedItem {

    private IControlDecoration decorator;
    private Color originalBackgroundColor;
    private Image originalImage;
    private Object currentValue;

    public WatchedItem(IControlDecoration decorator) {
        this.decorator = decorator;
        this.originalImage = decorator.getImage();
        this.originalBackgroundColor = decorator.getControl().getBackground();
    }

    public void setCurrentValue(String value) {
        this.currentValue = value;
    }

    public Object getCurrentValue() {
        return currentValue;
    }

    public void setImageAndColor(Image valueChangedImage, Color color) {
        changeDecoration(valueChangedImage, color);
    }

    public void restoreImageAndColor() {
        changeDecoration(originalImage, originalBackgroundColor);
    }

    private void changeDecoration(Image image, Color color) {

        if (color.equals(ColorHelper.getDefaultBackgroundColor())) {
            /*
             * Ugly hack for WDSCi to reset the background color to the default
             * color.
             */
            decorator.getControl().setBackground(null);
        } else {
            decorator.getControl().setBackground(color);
        }

        decorator.setImage(image);
        decorator.getControl().redraw();
    }
}
