/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.messagefileeditor;

public class FieldFormat {

	private String type;
	private boolean vary;
	private int bytes;
	private int length;
	private int decimalPositions;
	
	public FieldFormat() {
		type = "";
		vary = false;
		bytes = 0;
		length = 0;
		decimalPositions = 0;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isVary() {
		return vary;
	}

	public void setVary(boolean vary) {
		this.vary = vary;
	}

	public int getBytes() {
		return bytes;
	}

	public void setBytes(int bytes) {
		this.bytes = bytes;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getDecimalPositions() {
		return decimalPositions;
	}

	public void setDecimalPositions(int decimalPositions) {
		this.decimalPositions = decimalPositions;
	}
	
}
