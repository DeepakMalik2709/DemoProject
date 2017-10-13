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
    onRouteChange: Ember.observer('router.currentRouteName', function(){ 
    	 if ($(window).width() <= 992) {
            $('.left-side').addClass("collapse-left");
            $(".right-side").removeClass("hide");
            $('.row-offcanvas').removeClass("active relative");
        }
    }),
    showGooglePermissionMessage : _.once(function(){
        	setTimeout((function() {
     			if(confirm("Please give AllSchool permission to save your files to Google Drive and add events to your Google Calendar. Granting Google drive permission removes 10MB upload limit.")){
     				window.location.href= "/a/oauth/googleAllAuthorization";
     			}
    			 }), 2000);

    }),
    setupController: function(controller, model) {
        this._super(controller, model);
        controller.set("isLoggedIn", Ember.computed.notEmpty("model"));
        controller.set("showNotifications", false);
        controller.set("isSearchButtonDisabled", Ember.computed.empty("searchTerm"));
        if(model){
        	if(!model.get('loginUser.refreshTokenAccountEmail')){
        		var showGoogleDriveMsgDate = false;
        		var googleDriveMsgDate = model.get('loginUser.googleDriveMsgDate')
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
	        	   Ember.run.later((function() {
	        		   $(".sidebar .treeview").tree();
	        		 }), 2000);
	        });
	        this.contextService.fetchNotifications().then((response) => {
	        		if(response && response.length){
	        			  controller.set("showNotifications", true);
	        			  controller.set("notifications" ,response );
	        		}
	        	   
	        });
        }
    },


    actions: {

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
        	if(notification.entityId){
        		if(notification.type == 'TASK' || notification.type == 'POST'){
        			this.transitionTo('group.post', notification.entityId);
        		}
        	}else if(notification.groupId){
        		this.transitionTo('group.posts', notification.groupId);
        	}
        }
    }
});