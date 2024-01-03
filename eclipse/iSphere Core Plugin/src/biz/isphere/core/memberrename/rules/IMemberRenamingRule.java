/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.memberrename.rules;

import java.beans.PropertyVetoException;
import java.io.IOException;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;

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
    public void initialize(AS400 system, String libraryName, String fileName, String memberName) throws AS400Exception, PropertyVetoException,
        AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException, ObjectDoesNotExistException;

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
