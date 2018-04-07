import Ember from 'ember';
import ajaxMixin from '../mixins/ajax';
import utilsMixin from '../mixins/utils';

export default Ember.Service.extend(ajaxMixin ,utilsMixin ,{
	context : null,
	notifications : null,
	isLoggedIn(){
		return this.context != null;
	},
	setLoginUser(user){
		if(null !=user && this.context!=null){
			this.context.set( "loginUser", user)
		}
	},
	getLoginUser(){
		var user=null;
		if(this.context!=null){
			user = this.context.get( "loginUser");
		}
		return user;
	},
	fetchContext(callback) {
		if(this.context){
			if(typeof callback == "function"){
				return callback(this.context);
			}
			return this.context;
		}else{
			var url = "/rest/secure/context";
			return this.get(url).then((result ) =>{
				if(result.code==0){
					this.context = Ember.Object.create(result);
					if(result.properties.message){
						alert(result.properties.message);
					}
				}
				if(typeof callback == "function"){
					return callback(result);
				}
				return this.context;
			});
		}

	},
	fetchNotifications() {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
				var url = "/rest/secure/notifications";
				this.doGet(url).then((data ) =>{

					var notifications = [];
			    	  if(data.code ==0 && data.items){
			    		  var items = data.items;
			    		  for(var i = 0;i<data.items.length ; i++ ){
			    			  var item = items[i];
			    			  this.cleanupTimes(item);
			    			  notifications.pushObject(item);
			    		  }
			    		 // resolve( Ember.ArrayProxy.create({ content: Ember.A(items) }));
			    	  }
			    	  data.items = notifications;
			    	  resolve(data);
			      });
		 });

	},

	markNotificationAsRead() {
		var url = "/rest/secure/markNotificationAsRead";
		this.doGet(url);
	},
	refreshContext(callback) {
		this.context = null;
		this.fetchContext(callback);
	},
	updateProfilePhoto() {
		if(this.context && this.context.get( "loginUser")){
			this.context.set( "loginUser.photoUrl", this.context.get( "loginUser.photoUrl") + "?id=123");
		}
	},
	get(targetUrl){
		return Ember.$.when(
				Ember.$.ajax({
				"url" : targetUrl,
				method : "GET",
				})
			)
	},
	post(targetUrl , json){
		return Ember.$.when(
				Ember.$.ajax({
				"url" : targetUrl,
				method : "POST",
				contentType : "application/json;charset=UTF-8",
				data : JSON.stringify(json)
				})
			)
	},
	dashboardData:function(){
		return this.get("/rest/dashboard/dataList" );
	}

});
