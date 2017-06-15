package biz.isphere.core.compareeditor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.core.internal.Member;

public class CompareInputWithSaveNeededHandling extends CompareInput {

    private boolean isSaveNeeded = false;

    public CompareInputWithSaveNeededHandling(CompareEditorConfiguration config, Member ancestorMember, Member leftMember, Member rightMember) {
        super(config, ancestorMember, leftMember, rightMember);
    }

    @Override
    public boolean isSaveNeeded() {
        if (super.isSaveNeeded()) {
            isSaveNeeded = true;
        }
        return isSaveNeeded;
    }

    @Override
    public void saveChanges(IProgressMonitor pm) throws CoreException {
        isSaveNeeded = false;
        super.saveChanges(pm);
    }

}
