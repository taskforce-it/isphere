package biz.isphere.core.rsemanagement.filter;

import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.Messages;
import biz.isphere.core.rsemanagement.AbstractResource;

public class FilterEditingAreaRepository extends AbstractFilterEditingArea {

	public FilterEditingAreaRepository(Composite parent, AbstractResource[] resources, boolean both) {
		super(parent, resources, both);
	}

	@Override
	protected String[] getActions(boolean both) {
		return getActionsRepository(both);
	}
	
	public String getTitle() {
		return Messages.Filters + " " + getTitleRepository();
	}

}
