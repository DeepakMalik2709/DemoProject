import Ember from 'ember';

export function isNot([value, ...rest]) {
	 return !value;
}

export default Ember.Helper.helper(isNot);
