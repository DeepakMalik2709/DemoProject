import Ember from 'ember';

export default Ember.Component.extend({
	contextService: Ember.inject.service('context'),
	postService: Ember.inject.service('post'),
	useGoogleDrive : false,
	 init() {
		    this._super(...arguments);
		    this.useGoogleDrive = Ember.get(this.get("contextService").fetchContext().get("loginUser"), "useGoogleDrive");
		    this.set("showPreview" , false);
		    if(this.item.hasThumbnail){
		    	var thumbnailLink = "/img/no-preview-available.png";
			    if(this.item.hasThumbnail){
			    	thumbnailLink = "/a/secure/group/file/thumbnail?name=" + this.item.serverName ;
			    }
		    	Ember.set(this.item , "thumbnailLink" ,thumbnailLink );
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
		 addToLibrary(item){
			  Ember.set(this.item, "showLoading", true);
			  if(this.useGoogleDrive){
				  this.get("postService").addToLibrary(item).then((result)=>{
					  Ember.set(this.item, "showLoading", false);
					 
		    		if(result.code == 0  ){
		    			alert("file successfully add to your library.");
		    		}else{
		    			alert("Error code "+result.code+" Message :"+result.message);
		    		}
		    	});
				  console.log(item.name);
			  }
			  Ember.set(this.item, "showLoading", false);
		 },
		 hovered(){
			 this.showPopover();
		 },
		 removeFile(){
		 		this.sendAction("removeFile", this.item);
		 	}
	 }
});