/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.helpbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.DomDriver;

import biz.isphere.core.helpbuilder.configuration.Configuration;
import biz.isphere.core.helpbuilder.exception.JobCanceledException;
import biz.isphere.core.helpbuilder.model.Anchor;
import biz.isphere.core.helpbuilder.model.Project;
import biz.isphere.core.helpbuilder.model.Toc;
import biz.isphere.core.helpbuilder.model.Topic;
import biz.isphere.core.helpbuilder.utils.FileUtil;
import biz.isphere.core.helpbuilder.utils.LogUtil;

public class HelpBuilder {

    public static void main(String[] args) {

        try {
            HelpBuilder main = new HelpBuilder();
            main.run(args);
        } catch (JobCanceledException e) {
            e.printStackTrace();
        }
    }

    private void run(String[] args) throws JobCanceledException {

        if (args.length >= 2) {
            Configuration.getInstance().setConfigurationFile(args[0], args[1]);
        }

        File baseDir = Configuration.getInstance().getWorkspace();
        Project[] projects = Configuration.getInstance().getProjects();

        Toc[] tocs = loadTocs(projects);
        resolveLinks(baseDir, tocs);

        String html = generateHtml(tocs, "treemenu");
        writeTocToFile(Configuration.getInstance().getOutputFile(), html);

        copyHelpPages(projects, Configuration.getInstance().getOutputDirectory());
    }

    private void copyHelpPages(Project[] projects, String outputDirectory) throws JobCanceledException {

        try {

            for (Project project : projects) {
                File srcDir = new File(project.getHelpDirectory());
                File destDir = new File(outputDirectory);
                LogUtil.debug("Copying html files: " + srcDir + " -> " + destDir);
                FileUtils.copyDirectory(srcDir, destDir, new FileFilter() {

                    public boolean accept(File pathname) {
                        if (pathname.getPath().endsWith(".svn")) {
                            return false;
                        }
                        LogUtil.debug("Copying html file: " + pathname);
                        return true;
                    }
                });
            }

        } catch (IOException e) {
            throw new JobCanceledException(e.getLocalizedMessage(), e);
        }

        return;
    }

    private void writeTocToFile(String outputFile, String html) throws JobCanceledException {

        PrintWriter out = null;
        try {
            out = new PrintWriter(outputFile);
            out.write(html);
        } catch (FileNotFoundException e) {
            throw new JobCanceledException(e.getLocalizedMessage(), e);
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    private String generateHtml(Toc[] tocs, String menuName) {

        StringBuilder html = new StringBuilder();

        html.append("// ---------------------------------------------------------- //\n");
        html.append("//  http://www.dynamicdrive.com/dynamicindex1/navigate1.htm   //\n");
        html.append("// ---------------------------------------------------------- //\n");
        html.append("document.write('\\");
        html.append("<ul id=\"" + menuName + "\" class=\"treeview\">");
        html.append(Configuration.NEW_LINE);
        generateHtml(html, tocs[0].getTopics(), "", 1);
        html.append("</ul>");
        html.append(Configuration.NEW_LINE);
        html.append("');");

        return html.toString();
    }

    private void generateHtml(StringBuilder html, Topic[] topics, String parent, int level) {

        int count = 0;

        for (Topic topic : topics) {

            html.append(space(level * 2));
            html.append("<li>");

            if (topic.getHref() != null) {
                String href = removePluginPath(topic.getHref());
                html.append("<a href=\"../" + href + "\" target=\"content\" style=\"text-decoration:none\">");
            } else {
                LogUtil.warn("Attribute 'href' of topic " + topic.getLabel() + " not found; ");
            }
            html.append(topic.getLabel());
            if (topic.getHref() != null) {
                html.append("</a>");
            }

            if (topic.getTopics().length > 0) {
                html.append(Configuration.NEW_LINE);
                html.append(space(level * 2));
                html.append("<ul>");
                html.append(Configuration.NEW_LINE);
                generateHtml(html, topic.getTopics(), parent + count + ".", level + 1);
                html.append(space(level * 2));
                html.append("</ul>");
                html.append(Configuration.NEW_LINE);
                html.append(space(level * 2));
            }

            html.append("</li>");
            html.append(Configuration.NEW_LINE);
        }
    }

    private String removePluginPath(String href) {

        int i = href.indexOf("html/");
        if (i <= 0) {
            return href;
        }

        return href = href.substring(i);
    }

    private String space(int level) {

        StringBuilder space = new StringBuilder();
        while (level > 0) {
            space.append(" ");
            level--;
        }

        return space.toString();
    }

    private void resolveLinks(File baseDir, Toc[] tocs) throws JobCanceledException {

        try {

            for (Toc toc : tocs) {
                if (toc.getLinkTo() != null) {
                    Topic topic = findReferencedTopic(baseDir, toc, tocs);
                    if (topic != null) {
                        topic.addTopics(toc.getTopics());
                    }
                }
            }

        } catch (IOException e) {
            throw new JobCanceledException(e.getLocalizedMessage(), e);
        }
    }

    private Topic findReferencedTopic(File baseDir, Toc toc, Toc[] tocs) throws IOException {

        LogUtil.debug("Starting to find referenced topic: " + toc.getLabel() + " in folder: " + baseDir);

        String linkToFile = toc.getLinkToFile();
        if (linkToFile.startsWith("..")) {
            linkToFile = linkToFile.substring(2);
        }

        File tBaseDir;
        if (linkToFile.startsWith(".")) {
            linkToFile = linkToFile.substring(1);
            tBaseDir = new File(baseDir, toc.getParent().getPluginPath());
        } else {
            tBaseDir = baseDir;
        }

        String linkToTocPath = FileUtil.resolvePath(tBaseDir, linkToFile);
        String linkToReference = toc.getLinkToReference();

        if (linkToTocPath == null || linkToReference == null) {
            LogUtil.warn("...attributes 'linkToTocPath' or 'linkToReference' are [null].");
            return null;
        }

        for (Toc tocItem : tocs) {
            String pluginTocPath = tocItem.getPluginTocPath();
            if (linkToTocPath.equals(pluginTocPath)) {
                LogUtil.debug("...found referenced topic: " + linkToTocPath);
                return findAnchor(linkToReference, tocItem.getTopics());
            }
        }

        LogUtil.warn("...could not find referenced topic: " + toc.getLabel());
        return null;
    }

    private Topic findAnchor(String linkToReference, Topic[] topics) {
        LogUtil.debug("Start searching for anchor '" + linkToReference + "'...");
        if (topics != null) {
            for (Topic topic : topics) {
                Anchor anchor = topic.getAnchor();
                if (anchor != null && linkToReference.equals(anchor.getId())) {
                    LogUtil.debug("...found topic of anchor '" + anchor.getId() + "': " + topic.getLabel());
                    return topic;
                } else {
                    Topic matchingTopic = findAnchor(linkToReference, topic.getTopics());
                    if (matchingTopic != null) {
                        LogUtil.print("...found topic: " + matchingTopic.getLabel());
                        return matchingTopic;
                    }
                }
            }
        }
        LogUtil.warn("Could not find topic reference: " + linkToReference);
        return null;
    }

    private Toc[] loadTocs(Project[] projects) {

        List<Toc> tocs = new ArrayList<Toc>();

        for (int i = 0; i < projects.length; i++) {
            List<Toc> newTocs = loadFromXml(projects[i]);
            if (newTocs != null && newTocs.size() > 0) {
                tocs.addAll(newTocs);
                LogUtil.debug("Created table of content object: " + projects[i]);
            }
        }

        return tocs.toArray(new Toc[tocs.size()]);
    }

    private List<Toc> loadFromXml(Project project) {

        LogUtil.debug("Start loading table of content objects...");

        List<Toc> tocs = new ArrayList<Toc>();

        try {

            String[] tocPaths = project.getTocs();
            for (int i = 0; i < tocPaths.length; i++) {

                LogUtil.debug("Loading table of content from xml: " + tocPaths[i]);

                String tocPath = tocPaths[i];
                BufferedReader reader = new BufferedReader(new FileReader(new File(tocPath)));
                String line;
                StringBuffer xml = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    xml.append(line);
                }
                reader.close();

                Toc toc = (Toc)getXStream().fromXML(xml.toString());
                toc.setPluginTocPath(generatePluginTocPath(project, i));
                toc.setParent(project);

                tocs.add(toc);
            }

            LogUtil.debug("...finished loading table of content objects.");

            return tocs;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generatePluginTocPath(Project project, int index) {

        String tocPath = project.getTocs()[index];
        String[] parts = tocPath.split(Configuration.FORWARD_SLASH);
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equalsIgnoreCase(project.getPluginPath())) {
                parts[i] = project.getId();
            }
        }

        StringBuilder id = new StringBuilder();
        for (String part : parts) {
            if (id.length() > 0) {
                id.append(Configuration.FORWARD_SLASH);
            }
            id.append(part);
        }

        return id.toString();
    }

    private XStream getXStream() {

        XStream xstream = new XStream(new DomDriver("utf-8", new NoNameCoder()));
        xstream.autodetectAnnotations(true);
        xstream.alias("toc", Toc.class);
        xstream.alias("topic", Topic.class);
        xstream.alias("anchor", Anchor.class);

        return xstream;
    }
}
