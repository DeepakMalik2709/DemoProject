import Ember from 'ember';
import utilsMixin from '../mixins/utils';
import ajaxMixin from '../mixins/ajax';

export default Ember.Component.extend(utilsMixin,ajaxMixin,{
  classNames: ['ember-content-editable'],
  classNameBindings: ['extraClass', 'clearPlaceholderOnFocus:clear-on-focus'],
  attributeBindings: [
    'contenteditable',
    'placeholder',
    'spellcheck',
    'tabindex',
    'readonly',
    'disabled'
  ],
  autoCompleteList : [],
  showAutocomplete : false,
  autoCompleteSearchTerm : '',
  recipientList : [],
  hasError : false,
  thisQuery : null,
  $elem :null,
  editable: null,
  disabled: null,
  spellcheck: null,
  isText: null,
  type: null,
  readonly: null,
  allowNewlines: true,
  autofocus: false,
  clearPlaceholderOnFocus: false,
  inputType: "html",
  init : function(){
	 this._super(...arguments);
	 this.set('value', '');
	 
	 if(this.controllerRef){
		 this.controllerRef.component = this;
	 }
},
resetCommentBox : function(){
	 this.reset();
},
reset(){
	 this.$(".contenteditable").html('');
	 this.resetAutocomplete();
	 this._processInput();
},
  setup: Ember.on('didInsertElement', function() {
   // this.setValue();
    Ember.run.once(() => this._processInput());

    this.$().on('paste', (event) => {
      this.handlePaste(event, this);
    });

    if (this.get('autofocus')) {
      this.$().focus();
    }
  }),

  tidy: Ember.on('willDestroyElement', function() {
    this.$().off('paste');
  }),

  _observeValue: true,
  valueChanged: Ember.observer('value', function() {
    if (this.get('_observeValue')) {
      this.setValue();
    }
  }),

  setValue() {
    if (this.element) {
      this.$().text(this.get('value'));
    }
  },

  stringInterpolator(s) { return s; },

  _getInputValue() {
    if (this.get('inputType') === "html") {
      // Deocde html entities
      let val = this.$(".contenteditable").html();
      //val = this.$('<div/>').html(val).text();
      return val;
    } else {
      return this.$elem.innerText || this.$elem.textContent;
    }
  },

  _processInput() {
    let val = this._getInputValue();
    val = this.stringInterpolator(val);
    val = this.htmlSafe(val);

    this.set('_observeValue', false);
    this.set('value', val);
    this.set('_observeValue', true);
  },

  htmlSafe(val) {
    if (this.get('inputType') === "html") {
      return Ember.String.htmlSafe(val).toString();
    } else {
      return val;
    }
  },

  isUnderMaxLength(val) {
    return (
        Ember.isEmpty(this.get('maxlength')) ||
        val.length < this.get('maxlength')
    );
  },
  updateValue: Ember.on('keyUp', function(event) {
    this._processInput();
    this.handleKeyUp(event);
  }),

  handleKeyUp(e) {
		this.setDefaults();
		switch (e.which) {
		case this.keyCodes.ESCAPE:
			e.preventDefault();
			this.resetAutocomplete();
			this.sendAction('esc-key', this, event);
			break;
		case this.keyCodes.DOWN:
		case this.keyCodes.UP:
		case this.keyCodes.CTRL:
		case this.keyCodes.ENTER:
			Ember.K();
			break;
		case this.keyCodes.BACKSPACE:
			this.backspaceClick(e);
			break;
		default:
			this.dispatch(e);
		}
  },

  /* Events */
  handlePaste(e, _this) {
	  e.preventDefault();
		this.setDefaults();
		var text = (e.originalEvent || e).clipboardData.getData('text/plain') || prompt('Paste something..');
		var tokens = text.split(/[\s,]+/).uniq()
		var updatedText = text;
		var emails = [];
		for(var i = 0;i < tokens.length ; i++){
			var token  = tokens[i];
			if(this.isValidEmail(token)){
				var emailHtml = '<span class="atwho-inserted usertag" data-email="' + token  + '">' + token + '</span>'
				if(i== tokens.length-1){
					 emailHtml += "&nbsp;"
				}
				updatedText = updatedText.replaceAll(token, emailHtml);
				emails.push(token);
			}
		}
		//scope.validateGroupUser(emails);
		if(updatedText){
			updatedText = updatedText.replaceAll("\n", "<br>");
		}
		document.execCommand( 'insertHtml', false, updatedText);
  },

  keyDown(e) {
	  this.setDefaults();
		switch (e.which) {
		case this.keyCodes.ESCAPE:
			e.preventDefault();
			this.resetAutocomplete();
			break;
		case this.keyCodes.UP:
		/*	if(scope.pageState.showAutocomplete){
				e.preventDefault();
				scope.selectPrev(scope.wc.filteredUsers);
			}*/
			Ember.K();
			break;
		case this.keyCodes.DOWN:
			/*if(scope.pageState.showAutocomplete){
				e.preventDefault();
				scope.selectNext(scope.wc.filteredUsers);
			}*/
			Ember.K();
			break;
		case this.keyCodes.ENTER:
			if (e.altKey || e.ctrlKey || e.shiftKey || e.metaKey){
				if(e.altKey || e.ctrlKey || e.metaKey){
					this.addNewLine(e);
				}
				this.resetAutocomplete();
			}else if (this.showAutocomplete ) {
				/*var index = scope.getFocussedFileIndex(scope.wc.filteredUsers);
				if (index >= 0) {
					e.preventDefault();
					insert(scope.wc.filteredUsers[index]);
				} else {
					if(scope.pageState.autoCompleteSearchTerm && has2At(scope.pageState.autoCompleteSearchTerm)){
						e.preventDefault();
						var emailTyped = scope.pageState.autoCompleteSearchTerm.substring(1);
						insert({email :emailTyped , label : emailTyped });
					}
					scope.resetPostAutocomplete();
				}*/
				
				if(this.autoCompleteSearchTerm && this.has2At(this.autoCompleteSearchTerm)){
					e.preventDefault();
					var emailTyped = this.autoCompleteSearchTerm.substring(1);
					this.insert({email :emailTyped , label : emailTyped });
				}
				this.resetAutocomplete();
				
			}else if(true /*scope.pageState.currentPost && scope.pageState.currentPost.type*/){
		/*		var json = {
						comment : this._getInputValue(),
						recipients : this.get('recipientList'),
				}*/
				var comp = this;
				 this.sendAction('save',comp, event);
				e.preventDefault();
			}else{
				return ;
			}

			break;
		default:
			Ember.K();
			
			
		}
  },

  keyPress(event) {
    if (this.get('readonly')) {
      event.preventDefault();
      return false;
    }

    let val = this._getInputValue();
    if (!this.isUnderMaxLength(val)) {
      // Check if text is selected (typing will replace)
      if (window.getSelection().rangeCount > 0) {
        let start = window.getSelection().getRangeAt(0).startOffset;
        let end = window.getSelection().getRangeAt(0).endOffset;
        if (start === end) {
          event.preventDefault();
        }
      } else {
        event.preventDefault();
      }
    }

    if (this.get('type') === 'number') {
      const key = event.which || event.keyCode;
      if (key < 48 || key >= 58) {
        event.preventDefault();
        return false;
      }
    }

    this.sendAction('key-press', this, event);
  },
click(event){
	  this.setDefaults();
		this.dispatch(event);
  },
  focusIn(event) {
    this.sendAction('focus-in', this, event);
  },

  focusOut(event) {
    this.sendAction('focus-out', this, event);
  },

  mouseEnter(event) {
    this.sendAction('mouse-enter', this, event);
  },

  mouseLeave(event) {
    this.sendAction('mouse-leave', this, event);
  },
  setDefaults() {
	  Ember.set(this, 'hasError',false);
  },
  resetAutocomplete(){
	  Ember.set(this, 'showAutocomplete',false);
	  Ember.set(this, 'autoCompleteSearchTerm','');
  },
  dispatch(event){
	  var query = this.catchQuery(event);
	  this.$elem = Ember.$(event.target);
		if (query && query.text) {
			this.autoCompleteSearchTerm = query.text;
			Ember.set(this, 'showAutocomplete',true);
			Ember.run.later(()=>{this.repositionAutocomplete()},10);
			Ember.run.later(this.focusFirstInAutocompleteList, 200);
		}else{
			this.thisQuery = null;
			this.resetAutocomplete();
		}
  },
  repositionAutocomplete() {
		if(this.thisQuery && this.thisQuery.el && this.thisQuery.el.length){
			var rect = this._rect();
			var _window = window , offset, overflowOffset,  $el = this.$elem.find('.post-autocomplete'); //$("#tagUserAutocomplete");
			if($el.is(":visible")){
				element.parent().append($el);
				if (rect.bottom + $el.height() - $(_window).scrollTop() > $(_window).height()) {
					rect.bottom = rect.top - $el.height();
				}
				if (rect.left > (overflowOffset = $(_window).width() - $el.width() - 5)) {
					rect.left = overflowOffset;
				}
				offset = {
					left : rect.left,
					top : rect.bottom
				};
				$el.offset(offset);
			}
			
		}
		
	},
	 focusFirstInAutocompleteList(){
		/*if(scope.wc.filteredUsers && scope.wc.filteredUsers.length){
			scope.getFocussedFileIndex(scope.wc.filteredUsers);
			scope.select(scope.wc.filteredUsers, 0);
			if(scope.wc.filteredUsers.length < 2){
				scope.fetchMoreContacts();
			}
		}else{
			scope.fetchMoreContacts();
		}*/
		//TODO
		
	},
  catchQuery(e) {
		var $inputor = Ember.$(e.target);
		var $inserted, $query, _range, index, inserted, isString, lastNode, matched, offset, query, query_content, range;
		if (!(range = this._getRange())) {
			return;
		}
	
		if (!range.collapsed) {
			return;
		}
		if (e.which === this.keyCodes.ENTER) {
			return;
		}
		if (/firefox/i.test(navigator.userAgent)) {
			if ($(range.startContainer).is($inputor)) {
				this._clearRange();
				return;
			}
			if (e.which === this.keyCodes.BACKSPACE
					&& range.startContainer.nodeType === document.ELEMENT_NODE
					&& (offset = range.startOffset - 1) >= 0) {
				_range = range.cloneRange();
				_range.setStart(range.startContainer, offset);
				if ($(_range.cloneContents()).contents().last().is('.atwho-inserted')) {
					inserted = $(range.startContainer).contents().get(offset);
					this._setRange('after', $(inserted).contents().last());
				}
			} else if (e.which === this.keyCodes.LEFT
					&& range.startContainer.nodeType === document.TEXT_NODE) {
				$inserted = $(range.startContainer.previousSibling);
				if ($inserted.is('.atwho-inserted') && range.startOffset === 0) {
					this._setRange('after', $inserted.contents().last());
				}
			}
		}
		$(range.startContainer).closest('.atwho-inserted').addClass('atwho-query')
				.siblings().removeClass('atwho-query');
	
		if (($query = Ember.$(".atwho-query", this.$elem)).length > 0
				&& $query.is(':empty') && $query.text().length === 0) {
			$query.remove();
		}
		if (!this._movingEvent(e)) {
			$query.removeClass('atwho-inserted').removeAttr("data-email");
		}
		if ($query.length > 0) {
			switch (e.which) {
			case this.keyCodes.LEFT:
				this._setRange('before', $query.get(0), range);
				$query.removeClass('atwho-query');
				return;
			case this.keyCodes.RIGHT:
				this._setRange('after', $query.get(0).nextSibling, range);
				$query.removeClass('atwho-query');
				return;
			}
		}
		if ($query.length > 0 && (query_content = $query.attr('data-atwho-at-query'))) {
			$query.empty().html(query_content).attr('data-atwho-at-query', null);
			this._setRange('after', $query.get(0), range);
		}
		_range = range.cloneRange();
		_range.setStart(range.startContainer, 0);
		matched = this.matcher(_range.toString());
		isString = typeof matched === 'string';
		var _a_offset = 1 ;
		if ($query.length === 0 && isString && (index = range.startOffset - _a_offset - matched.length) >= 0) {
			range.setStart(range.startContainer, index);
			$query = $('<span/>', this.$elem).addClass('atwho-query');
			range.surroundContents($query.get(0));
			lastNode = $query.contents().last().get(0);
			if (/firefox/i.test(navigator.userAgent)) {
				range.setStart(lastNode, lastNode.length);
				range.setEnd(lastNode, lastNode.length);
				_clearRange(range);
			} else {
				this._setRange('after', lastNode, range);
			}
		}
		if (isString && matched.length < 1) {
			return;
		}
		if (isString && (matched.length <= 20 || this.has2At(matched)) ) {
			query = {
				text : matched,
				el : $query
			};
			return this.thisQuery = query;
		} else {
			this.thisQuery = {
				el : $query
			};
			if ($query.text().indexOf("@") >= 0) {
				if (this._movingEvent(e) && $query.hasClass('atwho-inserted')) {
					$query.removeClass('atwho-query');
				} else  {
					$query.removeClass();
				}
			}
			return null;
		}
	
	},
	_getRange() {
		var sel = window.getSelection();
		if (sel.rangeCount > 0) {
			return sel.getRangeAt(0);
		}
	}
	
	, _setRange(position, node, range) {
		if (range == null) {
			range = this._getRange();
		}
		if (!range) {
			return;
		}
		node = $(node)[0];
		if (position === 'after') {
			range.setEndAfter(node);
			range.setStartAfter(node);
		} else {
			range.setEndBefore(node);
			range.setStartBefore(node);
		}
		range.collapse(false);
		return this._clearRange(range);
	}
	
	, _unwrap(node) {
		var next;
		node = $(node).unwrap().get(0);
		if ((next = node.nextSibling) && next.nodeValue) {
			node.nodeValue += next.nodeValue;
			$(next).remove();
		}
		return node;
	}

	, _clearRange(range) {
		var sel;
		if (range == null) {
			range = this._getRange();
		}
		sel = window.getSelection();
		sel.removeAllRanges();
		return sel.addRange(range);
	}
	
	, _movingEvent(e) {
		var ref;
		return e.type === 'click' || ((ref = e.which) === this.keyCodes.RIGHT || ref === this.keyCodes.LEFT
						|| ref === this.keyCodes.UP || ref === this.keyCodes.DOWN);
	}
	
	, matcher(subtext) {
		if(!subtext){
			return false;
		}
		var _a, _y, match, regexp, space;
		var flag =  "@" ,  should_startWithSpace = true , acceptSpaceBar = true ;
		if (should_startWithSpace) {
			flag = '(?:^|\\s)' + flag;
		}
		space = acceptSpaceBar ? "\ " : "";
		regexp = new RegExp(flag + "([A-Za-z" + "-" + "0-9_" + space
				+ "\'\.\+\-]*)$|" + flag + "([^\\x00-\\xff]*)$", 'gi');
		match = regexp.exec(subtext);
		
		if (match) {
			return match[2] || match[1];
		} else {
			if(this.has2At(subtext) ){
				return subtext ;
			}
			return null;
		}
	}, 
	has2At(text){
		var len_at = (text.match(new RegExp("@", "g")) || []).length;
		if(len_at ==2 && text.startsWith("@") ){
			return true;
		}
		return false;
	},
	 backspaceClick(e) {
		if(this.showAutocomplete){
			this.dispatch(e)
			return;
		}	
		var $inputor = this.$elem;
		var $inserted, $query, _range, index, inserted, isString, lastNode, matched, offset, query, query_content, range;
		if (!(range = this._getRange())) {
			return;
		}
	
		if (!range.collapsed) {
			return;
		}

		if (/firefox/i.test(navigator.userAgent)) {
			if ($(range.startContainer).is($inputor)) {
				this._clearRange();
				return;
			}
			if (e.which === keyCode.BACKSPACE
					&& range.startContainer.nodeType === document.ELEMENT_NODE
					&& (offset = range.startOffset - 1) >= 0) {
				_range = range.cloneRange();
				_range.setStart(range.startContainer, offset);
				if ($(_range.cloneContents()).contents().last().is('.atwho-inserted')) {
					inserted = $(range.startContainer).contents().get(offset);
					this._setRange('after', $(inserted).contents().last());
				}
			} 
		}
		if($(range.startContainer.parentNode).hasClass("usertag")){
			var termsArr = range.startContainer.textContent.split(" ");
			if(termsArr.length > 1){
				termsArr.pop();
				$(range.startContainer.parentNode).removeClass().removeAttr("data-email").addClass("emptySpan");
				$(range.startContainer.parentNode).text(termsArr.join(" ")); 
			}else{
				$(range.startContainer).remove();
			}
			this._setRange('after', range.startContainer);
		}
		
		return;
	},
	insert(contact) {
		var data, range, suffix, suffixNode;
		if (!this.$elem.is(':focus')) {
			this.$elem.focus();
		}

		var thisHtml = contact.label ;
		if(contact.email != contact.label){
			thisHtml = thisHtml + " (" + contact.email + ")";
		}
		this.thisQuery.el.removeClass('atwho-query').addClass('atwho-inserted usertag')
				.html(thisHtml).attr('data-email', contact.email);
		if (range = this._getRange()) {
			range.setEndAfter(this.thisQuery.el[0]);
			range.collapse(false);
			range.insertNode(suffixNode = document.createTextNode("\u2008" ));
			this._setRange('after', suffixNode, range);
		}
		if (!this.$elem.is(':focus')) {
			this.$elem.focus();
		}
		this.recipientList.push(contact);
		//scope.validateGroupUser(contact.email);
		this.resetAutocomplete();
	},
	 addNewLine(evt){
		 var sel, range, br, addedBr = false;
		        if (typeof window.getSelection != "undefined") {
		            sel = window.getSelection();
		            if (sel.getRangeAt && sel.rangeCount) {
		                range = sel.getRangeAt(0);
		                range.deleteContents();
		                br = document.createElement("br");
		                range.insertNode(br);
		                range.setEndAfter(br);
		                range.setStartAfter(br);
		                sel.removeAllRanges();
		                sel.addRange(range);
		                addedBr = true;
		            }
		        } else if (typeof document.selection != "undefined") {
		            sel = document.selection;
		            if (sel.createRange) {
		                range = sel.createRange();
		                range.pasteHTML("<br>");
		                range.select();
		                addedBr = true;
		            }
		        }

		        // If successful, prevent the browser's default handling of the keypress
		        if (addedBr) {
		            if (typeof evt.preventDefault != "undefined") {
		                evt.preventDefault();
		            } else {
		                evt.returnValue = false;
		            }
		        }
	},
	_rect() {
		var $el = $(this.thisQuery.el[0]);
		var rect = $el.offset();
		rect.bottom = rect.top + this.thisQuery.el.height();
		return rect;
	},
	actions: {
    	updateResponse(response){
    		
    		 var event = {"id": this.get('schedule').get('eventId'),
    				 	"attendees":[{"responseStatus":response}]};
    		
    		    return new Ember.RSVP.Promise((resolve, reject) =>{
    		   	var url = '/rest/calendar/updateEvent';
    		   	this.doPost(url , event).then(function(data) {
    		   	 this.transitionTo('calendar')
    		     }, function(jqXHR) {
    		        jqXHR.then = null; // tame jQuery's ill mannered promises
    		        Ember.run(null, reject, jqXHR);
    		      });
    		    });
    	},
    	 willTransition(transition) {
	         //   let model = this.controller.get('model');
	            /*
	             * if(model.get('hasDirtyAttributes')){ let confirmation =
	             * confirm("leave without saving ? "); if(confirmation){
	             * model.rollbackAttributes(); }else{ transition.abort(); } }
	             */
	        }
	},
  
});