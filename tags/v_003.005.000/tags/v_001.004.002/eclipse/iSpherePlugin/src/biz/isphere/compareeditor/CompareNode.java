/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.compareeditor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
 import java.util.Calendar;

import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.graphics.Image;

public class CompareNode extends BufferedContent implements ITypedElement, IEditableContent{
			  
	private IResource fResource;
	private boolean considerDate;
	private int column;
	private File tempFile;
	private String yymmdd;
	
	public CompareNode(IResource fResource, boolean considerDate) {

		this.fResource = fResource;
		this.considerDate = considerDate;
		if (considerDate) {
			column = 6;
		}
		else {
			column = 12;
		}
		
		Assert.isNotNull(fResource);
		
	    Calendar calendar = Calendar.getInstance();
	    String day = Integer.toString(calendar.get(Calendar.DATE));
	    while (day.length() < 2) day = "0" + day; 
	    String year = Integer.toString(calendar.get(Calendar.YEAR)).substring(2);
	    String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
	    while (month.length() < 2) month = "0" + month;
	    yymmdd = year + month + day;

	}

	public String getName() {
		return fResource.getName();
	}
		
	public String getType() {
		return fResource.getFileExtension();
	}

	public Image getImage() {
		return CompareUI.getImage(fResource);
	}

	public boolean isEditable() {
		return true;
	}

	public ITypedElement replace(ITypedElement child, ITypedElement other) {
		return child;
	}

	protected InputStream createStream() throws CoreException {
		try {
			return new BufferedInputStream(new FileInputStream(getTempFile()));
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public File getTempFile() {
		try {
			if (tempFile == null) {
				File file = fResource.getLocation().toFile();
				tempFile = new File(file.getPath() + "_temp");
				BufferedReader in = new BufferedReader(new FileReader(file));
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(tempFile)));
				String oldString;
				while ((oldString = in.readLine()) != null) {
					String newString = new String(oldString.getBytes(),"UTF-8");
					out.println(newString.substring(column));
				}
				in.close();
				out.close();
			}
			return tempFile;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void refreshTempFile() {
		tempFile.delete();
		tempFile = null;
		getTempFile();
	}
	
	public void commit(IProgressMonitor pm) throws Exception {
	    IFile file = (IFile)fResource;
		byte[] bytes= getContent();	
		ByteArrayInputStream is = null;
		try {
		    String contents = new String(bytes);	
			StringBuffer updatedContents = new StringBuffer();
			BufferedReader in = new BufferedReader(new StringReader(contents));
			String s;
			int seq = 0;
			while ((s = in.readLine()) != null) {
			    if (seq < 990000) seq = seq + 100;
			    else if (seq < 999999) seq++;
			    String sequence = Integer.toString(seq);
			    while (sequence.length() < 6) sequence = "0" + sequence;
				String newStr = new String(s.getBytes("UTF-8"));
				if (considerDate) {
				    updatedContents.append(sequence + newStr + "\r\n");
				}
				else {
					updatedContents.append(sequence + yymmdd + newStr + "\r\n");
				}
			}
			in.close();
			is = new ByteArrayInputStream(updatedContents.toString().getBytes());
			if (file.exists())
				file.setContents(is, false, true, pm);
			else
				file.create(is, false, pm);
		} 
		finally {
			if (is != null)
				try {
					is.close();
				} 
				catch(IOException e) {
					e.printStackTrace();
				}
		}
		file.refreshLocal(IFile.DEPTH_INFINITE, pm);
	}

}

