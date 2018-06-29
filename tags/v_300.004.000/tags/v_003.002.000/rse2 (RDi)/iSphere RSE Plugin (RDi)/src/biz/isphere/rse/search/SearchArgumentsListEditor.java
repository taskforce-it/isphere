/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.search;

import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.search.AbstractSearchArgumentEditor;
import biz.isphere.core.search.SearchOptionConfig;

public class SearchArgumentsListEditor extends biz.isphere.core.search.SearchArgumentsListEditor {

    public SearchArgumentsListEditor(int aMaxNumSearchArguments) {
        this(aMaxNumSearchArguments, false);
    }

    public SearchArgumentsListEditor(int aMaxNumSearchArguments, boolean regularExpressionsOption) {
        this(aMaxNumSearchArguments, regularExpressionsOption, null);
    }

    public SearchArgumentsListEditor(int aMaxNumSearchArguments, boolean regularExpressionsOption, SearchOptionConfig[] additionalSearchOptions) {
        super(aMaxNumSearchArguments, regularExpressionsOption, additionalSearchOptions);
    }

    @Override
    protected AbstractSearchArgumentEditor createEditor(Composite aParent) {
        SearchArgumentEditor tEditor = new SearchArgumentEditor(isRegularExpressions());
        tEditor.createContents(aParent);
        tEditor.addSearchStringListener(this);
        tEditor.addButtonListener(this);
        return tEditor;
    }

}
