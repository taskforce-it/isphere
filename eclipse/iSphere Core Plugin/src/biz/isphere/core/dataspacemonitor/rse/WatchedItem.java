package biz.isphere.core.dataspacemonitor.rse;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

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

    public void restoreImage() {
        changeDecoration(originalImage, originalBackgroundColor);
    }

    private void changeDecoration(Image image, Color color) {
        decorator.setImage(image);
        decorator.getControl().setBackground(color);
        decorator.getControl().redraw();
    }
}
