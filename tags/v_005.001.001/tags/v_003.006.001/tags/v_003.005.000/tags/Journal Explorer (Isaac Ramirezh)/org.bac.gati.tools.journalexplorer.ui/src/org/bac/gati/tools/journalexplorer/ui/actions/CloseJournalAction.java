package org.bac.gati.tools.journalexplorer.ui.actions;

import org.eclipse.jface.action.Action;

public class CloseJournalAction extends Action {

	private String library;
	
	private String fileName;
	
	private String connectionName;

	public CloseJournalAction(String library, String fileName, String connectionName) {
		
		super();
		this.library = library;
		this.fileName = fileName;
		this.connectionName = connectionName;
	}

	@Override
	public String getText() {
		return this.connectionName.trim() + ":" + this.library.trim() + "/" + this.fileName.trim();
	}
	
	public String getLibrary() {
		return library;
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
	
	
}
