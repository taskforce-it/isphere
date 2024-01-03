/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.test;

import java.io.File;
import java.util.Properties;

import biz.isphere.antcontrib.winword.WdApplication;
import biz.isphere.antcontrib.winword.WdDocument;
import biz.isphere.antcontrib.winword.WdSaveFormat;

public class WinwordTest {

	public static final String PROJECT_HOME = "C:\\Workspaces\\rdp_095\\workspace\\iSphere Ant Contribution";

	public static void main(String[] args) {
		WinwordTest main = new WinwordTest();
		main.run(args);
	}

	private void run(String[] args) {

		String jacobLib = new File(PROJECT_HOME, "lib").getPath();
		Properties props = System.getProperties();
		props.setProperty("java.library.path",
				props.getProperty("java.library.path") + File.pathSeparator
						+ jacobLib);

		WdApplication winword = new WdApplication(true);

		try {

			String wordDocument = new File(PROJECT_HOME, "Test.doc").getPath();

			WdDocument document = winword.getDocuments().open(wordDocument);

			document.saveAs("Test.doc.txt", WdSaveFormat.TEXT);

			// winword.closeDocument(document, false);

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (winword != null) {
				winword.quit();
			}
		}
	}

}