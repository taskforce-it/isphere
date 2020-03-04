/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.bindingdirectoryeditor;

import java.io.IOException;
import java.sql.Connection;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CharacterDataArea;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.IllegalObjectTypeException;
import com.ibm.as400.access.ObjectDoesNotExistException;

public class BindingDirectoryEditorInput implements IEditorInput {
	
	private String id;
	private AS400 as400;
	private Connection jdbcConnection;
	private String connection;
	private String library;
	private String bindingDirectory;
	private String mode;
	private String name;
	private String toolTip;
	private Image image;
	private String level;
	
	public BindingDirectoryEditorInput(
			String id,
			AS400 as400,
			Connection jdbcConnection,
			String connection,
			String library,
			String bindingDirectory,
			String mode,
			String name, 
			String toolTip, 
			Image image) {
		this.id = id;
		this.as400 = as400;
		this.jdbcConnection = jdbcConnection;
		this.connection = connection;
		this.library = library;
		this.bindingDirectory = bindingDirectory;
		this.mode = mode;
		this.name = name;
		this.toolTip = toolTip;
		this.image = image;
		
		level = "V9R9M9";
		CharacterDataArea iSphere = new CharacterDataArea(as400, "/QSYS.LIB/QGPL.LIB/ISPHERE.DTAARA");
		try {
			String iSphereContent = iSphere.read();
			level = iSphereContent.substring(0, 6);
		} 
		catch (AS400SecurityException e1) {
		} 
		catch (ErrorCompletingRequestException e1) {
		} 
		catch (IllegalObjectTypeException e1) {
		} 
		catch (InterruptedException e1) {
		} 
		catch (IOException e1) {
		} 
		catch (ObjectDoesNotExistException e1) {
		}
		
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
        BindingDirectoryEditorInput other = (BindingDirectoryEditorInput)obj;
        if (!id.equals(other.id) ||
        		!connection.equals(other.connection) ||
        		!library.equals(other.library) ||
        		!bindingDirectory.equals(other.bindingDirectory))
            return false;
        return true;
    }

	public String getId() {
		return id;
	}

	public AS400 getAS400() {
		return as400;
	}

	public Connection getJDBCConnection() {
		return jdbcConnection;
	}

	public String getConnection() {
		return connection;
	}

	public String getLibrary() {
		return library;
	}

	public String getBindingDirectory() {
		return bindingDirectory;
	}

	public String getMode() {
		return mode;
	}

	public String getLevel() {
		return level;
	}

}
