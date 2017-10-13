import Ember from 'ember';

export default Ember.Component.extend({
	 init() {
		    this._super(...arguments);
		    var hoursArray = [];
		       for(var i = 0 ; i < 24; i++){
		    	   hoursArray.push({label : (i) , id :i});
		       }
		       var minutesArray = [];
		       for(var i = 0 ; i < 50; i = i+15){
		    	   minutesArray.push({label : i , id :i});
		       }
		       this.set("minutesArray", minutesArray);
		       this.set("hoursArray",hoursArray);
		       this.set("dateValue",0);
		  },
		  recalculate : function(){
	        	var date = Ember.get(this,"dateValue");
	        	if(date){
	        		date = date.getTime();
	        		var selectedHour = this.get("selectedHour.id");
	     			var selectedMinutes = this.get("selectedMinutes.id");
	     			if(selectedHour){
	     				date += (selectedHour * 60 * 60000);
	     			}
	     			if(selectedMinutes){
	     				date += selectedMinutes* 60000;
	     			}
	     			Ember.set(this.item, 'deadlineTime', date)
	        	}
		  },
    actions: {
    	addDate() {
      		 this.recalculate();
          },
    	addHour(hour) {
   		 this.set("selectedHour", hour);
   		 this.recalculate();
       },
       addMinutes(minutes) {
   		 this.set("selectedMinutes", minutes);
   		this.recalculate();
       },
    }
});