import Ember from 'ember';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(authenticationMixin,{

    contextService: Ember.inject.service('context'),
dateFormat:function(value){ var date =new Date(value); return date.getDay()+"/"+date.getMonth();}   ,
    tableSchema:[{ label: 'Name',		className:'time', sortable: true,	      valuePath: 'name',	     width: '20%'	    },
                { label: 'Subject',	className:'time',	  sortable: true,	      valuePath: 'subject',     width: '30%'	    },
                { label: 'From',	className:'time',	  sortable: true,	      valuePath: 'fromDateTime' , width: '10%'	,format:function(value){var date =new Date(value); return date.getDay()+"/"+date.getMonth();}},
                 { label: 'To',		className:'time',    sortable: true,	      valuePath: 'toDateTime',	width: '10%'	,format:function(value){  var date =new Date(value); return date.getDay()+"/"+date.getMonth();}   }],

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
            this.transitionTo('quiz.upsert');
        },
      }

});
