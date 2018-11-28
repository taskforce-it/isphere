/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.extension.point;

import org.eclipse.swt.events.ModifyListener;

/**
 * Interface for a time edit control. The intention is to create a wrapper for
 * the SWT DateTime class, which could be contributed by the
 * "iSphere Adapter Plugin" for RDi. Due to the lack of the dateTime class in
 * WDSCi, WDSCi users have to use the lightweight TimeEdit class.
 */
public interface ITimeEdit {

    public void setLayoutData(Object layoutData);

    public void addModifyListener(ModifyListener listener);

    public void removeModifyListener(ModifyListener listener);

    public void setEnabled(boolean enabled);

    public void setToolTipText(String text);

    public void setTime(int hours, int minutes, int seconds);

    public int getHours();

    public int getMinutes();

    public int getSeconds();

}
