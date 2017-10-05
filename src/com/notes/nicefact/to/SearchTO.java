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

	public void setFirst(int first) {
		this.first = first;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNextLink() {
		return nextLink;
	}

	public void setNextLink(String nextLink) {
		this.nextLink = nextLink;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public SearchTO() {
		super();
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
