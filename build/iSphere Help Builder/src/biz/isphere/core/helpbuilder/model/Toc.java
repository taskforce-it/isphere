/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.helpbuilder.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class Toc {

    @XStreamAsAttribute
    private String label;

    @XStreamAlias("link_to")
    @XStreamAsAttribute
    private String linkTo;

    @XStreamImplicit()
    private List<Topic> topic;

    @XStreamOmitField
    private String pluginTocPath;

    public Toc(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getLinkTo() {
        return linkTo;
    }

    public void setLinkTo(String linkTo) {
        this.linkTo = linkTo;
    }

    public String getLinkToReference() {

        if (linkTo == null) {
            return null;
        }

        int indexReference = linkTo.indexOf("#");
        if (indexReference == -1) {
            return null;
        }

        if (indexReference >= linkTo.length() - 1) {
            return "";
        }

        return linkTo.substring(indexReference + 1);
    }

    public String getLinkToFile() {

        if (linkTo == null) {
            return null;
        }

        int indexReference = linkTo.indexOf("#");
        if (indexReference == -1) {
            return null;
        }

        return linkTo.substring(0, indexReference);
    }

    public Topic[] getTopics() {
        List<Topic> topics = getOrCreateTopics();
        return topics.toArray(new Topic[topics.size()]);
    }

    public void setTopics(List<Topic> topics) {
        this.topic = topics;
    }

    public String getPluginTocPath() {
        return pluginTocPath;
    }

    public void setPluginTocPath(String pluginTocPath) {
        this.pluginTocPath = pluginTocPath;
    }

    private List<Topic> getOrCreateTopics() {
        if (topic == null) {
            topic = new ArrayList<Topic>();
        }
        return topic;
    }

    @Override
    public String toString() {
        return getLabel() + " -> "+ getLinkToFile();
    }
    
}
