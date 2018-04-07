import Ember from 'ember';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(authenticationMixin,{
    contextService: Ember.inject.service('context'),
dateFormat:function(value){ var date =new Date(value); return date.getDay()+"/"+date.getMonth();}   ,
    tableSchema:[{ label: 'Name',		className:'time', sortable: true,	      valuePath: 'name',	     width: '20%'	    },
                { label: 'Subject',	className:'time',	  sortable: true,	      valuePath: 'subject',     width: '30%'	    },
                { label: 'From',	className:'time',	  sortable: true,	      valuePath: 'fromDateTime' , width: '20%'	,format:function(value){ return new Date(value);}},
                 { label: 'To',		className:'time',    sortable: true,	      valuePath: 'toDateTime',	width: '20%'	,format:function(value){  return new Date(value);}   }],

	model() {
        return  this.store.findAll('quiz');
    },

    init() {
       this._super(...arguments);
     },
     setupController: function(controller, model) {
          this._super(controller, model);
          controller.set('tableSchema', this.tableSchema);
           controller.set('rows',model);
      },
       actions:{
        createQuiz(){
            this.transitionTo('quiz.create');
        },
        quizClick(row){
          var user = this.contextService.getLoginUser();
          console.log(user);
          console.log(row.get('createdByEmail'));
          if(user.email ===row.get('createdByEmail')){
            this.transitionTo('quiz.upsert',row.get('id'));
          }else{
            this.transitionTo('quiz.play',row.get('id'));
          }

        }
      }

});
