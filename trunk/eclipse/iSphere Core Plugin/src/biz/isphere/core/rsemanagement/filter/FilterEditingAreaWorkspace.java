package biz.isphere.core.rsemanagement.filter;

import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.Messages;
import biz.isphere.core.rsemanagement.AbstractResource;

public class FilterEditingAreaWorkspace extends AbstractFilterEditingArea {

	public FilterEditingAreaWorkspace(Composite parent, AbstractResource[] resources, boolean both) {
		super(parent, resources, both);
	}

	@Override
	protected String[] getActions(boolean both) {
		return getActionsWorkspace(both);
	}

	public String getTitle() {
		return Messages.Filters + " " + getTitleWorkspace();
	}

}
