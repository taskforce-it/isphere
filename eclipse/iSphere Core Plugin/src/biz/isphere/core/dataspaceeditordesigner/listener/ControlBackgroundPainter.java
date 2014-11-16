package biz.isphere.core.dataspaceeditordesigner.listener;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wb.swt.SWTResourceManager;

public class ControlBackgroundPainter extends MouseTrackAdapter {

    private static Color COLOR_BACKGROUND_DEFAULT = SWTResourceManager.getColor(255, 255, 255);

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

        if (oldBackgroundColor.equals(COLOR_BACKGROUND_DEFAULT)) {
            /*
             * Ugly hack for WDSCi to reset the background color to the default
             * color.
             */
            currentControl.setBackground(null);
        } else {
            currentControl.setBackground(oldBackgroundColor);
        }

        currentControl = null;
        oldBackgroundColor = null;
    }
}
