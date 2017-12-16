import Ember from 'ember';
import ajaxMixin from '../../mixins/ajax';
import authenticationMixin from '../../mixins/authentication';
import instituteMixin from '../../mixins/institute';
import utilsMixin from '../../mixins/utils';
export default Ember.Route.extend(ajaxMixin,authenticationMixin,instituteMixin,utilsMixin, {


    model(params) {
        return this.store.findRecord('group', params.groupId);
    },
    groupService: Ember.inject.service('group'),
    attendance : {
    	fromHour : 0,
    	fromMinutes : 0,
    },
    init() {
	    this._super(...arguments);
	  },
	  hoursArray : [],
	  minutesArray: [],
    setupController: function(controller, model) {
        this._super(controller, model);
        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
	       for(var i = 0 ; i < 24; i++){
	    	   this.hoursArray.push({label : (i) , id :i});
	       }
	       for(var i = 0 ; i < 50; i = i+15){
	    	   this.minutesArray.push({label : i , id :i});
	       }
	       controller.set("fromMinutesArray", Ember.copy(this.minutesArray, true));
	       controller.set("fromHoursArray", Ember.copy(this.hoursArray, true));
	       controller.set("toMinutesArray", Ember.copy(this.minutesArray, true));
	       controller.set("toHoursArray", Ember.copy(this.hoursArray, true));
	       controller.set("attendance", this.attendance);
	       controller.set("editAttendanceDate" , true);
    },

    fetchGroupAttendanceMembers (json){
    	var controller = this.get("controller");
    	controller.set("noRecords" , false);
    	if(typeof controller.get("members") == 'undefined'){
    		controller.set("members" , [])
		}
		var model = controller.get('model');
		controller.set("isLoading" , true);
		controller.set("editAttendanceDate" , false);
		var	url = "/rest/secure/group/" + model.get("id") + "/attendance/members";
		this.doPost(url, json).then((result)=>{
			controller.set("isLoading" , false);
			if(result.code ==0){
				if(result.item ){
					var attendanceUI = result.item;
					attendanceUI.dateUI = moment(attendanceUI.date).format("Do MMM YYYY");
					controller.set("attendanceUI", attendanceUI);
					if( attendanceUI.toTime ){
						var time = attendanceUI.toTime.split(":");
						 this.controller.set("toMinutesArray", Ember.copy(this.minutesArray, true));
					       this.controller.set("toHoursArray", Ember.copy(this.hoursArray, true));
						 var selectedHour = this.controller.get("toHoursArray").filterBy("id", parseInt(time[0]))[0];
				    	  if(selectedHour){
					    	  Ember.set(selectedHour, 'selected', true)
					      }
				    	  
				    	  var selectedMinutes = this.controller.get("toMinutesArray").filterBy("id", parseInt(time[1]))[0];
				    	  if(selectedMinutes){
					    	  Ember.set(selectedMinutes, 'selected', true)
					      }
					}
					
					
					this.cleanupAttendanceMembers(attendanceUI.members);
				}else{
					controller.set("noRecords" , true);
				}
			}else{
				controller.set("noRecords" , true);
			}
		})
    	
    },
    cleanupAttendanceMembers(members){
    	for(var i =0 ;i<members.length ;i++){
    		var member = members[i];
    		 Ember.defineProperty(member, 'isAbsent', Ember.computed("status", function(member) {
 	            return  Ember.get(this,'status') == 'ABSENT';
 	        }));
    		 Ember.defineProperty(member, 'isLeave', Ember.computed("status", function(member) {
 	            return  Ember.get(this,'status') == 'LEAVE';
 	        }));
    		 Ember.defineProperty(member, 'isPresent', Ember.computed("status", function(member) {
    	            return  Ember.get(this,'status') == 'PRESENT';
    	        }));
    	}
    },
    actions: {
    	setFromHour(hour){
    		this.set("attendance.fromHour", hour.id);
    	},
    	setFromMinutes(minutes) {
      		 this.set("attendance.fromMinutes", minutes.id);
          },
      	setToHour(hour){
        	  this.controller.set("attendanceUI.toHour", hour.id);
      	},
      	setToMinutes(minutes) {
      		 this.controller.set("attendanceUI.toMinutes", minutes.id);
            },
            changeDate(){
            	this.controller.set("editAttendanceDate" , true);

  			  this.controller.set("fromMinutesArray", Ember.copy(this.minutesArray, true));
  	       this.controller.set("fromHoursArray", Ember.copy(this.hoursArray, true));
            	 var selectedHour = this.controller.get("fromHoursArray").filterBy("id", this.get("attendance.fromHour"))[0];
		    	  if(selectedHour){
			    	  Ember.set(selectedHour, 'selected', true)
			      }
		    	  
		    	  var selectedMinutes = this.controller.get("fromMinutesArray").filterBy("id", this.get("attendance.fromMinutes"))[0];
		    	  if(selectedMinutes){
			    	  Ember.set(selectedMinutes, 'selected', true)
			      }
            },
          fetchAttendance(){
        	  var group = this.controller.get("model");
        	  var attendance = this.attendance;
        	var json = {
        		date : attendance.date.getTime(),
        		fromTime :this.get("attendance.fromHour") + ":" +this.get("attendance.fromMinutes"),
        		groupId : group.get("id"),
        	}
        	this.fetchGroupAttendanceMembers(json);
        	
          },
          markPresent(member) {
        	  if(Ember.get(member,"isPresent")){
        		  Ember.set(member, "status", ""); 
        	  }else{
        		  Ember.set(member, "status", "PRESENT");
        	  }
       		 
             },
             markAbsent(member) {
            	 if(Ember.get(member,"isAbsent")){
           		  Ember.set(member, "status", ""); 
           	  }else{
           		  Ember.set(member, "status", "ABSENT");
           	  }
                 },
                 markLeave(member) {
                	 if(Ember.get(member,"isLeave")){
               		  Ember.set(member, "status", ""); 
               	  }else{
               		  Ember.set(member, "status", "LEAVE");
               	  }
                     },
                     
            markAllPresent() {
              var members = this.controller.get("attendanceUI.members");
              members.forEach(function(member) {
            	  Ember.set(member, "status", "PRESENT");
            	  });
            },  
            
            markAllAbsent() {
                var members = this.controller.get("attendanceUI.members");
                members.forEach(function(member) {
              	  Ember.set(member, "status", "ABSENT");
              	  });
              },  
              markAllLeave() {
                  var members = this.controller.get("attendanceUI.members");
                  members.forEach(function(member) {
                	  Ember.set(member, "status", "LEAVE");
                	  });
                },                
          saveAttendance(){
        	  var attendance = this.controller.get("attendanceUI");
        	 var toTime =  this.controller.get("attendanceUI.toHour");
             var members = this.controller.get("attendanceUI.members");
             var model = this.controller.get("model");
             var msg = "";
             members.forEach(function(member) {
            	 if(!Ember.get(member,"status")){
            		 msg = msg + member.name + "\n";
            	 }
           	  });
             if(msg){
            	 alert("Please mark attendance for \n" + msg);
            	 return;
             }
        	 if(toTime){
        		 if( this.controller.get("attendanceUI.toMinutes")){
        			 toTime = toTime + ":" +  this.controller.get("attendanceUI.toMinutes");
        		 }else{
        			 toTime = toTime + ":00"; 
        		 }
        	 }
        	 attendance.toTime = toTime; 
        	  		var	url = "/rest/secure/group/" + model.get("id") + "/attendance/upsert";
        	  this.controller.set("isLoading" , true);
			this.doPost(url, attendance).then((result)=>{
				this.controller.set("isLoading" , false);
				if(result.code ==0){
					if(result.item ){
						alert(result.message);
						//this.transitionTo('group.attendance',model.get("id"));
					//	this.refresh();
						this.send('changeDate');
					}
				}else if(result.message){
					alert(result.message);
				}
			});
          },
       
          deleteAttendance(){
          	if(confirm("Delete this attendance ?")){
          		var model = this.controller.get("model");
          		 var attendance = this.controller.get("attendanceUI");
          		var	url = "/rest/secure/group/" + model.get("id") + "/attendance/delete";
          	  this.controller.set("isLoading" , true);
          		this.doPost(url, attendance).then((result)=>{
        			this.controller.set("isLoading" , false);
        			if(result.code ==0){
    					alert(result.message);
    					this.send('changeDate');
        			}else if(result.message){
        				alert(result.message);
        			}
        		});
          	}
          },
        error(reason){
        	this.transitionTo('dashboard');
        },
        downloadAttendanceReport(){
    		 window.open("/rest/attendance/" + this.get("controller").get("model").get("id") +"/report/download");
    	},
    }
});