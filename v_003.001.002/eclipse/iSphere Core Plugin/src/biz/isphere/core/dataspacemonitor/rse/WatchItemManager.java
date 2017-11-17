/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspacemonitor.rse;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.dataspacemonitor.internal.WatchedItem;
import biz.isphere.core.internal.ColorHelper;
import biz.isphere.core.internal.IControlDecoration;

public class WatchItemManager {

    Map<Control, WatchedItem> watchedItems;
    Image valueChangedImage;
    Color valueChangedColor;

    public WatchItemManager() {
        this.watchedItems = new HashMap<Control, WatchedItem>();
    }

    public void addControl(IControlDecoration decorator, String currentValue) {
        watchedItems.put(decorator.getControl(), new WatchedItem(decorator));
        setInitialValue(decorator, currentValue);
        decorator.show();
    }

    public void removeControl(IControlDecoration decorator) {
        WatchedItem watchedItem = watchedItems.get(decorator.getControl());
        watchedItem.restoreImageAndColor();
        watchedItems.remove(decorator.getControl());
        decorator.hide();
    }

    public boolean isWatchedControl(Control control) {
        if (watchedItems.containsKey(control)) {
            return true;
        }
        return false;
    }

    public void setCurrentValue(Control control, String controlValue) {
        checkControl(control);
        WatchedItem watchedItem = watchedItems.get(control);
        if (controlValue.equals(watchedItem.getCurrentValue())) {
            watchedItem.restoreImageAndColor();
        } else {
            watchedItem.setImageAndColor(getValueChangedImage(), getValueChangedColor());
        }
        watchedItem.setCurrentValue(controlValue);
    }

    private void setInitialValue(IControlDecoration decorator, String currentValue) {
        WatchedItem watchedItem = watchedItems.get(decorator.getControl());
        watchedItem.setCurrentValue(currentValue);
    }

    private void checkControl(Control control) {
        if (!isWatchedControl(control)) {
            throw new RuntimeException("Control not found: " + control); //$NON-NLS-1$
        }
    }

    private Image getValueChangedImage() {
        if (valueChangedImage == null) {
            valueChangedImage = ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_VALUE_CHANGED);
        }
        return valueChangedImage;
    }

    private Color getValueChangedColor() {
        if (valueChangedColor == null) {
            valueChangedColor = ColorHelper.getBackgroundColorOfChangedValues();
        }
        return valueChangedColor;
    }
}
