package biz.isphere.rcp.messagefilecompareeditor;

import biz.isphere.core.externalapi.IMessageFileCompareEditorConfiguration;

public class CompareConfiguration implements IMessageFileCompareEditorConfiguration {

    private boolean isLeftEditable;
    private boolean isLeftFileSelectorEnabled;
    private boolean isRightEditable;
    private boolean isRightFileSelectorEnabled;

    public CompareConfiguration() {
        this(true, true, true, true);
    }

    public CompareConfiguration(boolean isLeftEditable, boolean isLeftFileSelectorEnabled, boolean isRightEditable,
        boolean isRightFileSelectiorEnabled) {
        this.isLeftEditable = isLeftEditable;
        this.isLeftFileSelectorEnabled = isLeftFileSelectorEnabled;
        this.isRightEditable = isRightEditable;
        this.isRightFileSelectorEnabled = isRightFileSelectiorEnabled;
    }

    @Override
    public boolean isLeftEditorEnabled() {
        return isLeftEditable;
    }

    @Override
    public boolean isLeftSelectMessageFileEnabled() {
        return isLeftFileSelectorEnabled;
    }

    @Override
    public boolean isRightEditorEnabled() {
        return isRightEditable;
    }

    @Override
    public boolean isRightSelectMessageFileEnabled() {
        return isRightFileSelectorEnabled;
    }

}
