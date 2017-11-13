import Ember from 'ember';
import utilsMixin from './utils';

export default Ember.Mixin.create(utilsMixin , {
	contextService: Ember.inject.service('context'),
	roles : [{ id : "STUDENT" , label : "Student"},{ id : "TEACHER" , label : "Teacher"},{ id : "ADMIN" , label : "Admin"}],
	cleanupMembers (members){
		if(members ){
			for(var i =0;i<members.length; i++){
				var member = members[i];
				this.cleanupMember(member);
			}
		}
	},
	cleanupMember (member){
		if(member.positions && member.positions.length){
			Ember.set(member, "roles",[]);
			for(var i =0;i<member.positions.length; i++){
				var position = member.positions[i];
				var role = this.roles.filterBy("id", position)[0];
				if(role){
					Ember.get(member, "roles").pushObject( role);
				}
			}
		}else{
			Ember.set(member, "roles",[this.roles[0]]);
		}
	}
});