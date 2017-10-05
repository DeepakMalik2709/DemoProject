import Ember from 'ember';
import postMixin from '../mixins/post';
export default Ember.Component.extend(postMixin , {
	 postService: Ember.inject.service('post'),
    init() {
	    this._super(...arguments);
	    this.cleanupPost(this.item);
	  },
    actions: {
    	updateCommentReply(){
    		this.updateRecipients(this.item);
    		this.sendAction("update", this.item);
    	},
    	editCommentReply(){
        	this.originalComment = Ember.get(this.item, "comment");
        	Ember.set(this,"item.isEditing", true);
        	 this.$(".edit-comment").html(this.originalComment);
        },
        cancelEditing(){
        	Ember.set(this,"item.isEditing", false);
        	Ember.set(this, "item.comment", this.originalComment);
        },
    	
        deleteCommentReply( ){
        	this.sendAction("deleteCommentReply", this.item);
    	}
    }
});