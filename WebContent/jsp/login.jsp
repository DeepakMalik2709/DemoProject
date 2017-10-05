<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>AllSchool - Login</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="/css/lib/bootstrap.min.css" rel="stylesheet">
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

.login-icons{
	width:247px;
	height:54px;
	overflow:hidden;

}
    </style>
    <script src="/js/lib/jquery.min.1.11.2.js"></script>
    <script src="/js/lib/bootstrap.min.js"></script>
<link href='https://fonts.googleapis.com/css?family=Raleway:500' rel='stylesheet' type='text/css' />
</head>

<body>
<div class="middlePage">
<div class="page-header">
  <h1 class="logo"> <img src="/img/allshcool_logo.png" style="width: 38px;" /> AllSchool <small>Welcome to our place!</small></h1>
</div>

<div class="panel panel-info">
  <div class="panel-heading">
    <h3 class="panel-title">Please Sign In</h3>
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
<form class="form-horizontal"  method="POST" >
<fieldset>

  <input id="username" name="username" type="text" placeholder="Enter User Name" class="form-control input-md">
  <div class="spacing"><!-- <input type="checkbox" checked name="remember-me" id="checkboxes-0" ><small> Remember me</small> --></div>
  <input id="password" name="password" type="password" placeholder="Enter Password" class="form-control input-md" onkeyup="keyUp();">
  <div class="spacing"><a href="javascript:void(0);" data-toggle="modal" data-target="#forgot-password1"><small> Forgot Password?</small></a><a href="javascript:void(0);" class="pull-right" data-toggle="modal" data-target="#new-user-form"><small> Register</small></a><br/></div>
  <button  type="button" onclick="doLogin();"  id="singlebutton" name="singlebutton" class="btn btn-info btn-sm pull-right">Sign In</button>
  <span id="login-loader" class="pull-right" style="display:none;"><img src="/img/loading.gif" /></span>


</fieldset>
</form>
</div>
    
</div>
    
</div>
</div>

<p><a href="#">About</a> AllSchool</p>

</div>
 <div class="modal fade" id="forgot-password1" tabindex="-1" role="dialog" aria-labelledby="add-user" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title" id="myModalLabel">Forgot Password?</h4>
          </div>
            <form name='requestResetCodeForm1'>
          <div class="modal-body form-horizontal text-center clearfix">
          	<div class="top-buttons mtop-20 text-justify">To reset/change your password, enter your registered email address for us to send password reset instructions.
          	<br><br>Alternatively you can also you Login with Facebook or Google.</div><br>
          	<div class="form-group">
                <div class="col-sm-10 col-sm-offset-1">
                   <input type="email" name="primaryEmail" id ="primaryEmail" class="form-control"  placeholder="Email">
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-12">          
            		<button  type="button" onclick="sendPasswordResetInstructions()" class="btn btn-sky btn-sm" data-dismiss="modal" data-toggle="modal">Send reset instructions</button>
              	</div>
         	</div>
          </div>   
              </form>   
        </div>
      </div>
    </div>
    
    <div class="modal fade" id="new-user-form" tabindex="-1" role="dialog" aria-labelledby="add-user" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title" id="myModalLabel">New user</h4>
          </div>
            <form name='requestResetCodeForm1'>
          <div class="modal-body form-horizontal text-center clearfix">
          	<div class="top-buttons mtop-20 text-left">Please provide following details.</div><br>
          	<div class="form-group">
          	
          	 <div class="col-sm-10 col-sm-offset-1">
          	 <div class="row">
          	 <div class="col-sm-6"> <input type="text" name="firstName" id ="firstName" class="form-control"  placeholder="First Name"></div>
          	 <div class="col-sm-6"> <input type="text" name="lastName" id ="lastName" class="form-control"  placeholder="Last Name"></div>
          	 </div>
              </div>
                
            </div>
            
            <div class="form-group">
          	
          	 <div class="col-sm-10 col-sm-offset-1">
          	 <div class="row">
          	 <div class="col-sm-12"> 
          	   <input type="email" name="email" id ="email" class="form-control"  placeholder="Email">
          	  </div>
              </div>
                </div>
            </div>
            
               <div class="form-group">
          	
          	 <div class="col-sm-10 col-sm-offset-1">
          	 <div class="row">
          	 <div class="col-sm-12"> 
          	   <input type="password" name="accountPassword" id ="accountPassword" class="form-control"  placeholder="Password">
          	  </div>
              </div>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-12">          
            		<button  type="button" onclick="createAccount()" class="btn btn-sky btn-sm" >Create Account</button>
              	</div>
         	</div>
          </div>   
              </form>   
        </div>
      </div>
    </div>
    
    <script>
    
    function isValidEmail(text){
    	return /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(text);
    }
    
    function createAccount(){

		var json = {
				"email" : $("#email").val(),
				firstName : $("#firstName").val(),
				lastName : $("#lastName").val(),
				password : $("#accountPassword").val(),
			}

    	if(!json.firstName){
    		alert("First Name is required.");
    	}else if(!json.lastName){
    		alert("Last Name is required.");
    	}else if(!json.email){
    		alert("Email is required.");
    	}else if(!json.password){
    		alert("Password is required.");
    	}else if (!isValidEmail(json.email)) {
    		alert(json.email + " is not a valid email address");
    		
    	}else{
    		$("#new-user-form").modal("hide");
    		$.ajax({
    			url:"/a/public/register",
    			method:"post",
    			contentType: 'application/json',
    			data: JSON.stringify(json),
    			success : function(data){
    				if(data.code== 0){
    					var msg = "An email has been sent to "+ json.email + ". Please verify your account to login.";
    					$("#errorContainer .text-danger").html(msg);
    					alert(msg)
    				}else if(data.message){
    					$("#errorContainer .text-danger").html(data.message);
    					alert(data.message)
    				}else{
    					alert("An error occurred. Please try again later.")
    				}
    			}
    		})
    	}
    }
    
    function sendPasswordResetInstructions(){
    	var email = $("#primaryEmail").val();
    	if (isValidEmail(email)) {
    		$.ajax({
    			url:"/a/public/sendPasswordResetInstructions",
    			method:"post",
    			data: {email : email},
    			success : function(data){
    				if(data.code== 0){
    					var msg = "Password reset instructions sent to your email address.";
    					$("#errorContainer .text-danger").html(msg);
    					alert(msg)
    				}else{
    					$("#errorContainer .text-danger").html(data.message);
    					alert(data.message);
    				}
    			}
    		})
    	}else{
    		var msg = "Please enter a valid email address".
    		alert(msg);
    		$("#forgot-password1").modal("show");
    	}
    }
    
    function doLogin(){
    	
    	var json = {
				username : $("#username").val(),
				password : $("#password").val(),
			}

    	if(!json.username){
    		alert("Username is required.");
    	}else if(!json.password){
    		alert("Password is required.");
    	}else{
    		$("#singlebutton").hide();
        	$("#login-loader").show();
    	 	$.ajax({
    			url:"/a/public/login",
    			method:"post",
    			data: json,
    			success : function(data){
    				$("#singlebutton").show();
    		    	$("#login-loader").hide();
    				if(data.code== 0){
    					var recirectUrl = data.recirectUrl ;
    					if(!recirectUrl){
    						recirectUrl = "/home" ;
    					}
    					window.location.href = recirectUrl;
    				}else if(data.message){
    					$("#errorContainer .text-danger").html(data.message);
    				}
    			}
    		}) 
    	}
    }
    
    function keyUp(){
    	var evt = window.event;
		if(evt.which == 13){
			doLogin();
		}		    		
    }
    
    var contextMsg = "${commonContext.message}";
    if(contextMsg){
		$("#errorContainer .text-danger").html(contextMsg);
	}
    </script>
</body>
</html>