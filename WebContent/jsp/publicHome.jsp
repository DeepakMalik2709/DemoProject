<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html >
<html lang="en" ng-app="notesModule">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta charset="utf-8">
		<title>Notes</title>
		<meta name="generator" content="Bootply" />
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
 		<link href="/css/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
		<link href="/css/lib/bootstrap.min.css" rel="stylesheet">
		<!--[if lt IE 9]>
			<script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
		<link href="/css/public-styles.css" rel="stylesheet">
	</head>
	<body ng-controller="rootController">
<header class="navbar navbar-bright navbar-fixed-top" role="banner">
  <div class="container">
    <div class="navbar-header">
      <button class="navbar-toggle" type="button" data-toggle="collapse" data-target=".navbar-collapse">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a href="/" class="navbar-brand">Home</a>
    </div>
    <nav class="collapse navbar-collapse" role="navigation">
      <ul class="nav navbar-nav ng-hide">
        <li>
          <a href="#">Category</a>
        </li>
        <li>
          <a href="#">Category</a>
        </li>
        <li>
          <a href="#">Category</a>
        </li>
        <li>
          <a href="#">Category</a>
        </li>
      </ul>
      <ul class="nav navbar-right navbar-nav">
       <li ng-show="isLoggedIn();">
          <a href="/a/secure/dashboard#/tutorial/create">Create</a>
        </li>
        <li ng-show="isLoggedIn();">
          <a href="/a/secure/dashboard">Dashboard</a>
        </li>
        <li ng-hide="isLoggedIn();">
          <a href="/a/public/login">Login</a>
        </li>
      </ul>
    </nav>
  </div>
</header>

<div id="masthead">  
  <div class="container">
    <div class="row">
      <div class="col-md-12">
        <h3>Notes
          <p class="lead"></p>
        </h3>
      </div>
      <div class="row">
     <div class="col-md-8 col-md-offset-2">
        <form class="form-inline">
              <input type="text" class="form-control pull-left width80" placeholder="Search"   ng-model="pageState.searchTerm"  >
              <button ng-click="search();" ng-disabled="!pageState.searchTerm" type="submit" class="btn btn-default pull-left"><i class="glyphicon glyphicon-search"></i></button>
            </form>
        </div>
        </div>
    </div> 
  </div><!-- /cont -->
  
</div>


<div class="container">
  <div class="row">
    
    <div class="col-md-12"> 
      
      <div class="panel" ng-show="searchResults.length">
        <div class="panel-body" >
          
          
          
          <!--/stories-->
          <div class="row searchResult" ng-repeat="tutorial in searchResults">    
            <br>
           <!--  <div class="col-md-2 col-sm-3 text-center">
              <a class="story-title" href="#"><img alt="" src="http://api.randomuser.me/portraits/thumb/men/58.jpg" style="width:100px;height:100px" class="img-circle"></a>
            </div> -->
            <div class="col-md-12 col-sm-12">
              <h3>{{tutorial.title}}</h3>
              <div class="row">
               <div class="col-xs-9">
               <div> <small>{{tutorial.description}}</small></div>
                <div class="col-xs-3"></div>
              </div>
                <div class="col-xs-9">
                  <h4><span class="label label-default" ng-repeat = "tag in tutorial.tags">{{tag}}</span>&nbsp;</h4><h4>
                  <small style="font-family:courier,'new courier';" class="text-muted">{{tutorial.createdDisplayTime}}  
                  • <a ng-show="isLoggedIn();" ng-href="/a/secure/dashboard#/tutorial/{{tutorial.id}}" class="text-muted">Read More</a>
                   • <a href="javascript:void(0);" ng-click="tutorial.showVideo=!tutorial.showVideo" target="_blank" class="text-muted">Video</a>
                   </small>
                  </h4></div>
                <div class="col-xs-3"></div>
              </div>
              <br><br>
              <div class="row" ng-show="tutorial.showVideo">
               <div class="col-xs-12">
               <iframe ng-src="{{tutorial.trustedUrl}}" style="width:100%" frameborder="0" height="315" allowfullscreen></iframe>
               </div>
              </div>
            </div>
          </div>
        
          <!--/stories-->
          
          
          <a href="javascript:void(0);" ng-show="pageState.nextLink" ng-click="loadTutorials();" class="btn btn-primary pull-right btnNext">More <i class="glyphicon glyphicon-chevron-right"></i></a>
        
          
        </div>
      </div>
                                                                                       
	                                                
                                                      
   	</div><!--/col-12-->
  </div>
</div>
                                                
                                                                                
<hr>

<div class="container" id="footer">
  <div class="row">
    <div class="col col-sm-12">
      
      <h1>Follow Us</h1>
      <div class="btn-group">
       <a class="btn btn-twitter btn-lg" href="#"><i class="icon-twitter icon-large"></i> Twitter</a>
	   <a class="btn btn-facebook btn-lg" href="#"><i class="icon-facebook icon-large"></i> Facebook</a>
	   <a class="btn btn-google-plus btn-lg" href="#"><i class="icon-google-plus icon-large"></i> Google+</a>
      </div>
      
    </div>
  </div>
</div>

<hr>

<hr>

<footer>
  <div class="container">
    <div class="row">
      <div class="col-sm-6">
        <ul class="list-inline">
          <li><i class="icon-facebook icon-2x"></i></li>
          <li><i class="icon-twitter icon-2x"></i></li>
          <li><i class="icon-google-plus icon-2x"></i></li>
          <li><i class="icon-pinterest icon-2x"></i></li>
        </ul>
        
      </div>
      <div class="col-sm-6">
          <p class="pull-right">Built with <i class="icon-heart-empty"></i> at <a href="http://www.bootply.com">Bootply</a></p>      
      </div>
    </div>
  </div>
</footer>
	<!-- script references -->
	
	 <!-- jQuery -->
    <script type="text/javascript" src="/js/lib/jquery-3.1.1.min.js" >  </script>

    <!-- Bootstrap Core JavaScript -->
    <script src="/js/lib/bootstrap.min.js"></script>

    <!-- Metis Menu Plugin JavaScript -->
    <script src="/js/demo/metisMenu.min.js"></script>

    <!-- Morris Charts JavaScript -->
    <script src="/js/demo/raphael.min.js"></script>
   <!--  <script src="/js/demo/morris.min.js"></script>
    <script src="/js/demo/morris-data.js"></script> -->

    <!-- Custom Theme JavaScript -->
    <script src="/js/lib/angular.min.js"></script>
<script src="/js/lib/angular-bootstrap-1.0.8.js"></script>
<script src="/js/lib/ui-bootstrap-tpls-2.1.3.min.js"></script>
	<script src="/js/lib/underscore-min.js"></script>
	
	<script src="/js/lib/angular-route.js"></script>
	<script type="text/javascript" src="/js/lib/require.js"></script>
	<script src="/js/lib/angular-resource.js"></script>

	<script src="/js/notes-app.js"></script>
	</body>
</html>