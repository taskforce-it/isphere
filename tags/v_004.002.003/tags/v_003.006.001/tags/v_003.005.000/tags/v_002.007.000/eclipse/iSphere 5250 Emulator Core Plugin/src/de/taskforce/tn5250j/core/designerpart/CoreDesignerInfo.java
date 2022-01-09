// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this software; see the file COPYING.  If not, write to
// the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
// Boston, MA 02111-1307 USA

package de.taskforce.tn5250j.core.designerpart;

import org.eclipse.jface.viewers.StructuredViewer;

import de.taskforce.tn5250j.core.tn5250jpart.ITN5250JPart;
import de.taskforce.tn5250j.core.tn5250jpart.TN5250JInfo;

public abstract class CoreDesignerInfo extends TN5250JInfo {
	
	private String connection;
	private String session;
	private String library;
	private String sourceFile;
	private String member;
	private String editor;
	private String mode;
	private String currentLibrary;
	private String libraryList;
	private Object[] visibleObject;
	private StructuredViewer structuredViewer;
	private Object objectToBeSelected;

	public CoreDesignerInfo(ITN5250JPart tn5250jPart) {
		super(tn5250jPart);
		connection = "";
		session = "";
		library = "";
		sourceFile = "";
		member = "";
		editor = "";
		mode = "";
		currentLibrary = "";
		libraryList = "";
		visibleObject = null;
		structuredViewer = null;
		objectToBeSelected = null;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getLibrary() {
		return library;
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public String getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getCurrentLibrary() {
		return currentLibrary;
	}

	public void setCurrentLibrary(String currentLibrary) {
		this.currentLibrary = currentLibrary;
	}

	public String getLibraryList() {
		return libraryList;
	}

	public void setLibraryList(String libraryList) {
		this.libraryList = libraryList;
	}

	public Object[] getVisibleObject() {
		return visibleObject;
	}

	public void setVisibleObject(Object[] visibleObject) {
		this.visibleObject = visibleObject;
	}

	public StructuredViewer getStructuredViewer() {
		return structuredViewer;
	}

	public void setStructuredViewer(StructuredViewer structuredViewer) {
		this.structuredViewer = structuredViewer;
	}

	public Object getObjectToBeSelected() {
		return objectToBeSelected;
	}

	public void setObjectToBeSelected(Object objectToBeSelected) {
		this.objectToBeSelected = objectToBeSelected;
	}

	public String getTN5250JDescription() {
		return connection + "-" + library + "/" + sourceFile + "(" + member + ")";
	}
	
	public boolean isTN5250JEqual(TN5250JInfo tn5250jInfo) {
		CoreDesignerInfo designerInfo = (CoreDesignerInfo)tn5250jInfo;
		if (connection.equals(designerInfo.getConnection()) &&
			session.equals(designerInfo.getSession()) &&
			library.equals(designerInfo.getLibrary()) &&
			sourceFile.equals(designerInfo.getSourceFile()) &&
			member.equals(designerInfo.getMember())) {
			return true;
		}
		else {
			return false;
		}
	}
	
}
