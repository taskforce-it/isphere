/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.memberrename.rules;

import biz.isphere.base.internal.StringHelper;

public abstract class AbstractMemberRenamingRule implements IMemberRenamingRule {

    private String label;
    private String baseMemberName;

    public AbstractMemberRenamingRule(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setBaseMemberName(String memberName) {
        this.baseMemberName = memberName;
    }

    public String getBaseMemberName() {

        if (StringHelper.isNullOrEmpty(baseMemberName)) {
            throw new IllegalArgumentException("Field must not be empty: 'baseMemberName'"); //$NON-NLS-1$
        }

        return baseMemberName;
    }
}
