var notesModule = angular.module('notesModule', ['ngRoute','ngResource'  , 'ui.bootstrap']);

notesModule.config([ '$routeProvider', function($routeProvider,$routeSegmentProvider ) {
	$routeProvider.when('/dashboard', {
		templateUrl : '/html/dashboard.html',
	}).when('/tutorial/create', {
		templateUrl : '/html/tutorial/upsert.html',
	}).when('/tutorial/:tutorialId', {
		templateUrl : '/html/tutorial/view.html',
	}).when('/tutorial/:tutorialId/edit', {
		templateUrl : '/html/tutorial/upsert.html',
	})
	.otherwise({
		redirectTo : function(){
			return "/dashboard"
		}
	});
}]);

notesModule.controller('rootController', function($scope,$routeParams, $http,$route, $sce,$location,$timeout, $rootScope, $q,commonService,$window, $filter,  
		$compile) {
	
	$scope.pageState = {}
	$rootScope.appState = {};
	$scope.tutorial = {};

	$scope.searchResult = [];
	
	function initTutorial(){
		$scope.tutorial = {
				tags : [],
				convertToEmbed : true,
		};
	}
		
	$scope.initTutorialForm = function(){
		if($routeParams.tutorialId){
			if($routeParams.tutorialId != $scope.tutorial.id){
				initTutorial()
				commonService.fetchTutorial($routeParams.tutorialId).then(function(result){
					if(result.code == 0){
						$scope.tutorial= result.item;
					}
				});
			}
		}else{
			initTutorial();
		}
	}
	
	function getId(url) {
	    var regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
	    var match = url.match(regExp);

	    if (match && match[2].length == 11) {
	        return match[2];
	    } else {
	        return '';
	    }
	}

	$scope.saveTutorial = function(invalid){
		if($scope.tutorial.convertToEmbed){
			var videoId = getId($scope.tutorial.url );
			if(videoId){
				$scope.tutorial.url = "https://www.youtube.com/embed/" + videoId
			}
		}
		commonService.saveTutorial($scope.tutorial).then(function(result){
			if(result.code == 0){
				var item = result.item;
				$scope.tutorial= result.item;
				$scope.loadPath("tutorial/" + item.id);
			}
		});
	}
	
	
$scope.confirmAndDeleteTutorial = function(){
		if(confirm("Delete " + $scope.tutorial.title)){
			commonService.deleteTutorial($scope.tutorial.id);
			$scope.loadPath("dashboard");
		}
	}
	
	$scope.editTutorial = function(){
		$scope.loadPath("tutorial/" + $scope.tutorial.id + "/edit");
	}

	
	$scope.removeTagFromTutorial = function(tag){
		var index =  $scope.tutorial.tags.indexOf(tag);
		 $scope.tutorial.tags.splice(index,1);
	}
	
	$scope.addTagToTutorial = function(){
		$scope.tutorial.tags.push($scope.tutorial.newTag);
		$scope.tutorial.newTag = "";
		
	}
	
	$scope.isLoggedIn = function(){
		if(!$.isEmptyObject($rootScope.appState)  ){
			return $rootScope.appState.loginUser != null;
		}
		return false;
	}

	$scope.isLoggedInUser = function(email){
		
		if(!$.isEmptyObject($rootScope.appState)  ){
			return $rootScope.appState.loginUser.email == email;
		}
		return false;
	}
	
	$scope.searchPage = function(){
		window.location.href = "/a/public/home?q=" + $scope.pageState.searchTerm ;
	}
	
	$scope.search = function(){
		if($scope.pageState.searchTerm){
			$scope.searchResults = [];
			$scope.pageState.nextLink = "";
			$scope.loadTutorials();
		}
	}
	
	$scope.loadTutorials = function(){
		if(!$scope.pageState.isLoadingTutorials){
			$scope.pageState.isLoadingTutorials = true;
			commonService.searchTutorial($scope.pageState.searchTerm , $scope.pageState.nextLink).then(function(result){
				$scope.pageState.isLoadingTutorials = false;
				if(result.code == 0){
					$scope.searchResults.push.apply($scope.searchResults, result.items);
					$scope.pageState.nextLink = result.nextLink;
					cleanupAllTutorials();
				}
			});
		}
	}
	
	function cleanupAllTutorials(){
		for( var i =0; i < $scope.searchResults.length ;i++){
			var result = $scope.searchResults[i];
			cleanupTutorial(result);
		}
	}
	
	function cleanupTutorial(item){
		item.createdDisplayTime = getTimeDifference(item.createdTime);
		item.trustedUrl = trustUrl(item.url);
	}
	
	$scope.reset1 = function(){
		
	}
		
/*	helpers */
	function trustUrl(url){
		return $sce.trustAsResourceUrl (url);
	}
	
	var getTimeDifference = function(time){
		var diff =  (new Date().getTime() - time) / 1000;
		var msg = "";
		if(diff < 60){
			var secs = Math.floor(diff);
			if(secs <= 0){
				//msg = "Now";
				msg = 'now ';
			}else{
				//msg = secs + " seconds ago"
				msg = secs + " " + 'seconds ago';
			}
			
		}else if(diff < 3600){
			var mins = Math.floor(diff/60) ;
			if(mins == 1){
				//msg = mins + " minute ago"
				msg = mins + " " + 'minute ago';
			}else{
				//msg = mins + " minutes ago"
				msg = mins + " " + 'minutes ago';
			}
			
		}else if(diff < 86400){
			var hours = Math.floor(diff/3600);
			if(hours == 1){
				//msg =  hours + " hour ago"
				msg = hours + " " + 'hour ago';
			}else{
				//msg =  hours + " hours ago"
				msg = hours + " " + 'hours ago';
			}
			
		}else {
			msg = $filter('date')((new Date(time)  ), "dd-MMM-yyyy HH:mm a" );
		}
		return msg;
	}
	
	$rootScope.modelOptions =  {
			updateOn: 'default blur', 
			debounce: { 'default': 150, 'blur': 0 }
	}

	$scope.loadPath = function(path, searchParams){
		if(searchParams){
			$location.search(searchParams);
		}else{
			$location.search({});
		}
		if($location.path().endsWith(path)){
			$route.reload();
		}else{
			$location.path(path);
		}
	}

	$rootScope.redirect  = function(url){
		window.open(url,'_blank');
	}
	
	$scope.reset = function(){
		$scope.pageState = {
				searchTerm : getQueryVariable("q"),
		};
		$scope.search();
	}
	
	function init(){
		
		$scope.reset();
		commonService.initContext();
	}
	
	init();
});

notesModule.controller('notesController', function($scope, $http,$route, $sce,$location,$timeout, $rootScope, $q,commonService,$window, $filter,  
		$compile) {
	
	$scope.reset = function(){
		
	}
	
	function init(){
		
		$scope.reset();
	}
	
	init();
});

//Loads the correct sidebar on window load,
//collapses the sidebar on window resize.
// Sets the min-height of #page-wrapper to window size
$(function() {
	
	$('#side-menu').metisMenu();
	
    $(window).bind("load resize", function() {
        var topOffset = 50;
        var width = (this.window.innerWidth > 0) ? this.window.innerWidth : this.screen.width;
        if (width < 768) {
            $('div.navbar-collapse').addClass('collapse');
            topOffset = 100; // 2-row-menu
        } else {
            $('div.navbar-collapse').removeClass('collapse');
        }

        var height = ((this.window.innerHeight > 0) ? this.window.innerHeight : this.screen.height) - 1;
        height = height - topOffset;
        if (height < 1) height = 1;
        if (height > topOffset) {
            $("#page-wrapper").css("min-height", (height) + "px");
        }
    });

    var url = window.location;
    // var element = $('ul.nav a').filter(function() {
    //     return this.href == url;
    // }).addClass('active').parent().parent().addClass('in').parent();
    var element = $('ul.nav a').filter(function() {
        return this.href == url;
    }).addClass('active').parent();

    while (true) {
        if (element.is('li')) {
            element = element.parent().addClass('in').parent();
        } else {
            break;
        }
    }
});



notesModule.service('commonService', function($http, $q, $rootScope) {
	
	this.saveTutorial = function(json) {
		return post("/a/secure/tutorial/upsert",json);
	}
	
	this.fetchTutorial = function(id) {
		return get("/a/public/tutorial/"+id);
	}
	
	this.searchTutorial = function(term , nextLink) {
		var url = "/a/public/tutorial/search?q="+ term ;
		if(nextLink){
			url = nextLink;
		}
		return get(url);
	}
	
	
	this.initContext = function(){
		get("/a/secure/context").then(function(result){
			if(result.code == 0){
				$rootScope.appState = result;
			}
		})
	}
	
	/* helper methods */
	function post(targetUrl , json){
		var deferred = $q.defer();
		$http({
			url : targetUrl,
			method : "POST",
			data  : json
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		});
		return deferred.promise;
	}
	function postWithHeaders(targetUrl ,headers, json){
		var deferred = $q.defer();
		$http({
			url : targetUrl,
			method : "POST",
			data  : json,
			headers : headers,
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		});
		return deferred.promise;
	}
	
	function formSubmit(targetUrl, json) {
		var deferred = $q.defer();
		$http({
			url : targetUrl,
			method : "POST",
			headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			data : $.param(json),
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		});
		return deferred.promise;
	}
	
	function get(targetUrl, json) {
		var deferred = $q.defer();
		$http({
			url : targetUrl,
			method : "GET",
			params : json
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		});
		return deferred.promise;
	}
	
	function deleteRequest( targetUrl) {
		var deferred = $q.defer();
		$http({
			url : targetUrl,
			method : "DELETE",
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		});
		return deferred.promise;
	}
	
	function getWithHeaders( targetUrl, headers, json) {
		var deferred = $q.defer();
		$http({
			url : targetUrl,
			method : "GET",
			params : json,
			headers : headers
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		});
		return deferred.promise;
	}
	
	function deleteWithHeaders( targetUrl, headers) {
		var deferred = $q.defer();
		$http({
			url : targetUrl,
			method : "DELETE",
			headers : headers
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		});
		return deferred.promise;
	}

	function serializeData( data ) {
	  
	                    var buffer = [];
	                     // Serialize each key in the object.
	                    for ( var name in data ) {
	 
	                        if ( ! data.hasOwnProperty( name ) ) { 
	                            continue; 
	                        }
	 
	                 var value = data[ name ]; 
							if(typeof value =="object"){
								if(Array.isArray(value)){
									for(var i=0;i< value.length;i++){
										 buffer.push(
											encodeURIComponent( name ) +
											"=" +
											encodeURIComponent( ( value[i] == null ) ? "" : value[i] )); 
										}
								}else{
									for(var i in value){
										 buffer.push(
											encodeURIComponent( name ) +
											"=" +
											encodeURIComponent( ( value[i] == null ) ? "" : value[i] )); 
										}
								}
								
							}else{
								 buffer.push(
									encodeURIComponent( name ) +
									"=" +
									encodeURIComponent( ( value == null ) ? "" : value )
								); 
							}
	                    }
	 
	                    // Serialize the buffer and clean it up for transportation.
	                    var source = buffer
	                        .join( "&" )
	                        .replace( /%20/g, "+" );
	 
	                    return( source ); 
	                }
	                
	
	
    this.inputEvent = function(event) {
        if (angular.isDefined(event.touches)) {
            return event.touches[0];
        }
        //Checking both is not redundent. If only check if touches isDefined, angularjs isDefnied will return error and stop the remaining scripty if event.originalEvent is not defined.
        else if (angular.isDefined(event.originalEvent) && angular.isDefined(event.originalEvent.touches)) {
            return event.originalEvent.touches[0];
        }
        return event;
    };
    
}).directive('ngEnter', function() {
    return function(scope, elm, attr) {
        var elem = elm[0];
        elm.bind('keyup', function(event) {
        	  var code = ('which' in event) ? event.which : event.keyCode;
        	  if(code == keyCode.ENTER){
        		  scope.$apply(attr.ngEnter);
        	  }else if(attr.ngEscape && code == keyCode.ESCAPE){
        		  scope.$apply(attr.ngEscape);
        	  }
        });
    };
})

var keyCode= {
			COMMA: 44,
			COMMA2: 188,
			DELETE: 46,
			DOWN: 40,
			END: 35,
			ENTER: 13,
			ESCAPE: 27,
			HOME: 36,
			LEFT: 37,
			PAGE_DOWN: 34,
			PAGE_UP: 33,
			PERIOD: 190,
			RIGHT: 39,
			TAB: 9,
			UP: 38,
			SPACE : 32,
			BACKSPACE : 8
		};

	function inKeyCodes(code){
		for(var i in keyCode){
			if(keyCode[i]==code){
				return true;
			}
		}
		return false;
	}

	Array.prototype.last = function() {
	    return this[this.length-1];
}
 
 Array.prototype.isEmpty = function() {
	    return this.length <= 0;
}
 String.prototype.replaceAll = function(search, replacement) {
	    var target = this;
	    return target.split(search).join(replacement);
	};
	
if (typeof String.prototype.endsWith !== 'function') {
    String.prototype.endsWith = function(suffix) {
        return this.indexOf(suffix, this.length - suffix.length) !== -1;
    };
}	
	
function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split('&');
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split('=');
        if (decodeURIComponent(pair[0]) == variable) {
            return decodeURIComponent(pair[1]);
        }
    }
}
