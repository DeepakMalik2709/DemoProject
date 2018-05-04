package com.notes.nicefact.util;

import org.apache.commons.lang.StringUtils;

/**
 * Paggination 
 * @author Dheeraj Kuamr
 *
 */
public class Pagination {
	//static Logger logger = LogManager.getLogger(Pagination.class);

	private Integer offset;
	private Integer limit;
	private String sortOrder;
	private String sortColumn;
	private String searchTerm;
	private String[] searchFields;
	private String joinOperation;
	private String[] returnFields;
	public Pagination() {
		
	}
	
	public Pagination(Integer offset, Integer limit, String sortOrder, String sortColumn) {
		this.offset = offset;
		this.limit = limit;
		this.sortOrder = sortOrder;
		this.sortColumn = sortColumn;
	}
	
	public Pagination(Integer offset, Integer limit, String sortOrder, String sortColumn, String searchTerm, String[] searchFields, String[] returnFields, String joinOperation) {
		this.offset = offset;
		this.limit = limit;
		this.sortOrder = sortOrder;
		this.sortColumn = sortColumn;
		this.searchTerm = searchTerm;
		this.searchFields = searchFields;
		this.returnFields = returnFields;
		this.joinOperation = joinOperation;
	}
	
	/**
	 * @return the offset
	 */
	public Integer getOffset() {
		//Decrease by 1 due to Actual index on pagination offset will start from 0. 
		//return (offset == null ? null : offset.intValue()-1);
		return offset;
	}
	
	/**
	 * @return the limit
	 */
	public Integer getLimit() {
		return limit;
	}
	
	/**
	 * @return the sortColumn
	 */
	public String getSortColumn() {
		return sortColumn;
	}
	
	/**
	 * @return the sortOrder
	 */
	public String getSortOrder() {
		return sortOrder;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public String[] getSearchFields() {
		return searchFields;
	}

	public String[] getReturnFields() {
		return returnFields;
	}

	public String getJoinOperation() {
		if(StringUtils.isBlank(joinOperation)){
			return Constants.OPER_OR;
		}
		return joinOperation;
	}
	
}