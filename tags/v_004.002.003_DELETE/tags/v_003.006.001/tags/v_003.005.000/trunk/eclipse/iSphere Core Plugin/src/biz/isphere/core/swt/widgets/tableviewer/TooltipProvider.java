/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.tableviewer;

/**
 * This interface must be implemented by a LabelProvider for TableViewers that
 * utilize
 * <code>biz.isphere.core.swt.widgets.tableviewer.TableViewerTooltipSupport</code>.
 */
public interface TooltipProvider {

    public String getTooltipText(Object element, int columnIndex);

}
