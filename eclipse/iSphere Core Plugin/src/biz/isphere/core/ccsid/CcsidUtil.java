/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.ccsid;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Properties;

public final class CcsidUtil {

    private Properties map;
    private DecimalFormat ccsidFormat = new DecimalFormat("##000");

    public String getAsciiCodepage(int ebcdic) {
        return getEbcdicAsciiMap().getProperty(ccsidFormat.format(ebcdic));
    }

    private Properties getEbcdicAsciiMap() {
        if (map != null) {
            return map;
        }

        InputStream reader = null;

        try {
            map = new Properties();
            reader = getClass().getClassLoader().getResourceAsStream("biz/isphere/core/ccsid/ebcdicAsciiMapping.txt");
            map.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        return map;
    }

}