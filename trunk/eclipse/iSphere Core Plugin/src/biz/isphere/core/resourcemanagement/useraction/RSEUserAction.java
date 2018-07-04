/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.useraction;

import biz.isphere.core.resourcemanagement.AbstractResource;

public class RSEUserAction extends AbstractResource {

    private RSEDomain domain;
    private String label;
    private String originalName;
    private String commandString;
    private boolean promptFirst;
    private boolean refreshAfter;
    private boolean showAction;
    private boolean singleSelection;
    private boolean invokeOnce;
    private String comment;
    private String[] fileTypes;
    private boolean isIBM;
    private String vendor;
    private Object origin;

    public RSEUserAction() {
        super(true);
        this.domain = null;
        this.label = null;
        this.commandString = null;
        this.promptFirst = false;
        this.refreshAfter = false;
        this.showAction = true;
        this.singleSelection = false;
        this.invokeOnce = false;
        this.comment = null;
        this.fileTypes = new String[0];
        this.isIBM = false;
        this.vendor = null;
        this.originalName = null;
        this.origin = null;
    }

    public RSEUserAction(RSEDomain domain, String label, String commandString, boolean isPromptFirst, boolean isRefreshAfter, boolean isShowAction,
        boolean isSingleSelection, boolean isInvokeOnce, String comment, String[] fileTypes, boolean isIBM, String vendor, String originalName,
        Object origin) {
        super(true);
        this.domain = domain;
        this.label = label;
        this.commandString = commandString;
        this.promptFirst = isPromptFirst;
        this.refreshAfter = isRefreshAfter;
        this.showAction = isShowAction;
        this.singleSelection = isSingleSelection;
        this.invokeOnce = isInvokeOnce;
        this.comment = comment;
        this.fileTypes = fileTypes;
        this.isIBM = isIBM;
        this.vendor = vendor;
        this.originalName = originalName;
        this.origin = origin;
    }

    private String getDomainAsString() {
        return null;
    }

    public RSEDomain getDomain() {
        return domain;
    }

    public void setDomain(RSEDomain domain) {
        this.domain = domain;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = nullOnEmpty(label);
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getOriginalName() {
        return nullOnEmpty(originalName);
    }

    public String getCommandString() {
        return commandString;
    }

    public void setCommandString(String commandString) {
        this.commandString = nullOnEmpty(commandString);
    }

    public boolean isPromptFirst() {
        return promptFirst;
    }

    public void setPromptFirst(boolean promptFirst) {
        this.promptFirst = promptFirst;
    }

    public boolean isRefreshAfter() {
        return refreshAfter;
    }

    public void setRefreshAfter(boolean refreshAfter) {
        this.refreshAfter = refreshAfter;
    }

    public boolean isShowAction() {
        return showAction;
    }

    public void setShowAction(boolean showAction) {
        this.showAction = showAction;
    }

    public boolean isSingleSelection() {
        return singleSelection;
    }

    public void setSingleSelection(boolean singleSelection) {
        this.singleSelection = singleSelection;
    }

    public boolean isInvokeOnce() {
        return invokeOnce;
    }

    public void setInvokeOnce(boolean invokeOnce) {
        this.invokeOnce = invokeOnce;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = nullOnEmpty(comment);
    }

    public String[] getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(String[] fileTypes) {
        this.fileTypes = fileTypes;
    }

    public boolean isIBM() {
        return isIBM;
    }

    public void setIBM(boolean isIBM) {
        this.isIBM = isIBM;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = nullOnEmpty(vendor);
    }

    public Object getOrigin() {
        return origin;
    }

    public void setOrigin(Object origin) {
        this.origin = origin;
    }

    @Override
    public String getKey() {
        return Integer.toString(domain.getDomainType()) + ":" + getLabel(); //$NON-NLS-1$
    }

    @Override
    public String getValue() {

        StringBuilder buffer = new StringBuilder();

        appendAttribute(buffer, label);
        appendAttribute(buffer, originalName);
        appendAttribute(buffer, commandString);
        appendAttribute(buffer, promptFirst);
        appendAttribute(buffer, refreshAfter);
        appendAttribute(buffer, showAction);
        appendAttribute(buffer, singleSelection);
        appendAttribute(buffer, invokeOnce);
        appendAttribute(buffer, comment);
        appendAttribute(buffer, fileTypes);
        appendAttribute(buffer, isIBM);
        appendAttribute(buffer, vendor);

        System.out.println(buffer.toString());

        return buffer.toString();
    }

    private void appendAttribute(StringBuilder buffer, String value) {

        if (buffer.length() > 0) {
            buffer.append(":"); //$NON-NLS-1$
        }
        buffer.append(value);
    }

    private void appendAttribute(StringBuilder buffer, String[] values) {

        if (buffer.length() > 0) {
            buffer.append(":"); //$NON-NLS-1$
        }

        buffer.append("["); //$NON-NLS-1$
        boolean isFirstItem = true;
        for (String value : values) {
            if (!isFirstItem) {
                buffer.append(","); //$NON-NLS-1$
            } else {
                isFirstItem = false;
            }
            buffer.append(value);
        }
        buffer.append("]"); //$NON-NLS-1$
    }

    private void appendAttribute(StringBuilder buffer, boolean value) {

        if (buffer.length() > 0) {
            buffer.append(":"); //$NON-NLS-1$
        }
        buffer.append(Boolean.toString(value));
    }

    private String nullOnEmpty(String value) {

        if (value == null || value.trim().length() == 0) {
            return null;
        }

        return value;
    }

    @Override
    public String toString() {
        return getDomainAsString() + ": " + getLabel() + ": " + getCommandString(); //$NON-NLS-1$  //$NON-NLS-2$
    }
}
