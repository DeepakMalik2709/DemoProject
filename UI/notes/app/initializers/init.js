/*export function initialize( application ) {
  // application.inject('route', 'foo', 'service:foo');
}*/

export function initialize(application) {  
	application.inject('route', 'tutorialService', 'service:tutorial');
	application.inject('route', 'eventService', 'service:new.schedule');
	application.inject('route', 'contextService', 'service:context');
	application.inject('controller', 'contextService', 'service:context');
}

export default {
  name: 'init',
  initialize
};
