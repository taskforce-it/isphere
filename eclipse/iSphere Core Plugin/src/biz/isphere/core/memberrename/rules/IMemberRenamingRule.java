/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.memberrename.rules;

import java.beans.PropertyVetoException;

import com.ibm.as400.access.QSYSObjectPathName;

import biz.isphere.core.memberrename.RenameMemberActor;
import biz.isphere.core.memberrename.adapters.IMemberRenamingRuleAdapter;
import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;

/**
 * This class specifies the rules that are applied when producing a new backup
 * member name.
 */
public interface IMemberRenamingRule {

    /**
     * Returns the UI label of the rule.
     * 
     * @return UI label
     */
    public String getLabel();

    /**
     * Sets the name of the member that is renamed. This is the first method
     * that must be called after a new rule has been created.
     * 
     * @param memberName - name of the member that is renamed.
     */
    public void setBaseMemberName(String memberName);

    /**
     * Returns the mask for producing a list of members that exist on the
     * system. This method is called by the {@link RenameMemberActor} in order
     * to produce a list of members that exist on the system.
     * 
     * @return list of existing members
     */
    public String getMemberNameFilter();

    /**
     * Optional method. Sets the list of members that exist on the system. This
     * method can be called before <code>getNextName(String)</code>. It can be
     * used for calculating the last (highest) backup member name used, so that
     * gaps can be skipped.
     * 
     * @param existingMemberPaths - list of members on the system, that matches
     *        the rule. The path must match the value returned by
     *        {@link QSYSObjectPathName#getPath()}.
     */
    public void setExistingMembers(String[] existingMemberPaths);

    /**
     * Returns the next backup member name.
     * 
     * @return next backup member name
     * @throws NoMoreNamesAvailableException
     * @throws PropertyVetoException
     */
    public String getNextName() throws NoMoreNamesAvailableException, PropertyVetoException;

    /**
     * Returns the UI adapter of the rule. The adapter must implement the
     * {@link IMemberRenamingRuleAdapter} interface.
     * 
     * @return UI adapter of the rule.
     */
    public IMemberRenamingRuleAdapter getAdapter();
}
