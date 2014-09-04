/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.compareeditor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.compare.structuremergeviewer.Differencer;

import biz.isphere.base.internal.StringHelper;

public class CompareDifferencer extends Differencer {

    CompareEditorConfiguration config = null;

    public CompareDifferencer(CompareEditorConfiguration aCompareEditorConfiguration) {
        config = aCompareEditorConfiguration;
    }
    @Override
    protected boolean contentsEqual(Object input1, Object input2) {

        if (input1 == input2) return true;

        InputStream is1 = getStream(input1, config.isIgnoreCase());
        InputStream is2 = getStream(input2, config.isIgnoreCase());
        BufferedReader br1 = null;
        BufferedReader br2 = null;

        if (is1 == null && is2 == null) return true;

        try {
            if (is1 == null || is2 == null) return false;

            br1 = new BufferedReader(new InputStreamReader(is1));

            br2 = new BufferedReader(new InputStreamReader(is2));

            while (true) {

                String s1;
                String s2;

                s1 = br1.readLine();
                s2 = br2.readLine();

                if (s1 == null && s2 == null) return true;
                if (s1 == null && s2 != null) break;
                if (s2 == null && s1 != null) break;

                /*
                 * We need to trim the input strings, because the LPEX editor
                 * may also strips trailing spaces on save depending on it
                 * configuration properties. (LPEX Editor -> Save -> Trim
                 * trailing blanks) Hence two actually identical lines may be
                 * shown as different, when one input source has been saved on
                 * the PC (and trimmed) whereas the other input source has just
                 * been downloaded from the i (not trimmed).
                 */
                s1 = StringHelper.trimR(s1);
                s2 = StringHelper.trimR(s2);
                if (!s1.equals(s2)) break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is1 != null) {
                try {
                    is1.close();
                    br1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is2 != null) {
                try {
                    is2.close();
                    br2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;

    }

    private InputStream getStream(Object o, boolean ignoreCase) {
        try {
            return new BufferedInputStream(new FileInputStream(((CompareNode)o).getTempFile(ignoreCase)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
