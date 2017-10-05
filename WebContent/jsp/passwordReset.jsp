<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html lang="en" ng-app="trackerApp">
<head>
    <meta charset="utf-8">
    <title>Notes - Password reset</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet">
    <style type="text/css">
        @charset "UTF-8";
/* CSS Document */

body {
    width:100px;
	height:100px;
  background: -webkit-linear-gradient(90deg, #16222A 10%, #3A6073 90%); /* Chrome 10+, Saf5.1+ */
  background:    -moz-linear-gradient(90deg, #16222A 10%, #3A6073 90%); /* FF3.6+ */
  background:     -ms-linear-gradient(90deg, #16222A 10%, #3A6073 90%); /* IE10 */
  background:      -o-linear-gradient(90deg, #16222A 10%, #3A6073 90%); /* Opera 11.10+ */
  background:         linear-gradient(90deg, #16222A 10%, #3A6073 90%); /* W3C */
font-family: 'Raleway', sans-serif;
}

p {
	color:#CCC;
}

.spacing {
	padding-top:7px;
	padding-bottom:7px;
}
.middlePage {
	width: 680px;
    height: 500px;
    position: absolute;
    top:0;
    bottom: 0;
    left: 0;
    right: 0;
    margin: auto;
}

.logo {
	color:#CCC;
}
.pb10{
padding-bottom:10px;
}
    </style>
    <script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
<link href='http://fonts.googleapis.com/css?family=Raleway:500' rel='stylesheet' type='text/css' />
</head>

<body>
<div class="middlePage">
<div class="page-header">
  <h1 class="logo">Notes <small>Welcome to our place!</small></h1>
</div>

<div class="panel panel-info" ng-controller="passwordResetController">
  <div class="panel-heading">
    <h3 class="panel-title">Password Reset</h3>
  </div>
  <div class="panel-body">
 <div class="col-md-10 col-md-offset-1 errorContainer pb10"  id="errorContainer">
		<div class="text-danger"></div>
  </div>
  <div class="row">
  
<div class="col-md-5" >
<a href="/a/oauth/googleLogin"><img class="login-icons" src="/img/signin_google.png" /></a><br/>
<a href="/a/oauth/facebookLogin"><img class="login-icons" src="/img/signin_facebook.png" /></a><br/>
</div>

    <div class="col-md-7" style="border-left:1px solid #ccc;height:160px">
<form class="form-horizontal" action="/a/public/updatePassword" method="POST" >
<fieldset>

  <input id="password" name="password" ng-model="pageState.password" type="password" placeholder="Password" class="form-control input-md">
  <div class="spacing"></div>
  <input id="passwordRe" name="passwordRe" type="password" ng-model="pageState.passwordRe" placeholder="Retype Password" class="form-control input-md">
  <div class="spacing"><a href="/" ><small>Go to login page</small></a><br/></div>
  <button ng-class="{disabled : (!pageState.password || pageState.password !=pageState.passwordRe)}"  type="submit"  id="singlebutton" name="singlebutton" class="btn btn-info btn-sm pull-right">Update Password</button>


</fieldset>
</form>
</div>
    
</div>
    
</div>
</div>

<p><a href="#">About</a> Notes</p>

</div>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.14/angular.min.js"></script>
<script>
var trackerApp = angular.module('trackerApp',[]);
trackerApp.controller('passwordResetController', function($scope, $http, $q) {
	$scope.pageState = {
			password : null,
			passwordRe : null,
	}
	
	
	
});
var contextMsg = "${commonContext.message}";
if(contextMsg){
	$("#errorContainer .text-danger").html(contextMsg);
}
</script>
</body>
</html>