/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

import org.eclipse.swt.custom.CTabFolder;

public interface ITN5250JPart {

    public CTabFolder getTabFolderSessions();

    public void addTN5250JPanel(TN5250JPanel tn5250jPanel);

    public void removeTN5250JPanel(TN5250JPanel tn5250jPanel);

    public boolean isMultiSession();

    public void setAddSession(boolean value);

    public void setRemoveSession(boolean value);

    public void setBindingService(boolean value);

    public int findSessionTab(TN5250JInfo tn5250jInfo);

}
