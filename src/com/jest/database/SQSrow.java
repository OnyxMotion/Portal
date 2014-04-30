package com.jest.database;

import java.util.ArrayList;

public class SQSrow {

	private ArrayList<SQSkvpair> rowItems;

	public SQSrow(SQSkvpair... kvpairs) {
		rowItems = new ArrayList<SQSkvpair>();
		int i; int len = kvpairs.length;
		for(i = 0; i < len; i++) {
			rowItems.add(kvpairs[i]); 
		}
	}

	public void addData(SQSkvpair kvp) {
		rowItems.add(kvp); 
	}

	public ArrayList<SQSkvpair> getData() { return this.rowItems; }

}
