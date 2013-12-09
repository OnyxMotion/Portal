package com.jest.database;


public class SQSkvpair {

	private String columnName;
	private String data;
	private String comparator = "=";

	public SQSkvpair(String columnName, String data) {
		this.columnName = columnName;
		this.data = data; 
	}

	public SQSkvpair(String columnName, String data, String comparator) {
		this.columnName = columnName;
		this.data = data; 
		this.comparator = comparator;
	}

	public String getColName() { return this.columnName; }
	public String getData() { return this.data; }

	//SELECT ... WHERE columnName comparator data
	//e.g. comparator is =, !=, >, <, >=, etc
	public void setComparator(String code) { this.comparator = code;  }
	public String getComparator() { return this.comparator; }

}