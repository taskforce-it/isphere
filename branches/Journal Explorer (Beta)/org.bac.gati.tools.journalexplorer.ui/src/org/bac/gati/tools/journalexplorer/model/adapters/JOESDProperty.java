package org.bac.gati.tools.journalexplorer.model.adapters;

import java.util.ArrayList;
import org.bac.gati.tools.journalexplorer.internals.JoesdParser;
import org.bac.gati.tools.journalexplorer.model.Journal;
import org.bac.gati.tools.journalexplorer.model.MetaColumn;
import org.bac.gati.tools.journalexplorer.model.MetaDataCache;
import org.bac.gati.tools.journalexplorer.model.MetaTable;
import com.ibm.as400.access.Record;

public class JOESDProperty extends JournalProperty {

	private Journal journal;
	
	private MetaTable metatable;
	
	private ArrayList<JournalProperty> specificProperties;
	
	private Record parsedJOESD; 
	
	private boolean errorParsing;
	
	public JOESDProperty(String name, Object value, Object parent, Journal journal) {
		
		super(name, "", parent);
		this.journal = journal;
		this.errorParsing = false;
		
		this.executeParsing();
	}
	
	public void executeParsing() {
		try {
			this.initialize();
			this.parseJOESD();
		} catch (Exception exception) {
			this.value = exception.getMessage();
			this.errorParsing = true;
		}
	}
	
	private void initialize() throws Exception {

		this.errorParsing = false;
		this.value = "";
		
		this.metatable = null;
		
		this.parsedJOESD = null;
		
		if (this.specificProperties != null) {
			this.specificProperties.clear();
		} else {
			this.specificProperties = new ArrayList<JournalProperty>();
		}
	}
	
	private void parseJOESD() throws Exception {
		
		String columnName;
		
		this.metatable = MetaDataCache.INSTANCE.retrieveMetaData(this.journal);
		
		this.parsedJOESD = new JoesdParser(this.metatable).procesar(this.journal);
		
		for (MetaColumn column : this.metatable.getColumns()) {
			columnName = column.getName().trim();
			if (column.getColumnText().trim() != "") {
				columnName += " (" + column.getColumnText().trim() + ")";
			}
			
			this.specificProperties.add(new JournalProperty(columnName, this.parsedJOESD.getField(column.getName()).toString(), this));
		}
	}
	
	public Object[] toPropertyArray() {
		if (this.specificProperties != null) {
			return this.specificProperties.toArray();
		} else {
			return null;
		}
	}
	
	@Override
	public int compareTo(JournalProperty comparable) {
		
		if (comparable instanceof JOESDProperty) {
			JOESDProperty joesdSpecificProperty = (JOESDProperty) comparable;
			
			if (joesdSpecificProperty.parsedJOESD.getNumberOfFields() != this.parsedJOESD.getNumberOfFields()) {
				this.highlighted = comparable.highlighted = true;
				return -1;
				
			} else {
				int status = 0;
					
				for (int i = 0; i < this.specificProperties.size(); i++) {
					
					if (this.specificProperties.get(i).compareTo(joesdSpecificProperty.specificProperties.get(i)) != 0) {
						status = -1;
					}
				}
				return status;
			}
		} else {
			return -1;
		}
	}
	
	public boolean isErrorParsing() {
		return this.errorParsing;
	}
}
