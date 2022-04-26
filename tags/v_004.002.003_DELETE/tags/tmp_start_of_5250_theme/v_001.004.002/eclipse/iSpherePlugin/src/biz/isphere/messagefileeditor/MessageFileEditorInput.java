/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.messagefileeditor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.ibm.as400.access.AS400;

public class MessageFileEditorInput implements IEditorInput {
	
	private String id;
	private AS400 as400;
	private String connection;
	private String library;
	private String messageFile;
	private String mode;
	private String name;
	private String toolTip;
	private Image image;
	
	public MessageFileEditorInput(
			String id,
			AS400 as400,
			String connection,
			String library,
			String messageFile,
			String mode,
			String name, 
			String toolTip, 
			Image image) {
		this.id = id;
		this.as400 = as400;
		this.connection = connection;
		this.library = library;
		this.messageFile = messageFile;
		this.mode = mode;
		this.name = name;
		this.toolTip = toolTip;
		this.image = image;
	}

	public boolean exists() {
		return false;
	}

	public Image getImage() {
		return image;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return name;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return toolTip;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public int hashCode() {
		return id.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MessageFileEditorInput other = (MessageFileEditorInput)obj;
        if (!id.equals(other.id) ||
        		!connection.equals(other.connection) ||
        		!library.equals(other.library) ||
        		!messageFile.equals(other.messageFile))
            return false;
        return true;
    }

	public String getId() {
		return id;
	}

	public AS400 getAS400() {
		return as400;
	}

	public String getConnection() {
		return connection;
	}

	public String getLibrary() {
		return library;
	}

	public String getMessageFile() {
		return messageFile;
	}

	public String getMode() {
		return mode;
	}

}
