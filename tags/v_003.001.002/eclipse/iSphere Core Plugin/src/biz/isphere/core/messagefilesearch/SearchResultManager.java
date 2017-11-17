/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilesearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.exception.LoadFileException;
import biz.isphere.core.internal.exception.SaveFileException;
import biz.isphere.core.search.SearchOptions;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class SearchResultManager {

    public static final String FILE_EXTENSION = "msgfsr"; //$NON-NLS-1$

    public void saveToXml(String fileName, SearchResultTabFolder searchResults) throws SaveFileException {

        File file = null;

        try {
            file = new File(fileName);
            FileOutputStream stream = new FileOutputStream(file);
            serialize(searchResults, stream);
            stream.flush();
            stream.close();
        } catch (Exception e) {
            ISpherePlugin.logError(e.getMessage(), e);
            throw new SaveFileException(file);
        }
    }

    public SearchResultTabFolder loadFromXml(String fileName) throws LoadFileException {

        File file = new File(fileName);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            StringBuffer xml = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                xml.append(line);
            }
            reader.close();

            return (SearchResultTabFolder)getXStream().fromXML(xml.toString());

        } catch (Throwable e) {
            ISpherePlugin.logError(e.getMessage(), e);
            throw new LoadFileException(file);
        }
    }

    private void serialize(SearchResultTabFolder searchResults, OutputStream stream) {
        XStream xstream = getXStream();
        xstream.toXML(searchResults, stream);
    }

    private XStream getXStream() {
        XStream xstream = new XStream(new DomDriver());
        xstream.autodetectAnnotations(true);
        xstream.alias("sourceFileSearch", SearchResultTabFolder.class); //$NON-NLS-1$
        xstream.alias("tabItem", SearchResultTab.class); //$NON-NLS-1$
        xstream.alias("searchOptions", SearchOptions.class); //$NON-NLS-1$
        xstream.alias("messageFile", SearchResult.class); //$NON-NLS-1$
        xstream.alias("messageId", SearchResultMessageId.class); //$NON-NLS-1$
        return xstream;
    }
}
