import Ember from 'ember';
import Table from 'ember-light-table';
import scrollMixin from '../mixins/scroll';
import authenticationMixin from '../mixins/authentication';
import ajaxMixin from '../mixins/ajax';

export default Ember.Component.extend(scrollMixin,authenticationMixin,ajaxMixin,{
	groudId:null,
	model:null,
	rows:[],
	tableSchema:[{
		 label: 'Student Attendance',	      sortable: true,	      align: 'center',
		 subColumns:[{
			  label: 'Date',		  sortable: true,	      valuePath: 'date',	      width: '150px'	    }, {
		      label: 'Email',		   sortable: true,	      valuePath: 'email',	      width: '150px'	    }, {
		      label: 'Status',		   sortable: true,	      valuePath: 'status',	      width: '150px'	    }]
				}],
	columns:Ember.computed(function() {
    return this.tableSchema;
  }),

  init() {
	    this._super(...arguments);
	    this.set("model" , this.item,true);
	    this.groupId=this.get("model.groupId");
	    this.refreshTable( this.get('rows'));

  },
  refreshTable(data){
	  let table = new Table(this.get('columns'),data, { enableSync: true });
      let sortColumn = table.get('allColumns').findBy('valuePath', this.get('sort'));

      // Setup initial sort column
      if (sortColumn) {
      sortColumn.set('sorted', true);
      }

      this.set('table', table);
  },
  fetchAttByDate(start,end){
  	var url = "/rest/secure/group/" +this.groupId + "/attendance?fromDate="+new Date(start).getTime()+"&toDate="+new Date(end).getTime();
  	this.doGet(url).then((result)=>{
			if(result.code ==0){
				if(result.item){
					 this.set("rows" , result.item.members,true);
					 this.refreshTable( this.get('rows'));
				}
			}
		})
  },
    actions: {
    	fetchStudentAttendance(item){
			if(item.get('start') > item.get('end')){
				alert("from date must to less then to date");
			}else{
				this.fetchAttByDate(item.get('start'),item.get('end'));
			}
    	},
    	 onColumnClick(column) {
    	      if (column.sorted) {
    	        this.setProperties({
    	          dir: column.ascending ? 'asc' : 'desc',
    	          sort: column.get('valuePath'),
    	          canLoadMore: true,
    	          page: 0
    	        });
    	        this.get('model').clear();
    	      }
    	    },
        cancelClicked(item) {
        	 this.transitionTo('calendar');
        },
        willTransition(transition) {
         //   let model = this.controller.get('model');
            /*
             * if(model.get('hasDirtyAttributes')){ let confirmation =
             * confirm("leave without saving ? "); if(confirmation){
             * model.rollbackAttributes(); }else{ transition.abort(); } }
             */
        }
    }

});
