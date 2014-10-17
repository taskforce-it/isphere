package biz.isphere.rse.dataareaeditor;

import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.dataareaeditor.AbstractDataAreaEditor;
import biz.isphere.core.dataareaeditor.StatusBar;

public class DataAreaEditor extends AbstractDataAreaEditor{

    protected StatusBar createStatusBar(Composite aParent) {
        StatusBar statusBar = new StatusBar(aParent);
        return statusBar;
    }
    
    protected StatusBar createStatusBar(Composite aParent, int aRow, int aColumn) {
        StatusBar statusBar = new StatusBar(aParent);
//        statusBar.setPosition(aRow, aColumn);
        return statusBar;
    }
    
}
