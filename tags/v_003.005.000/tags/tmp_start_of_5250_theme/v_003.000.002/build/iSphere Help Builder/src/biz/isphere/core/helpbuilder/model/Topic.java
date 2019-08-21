/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.helpbuilder.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class Topic {

    @XStreamAsAttribute
    private String label;

    @XStreamAsAttribute
    private String href;

    private Anchor anchor;

    @XStreamImplicit()
    private List<Topic> topic;

    @XStreamOmitField
    private List<Toc> toc;

    public Topic(String label, String href) {
        this.label = label;
        this.href = href;
    }

    public String getLabel() {
        return label;
    }

    public String getHref() {
        return href;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    public Topic[] getTopics() {
        List<Topic> topics = getOrCreateTopics();
        return topics.toArray(new Topic[topics.size()]);
    }

    public void setTopics(List<Topic> topics) {
        this.topic = topics;
    }

    public void addTopics(Topic[] topics) {
        getOrCreateTopics().addAll(Arrays.asList(topics));
    }

    public Toc[] getTocs() {
        List<Toc> tocs = getOrCreateTocs();
        return tocs.toArray(new Toc[tocs.size()]);
    }

    public void setTocs(List<Toc> tocs) {
        this.toc = tocs;
    }

    public void addToc(Toc toc) {
        this.getOrCreateTocs().add(toc);
    }

    private List<Topic> getOrCreateTopics() {
        if (topic == null) {
            topic = new ArrayList<Topic>();
        }
        return topic;
    }

    private List<Toc> getOrCreateTocs() {
        if (toc == null) {
            toc = new ArrayList<Toc>();
        }
        return toc;
    }

    @Override
    public String toString() {
        if (href != null) {
            return label + ": " + href;
        } else {
            return label + ": " + anchor;
        }
    }
}
