package com.notes.nicefact.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class GridRequestParser {

	
	public static Pagination parseRequestForPagination(HttpServletRequest request) {
		Pagination pagination = null; 
		int pageNo = Utils.getIntegerFromRequest(request, "pageNo");
		int limit = Utils.getIntegerFromRequest(request, "limit");
		String sortOrder = Utils.getParamFromRequest(request, "sortOrder");
		String sortColumn = Utils.getParamFromRequest(request, "sortColumn");
		int offset = pageNo == 1? 0 : ((pageNo-1 ) * limit);
		pagination = new Pagination(offset, limit,sortOrder, sortColumn);
		return pagination;
	}

	public static String paginationToQuery(Pagination pagination) {
		String query =  " order by " + pagination.getSortColumn() +" " + pagination.getSortOrder() ;
		
		return query;
	}
	
	public static String paginationToSearchQuery(String tableName) {
		return paginationToSearchQuery(tableName, null , null);
	}
	
	public static String paginationToSearchQuery(String tableName, String additionalQuery,  String additionalQueryJoinOperator) {
		StringBuilder query = new StringBuilder();
		Pagination pagination =  CurrentContext.getPagination();
		if (pagination!=null) {
			 query.append("select ") ;
			 if(pagination.getReturnFields()!=null && pagination.getReturnFields().length>0){
				 query.append("NEW map(") ;
				 for(String field : pagination.getReturnFields()){
					 query.append( "a." + field + " as " + field + "," );
				 }
				 query.deleteCharAt(query.length()-1);
				 query.append(")") ;
			 }else{
				 query.append("a") ;
			 }
			// query.append("a") ;
			 query.append(" from " + tableName + " a") ;
			 query.append(" where a.isDeleted = false AND a.isActive = true ");
			 if(pagination.getSearchFields()!=null && pagination.getSearchFields().length>0){
				 query.append("AND (") ;
				 for(String field : pagination.getSearchFields()){
					 query.append( " a." + field + " like '%" + pagination.getSearchTerm() +"%' " + pagination.getJoinOperation() );
				 }
				 query.setLength(query.length()-pagination.getJoinOperation().length() );
				 query.append(") ") ;
				 if(StringUtils.isNotBlank(additionalQuery)){
					query.append(additionalQueryJoinOperator + " " + additionalQuery) ;
				 }
			 }else if(StringUtils.isNotBlank(additionalQuery)){
				 query.append(" AND " + additionalQuery) ;
			 }
			 query.append( " order by a." + pagination.getSortColumn() + " "
					+ pagination.getSortOrder());
		}
		return query.toString();
	}
	
}
