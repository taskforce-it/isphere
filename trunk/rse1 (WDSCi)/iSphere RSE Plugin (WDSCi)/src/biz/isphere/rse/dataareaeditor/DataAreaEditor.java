package biz.isphere.rse.dataareaeditor;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.dataareaeditor.AbstractDataAreaEditor;
import biz.isphere.core.dataareaeditor.DataAreaText;
import biz.isphere.core.dataareaeditor.StatusBar;

public class DataAreaEditor extends AbstractDataAreaEditor{
    
    StatusBar statusBar;

    @Override
    protected void addCaretListener(DataAreaText aTextControl) {
//        aTextControl.addCaretListener(new CaretListener() {
//            public void caretMoved(CaretEvent anEvent) {
//                DataAreaText textControl = (DataAreaText)anEvent.getSource();
//                int row = textControl.getCaretRow();
//                int column = textControl.getCaretColumn();
//                statusBar.setPosition(row, column);
//            }
//        });
    }

    protected StatusBar createStatusBar(Composite aParent) {
        statusBar = new StatusBar(aParent);
        return statusBar;
    }
    
    protected StatusBar createStatusBar(Composite aParent, int aRow, int aColumn) {
        statusBar = new StatusBar(aParent);
//        statusBar.setPosition(aRow, aColumn);
        return statusBar;
    }
    
}
