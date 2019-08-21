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

public abstract class AbstractFilterEditingArea extends AbstractEditingArea {

	public AbstractFilterEditingArea(Composite parent, AbstractResource[] resources, boolean both) {
		super(parent, resources, both);
	}

	public void addTableColumns(Table tableResources) {
		
		TableColumn columnName = new TableColumn(tableResources, SWT.NONE);
		columnName.setWidth(Size.getSize(150));
		columnName.setText(Messages.Name);
		
		TableColumn columnType = new TableColumn(tableResources, SWT.NONE);
		columnType.setWidth(Size.getSize(100));
		columnType.setText(Messages.Type);
		
		TableColumn columnFilterStrings = new TableColumn(tableResources, SWT.NONE);
		columnFilterStrings.setWidth(Size.getSize(400));
		columnFilterStrings.setText(Messages.Strings);
		
	}
	
	public String getTableColumnText(Object resource, int columnIndex) {
		if (columnIndex == 0) {
			return (((RSEFilter)resource).getName());
		}
		else if (columnIndex == 1) {
			return RSEFilter.getTypeText((((RSEFilter)resource).getType()));
		}
		else if (columnIndex == 2) {
			return (((RSEFilter)resource).getDisplayFilterString());
		}
		else {
			return "";
		}
	}
	
	public int compareResources(Object resource1, Object resource2) {
		return (((RSEFilter)resource1).getName()).compareTo((((RSEFilter)resource2).getName()));
	}
	
}
