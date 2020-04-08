/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.search;

import biz.isphere.core.Messages;

public class SearchOptionConfig {

    private MatchOption option;
    private String label;

    private boolean caseEnabled;
    private boolean regExEnabled;
    private boolean conditionEnabled;

    private boolean columnRangeEnabled;
    private boolean includeFirstLevelTextEnabled;
    private boolean includeSecondLevelTextEnabled;
    private boolean includeMessageIdEnabled;

    public static SearchOptionConfig[] getAdditionalMessageFileSearchOptions() {

        SearchOptionConfig searchOptionMessageId = new SearchOptionConfig(MatchOption.MSGID, Messages.MatchMessageIdCondition);
        searchOptionMessageId.setCaseEnabled(false);
        searchOptionMessageId.setRegularExpressionEnabled(false);
        searchOptionMessageId.setConditionEnabled(false);
        searchOptionMessageId.setColumnRangeEnabled(false);
        searchOptionMessageId.setIncludeFirstLevelTextEnabled(false);
        searchOptionMessageId.setIncludeSecondLevelTextEnabled(false);
        searchOptionMessageId.setIncludeMessageIdEnabled(false);

        return new SearchOptionConfig[] { searchOptionMessageId };
    }

    public static SearchOptionConfig[] getAdditionalLineModeSearchOptions() {

        SearchOptionConfig searchOptionMessageId = new SearchOptionConfig(MatchOption.LINE, Messages.MatchLine);

        return new SearchOptionConfig[] { searchOptionMessageId };
    }

    public SearchOptionConfig(MatchOption option, String label) {
        this.option = option;
        this.label = label;

        this.caseEnabled = true;
        this.regExEnabled = true;
        this.conditionEnabled = true;

        this.columnRangeEnabled = true;

        this.includeFirstLevelTextEnabled = true;
        this.includeSecondLevelTextEnabled = true;
        this.includeMessageIdEnabled = true;
    }

    public MatchOption getOption() {
        return option;
    }

    public void setOption(MatchOption option) {
        this.option = option;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isCaseEnabled() {
        return caseEnabled;
    }

    public void setCaseEnabled(boolean enabled) {
        this.caseEnabled = enabled;
    }

    public boolean isRegularExpressionEnabled() {
        return regExEnabled;
    }

    public void setRegularExpressionEnabled(boolean enabled) {
        this.regExEnabled = enabled;
    }

    public boolean isConditionEnabled() {
        return conditionEnabled;
    }

    public void setConditionEnabled(boolean enabled) {
        this.conditionEnabled = enabled;
    }

    public boolean isColumnRangeEnabled() {
        return columnRangeEnabled;
    }

    public void setColumnRangeEnabled(boolean enabled) {
        this.columnRangeEnabled = enabled;
    }

    public boolean isIncludeFirstLevelTextEnabled() {
        return includeFirstLevelTextEnabled;
    }

    public void setIncludeFirstLevelTextEnabled(boolean enabled) {
        this.includeFirstLevelTextEnabled = enabled;
    }

    public boolean isIncludeSecondLevelTextEnabled() {
        return includeSecondLevelTextEnabled;
    }

    public void setIncludeSecondLevelTextEnabled(boolean enabled) {
        this.includeSecondLevelTextEnabled = enabled;
    }

    public boolean isIncludeMessageIdEnabled() {
        return includeMessageIdEnabled;
    }

    public void setIncludeMessageIdEnabled(boolean enabled) {
        this.includeMessageIdEnabled = enabled;
    }
}
