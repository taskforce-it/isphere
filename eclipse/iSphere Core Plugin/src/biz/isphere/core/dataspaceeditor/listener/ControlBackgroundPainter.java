package biz.isphere.core.dataspaceeditor.listener;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class ControlBackgroundPainter extends MouseTrackAdapter {

    private Color selectedControlBackgroundColor;

    private Control currentControl;
    private Color oldBackgroundColor;

    public ControlBackgroundPainter(Color color) {
        this.selectedControlBackgroundColor = color;
    }

    public void mouseEnter(MouseEvent event) {

        Object object = event.getSource();
        if (!(object instanceof Control)) {
            currentControl = null;
            oldBackgroundColor = null;
            return;
        }

        Control control = (Control)event.getSource();

        currentControl = control;
        oldBackgroundColor = control.getBackground();

        control.setBackground(selectedControlBackgroundColor);
    }

    @Override
    public void mouseExit(MouseEvent event) {
        if (currentControl != event.getSource()) {
            return;
        }

        boolean isEnabled = currentControl.isEnabled();
        if (currentControl instanceof Text) {
            isEnabled &= ((Text)currentControl).getEditable();
        }

        if (isEnabled) {
            currentControl.setBackground(oldBackgroundColor);
        } else {
            currentControl.setBackground(null);
        }

        currentControl = null;
        oldBackgroundColor = null;
    }
}
