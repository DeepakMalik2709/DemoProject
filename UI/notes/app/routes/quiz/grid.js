import Ember from 'ember';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(authenticationMixin,{

    contextService: Ember.inject.service('context'),

    tableSchema:[{ label: 'Name',		  sortable: true,	      valuePath: 'name',	     width: '150px'	    },
                { label: 'Subject',		  sortable: true,	      valuePath: 'subject',     width: '150px'	    },
                { label: 'From',		  sortable: true,	      valuePath: 'fromDateTime' , width: '150px'	,format:function(value){  return new Date(value);}   },
                 { label: 'To',		    sortable: true,	      valuePath: 'toDateTime',	width: '150px'	,format:function(value){  return new Date(value);}   }],

	model() {
        return  this.store.findAll('quiz');
    },
    dateFormat:function(value){
      //var date = new Date(value);
      //  return new Date(Ember.Date.parse(value)).toLocaleDateString();
      return new Date(value);
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
