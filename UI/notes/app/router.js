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
  this.route('terms');
  this.route('group', function() {
        this.route('posts',{path : '/:groupId/posts'});
        this.route('view',{path : '/:groupId/view'});
        this.route('public',{path : '/:groupId'});
        this.route('edit',{path : '/:groupId/edit'});
        this.route('create',{path : '/create'});
        this.route('post',{path : '/post/:postId'});
        this.route('task.create',{path : '/task/create'});
        this.route('task.edit',{path : '/task/edit/:taskId'});
        this.route('attendance',{path : '/:groupId/attendance'});
      });
  this.route('attendance', function() {
      this.route('view',{path : '/:groupId'});
  });
  this.route('tag', function() {
      this.route('view',{path : '/:groupId'});
       this.route('edit',{path : '/edit/:tagId'});
        this.route('create',{path : '/create'});
      });
  this.route('institute', function() {
      this.route('public',{path : '/:instituteId'});
      this.route('view',{path : '/:instituteId/view'});
       this.route('edit',{path : '/:instituteId/edit'});
        this.route('create',{path : '/create'});
      });
  this.route('calendar');

  this.route('new', function() {
    this.route('schedule');
  });
  this.route('quiz', function() {
    this.route('create');
    this.route('upsert',{path:'/upsert/:quizId'});
    this.route('play',{path:'/play/:quizId'});
    this.route('grid');
  });
});

export default Router;
