/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcefilesearch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import biz.isphere.core.ISpherePlugin;


public class SearchResult {

	private String library;
	private String file;
	private String member;
	private String description;
	private SearchResultStatement[] statements;

	public SearchResult() {
		library = "";
		file = "";
		member = "";
		description = "";
		statements = null;
	}

	public String getLibrary() {
		return library;
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SearchResultStatement[] getStatements() {
		return statements;
	}

	public void setStatements(SearchResultStatement[] statements) {
		this.statements = statements;
	}
	
	public static SearchResult[] getSearchResults(Connection jdbcConnection, int handle) {
		
		String _separator;
		try {
			_separator = jdbcConnection.getMetaData().getCatalogSeparator();
		} 
		catch (SQLException e) {
			_separator = ".";
			e.printStackTrace();
		}
		
		ArrayList<SearchResult> arrayListSearchResults = new ArrayList<SearchResult>();
			
		PreparedStatement preparedStatementSelect = null;
		ResultSet resultSet = null;
		
		try {

			preparedStatementSelect = jdbcConnection.prepareStatement("SELECT * FROM " + ISpherePlugin.getISphereLibrary() + _separator + "FNDSTRO WHERE XOHDL = ? ORDER BY XOHDL, XOLIB, XOFILE, XOMBR", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			preparedStatementSelect.setString(1, Integer.toString(handle));
			resultSet = preparedStatementSelect.executeQuery();
			
			String _library = "";
			String _file = "";
			String _member = "";

			String library;
			String file;
			String member;

			SearchResult _searchResult = null;
			ArrayList<SearchResultStatement> alStatements = null;
			
			while (resultSet.next()) {
				
				library = resultSet.getString("XOLIB").trim();
				file = resultSet.getString("XOFILE").trim();
				member = resultSet.getString("XOMBR").trim();

				if (!_library.equals(library) || !_file.equals(file) || !_member.equals(member)) {

					if (_searchResult != null) {
						
						SearchResultStatement[] _statements = new SearchResultStatement[alStatements.size()];
						alStatements.toArray(_statements);

						_searchResult.setStatements(_statements);
						
			    		arrayListSearchResults.add(_searchResult);
			    		
					}
					
					_library = library;
					_file = file;
					_member = member;

					_searchResult = new SearchResult();
					_searchResult.setLibrary(library);
					_searchResult.setFile(file);
					_searchResult.setMember(member);

					alStatements = new ArrayList<SearchResultStatement>();
					
				}
				
				SearchResultStatement statement = new SearchResultStatement();
				statement.setStatement(resultSet.getInt("XOSTMT"));
				statement.setLine(resultSet.getString("XOLINE"));
				alStatements.add(statement);
				
			}
			
			if (_searchResult != null) {
				
				SearchResultStatement[] _statements = new SearchResultStatement[alStatements.size()];
				alStatements.toArray(_statements);

				_searchResult.setStatements(_statements);
				
	    		arrayListSearchResults.add(_searchResult);
	    		
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
		
		SearchResult[] _searchResults = new SearchResult[arrayListSearchResults.size()];
		arrayListSearchResults.toArray(_searchResults);
		return _searchResults;
		
	}
	
}
