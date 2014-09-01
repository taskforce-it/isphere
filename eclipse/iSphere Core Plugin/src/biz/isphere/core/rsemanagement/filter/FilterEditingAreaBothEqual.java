package biz.isphere.core.rsemanagement.filter;

import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.Messages;
import biz.isphere.core.rsemanagement.AbstractResource;

public class FilterEditingAreaBothEqual extends AbstractFilterEditingArea {

	public FilterEditingAreaBothEqual(Composite parent, AbstractResource[] resources, boolean both) {
		super(parent, resources, both);
	}

	@Override
	protected String[] getActions(boolean both) {
		return getActionsBothEqual();
	}

	public String getTitle() {
		return Messages.Filters + " " + getTitleBothEqual() + " " + Messages.type_and_string;
	}

}
