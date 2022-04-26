/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class HighlightedAttributes {

    @Expose(serialize = true, deserialize = true)
    private Map<Integer, Set<String>> attributes;

    public HighlightedAttributes() {
        this.attributes = new HashMap<Integer, Set<String>>();
    }

    public void add(int index, String value) {
        add(new HighlightedAttribute(index, value));
    }

    public void add(HighlightedAttribute attribute) {
        getValues(attribute).add(attribute.getValue());
    }

    public void remove(HighlightedAttribute attribute) {
        getValues(attribute).remove(attribute.getValue());
    }

    public boolean isHighlighted(int index, String value) {
        return getValues(index).contains(value);
    }

    public void clear() {
        attributes.clear();
    }

    private Set<String> getValues(HighlightedAttribute attribute) {
        return getValues(attribute.getIndex());
    }

    private Set<String> getValues(int index) {

        Set<String> values = attributes.get(index);
        if (values == null) {
            values = new HashSet<String>();
            attributes.put(index, values);
        }

        return values;
    }
}
