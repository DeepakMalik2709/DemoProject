import Ember from 'ember';
import utilsMixin from '../mixins/utils';

export default Ember.Component.extend(utilsMixin,{
	  tagService: Ember.inject.service('tag'),
	  tags :[],
	  fetchTags (searchTerm){
	    	if(searchTerm && searchTerm.length > 2){
	    		this.set("isLoading" , true);
	    		this.get("tagService").search(searchTerm).then((result)=>{
	    			this.set("isLoading" , false);
	    			this.set("tags", result.items);
	    		});
	    		
	    	}
	    },
	    doSearchFn(){
    		var  searchTerm = this.searchTerm;
    		this.fetchTags(searchTerm);
    	},
    	keyUp : function(event){
    		var thisKeyCode = event.keyCode;
    		var tags = this.tags;
			 if(thisKeyCode ===this.keyCodes.ESCAPE ) {
				 this.resetList();
			}else if(thisKeyCode ===this.keyCodes.UP) {
				 this.selectPrev(tags);
			}else if(thisKeyCode ===this.keyCodes.DOWN) {
				this.selectNext(tags);
			}else  {
				Ember.run.throttle(this, this.doSearchFn, 100);
			}
    	},
    	resetList(){
    		this.set("tags", []);
    		this.set("searchTerm", "");
    	},
    	
    actions: {
    	tagClick(tag) {
    		this.sendAction("tagClick", tag);
    		this.resetList();
        },
        returnPressed(){
        	var tags = this.tags;
        	if(tags && tags.length){
        		var index = this.getFocussedFileIndex(tags);
				if (index < 0) {
					Ember.run.throttle(this, this.doSearchFn, 100);
				}else{
					this.sendAction("tagClick", tags[index]);
					this.resetList();
				}
        	}
        }
    }
});