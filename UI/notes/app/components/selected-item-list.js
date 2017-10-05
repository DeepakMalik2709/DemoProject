import Ember from 'ember';

export default Ember.Component.extend({
	
    actions: {

    	addItem(itemToAdd) {
            var items = this.get("items");
            var index = items.indexOf(itemToAdd);
            if(index < 0){
            	items.pushObject(lang);
    		}
        },
        removeItem(itemToRemove) {
        	  var items = this.get("items");
        	  items.removeObject(itemToRemove);
        },
    }
});