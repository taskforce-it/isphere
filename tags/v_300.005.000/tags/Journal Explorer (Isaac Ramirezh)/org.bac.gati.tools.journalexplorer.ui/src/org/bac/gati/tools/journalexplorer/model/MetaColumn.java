package org.bac.gati.tools.journalexplorer.model;

public class MetaColumn {
	
	public enum DataType {
		TIME, TIMESTMP, DATE, CHAR, VARCHAR, CLOB, REAL, DOUBLE, SMALLINT, INTEGER, BIGINT, DECIMAL, NUMERIC
	};

	private String name;

	private String columnText;
	
	private int size;

	private int precision;

	private DataType dataType;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public void setDataType(String dataType) throws Exception {
		try {
			this.dataType = DataType.valueOf(dataType.toUpperCase());
		} catch (Exception exception) {
			throw new Exception("Unsupported datatype: " + dataType);
		}
	}

	public String getColumnText() {
		return columnText;
	}

	public void setColumnText(String columnText) {
		this.columnText = columnText;
	}
}
