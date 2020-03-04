/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

public class SpooledFileFilter {

    private String user;
    private String outputQueue;
    private String outputQueueLibrary;
    private String userData;
    private String formType;

    public SpooledFileFilter() {
        super();
    }

    public SpooledFileFilter(String filterString) {
        this();
        setFilters(filterString);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOutputQueue() {
        return outputQueue;
    }

    public void setOutputQueue(String outputQueue) {
        this.outputQueue = outputQueue;
    }

    public String getOutputQueueLibrary() {
        return outputQueueLibrary;
    }

    public void setOutputQueueLibrary(String outputQueueLibrary) {
        this.outputQueueLibrary = outputQueueLibrary;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getFilterString() {
        StringBuffer filterString = new StringBuffer();
        if (user == null)
            filterString.append("*/");
        else
            filterString.append(user + "/");
        if (outputQueue == null)
            filterString.append("*/");
        else
            filterString.append(outputQueue + "/");
        if (outputQueueLibrary == null)
            filterString.append("*/");
        else
            filterString.append(outputQueueLibrary + "/");
        if (userData == null)
            filterString.append("*/");
        else
            filterString.append(userData + "/");
        if (formType == null)
            filterString.append("*/");
        else
            filterString.append(formType + "/");
        return filterString.toString();
    }

    private void setFilters(String filterString) {
        int index;
        index = filterString.indexOf("/");
        String temp = filterString.substring(0, index);
        if (!temp.equals("*")) setUser(temp);
        String parseText = filterString.substring(index + 1);
        index = parseText.indexOf("/");
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) setOutputQueue(temp);
        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/");
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) setOutputQueueLibrary(temp);
        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/");
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) setUserData(temp);
        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/");
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) setFormType(temp);
    }

}
