/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;

public class ExcludedEntries {

    @Expose(serialize = true, deserialize = true)
    private Map<BigInteger, List<JobTraceEntry>> excludedEntries;

    public ExcludedEntries() {
        this.excludedEntries = new HashMap<BigInteger, List<JobTraceEntry>>();
    }

    public void addAll(BigInteger nanosSinceStarted, List<JobTraceEntry> excludedEntries) {
        getEntries(nanosSinceStarted).addAll(excludedEntries);
    }

    public JobTraceEntry[] getAll(BigInteger nanosSinceStarted) {
        List<JobTraceEntry> jobTraceEntries = getEntries(nanosSinceStarted);
        return getEntries(nanosSinceStarted).toArray(new JobTraceEntry[jobTraceEntries.size()]);
    }

    public void removeAll(BigInteger nanosSinceStarted) {
        excludedEntries.remove(nanosSinceStarted);
    }

    public boolean isExcluded(BigInteger nanosSinceStarted) {
        return excludedEntries.containsKey(nanosSinceStarted);
    }

    public void setHighlighted(BigInteger nanosSinceStarted, boolean isHighlighted) {

        if (isExcluded(nanosSinceStarted)) {
            List<JobTraceEntry> entries = excludedEntries.get(nanosSinceStarted);
            for (JobTraceEntry jobTraceEntry : entries) {
                jobTraceEntry.setHighlighted(isHighlighted);
            }
        }
    }

    private List<JobTraceEntry> getEntries(BigInteger nanosSinceStarted) {

        List<JobTraceEntry> entries = excludedEntries.get(nanosSinceStarted);
        if (entries == null) {
            entries = new LinkedList<JobTraceEntry>();
            excludedEntries.put(nanosSinceStarted, entries);
        }

        return entries;
    }
}
