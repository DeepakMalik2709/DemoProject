import Ember from 'ember';

export default Ember.Mixin.create({
	
	 beforeModel(transition) {
		 var context = this.contextService.fetchContext(result=>{
				if(result && result.code==0){
				}else{
					 this.transitionTo("");
				}
			});
		  }
});