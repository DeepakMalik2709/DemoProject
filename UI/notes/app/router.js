import Ember from 'ember';
import config from './config/environment';

const Router = Ember.Router.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.route('tutorial', function() {
        this.route('view',{path : '/:tutorialId'});
        this.route('edit',{path : '/:tutorialId/edit'});
        this.route('create',{path : '/create'});
      });
  this.route('dashboard', function() {  });
  this.route('home', function() {  });
  this.route('profile');
  this.route('group', function() {
        this.route('posts',{path : '/:groupId/posts'});
        this.route('view',{path : '/:groupId'});
        this.route('edit',{path : '/:groupId/edit'});
        this.route('create',{path : '/create'});
        this.route('post',{path : '/post/:postId'});
        this.route('task.create',{path : '/task/create'});
        this.route('task.edit',{path : '/task/edit/:taskId'});
      });
  this.route('tag', function() {
      this.route('view',{path : '/view'});
       this.route('edit',{path : '/edit/:tagId'});
        this.route('create',{path : '/create'});
      });
  this.route('calendar');

  this.route('new', function() {
    this.route('schedule');
  });
});

export default Router;
