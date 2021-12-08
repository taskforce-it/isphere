/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.memberrename.rules;

import java.beans.PropertyVetoException;

import biz.isphere.core.memberrename.adapters.IMemberRenamingRuleAdapter;
import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;

public interface IMemberRenamingRule {

    public String getLabel();

    public String getBaseName(String memberName) throws NoMoreNamesAvailableException, PropertyVetoException;

    public String getNextName(String currentMemberName) throws NoMoreNamesAvailableException, PropertyVetoException;

    public String formatName(String memberName) throws PropertyVetoException;

    public IMemberRenamingRuleAdapter getAdapter();
}
