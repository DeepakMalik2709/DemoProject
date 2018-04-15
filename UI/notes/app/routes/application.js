import Ember from 'ember';
const {
    inject,
    computed
} = Ember;
export default Ember.Route.extend({


    model() {
        return this.contextService.fetchContext();
    },
    context: null,
    init: function() {

    },
    router: Ember.inject.service('-routing'),
    groupService: Ember.inject.service('group'),
	scheduleService: Ember.inject.service('new.schedule'),

    onRouteChange: Ember.observer('router.currentPath', function(a){
    	hideSidebarMobile();
    }),

    showGooglePermissionMessage : _.once(function(){
        	setTimeout((function() {
     			if(confirm("Please give AllSchool permission to save your files to Google Drive and add events to your Google Calendar.")){
     				window.location.href= "/a/oauth/googleAllAuthorization";
     			}
    			 }), 2000);

    }),
    showAddInstituteMessage : _.once(function(){
    	var this1 = this;
    	setTimeout((function() {
 			if(confirm("Please add your institute to AllSchool to help connect with your peers.")){
 				this1.transitionTo('profile');
 			}
			 }), 2000);

    }),
    openChannel :_.once(function(apiKey, email){
      if(apiKey==undefined ){ return false;}
    	var realtime = new Ably.Realtime(apiKey);
    	var channel = realtime.channels.get(email);

        channel.on(function(channelStateChange) {
         // console.log(channelStateChange);
          if (channelStateChange.event == 'attached') {
          }
        });
        var controller = this.controller;
        channel.subscribe(function(msg) {
        	//console.log(msg)
          var data = msg.data;
          if(msg.name == "notification"){
        	  var notifications = controller.get("notifications");
        	  var addNotification = true;
        	  if(notifications){
        		  var existing = notifications.filterBy("id", data.id) ;
        		  addNotification = (existing.length <= 0 );
        	  }

        	 if(addNotification){
        		  controller.set("showNotifications", true);
             	 controller.set("newNotifications", true);
             	controller.get("notifications").unshiftObject(data);
        	 }
        	 if(data.item){
	        	 if("COMMENT" == data.type || "COMMENT_REPLY" == data.type){
	        		 $.event.trigger( "newComment" , data );
	        	 }
     		}

          }else  if(msg.name == "post"){
        	  $.event.trigger( "postUpdate" , data );
          }
        });
    }),
    setupController: function(controller, model) {
        this._super(controller, model);
        controller.set("isLoggedIn", Ember.computed.notEmpty("model"));
        controller.set("showNotifications", false);
        controller.set("isSearchButtonDisabled", Ember.computed.empty("searchTerm"));
        if(model){
        	if(model.get('institutes').length <= 0){
	        	var showAddInstituteMessage = false;
	        	var addInstituteMsgDate = model.get('loginUser.addInstituteMsgDate');
	    		if(addInstituteMsgDate){
        			model.set('loginUser.addInstituteMsgDate', null);
        			var diff = new Date().getTime() - addInstituteMsgDate ;
        			if(diff > (1*24*60*60*1000)){
        				showAddInstituteMessage = true;
        			}
        		}else{
        			showAddInstituteMessage = true;
        		}

	    		if(showAddInstituteMessage){
	    			this.showAddInstituteMessage();
	    		}
	        }
        	if(!model.get('loginUser.refreshTokenAccountEmail')){
        		var showGoogleDriveMsgDate = false;
        		var googleDriveMsgDate = model.get('loginUser.googleDriveMsgDate');
        		if(googleDriveMsgDate){
        			model.set('loginUser.googleDriveMsgDate', null);
        			var diff = new Date().getTime() - googleDriveMsgDate ;
        			if(diff > (1*24*60*60*1000)){
        				showGoogleDriveMsgDate = true;
        			}
        		}else{
        			showGoogleDriveMsgDate = true;
        		}
        		if(showGoogleDriveMsgDate){
        			this.showGooglePermissionMessage();
        		}
        	}

	        let request = this.get('groupService').fetchMyGroups();
	        request.then((response) => {
	        	   controller.set("myGroups" ,response );
	        	   $.event.trigger( "sidebarUpdated");
	        });
	        this.contextService.fetchNotifications().then((response) => {
	        		if(response && response.code == 0){
        				if(response.items.length){
        				  controller.set("showNotifications", true);
        				  if( !controller.get("notifications" )){
        					  controller.set("newNotifications", response.newNotifications);
        				  }
   	        			  controller.set("notifications" ,response.items );
        				}

	        		}

	        });
	  /*  	if(!model.get('loginUser.todayScheduleCount')){
	        this.get('scheduleService').fetchtodayScheduleCount().then((response) => {
	        	   controller.set("todayScheduleCount" ,response.total );
	        	   model.set('loginUser.todayScheduleCount', response.total);
	        	   $.event.trigger( "sidebarUpdated");
	        });
	    	}else{
	    		 controller.set("todayScheduleCount" ,model.get('loginUser.todayScheduleCount') );
	    	} */

	    	var apiKey = model.get('pushToken.token');
	        this.openChannel(apiKey, model.get('loginUser.email'));
        }
        $.event.trigger( "sidebarUpdated");


    },


    actions: {
    	checkCalendarAauthentication(model){
    		var googleCalendarPermission = model.get('loginUser.useGoogleCalendar');
    		if(!googleCalendarPermission){
    			setTimeout(function() {
         			if(confirm("Please give AllSchool permission to save your files to Google Drive and add events to your Google Calendar. Granting Google drive permission removes 10MB upload limit.")){
         				window.location.href= "/a/oauth/googleAllAuthorization";
         			}
        			 })
    		}else{
    			this.transitionTo('calendar');
    		}

    	},
        doNavbarSearch() {
            var searchTerm = this.get('controller.searchTerm')
            this.set('controller.searchTerm', '');
            this.transitionTo('application', {
                queryParams: {
                    q: searchTerm
                }
            });
        },

        markNotificationAsRead(){
        	var notifications = this.get('controller.notifications');
        	for(var i =0; i<notifications.length;i++){
        		var notification = notifications[i];
        		if(!notification.isRead){
        			this.contextService.markNotificationAsRead();
        			notification.isRead = true;
        			break;
        		}
        	}
        },

        notificationClick(notification){
        	this.controller.set("newNotifications",false);
        	if(notification.entityId){
        		if(notification.isPost || notification.isTask || notification.isSchedule){
        			this.transitionTo('group.post', notification.entityId);
        		}else if("COMMENT" == notification.type || "COMMENT_REPLY" == notification.type){
        			this.transitionTo('group.post', notification.entityId);
        		}
        	}else if(notification.groupId){
        		if(notification.showDetailpage){
        			this.transitionTo('group.view', notification.groupId);
        		}else{
        			this.transitionTo('group.posts', notification.groupId);
        		}
        	}else if(notification.instituteId){
        		if(notification.showDetailpage){
        			this.transitionTo('institute.view', notification.instituteId);
        		}else{
        			this.transitionTo('institute.public', notification.instituteId);
        		}
        	}
        },
        hideAd(){
        	$(".footer-ad").hide();
        	setTimeout(function(){
        		$(".footer-ad").show(400);
        	},10000)
        }
    }
});
