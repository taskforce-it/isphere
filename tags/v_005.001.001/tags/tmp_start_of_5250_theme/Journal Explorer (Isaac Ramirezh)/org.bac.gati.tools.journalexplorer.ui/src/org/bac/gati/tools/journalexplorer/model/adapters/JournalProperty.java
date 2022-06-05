package org.bac.gati.tools.journalexplorer.model.adapters;

public class JournalProperty implements Comparable<JournalProperty> {

	public String name;
	public Object value;
	public Object parent;
	public boolean highlighted;
	
	public JournalProperty(String name, Object value, Object parent) {
		this.name = name;
		this.value = value;
		this.parent = parent;
	}
	
	public int compareTo(JournalProperty comparable) {
		
		if (this.name.equals(comparable.name) && this.value.equals(comparable.value)) {
			this.highlighted = comparable.highlighted = false;
			return 0;
		} else {
			this.highlighted = comparable.highlighted = true;
			return -1;
		}
	}
	
}
