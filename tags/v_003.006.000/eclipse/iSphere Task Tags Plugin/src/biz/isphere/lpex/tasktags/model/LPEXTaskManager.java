/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.tasktags.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.MarkerUtilities;

import biz.isphere.base.internal.JDTCoreUtils;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.lpex.tasktags.ISphereLpexTasksPlugin;
import biz.isphere.lpex.tasktags.preferences.Preferences;

/**
 * This class is used to hold all the procedures used to manage the LPEX task
 * tags of a given document.
 * 
 * @author Thomas Raddatz
 */
public class LPEXTaskManager {

    private IResource resource;

    private IDocument document;

    private Preferences preferences;

    private final Integer TASK_PRIORITY_LOW = new Integer(IMarker.PRIORITY_LOW);

    private final Integer TASK_PRIORITY_NORMAL = new Integer(IMarker.PRIORITY_NORMAL);

    private final Integer TASK_PRIORITY_HIGH = new Integer(IMarker.PRIORITY_HIGH);

    private static final String XML_END_COMMENT = "-->";

    private static final String CL_END_COMMENT = "*/";

    public LPEXTaskManager(IResource aResource, IDocument aDocument) {
        resource = aResource;
        document = aDocument;
        preferences = Preferences.getInstance();
    }

    /**
     * Scans the document for LPEX marker tags.
     * 
     * @throws CoreException
     */
    public void createMarkers() throws CoreException {
        if (resource != null && document != null) {
            for (LPEXTask tTask : parseDocument()) {
                Map<String, Object> tAttrs = new HashMap<String, Object>();
                tAttrs.put("userEditable", false);
                tAttrs.put("priority", new Integer(tTask.getPriority()));
                MarkerUtilities.setLineNumber(tAttrs, tTask.getLine());
                MarkerUtilities.setMessage(tAttrs, tTask.getMessage());
                MarkerUtilities.setCharStart(tAttrs, tTask.getCharStart());
                MarkerUtilities.setCharEnd(tAttrs, tTask.getCharEnd());
                MarkerUtilities.createMarker(resource, tAttrs, LPEXTask.ID);
            }
        }
    }

    /**
     * Removes all LPEX marker tags from the document.
     * 
     * @throws CoreException
     */
    public void removeMarkers() throws CoreException {
        if (resource == null) {
            return;
        }
        IMarker[] tMarkers = null;
        int depth = 2;
        tMarkers = resource.findMarkers(LPEXTask.ID, true, depth);
        for (int i = tMarkers.length - 1; i >= 0; i--) {
            tMarkers[i].delete();
        }
    }

    /**
     * Returns the name of the document.
     * 
     * @return document name.
     */
    public String getDocumentName() {
        if (resource != null) {
            return resource.getFullPath().toString();
        }
        return "null";
    }

    /**
     * Returns <code>true</code> if LPEX marker tags are enabled, else
     * <code>false</code>.
     * 
     * @return <code>true</code>, if marker tags are enabled.
     */
    public boolean markerAreEnabled() {
        if (resource == null) {
            return false;
        }
        if (!(preferences.isEnabled() && preferences.supportsResource(resource))) {
            return false;
        }
        return true;
    }

    private ArrayList<LPEXTask> parseDocument() {
        ArrayList<LPEXTask> tTaskList = new ArrayList<LPEXTask>();
        try {
            Map<String, Integer> tJavaTasks = loadJavaTasks();
            Pattern tPattern = compilePattern(tJavaTasks);
            Matcher tMatcher = tPattern.matcher(document.get());
            while (tMatcher.find()) {
                if (tMatcher.groupCount() == 1) {
                    tTaskList.add(produceTask(tMatcher, tJavaTasks));
                }
            }
        } catch (Exception e) {
            ISphereLpexTasksPlugin.logError("Failed to parse document: " + getDocumentName(), e);
        }
        return tTaskList;
    }

    private Map<String, Integer> loadJavaTasks() {
        String tJavaTaskTags = getValue(JavaCore.COMPILER_TASK_TAGS);
        String tJavaTaskPrios = getValue(JavaCore.COMPILER_TASK_PRIORITIES);
        String[] tTags = StringHelper.getTokens(tJavaTaskTags, ","); //$NON-NLS-1$
        String[] tPrios = StringHelper.getTokens(tJavaTaskPrios, ","); //$NON-NLS-1$
        Map<String, Integer> tJavaTasks = new HashMap<String, Integer>(tTags.length);
        for (int i = 0; i < tTags.length; i++) {
            tJavaTasks.put(tTags[i], getPriority(tPrios[i]));
        }
        return tJavaTasks;
    }

    private Pattern compilePattern(Map<String, Integer> aJavaTasks) {
        return Pattern.compile(produceTagList(aJavaTasks), Pattern.CASE_INSENSITIVE);
    }

    private String produceTagList(Map<String, Integer> aJavaTasks) {
        StringBuilder tTagList = new StringBuilder();
        for (String tagName : aJavaTasks.keySet()) {
            if (tTagList.length() != 0) {
                tTagList.append("|");
            }
            tTagList.append(tagName);
        }
        return "(?:(?:(?://|\\*|<!--).*\\s)(" + tTagList.toString() + ")(?::|\\s))";
    }

    private Integer getPriority(String priority) {
        if (JavaCore.COMPILER_TASK_PRIORITY_LOW.equals(priority)) {
            return TASK_PRIORITY_LOW;
        } else if (JavaCore.COMPILER_TASK_PRIORITY_NORMAL.equals(priority)) {
            return TASK_PRIORITY_NORMAL;
        } else if (JavaCore.COMPILER_TASK_PRIORITY_HIGH.equals(priority)) {
            return TASK_PRIORITY_HIGH;
        }
        return TASK_PRIORITY_NORMAL;
    }

    private String getValue(String aKey) {
        return JDTCoreUtils.getJDTCoreKey(aKey);
    }

    private LPEXTask produceTask(Matcher aMatcher, Map<String, Integer> aJavaTasks) throws Exception {
        int tStart = aMatcher.start(1);
        int tEnd = aMatcher.end(1);
        IRegion tRange = document.getLineInformationOfOffset(tEnd);

        String tTaskTag = aMatcher.group(1).trim();
        String tTaskDescription = retrieveTaskDescription(tStart, tRange);
        Integer tPriority = findPriority(aJavaTasks, tTaskTag);

        LPEXTask tTask = new LPEXTask(tTaskTag, tTaskDescription, document.getLineOfOffset(tStart) + 1, tPriority, tStart);
        return tTask;
    }

    private String retrieveTaskDescription(int tStart, IRegion tRange) throws BadLocationException {

        String description = document.get(tStart, tRange.getOffset() + tRange.getLength() - tStart).trim();
        if (description != null) {
            if (description.endsWith(XML_END_COMMENT)) {
                description = description.substring(0, description.length() - XML_END_COMMENT.length());
            } else if (description.endsWith(CL_END_COMMENT)) {
                description = description.substring(0, description.length() - CL_END_COMMENT.length());
            }
        }

        return description.trim();
    }

    private Integer findPriority(Map<String, Integer> aJavaTasks, String aTaskTag) {
        Integer tPriority = aJavaTasks.get(aTaskTag);
        if (tPriority != null) {
            return tPriority;
        }
        return TASK_PRIORITY_NORMAL;
    }
}
