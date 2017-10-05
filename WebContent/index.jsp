<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>

<html lang="en" ng-app="notesApp">
<head>
<c:redirect url="/a/public/home" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="/js/lib/jquery.min.1.11.2.js" >  </script>

<script>

function get(){
	$.ajax({
		url : "/a/notes/hello",
		type: 'get',
		 success : function(result){
			 console.log(result);
			 $("#result").html(JSON.stringify(result));
		 },
		 error:function(result){
			 console.log("error", result);
			 $("#result").html(JSON.stringify(result));
		 },
		})
}

function post(){
	$.ajax({
		url : "/a/notes/save",
		data : JSON.stringify({email : "jij"}),
		type: 'post',
		 contentType: 'application/json',
		 success : function(result){
			 console.log(result);
			 $("#result").html(JSON.stringify(result));
		 }
		})
}
</script>
<title>Insert title here</title>
</head>
<body>
Index file

<br>

<button onclick="get();">GET</button>
<br>
 <button onclick="post();">Post</button>
 <br>
 <div id="result">
 </div>
 
 
 <div>
 <img src="${loginUser.photoUrl}">
 </div>
 <div>
 ${loginUser.displayName } - ${loginUser.email } 
 </div>
 <div>
 <a href="/a/logout">Logout</a>
 </div>
</body>
</html>