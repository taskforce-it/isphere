/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.taskdef;

import java.util.Vector;

import org.apache.tools.ant.BuildException;

import biz.isphere.antcontrib.internal.FileHelper;
import biz.isphere.antcontrib.winword.WdApplication;
import biz.isphere.antcontrib.winword.WdDocument;

public class Winword extends BasicTask {

    private String file;
    private boolean visible;

    private WdApplication winword;
    private Vector<SaveAs> saveAsFiles;

    public Winword() {
        super();

        saveAsFiles = new Vector<SaveAs>();
        visible = true;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public SaveAs createSaveAs() {
        SaveAs saveAs = new SaveAs();
        saveAsFiles.add(saveAs);
        return saveAs;
    }

    protected void executeTask() {

        if (file == null) {
            throw new BuildException("No file set.");
        }

        try {

            winword = new WdApplication(visible);
            WdDocument document = winword.getDocuments().open(file);

            // handle nested elements
            for (SaveAs saveAs : saveAsFiles) {
                if (isNullOrEmpty(saveAs.getToFile())) {
                    if (saveAs.getSaveFormat() == null) {
                        throw new BuildException("Neither 'toFile' nor 'saveFormat' specified.");
                    } else {
                        saveAs.setToFile(FileHelper.getBareFileName(file));
                    }
                }
                document.saveAs(saveAs.getToFile(), saveAs.getSaveFormat());
            }

        } finally {
            if (winword != null) {
                try {
                    winword.quit();
                } catch (Throwable e) {
                    throw new BuildException("Failed to close MS-Word", e);
                }
            }
        }
    }
}