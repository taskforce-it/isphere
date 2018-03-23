/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

import java.util.ArrayList;

public class MessageDescription {

	private String connection;
	private String library;
	private String messageFile;
	private String messageId;
	private String message;
	private String helpText;
	private ArrayList fieldFormats;
	
	public MessageDescription() {
		connection = "";
		library = "";
		messageFile = "";
		messageId = "";
		message = "";
		helpText = "";
		fieldFormats = new ArrayList();
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getLibrary() {
		return library;
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public String getMessageFile() {
		return messageFile;
	}

	public void setMessageFile(String messageFile) {
		this.messageFile = messageFile;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}
	
	public ArrayList getFieldFormats() {
		return fieldFormats;
	}

	public void setFieldFormats(ArrayList fieldFormats) {
		this.fieldFormats = fieldFormats;
	}

}
