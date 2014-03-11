package biz.isphere.adapter.swt.widgets;

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class XFileDialog {

    private FileDialog fileDialog;

    public XFileDialog(Shell aShell, int aStyle) {
        fileDialog = new FileDialog(aShell, aStyle);
    }

    public XFileDialog(Shell aShell) {
        fileDialog = new FileDialog(aShell);
    }

    public String open() {
        return fileDialog.open();
    }

    public void setOverwrite(boolean anOverwrite) {
        fileDialog.setOverwrite(anOverwrite);
    }

    public boolean getOverwrite() {
        return fileDialog.getOverwrite();
    }

    public void setText(String aText) {
        fileDialog.setText(aText);
    }

    public void setFileName(String aFileName) {
        fileDialog.setFileName(aFileName);
    }

    public void setFilterPath(String aFilterPath) {
        fileDialog.setFilterPath(aFilterPath);
    }

    public void setFilterExtensions(String[] aFilterExtensions) {
        fileDialog.setFilterExtensions(aFilterExtensions);
    }
}
