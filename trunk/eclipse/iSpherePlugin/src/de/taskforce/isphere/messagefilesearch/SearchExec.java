/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package de.taskforce.isphere.messagefilesearch;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.Job;
import com.ibm.as400.access.ObjectDoesNotExistException;

import de.taskforce.isphere.ISpherePlugin;

public class SearchExec {
	
	private class Search implements IRunnableWithProgress {

		private AS400 _as400;
		private String _host;
		private Connection _jdbcConnection;
		private String _string;
		private int _fromColumn;
		private int _toColumn;
		private String _case;
		private ArrayList<SearchElement> _searchElements;
		private int _handle;
		private int _counter;

		public Search(
				AS400 _as400,
				String _host,
				Connection _jdbcConnection,
				String _string,
				int _fromColumn,
				int _toColumn,
				String _case,
				ArrayList<SearchElement> _searchElements) {
			this._as400 = _as400;
			this._host = _host;
			this._jdbcConnection = _jdbcConnection;
			this._string = _string;
			this._fromColumn = _fromColumn;
			this._toColumn = _toColumn;
			this._case = _case;
			this._searchElements = _searchElements;
		}

		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			
			String currentLibrary = getCurrentLibrary(_as400);
		
			if (currentLibrary != null) {

				if (setCurrentLibrary(_as400, iSphereLibrary)) {
					
					_handle = new XFNDSTR_getHandle().run(_as400);
					
					if (_handle > 0) {
						
						SearchElement.setSearchElements(_jdbcConnection, _handle, _searchElements);
						
						int _numberOfSearchElements = new XFNDSTR_getNumberOfSearchElements().run(_as400, _handle);
						
						monitor.beginTask("Searching", _numberOfSearchElements);
						
						new DoSearch(
								_as400,
								_handle,
								_string,
								_fromColumn,
								_toColumn,
								_case).start();

						int _lastCounter = 0;
						
						getStatus();
						
						boolean _cancel = false;
						
						while (_counter != -1) {
							
							monitor.worked(_counter - _lastCounter);
							
							_lastCounter = _counter;
							
							if(monitor.isCanceled()) {
								_cancel = true;
								cancel();
								break;
							}

							try {
								Thread.sleep(500);
							} 
							catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							getStatus();

						}
						
						monitor.worked(_numberOfSearchElements - _lastCounter);

						monitor.done();
						
						if (!_cancel) {
							_searchResults = SearchResult.getSearchResults(_jdbcConnection, _handle, _as400, _host);
						}
						
						new XFNDSTR_clear().run(_as400, _handle);
						
					}
					
					setCurrentLibrary(_as400, currentLibrary);
					
				}
				
			}
			
		}

		private String getCurrentLibrary(AS400 _as400) {
			
			String currentLibrary = null;
			
			Job[] jobs = _as400.getJobs(AS400.COMMAND);
			
			if (jobs.length == 1) {
				
				try {
					if (!jobs[0].getCurrentLibraryExistence()) {
						currentLibrary = "*CRTDFT";
					}
					else {
						currentLibrary = jobs[0].getCurrentLibrary();
					}
				} 
				catch (AS400SecurityException e) {
					e.printStackTrace();
				} 
				catch (ErrorCompletingRequestException e) {
					e.printStackTrace();
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				} 
				catch (IOException e) {
					e.printStackTrace();
				} 
				catch (ObjectDoesNotExistException e) {
					e.printStackTrace();
				}
				
			}
			
			return currentLibrary;

		}

		private boolean setCurrentLibrary(AS400 _as400, String currentLibrary) {
			
			String command = "CHGCURLIB CURLIB(" + currentLibrary + ")";
			CommandCall commandCall = new CommandCall(_as400);
			boolean ok = false;
			try {
				if (commandCall.run(command)) {
					ok = true;
				}
			} 
			catch (AS400SecurityException e) {
				e.printStackTrace();
			} 
			catch (ErrorCompletingRequestException e) {
				e.printStackTrace();
			} 
			catch (IOException e) {
				e.printStackTrace();
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			} 
			catch (PropertyVetoException e) {
				e.printStackTrace();
			}

			return ok;
			
		}
		
		private void getStatus() {
			
			String _separator;
			try {
				_separator = _jdbcConnection.getMetaData().getCatalogSeparator();
			} 
			catch (SQLException e) {
				_separator = ".";
				e.printStackTrace();
			}
			
			PreparedStatement preparedStatementSelect = null;
			ResultSet resultSet = null;
			try {
				preparedStatementSelect = _jdbcConnection.prepareStatement("SELECT XSCNT, XSCNL FROM " + iSphereLibrary + _separator + "XFNDSTRS WHERE XSHDL = ?");
				preparedStatementSelect.setInt(1, _handle);
				resultSet = preparedStatementSelect.executeQuery();
				if (resultSet.next()) {
					_counter = resultSet.getInt("XSCNT");
				}
			}
			catch (SQLException e) {
			}
			if (resultSet != null) {
				try {
					resultSet.close();
				} 
				catch (SQLException e1) {
				}
			}
			if (preparedStatementSelect != null) {
				try {
					preparedStatementSelect.close();
				} 
				catch (SQLException e1) {
				}
			}
			
		}
		
		private void cancel() {
			
			String _separator;
			try {
				_separator = _jdbcConnection.getMetaData().getCatalogSeparator();
			} 
			catch (SQLException e) {
				_separator = ".";
				e.printStackTrace();
			}
			
			PreparedStatement preparedStatementUpdate = null;
			try {
				preparedStatementUpdate = _jdbcConnection.prepareStatement("UPDATE " + iSphereLibrary + _separator + "XFNDSTRS SET XSCNL = '*YES' WHERE XSHDL = ?");
				preparedStatementUpdate.setInt(1, _handle);
				preparedStatementUpdate.executeUpdate();
			}
			catch (SQLException e) {
			}
			if (preparedStatementUpdate != null) {
				try {
					preparedStatementUpdate.close();
				} 
				catch (SQLException e1) {
				}
			}
			
		}
		
	}

	private class DoSearch extends Thread {
		
		private AS400 _as400;
		private int _handle;
		private String _string;
		private int _fromColumn;
		private int _toColumn;
		private String _case;

		public DoSearch(
				AS400 _as400,
				int _handle,
				String _string,
				int _fromColumn,
				int _toColumn,
				String _case) {
			this._as400 = _as400;
			this._handle = _handle;
			this._string = _string;
			this._fromColumn = _fromColumn;
			this._toColumn = _toColumn;
			this._case = _case;
		}

		public void run() {
			new XFNDSTR_search().run(_as400, _handle, _string, _fromColumn, _toColumn, _case);
		}
		
	}
	
	private String iSphereLibrary;
	private SearchResult[] _searchResults;
	
	public SearchResult[] execute(
			AS400 _as400,
			String _host,
			Connection _jdbcConnection,
			String _string,
			int _fromColumn,
			int _toColumn,
			String _case,
			ArrayList<SearchElement> _searchElements) {
		
		iSphereLibrary = ISpherePlugin.getISphereLibrary();

		_searchResults = null;

		Search runnableWithProgress = 
				new Search(
						_as400,
						_host,
						_jdbcConnection,
						_string,
						_fromColumn,
						_toColumn,
						_case,
						_searchElements);
		try {
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runnableWithProgress);
		} 
		catch (InvocationTargetException e) {
		} 
		catch (InterruptedException e) {
		}
		
		if (_searchResults == null) {
			return new SearchResult[0];
		}
		else {
			return _searchResults;
		}
		
	}
	
}
