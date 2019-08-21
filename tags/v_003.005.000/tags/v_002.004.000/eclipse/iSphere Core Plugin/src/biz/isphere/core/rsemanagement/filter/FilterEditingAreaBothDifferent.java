/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.rsemanagement.filter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.Size;
import biz.isphere.core.rsemanagement.AbstractEditingArea;
import biz.isphere.core.rsemanagement.AbstractResource;

public class FilterEditingAreaBothDifferent extends AbstractEditingArea {

	public FilterEditingAreaBothDifferent(Composite parent, AbstractResource[] resources, boolean both) {
		super(parent, resources, both);
	}

	@Override
	public void addTableColumns(Table tableResources) {
		
		TableColumn columnName = new TableColumn(tableResources, SWT.NONE);
		columnName.setWidth(Size.getSize(150));
		columnName.setText(Messages.Name);
		
		TableColumn columnWorkspaceType = new TableColumn(tableResources, SWT.NONE);
		columnWorkspaceType.setWidth(Size.getSize(100));
		columnWorkspaceType.setText("Wrk.-" + Messages.Type);
		
		TableColumn columnRepositoryType = new TableColumn(tableResources, SWT.NONE);
		columnRepositoryType.setWidth(Size.getSize(100));
		columnRepositoryType.setText("Rep.-" + Messages.Type);
		
		TableColumn columnWorkspaceFilterStrings = new TableColumn(tableResources, SWT.NONE);
		columnWorkspaceFilterStrings.setWidth(Size.getSize(200));
		columnWorkspaceFilterStrings.setText("Wrk.-" + Messages.Strings);
		
		TableColumn columnRepositoryFilterStrings = new TableColumn(tableResources, SWT.NONE);
		columnRepositoryFilterStrings.setWidth(Size.getSize(200));
		columnRepositoryFilterStrings.setText("Rep.-" + Messages.Strings);
		
	}

	@Override
	public String getTableColumnText(Object resource, int columnIndex) {
		RSEFilterBoth filterBoth = (RSEFilterBoth)resource;
		if (columnIndex == 0) {
			return filterBoth.getName();
		}
		else if (columnIndex == 1) {
			return RSEFilter.getTypeText(((RSEFilter)filterBoth.getResourceWorkspace()).getType());
		}
		else if (columnIndex == 2) {
			return RSEFilter.getTypeText(((RSEFilter)filterBoth.getResourceRepository()).getType());
		}
		else if (columnIndex == 3) {
			return (((RSEFilter)filterBoth.getResourceWorkspace()).getDisplayFilterString());
		}
		else if (columnIndex == 4) {
			return (((RSEFilter)filterBoth.getResourceRepository()).getDisplayFilterString());
		}
		else {
			return "";
		}
	}

	@Override
	public int compareResources(Object resource1, Object resource2) {
		return (((RSEFilterBoth)resource1).getName()).compareTo((((RSEFilterBoth)resource2).getName()));
	}

	@Override
	protected String[] getActions(boolean both) {
		return getActionsBothDifferent();
	}

	public String getTitle() {
		return Messages.Filters + " " + getTitleBothDifferent() + " " + Messages.type_or_string;
	}
	
}
