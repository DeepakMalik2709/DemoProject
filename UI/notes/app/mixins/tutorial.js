import Ember from 'ember';


export default Ember.Mixin.create({
	  cleanupAllTutorials(myTutorials) {
	        myTutorials.forEach((item1) => this.cleanupTutorial(item1));
	    },

	    cleanupTutorial(item1) {
	        Ember.set(item1, "createdDisplayTime", this.getTimeDifference(item1.createdTime));
	    },
	    cleanupNewResults(myTutorials) {
	        myTutorials.forEach((item1) => this.cleanupNewTutorial(item1));
	    },

	    cleanupNewTutorial(item1) {
	    	if(Ember.get(item1, "url")){
	    		Ember.set(item1, "showVideo", true);
	    	}
	    },
	    getTimeDifference(time) {
	        var diff = (new Date().getTime() - time) / 1000;
	        var msg = "";
	        if (diff < 60) {
	            var secs = Math.floor(diff);
	            if (secs <= 0) {
	                msg = 'now ';
	            } else {
	                msg = secs + " " + 'seconds ago';
	            }

	        } else if (diff < 3600) {
	            var mins = Math.floor(diff / 60);
	            if (mins == 1) {
	                msg = mins + " " + 'minute ago';
	            } else {
	                msg = mins + " " + 'minutes ago';
	            }

	        } else if (diff < 86400) {
	            var hours = Math.floor(diff / 3600);
	            if (hours == 1) {
	                msg = hours + " " + 'hour ago';
	            } else {
	                msg = hours + " " + 'hours ago';
	            }

	        } else {
	            msg = moment(new Date(time), "dd-MMM-yyyy HH:mm a");
	        }
	        return msg;
	    },
});