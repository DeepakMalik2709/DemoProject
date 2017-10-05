import Ember from 'ember';

export default Ember.Component.extend({
	
	
	 init() {
		    this._super(...arguments);
		    this.set("showPreview" , false);
		    if(this.item.hasThumbnail){
		    	this.item.thumbnailLink = "/a/secure/group/file/thumbnail?name=" + this.item.serverName ;
		    }else{
		    	this.item.thumbnailLink = "/img/no-preview-available.png"
		    }
	 },
	
	 mouseLeave : function(){
		 this.hidePopover();
	 }, 
	 focusOut : function(){
		 this.hidePopover();
	 },
	 
	 hidePopover : function(){
		 this.set("showPreview" , false);
	 },
	 showPopover : function(){
		 this.set("showPreview" , true);
	 },
	 actions: {
		 hovered(){
			 this.showPopover();
		 },
		 removeFile(){
		 		this.sendAction("removeFile", this.item);
		 	}
	 }
});