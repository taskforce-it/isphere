package biz.isphere.core.spooledfiles.view;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import biz.isphere.core.spooledfiles.SpooledFile;

public class WorkWithSpooledFilesContentProvider implements IStructuredContentProvider {

    private SpooledFile[] spooledFiles;

    public Object[] getElements(Object inputElement) {
        return spooledFiles;
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.spooledFiles = (SpooledFile[])newInput;
    }

}
