package biz.isphere.core.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.part.ViewPart;

public abstract class AbstractSaveableViewPart extends ViewPart implements ISaveablePart2 {

    public void doSave(IProgressMonitor monitor) {
    }

    public void doSaveAs() {
    }

    public boolean isDirty() {
        return false;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public boolean isSaveOnCloseNeeded() {
        return false;
    }

    public int promptToSaveOnClose() {
        return ISaveablePart2.NO;
    }
}
