/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.beans.Beans;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class Size {

	private static int fontMagnitude = -1;
	
	public static int getSize(int size) {
		float floatSize = new Integer(size).floatValue();
		if (!Beans.isDesignTime()) {
			if (fontMagnitude == -1) {
				setFontMagnitude();
			}
			floatSize = floatSize / 100 * fontMagnitude;
		}
		int intSize = new Float(floatSize).intValue();
		return intSize;
	}
	
	private static void setFontMagnitude() {
		FontData[] fontData = Display.getDefault().getSystemFont().getFontData();
		int fontHeight = 7;
		for (int idx=0; idx < fontData.length; idx++) {
			if (fontData[idx].getHeight() > fontHeight) {
				fontHeight = fontData[idx].getHeight();
			}
		}
		float magnitude = 100 / 7 * fontHeight;
		fontMagnitude = new Float(magnitude).intValue();
	}
	
}
