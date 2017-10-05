import Ember from 'ember';
import DS from 'ember-data';
import ajaxMixin from '../mixins/ajax';

export default DS.Store.extend(ajaxMixin ,{
		tags : {},
		init() {
		    this._super(...arguments);
		  },
		  
		  search: function(term) {
			  return  new Ember.RSVP.Promise((resolve, reject) =>{
				  if(this.tags.hasOwnProperty(term)){
					  resolve({items : this.tags[term]});
				  }else{
						  var url = "/rest/secure/tags/search?q=" + term;
							this.doGet(url).then((data ) =>{
								this.tags[term] = data.items;
								  resolve(data);
						      });
				  }
			 });
		
	 },
});