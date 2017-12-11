package com.notes.nicefact.to;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.notes.nicefact.util.CurrentContext;

public class SearchTO {
	int first = 0;
	int limit = 10;

	String searchTerm;
	String email;
	long groupId;
	long date;
	String fromTime;
	long studentId;
	
	HttpServletRequest request;

	public int getFirst() {
		return first;
	}

	public SearchTO setFirst(int first) {
		this.first = first;
		return this;
	}

	public int getLimit() {
		return limit;
	}

	public SearchTO setLimit(int limit) {
		this.limit = limit;
		return this;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public SearchTO setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public SearchTO setEmail(String email) {
		this.email = email;
		return this;
	}

	public long getDate() {
		return date;
	}

	public SearchTO setDate(long date) {
		this.date = date;
		return this;
	}

	public String getFromTime() {
		return fromTime;
	}

	public SearchTO setFromTime(String fromTime) {
		this.fromTime = fromTime;
		return this;
	}

	public HttpServletRequest getRequest() {
		return request;
	}
	
	public SearchTO setRequest(HttpServletRequest request){
		this.request = request;
		searchTerm = request.getParameter("q");
		if (StringUtils.isNotBlank(request.getParameter("offset"))) {
			first = Integer.parseInt(request.getParameter("offset"));
		}
		if(null !=request.getParameter("date")){
			this.date = Long.parseLong(request.getParameter("date"));
		}
		return this;
	}

	public String getNextLink() {
		String nextLink = null;
		if (null != request) {
			nextLink = request.getRequestURI() + "?offset=" + (first + limit);
			if (StringUtils.isNotBlank(searchTerm)) {
				nextLink += "&q=" + searchTerm;
			}
		}
		return nextLink;
	}

	public long getGroupId() {
		return groupId;
	}

	public SearchTO setGroupId(long groupId) {
		this.groupId = groupId;
		return this;
	}

	private SearchTO() {
		super();
		if (CurrentContext.getAppUser() != null) {
			this.email = CurrentContext.getEmail();
		}
	}

	public static SearchTO getInstances() {
		SearchTO x = new SearchTO();
		return x;
	}

	public SearchTO(HttpServletRequest request, int max) {
		super();
		this.limit = max;
		setRequest(request);
	}
}
