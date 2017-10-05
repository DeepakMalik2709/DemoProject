import Ember from 'ember';

export default Ember.Component.extend({
  content: [],
  prompt: null,
  
  init: function () {
    this._super(...arguments);
    if (!this.get('content')) {
      this.set('content', []);
    }else if(this.get("selection")){
    	let list = this.get('content');
    	let selection = this.get("selection");
    	list.forEach(function(item, index, enumerable){
           if(selection == item.id){
        	   Ember.set(item,"selected", true);
           }else{
        	   Ember.set(item,"selected", false);
           }
        });
    }
  },
  
  actions: {
    change: function () {
      let selectedIndex = this.$('select')[0].selectedIndex;
      let content = this.get('content');
      
      // decrement index by 1 if we have a prompt
      let hasPrompt = !!this.get('prompt');
      let contentIndex = hasPrompt ? selectedIndex - 1 : selectedIndex;
      let _selection = content[contentIndex];
  
      this.sendAction('willChangeAction', _selection);

      if (this.get('optionValuePath')) {
        this.set('selection', _selection[this.get('optionValuePath')]);
      } else {
        this.set('selection', _selection);
      }

      this.sendAction('didChangeAction', _selection);
    }
  }
});