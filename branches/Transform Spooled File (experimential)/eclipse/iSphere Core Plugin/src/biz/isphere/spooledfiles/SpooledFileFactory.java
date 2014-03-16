/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.spooledfiles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.ISpherePlugin;
import biz.isphere.internal.ISphereHelper;

import com.ibm.as400.access.AS400;


public class SpooledFileFactory {

	public static SpooledFile[] getSpooledFiles(Shell shell, AS400 as400, Connection jdbcConnection, SpooledFileFilter filter) {
		
		if (ISphereHelper.checkISphereLibrary(shell, as400)) {
		
			String iSphereLibrary = ISpherePlugin.getISphereLibrary();
			
			String currentLibrary = null;
			try {
				currentLibrary = ISphereHelper.getCurrentLibrary(as400);
			} 
			catch (Exception e) {
			}
		
			if (currentLibrary != null) {
	
				boolean ok = false;
				try {
					ok = ISphereHelper.setCurrentLibrary(as400, iSphereLibrary);
				} 
				catch (Exception e1) {
				}
				
				if (ok) {
					
					SpooledFile[] _spooledFiles = null;
					
					new SPLF_prepare().run(as400);
		
					if (filter.getUser() != null) {
						new SPLF_setUser().run(as400, filter.getUser());
					}
	
					if (filter.getOutputQueue() != null) {
						String library;
						if (filter.getOutputQueueLibrary() != null) {
							library = filter.getOutputQueueLibrary();
						}
						else {
							library = "*LIBL";
						}
						new SPLF_setOutputQueue().run(as400, filter.getOutputQueue(), library);
					}
					
					if (filter.getUserData() != null) {
						new SPLF_setUserData().run(as400, filter.getUserData());
					}
	
					if (filter.getFormType() != null) {
						new SPLF_setFormType().run(as400, filter.getFormType());
					}
					
					int handle = new SPLF_build().run(as400);
					
					if (handle > 0) {
						
						String _separator;
						try {
							_separator = jdbcConnection.getMetaData().getCatalogSeparator();
						} 
						catch (SQLException e) {
							_separator = ".";
							e.printStackTrace();
						}
						
						ArrayList<SpooledFile> arrayListSpooledFiles = new ArrayList<SpooledFile>();
							
						PreparedStatement preparedStatementSelect = null;
						ResultSet resultSet = null;
						
						try {
	
							preparedStatementSelect = jdbcConnection.prepareStatement("SELECT * FROM " + ISpherePlugin.getISphereLibrary() + _separator + "SPLF WHERE SFHDL = ? ORDER BY SFHDL, SFCNT", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
							preparedStatementSelect.setString(1, Integer.toString(handle));
							resultSet = preparedStatementSelect.executeQuery();
							
							while (resultSet.next()) {
	
								SpooledFile _spooledFile = new SpooledFile();
								_spooledFile.setAS400(as400);
								_spooledFile.setFile(resultSet.getString("SFSPLF").trim());
								_spooledFile.setFileNumber(resultSet.getInt("SFSPLFNBR"));
								_spooledFile.setJobName(resultSet.getString("SFJOBNAME").trim());
								_spooledFile.setJobUser(resultSet.getString("SFJOBUSR").trim());
								_spooledFile.setJobNumber(resultSet.getString("SFJOBNBR").trim());
								_spooledFile.setJobSystem(resultSet.getString("SFJOBSYS").trim());
								_spooledFile.setCreationDate(resultSet.getString("SFCRTDATEX").trim());
								_spooledFile.setCreationTime(resultSet.getString("SFCRTTIMEX").trim());
								_spooledFile.setStatus(resultSet.getString("SFSTS").trim());
								_spooledFile.setOutputQueue(resultSet.getString("SFOUTQ").trim());
								_spooledFile.setOutputQueueLibrary(resultSet.getString("SFOUTQLIB").trim());
								_spooledFile.setOutputPriority(resultSet.getString("SFOUTPTY").trim());
								_spooledFile.setUserData(resultSet.getString("SFUSRDTA").trim());
								_spooledFile.setFormType(resultSet.getString("SFFORMTYPE").trim());
								_spooledFile.setCopies(resultSet.getInt("SFCOPIES"));
								_spooledFile.setPages(resultSet.getInt("SFPAGES"));
								_spooledFile.setCurrentPage(0);
								
					    		arrayListSpooledFiles.add(_spooledFile);
								
							}
							
						}
						catch (SQLException e) {
							e.printStackTrace();
						}
	
						if (resultSet != null) {
							try {
								resultSet.close();
							} 
							catch (SQLException e) {
								e.printStackTrace();
							}
						}
						
						if (preparedStatementSelect != null) {
							try {
								preparedStatementSelect.close();
							} 
							catch (SQLException e) {
								e.printStackTrace();
							}
						}
						
						_spooledFiles = new SpooledFile[arrayListSpooledFiles.size()];
						arrayListSpooledFiles.toArray(_spooledFiles);
						
						new SPLF_clear().run(as400, handle);
						
					}
					
					try {
						ISphereHelper.setCurrentLibrary(as400, currentLibrary);
					} 
					catch (Exception e) {
					}
					
					if (_spooledFiles != null) {
						return _spooledFiles;
					}
					
				}
				
			}
			
		}
		
		return new SpooledFile[0];
		
	}
	
}
