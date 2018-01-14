export default Ember.Component.extend( {
	init(){
		this._super(...arguments);
		
	},
	  didInsertElement() {
	    let _this = this;
	    Ember.$("." + this.elemClass).scroll(function() {
	      if(Ember.$(this).scrollTop() + Ember.$(this).innerHeight() >= Ember.$(this)[0].scrollHeight) {
	        _this.sendAction("scrollAction"); // Triggering passed controller’s action
	      }
	    });
	  },
	  actions: {
	  }
});