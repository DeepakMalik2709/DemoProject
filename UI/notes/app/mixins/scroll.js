import Ember from 'ember';

export default Ember.Mixin.create({
	
	 bindScrolling: function(opts) {
		    var onScroll, _this = this;
		    onScroll = function(){ 
		    	 var raw =  $("body")[0];
		    	var diff = 800;
		    	
	        	  if (raw.scrollTop + diff >=  raw.scrollHeight) {
	        		  if( typeof _this.scrolled == "function"){
	  		    		Ember.run.throttle(_this, function(){
	  		    			_this.scrolled();
	  		    		} , 2000);
	  		    	}
	              }
		    	
		    };

		    $(document).bind('touchmove', onScroll);
		    $(window).bind('scroll', onScroll);
		  },

		  unbindScrolling: function() {
		    $(window).unbind('scroll');
		    $(document).unbind('touchmove');
		  }
});