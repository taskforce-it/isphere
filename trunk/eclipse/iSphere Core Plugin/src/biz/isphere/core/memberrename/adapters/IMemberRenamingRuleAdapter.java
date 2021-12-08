/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.memberrename.adapters;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;

public interface IMemberRenamingRuleAdapter {

    public void initializeDefaultPreferences(IPreferenceStore preferenceStore);

    public void storePreferences();

    public void loadPreferences();

    public void loadDefaultPreferences();

    public String validatePreferences();

    public Composite createComposite(Composite parent);
}
