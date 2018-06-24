package com.notes.nicefact.to;

public class SearchInfoTO {

	private int first = 0;
	private int limit = 10;
	private String searchIn;
	private String searchData;
	
	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getSearchIn() {
		return searchIn;
	}
	
	public void setSearchIn(String searchIn) {
		this.searchIn = searchIn;
	}
	
	public String getSearchData() {
		return searchData;
	}
	
	public void setSearchData(String searchData) {
		this.searchData = searchData;
	}
}
