import Ember from 'ember';


export default Ember.Mixin.create({
	readAsBinary(buffer){
        var binary = "";
        var bytes = new Uint8Array(buffer);
        var length = bytes.byteLength;
        for (var i = 0; i < length; i++) {
          binary += String.fromCharCode(bytes[i]);
        }
        return binary;
	},

	getPhotoUrl(email) {
		if(email){
			return  `/rest/public/${email}/photo` ;
		}
		return '';
	},
    cleanupTimes(item) {
		if(item.createdTime){
			Ember.set(item, "createdDisplayTime", this.getTimeDifference(Ember.get(item, 'createdTime')));
		}
		if(item.updatedTime){
			Ember.set(item, "updatedDisplayTime", this.getTimeDifference(Ember.get(item, 'updatedTime')));
		}
		if(Ember.get(item,'deadlineTime')){
			Ember.set(item, "deadlineDisplayTime", this.getFullTime(Ember.get(item, 'deadlineTime')));
		}
		if(Ember.get(item,'fromDate')){
			if(Ember.get(item,'allDayEvent')){
				Ember.set(item, "fromDisplayTime", this.getDate(Ember.get(item, 'fromDate')));
			}else{
				Ember.set(item, "fromDisplayTime", this.getFullTime(Ember.get(item, 'fromDate')));
			}
		}
		if(Ember.get(item,'toDate')){
			if(Ember.get(item,'allDayEvent')){
				Ember.set(item, "toDisplayTime", this.getDate(Ember.get(item, 'toDate')));
			}else{
				Ember.set(item, "toDisplayTime", this.getFullTime(Ember.get(item, 'toDate')));
			}
			
		}
    },
	   getTimeDifference(time) {
        var diff = (new Date().getTime() - time) / 1000;
        var msg = "";
        if (diff < 60) {
            var secs = Math.floor(diff);
            if (secs <= 0) {
                msg = 'now ';
            } else {
                msg = secs + " " + 'seconds ago';
            }
        } else if (diff < 3600) {
            var mins = Math.floor(diff / 60);
            if (mins == 1) {
                msg = mins + " " + 'minute ago';
            } else {
                msg = mins + " " + 'minutes ago';
            }
        } else if (diff < 86400) {
            var hours = Math.floor(diff / 3600);
            if (hours == 1) {
                msg = hours + " " + 'hour ago';
            } else {
                msg = hours + " " + 'hours ago';
            }

        } else {
            msg = this.getFullTime(time);
        //    console.log( time, new Date(time), msg)
        }
        return msg;
    },
    getFullTime(time){
    	return moment(time).format("Do MMM YYYY hh:mm a");
    },
    getDate(time){
    	return moment(time).format("Do MMM YYYY");
    },
    
    dateTimeFormatAMPM: function(date) {
  	  var hours = date.getHours();
  	  var minutes = date.getMinutes();
  	  var ampm = hours >= 12 ? 'pm' : 'am';
  	  hours = hours % 12;
  	  hours = hours ? hours : 12; // the hour '0' should be '12'
  	  minutes = minutes < 10 ? '0'+minutes : minutes;
  	  var strTime = hours + ':' + minutes + ' ' + ampm;
  	  return strTime;
     },
      
    keyCodes : {
			COMMA: 44,
			COMMA2: 188,
			DELETE: 46,
			DOWN: 40,
			END: 35,
			ENTER: 13,
			ESCAPE: 27,
			HOME: 36,
			LEFT: 37,
			PAGE_DOWN: 34,
			PAGE_UP: 33,
			PERIOD: 190,
			RIGHT: 39,
			TAB: 9,
			UP: 38,
			SPACE : 32,
			BACKSPACE : 8
		},
		  isValidEmail(email){
			 var EMAIL_REGEXP = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$/  ;
			    if(email && EMAIL_REGEXP.test(email)){
			        return true;
			    }else{
			        return false;
			    }
			},
			
			getDomain(email){
				if(email){
					return email.substring(email.indexOf("@")+1)
				}
				return "";
			},
			getFocussedFileIndex (list) {
				var index = -1;
				var file;
				for ( var i in list) {
					file = list[i];
					if (Ember.get(file , "isFocussed")) {
						index = i;
						Ember.set(file , "isFocussed", false);
					}
				}
				return parseInt(index);
			},

			selectNext (list) {
				if (list.length) {
					var index = this.getFocussedFileIndex(list);
					if (index + 1 == list.length) {
						index = 0;
					} else {
						++index;
					}
					this.select(list, index);
				}
			},

			selectPrev ( list) {
				if (list.length) {
					var index = this.getFocussedFileIndex(list);
					if (index <= 0) {
						index = list.length - 1;
					} else {
						--index;
					}
					this.select(list, index);
				}
			},

			select (list, index) {
				Ember.set(list[index] , "isFocussed", true);
			}
});