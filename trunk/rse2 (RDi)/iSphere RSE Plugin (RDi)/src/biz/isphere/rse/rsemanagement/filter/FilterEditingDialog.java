package biz.isphere.rse.rsemanagement.filter;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.rsemanagement.AbstractResource;
import biz.isphere.core.rsemanagement.filter.AbstractFilterEditingDialog;
import biz.isphere.core.rsemanagement.filter.RSEFilter;
import biz.isphere.core.rsemanagement.filter.RSEFilterBoth;

public class FilterEditingDialog extends AbstractFilterEditingDialog {

    public FilterEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth, String workspace,
        String repository, RSEFilter[] resourceWorkspace, RSEFilter[] resourceRepository, RSEFilterBoth[] resourceBothDifferent,
        RSEFilter[] resourceBothEqual) {
        super(parentShell, editWorkspace, editRepository, editBoth, workspace, repository, resourceWorkspace, resourceRepository, resourceBothDifferent,
            resourceBothEqual);
    }

    @Override
    protected void pushToWorkspace(AbstractResource resource) {
        RSEFilter filter = (RSEFilter)resource;
        RSEFilterHelper.createFilter(filter.getFilterPool(), filter.getName(), filter.getType(), new Vector<String>(Arrays.asList(filter.getFilterStrings())));
    }

    @Override
    protected void deleteFromWorkspace(AbstractResource resource) {
        RSEFilter filter = (RSEFilter)resource;
        RSEFilterHelper.deleteFilter(filter.getFilterPool(), filter.getName());
    }

    @Override
    protected boolean saveFiltersToXML(File toFile, RSEFilter[] filters) {
        return XMLFilterHelper.saveFiltersToXML(toFile, filters);
    }

}
