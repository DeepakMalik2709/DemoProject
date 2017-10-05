import Ember from 'ember';

export default Ember.Component.extend({
	
	
	 init() {
		    this._super(...arguments);
		    this.set("showPreview" , false);
		    var thumbnailLink = "/img/no-preview-available.png";
		    if(this.item.hasThumbnail){
		    	thumbnailLink = "/a/public/tutorial/file/thumbnail?name=" + this.item.serverName ;
		    }
		    Ember.set(this.item, "thumbnailLink" , thumbnailLink);
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