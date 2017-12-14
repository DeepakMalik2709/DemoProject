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
    onRouteChange: Ember.observer('router.currentPath', function(a){ 
    	hideSidebarMobile();
    }),
    showGooglePermissionMessage : _.once(function(){
        	setTimeout((function() {
     			if(confirm("Please give AllSchool permission to save your files to Google Drive and add events to your Google Calendar. Granting Google drive permission removes 10MB upload limit.")){
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