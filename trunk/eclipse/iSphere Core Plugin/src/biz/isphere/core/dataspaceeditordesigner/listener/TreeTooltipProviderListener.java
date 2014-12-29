package biz.isphere.core.dataspaceeditordesigner.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import biz.isphere.core.dataspaceeditordesigner.model.DEditor;

public class TreeTooltipProviderListener implements Listener {

    private Tree tree;

    public TreeTooltipProviderListener(Tree tree) {
        this.tree = tree;
    }

    public void handleEvent(Event event) {
        switch (event.type) {
        case SWT.MouseHover: {
            Point coords = new Point(event.x, event.y);
            TreeItem item = tree.getItem(coords);
            if (item != null) {
                if (item.getData() instanceof DEditor) {
                    DEditor dEditor = (DEditor)item.getData();
                    tree.setToolTipText(dEditor.getDescription());
                }
            }
        }
        }
    }

}
