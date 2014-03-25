package biz.isphere.adapter.swt.widgets.extension.point;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.adapter.swt.widgets.XFileDialog;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;
import biz.isphere.core.swt.widgets.extension.point.IWidgetFactory;

public class XWidgetFactory implements IWidgetFactory {

    @Override
    public IFileDialog getDialog(Shell aParent, int aStyle) {
        return new XFileDialog(aParent, aStyle);
    }

    @Override
    public IFileDialog getDialog(Shell aParent) {
        return new XFileDialog(aParent);
    }

}
