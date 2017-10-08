import Ember from 'ember';
import moment from 'moment';
import tutorialMixin from '../mixins/tutorial';
import scrollMixin from '../mixins/scroll';

export default Ember.Route.extend(tutorialMixin,scrollMixin,{
    queryParams: {
        q: {
            refreshModel: true
        }
    },
    model(params) {
        var search = Ember.Object.create({
            searchTerm: params.q,
            searchResults: [],
            nextLink: "",
            isLoading: false,
            hasMoreResults : true,
        });
        return search;
    },

    init: function() {},
    setupController: function(controller, model) {
        this._super(controller, model);
        let isLoggedIn = this.controllerFor("application").get("isLoggedIn");
        this.controller.set("isLoggedIn", isLoggedIn);
        controller.set("isSearchButtonDisabled", Ember.computed.empty("model.searchTerm"));
        model.set("noBoxShadow" , true);
        if (model.get("searchTerm")) {
            this.fetchSearchResult();
        }else if(isLoggedIn){
        	this.transitionTo("home");
        }else{
        	 this.fetchTrending();
        }
        this.bindScrolling();
    },
    scrolled: function() {
    	  var model = this.get("controller").get("model");
    	if (model.get("searchTerm")) {
            this.fetchSearchResult();
        }else{
        	 this.fetchTrending();
        }
      },
      willDestroy : function(){
      	this.unbindScrolling();
      },
    fetchSearchResult: function(term) {
        var thisSearch = this.get("controller").get("model");
        if (!thisSearch.isLoading) {
        	this.controller.set("model.isLoading", true);
        	 this.controller.set("model.noBoxShadow" , true);
            this.tutorialService.searchTutorial(thisSearch.searchTerm, thisSearch.nextLink).then((result) => {
            	this.controller.set("model.isLoading", false);
                if (result.code == 0) {
                    thisSearch.searchResults.pushObjects(result.items);
                    if( result.nextLink){
                 	   this.controller.set("model.nextLink", result.nextLink);
                 	   this.controller.set("model.hasMoreResults", true);
                    }else{
                 	   this.controller.set("model.hasMoreResults", false);
                    }
                    this.cleanupNewResults(result.items);
                    this.cleanupAllTutorials( thisSearch.searchResults);
                    this.controller.set("model.title", "Search result");
                    this.controller.set("model.noBoxShadow" , thisSearch.searchResults.length <1);
                    this.controller.set("model.noResults", thisSearch.searchResults.length <1);
                }
            });
        }
    },

    fetchTrending: function() {
    	   var thisSearch = this.get("controller").get("model");
           if (!thisSearch.isLoading && thisSearch.hasMoreResults) {
        	   this.controller.set("model.isLoading", true);
               this.controller.set("model.noBoxShadow" , true);
               this.tutorialService.fetchTrending( thisSearch.nextLink).then((result) => {
            	   this.controller.set("model.isLoading", false);
                   if (result.code == 0) {
                       thisSearch.searchResults.pushObjects(result.items);
                       if( result.nextLink){
                    	   this.controller.set("model.nextLink", result.nextLink);
                    	   this.controller.set("model.hasMoreResults", true);
                       }else{
                    	   this.controller.set("model.hasMoreResults", false);
                       }
                       this.cleanupNewResults(result.items);
                       this.cleanupAllTutorials( thisSearch.searchResults);
                       this.controller.set("model.title", "Trending");
                       this.controller.set("model.noBoxShadow" , thisSearch.searchResults.length <1);
                       this.controller.set("model.noResults", thisSearch.searchResults.length <1);
                   }
               });
           }
    },
    
    actions: {
        doSearch() {
            var model = this.get("controller").get("model");
            if (model.searchTerm) {
                model.set("searchResults", []);
                model.set("nextLink", "");
                this.fetchSearchResult();
            }

        },
        loadTutorials() {
            this.fetchSearchResult();
        }
    }
});