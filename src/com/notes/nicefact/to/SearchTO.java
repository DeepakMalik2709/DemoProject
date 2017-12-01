package com.notes.nicefact.to;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.notes.nicefact.util.CurrentContext;

public class SearchTO {
	int first = 0;
	int limit = 10;

	String searchTerm;
	String nextLink;
	String email;
	long groupId;

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

	public String getNextLink() {
		return nextLink;
	}

	public SearchTO setNextLink(String nextLink) {
		this.nextLink = nextLink;
		return this;
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
	}

	public static SearchTO getInstances(){
		SearchTO x = new SearchTO();
		return x;
	}
	
	public SearchTO(HttpServletRequest request, int max) {
		super();
		searchTerm = request.getParameter("q");
		this.limit = max;
		if (StringUtils.isNotBlank(request.getParameter("offset"))) {
			first = Integer.parseInt(request.getParameter("offset"));
		}
		if (CurrentContext.getAppUser() != null) {
			this.email = CurrentContext.getEmail();
		}

		this.nextLink = request.getRequestURI() + "?offset=" + (first + max);
		if (StringUtils.isNotBlank(searchTerm)) {
			this.nextLink += "&q=" + searchTerm;
		}
	}
}
