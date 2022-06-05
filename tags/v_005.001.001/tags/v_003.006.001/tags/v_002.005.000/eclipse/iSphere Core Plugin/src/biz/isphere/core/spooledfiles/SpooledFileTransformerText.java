/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import biz.isphere.core.ISpherePlugin;

import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.SpooledFile;

public class SpooledFileTransformerText extends AbstractSpooledFileTransformer {

    private BufferedWriter writer = null;

    public SpooledFileTransformerText(SpooledFile spooledFile) {
        super(spooledFile);
    }

    @Override
    protected QSYSObjectPathName getWorkstationCustomizationObject() {
        return new QSYSObjectPathName(ISpherePlugin.getISphereLibrary(), "SPLFTXT", "WSCST");
    }

    @Override
    protected void openPrinter(String target) throws IOException {
        writer = new BufferedWriter(new FileWriter(target));
    }

    @Override
    protected void closePrinter() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }

    @Override
    protected void initPrinter() throws IOException {
    }

    @Override
    protected void resetPrinter() throws IOException {
    }

    @Override
    protected void formfeed() throws IOException {
        writer.write("- New Page -");
        newLine();
    }

    @Override
    protected void newLine() throws IOException {
        writer.write(CR_LF);
    }

    @Override
    protected void print(String text) throws IOException {
        writer.write(text);
    }

}
