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
		    
		    if(this.item.name.length>12){
		    	var shortName= this.item.name.substring(0,10)+"..";
		    	Ember.set(this.item , "shortName" ,shortName );
		    }else{
		    	Ember.set(this.item , "shortName" ,this.item.name );
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
	 shortenName : function(name, size){
		 return name+"&nbsp;("+size+")";
	 },
	 actions: {
		 addToLibrary(item){
			  if(this.useGoogleDrive){
				  Ember.set(this.item, "showLoading", true);
				  this.get("postService").addToLibrary(item).then((result)=>{
					  Ember.set(this.item, "showLoading", false);
		    		if(result.code == 0  ){
		    			alert("file added to your library.");
		    		}else{
		    			alert("Error code "+result.code+" Message :"+result.message);
		    		}
		    	});
			  }
			  
		 },
		 hovered(){
			 this.showPopover();
		 },
		 removeFile(){
		 		this.sendAction("removeFile", this.item);
	 	},
	 	openFile(item){
	 		if(Ember.get(item,"driveLink")){
	 			window.open(Ember.get(item,"driveLink"));
	 		}
	 	}
	 }
});