(function(){function t(t){k.set(t)}function e(t){if(100!=t.get(pn)&&b(Tt(t,Qe))%1e4>=100*Et(t,pn))throw"abort"}function n(t){if(Q(Tt(t,rn)))throw"abort"}function i(){var t=W.location.protocol
if("http:"!=t&&"https:"!=t)throw"abort"}function r(e){try{K.navigator.sendBeacon?t(42):K.XMLHttpRequest&&"withCredentials"in new K.XMLHttpRequest&&t(40)}catch(t){}e.set(Le,x(e),!0),e.set(Kt,Et(e,Kt)+1)
var n=[]
xt.map(function(t,i){i.F&&void 0!=(t=e.get(t))&&t!=i.defaultValue&&("boolean"==typeof t&&(t*=1),n.push(i.F+"="+N(""+t)))}),n.push("z="+kt()),e.set(zt,n.join("&"),!0)}function a(t){var e=Tt(t,kn)||ht()+"/collect",n=Tt(t,Xt)
if(!n&&t.get(Bt)&&(n="beacon"),n){var i=Tt(t,zt),r=t.get(Ft)
r=r||L,"image"==n?pt(e,i,r):"xhr"==n&&vt(e,i,r)||"beacon"==n&&mt(e,i,r)||dt(e,i,r)}else dt(e,Tt(t,zt),t.get(Ft))
e=t.get(rn),e=yt(e),n=e.hitcount,e.hitcount=n?n+1:1,e=t.get(rn),delete yt(e).pending_experiments,t.set(Ft,L,!0)}function o(t){(K.gaData=K.gaData||{}).expId&&t.set(xe,(K.gaData=K.gaData||{}).expId),(K.gaData=K.gaData||{}).expVar&&t.set(Se,(K.gaData=K.gaData||{}).expVar)
var e=t.get(rn)
if(e=yt(e).pending_experiments){var n=[]
for(i in e)e.hasOwnProperty(i)&&e[i]&&n.push(encodeURIComponent(i)+"."+encodeURIComponent(e[i]))
var i=n.join("!")}else i=void 0
i&&t.set(Te,i,!0)}function s(){if(K.navigator&&"preview"==K.navigator.loadPurpose)throw"abort"}function c(t){var e=K.gaDevIds
E(e)&&0!=e.length&&t.set("&did",e.join(","),!0)}function u(t){if(!t.get(rn))throw"abort"}function l(e){var n=Et(e,Ie)
500<=n&&t(15)
var i=Tt(e,qt)
if("transaction"!=i&&"item"!=i){i=Et(e,Re)
var r=(new Date).getTime(),a=Et(e,Ae)
if(0==a&&e.set(Ae,r),a=Math.round(2*(r-a)/1e3),0<a&&(i=Math.min(i+a,20),e.set(Ae,r)),0>=i)throw"abort"
e.set(Re,--i)}e.set(Ie,++n)}function f(e,n,i,r){n[e]=function(){try{return r&&t(r),i.apply(this,arguments)}catch(t){throw wt("exc",e,t&&t.name),t}}}function h(){var t,e
if((e=(e=K.navigator)?e.plugins:null)&&e.length)for(var n=0;n<e.length&&!t;n++){var i=e[n];-1<i.name.indexOf("Shockwave Flash")&&(t=i.description)}if(!t)try{var r=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7")
t=r.GetVariable("$version")}catch(t){}if(!t)try{r=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6"),t="WIN 6,0,21,0",r.AllowScriptAccess="always",t=r.GetVariable("$version")}catch(t){}if(!t)try{r=new ActiveXObject("ShockwaveFlash.ShockwaveFlash"),t=r.GetVariable("$version")}catch(t){}return t&&(r=t.match(/[\d]+/g))&&3<=r.length&&(t=r[0]+"."+r[1]+" r"+r[2]),t||void 0}function g(t,e,n){"none"==e&&(e="")
var i=[],r=tt(t)
t="__utma"==t?6:2
for(var a=0;a<r.length;a++){var o=(""+r[a]).split(".")
o.length>=t&&i.push({hash:o[0],R:r[a],O:o})}if(0!=i.length)return 1==i.length?i[0]:d(e,i)||d(n,i)||d(null,i)||i[0]}function d(t,e){if(null==t)var n=t=1
else n=b(t),t=b(C(t,".")?t.substring(1):"."+t)
for(var i=0;i<e.length;i++)if(e[i].hash==n||e[i].hash==t)return e[i]}function p(t){var e=t.get(Qe)
if(t.get(yn)){var n=t.get(wn)
return t=m(n+e,0),"_ga=2."+N(t+"."+n+"-"+e)}return t=m(e,0),"_ga=1."+N(t+"."+e)}function v(t,e){var n=new Date,i=K.navigator,r=i.plugins||[]
for(t=[t,i.userAgent,n.getTimezoneOffset(),n.getYear(),n.getDate(),n.getHours(),n.getMinutes()+e],e=0;e<r.length;++e)t.push(r[e].description)
return b(t.join("."))}function m(t,e){var n=new Date,i=K.navigator
return b([t,i.userAgent,i.language||"",n.getTimezoneOffset(),n.getYear(),n.getDate(),n.getHours(),n.getMinutes()+e].join("."))}function w(t,e){if(e==W.location.hostname)return!1
for(var n=0;n<t.length;n++)if(t[n]instanceof RegExp){if(t[n].test(e))return!0}else if(0<=e.indexOf(t[n]))return!0
return!1}function y(t){return 0<=t.indexOf(".")||0<=t.indexOf(":")}function b(t){var e,n=1
if(t)for(n=0,e=t.length-1;0<=e;e--){var i=t.charCodeAt(e)
n=(n<<6&268435455)+i+(i<<14),i=266338304&n,n=0!=i?n^i>>21:n}return n}var _=function(t){this.w=t||[]}
_.prototype.set=function(t){this.w[t]=!0},_.prototype.encode=function(){for(var t=[],e=0;e<this.w.length;e++)this.w[e]&&(t[Math.floor(e/6)]^=1<<e%6)
for(e=0;e<t.length;e++)t[e]="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".charAt(t[e]||0)
return t.join("")+"~"}
var k=new _,O=function(t,e){var n=new _(S(t))
n.set(e),t.set(Ne,n.w)},x=function(t){t=S(t),t=new _(t)
for(var e=k.w.slice(),n=0;n<t.w.length;n++)e[n]=e[n]||t.w[n]
return new _(e).encode()},S=function(t){return t=t.get(Ne),E(t)||(t=[]),t},T=function(t){return"function"==typeof t},E=function(t){return"[object Array]"==Object.prototype.toString.call(Object(t))},j=function(t){return void 0!=t&&-1<(t.constructor+"").indexOf("String")},C=function(t,e){return 0==t.indexOf(e)},I=function(t){return t?t.replace(/^[\s\xa0]+|[\s\xa0]+$/g,""):""},A=function(){for(var t=K.navigator.userAgent+(W.cookie?W.cookie:"")+(W.referrer?W.referrer:""),e=t.length,n=K.history.length;0<n;)t+=n--^e++
return[_t()^2147483647&b(t),Math.round((new Date).getTime()/1e3)].join(".")},R=function(t){var e=W.createElement("img")
return e.width=1,e.height=1,e.src=t,e},L=function(){},N=function(e){return encodeURIComponent instanceof Function?encodeURIComponent(e):(t(28),e)},P=function(e,n,i,r){try{e.addEventListener?e.addEventListener(n,i,!!r):e.attachEvent&&e.attachEvent("on"+n,i)}catch(e){t(27)}},D=/^[\w\-:\/.?=&%!]+$/,$=function(t,e,n,i){t&&(n?(i="",e&&D.test(e)&&(i=' id="'+e+'"'),D.test(t)&&W.write("<script"+i+' src="'+t+'"><\/script>')):(n=W.createElement("script"),n.type="text/javascript",n.async=!0,n.src=t,i&&(n.onload=i),e&&(n.id=e),t=W.getElementsByTagName("script")[0],t.parentNode.insertBefore(n,t)))},M=function(){return"https:"==W.location.protocol},G=function(t,e){return(t=t.match("(?:&|#|\\?)"+N(e).replace(/([.*+?^=!:${}()|\[\]\/\\])/g,"\\$1")+"=([^&#]*)"))&&2==t.length?t[1]:""},V=function(){var t=""+W.location.hostname
return 0==t.indexOf("www.")?t.substring(4):t},U=function(t){var e=W.referrer
if(/^https?:\/\//i.test(e)){if(t)return e
t="//"+W.location.hostname
var n=e.indexOf(t)
if(!(5!=n&&6!=n||"/"!=(t=e.charAt(n+t.length))&&"?"!=t&&""!=t&&":"!=t))return
return e}},H=function(t,e){if(1==e.length&&null!=e[0]&&"object"==typeof e[0])return e[0]
for(var n={},i=Math.min(t.length+1,e.length),r=0;r<i;r++){if("object"==typeof e[r]){for(var a in e[r])e[r].hasOwnProperty(a)&&(n[a]=e[r][a])
break}r<t.length&&(n[t[r]]=e[r])}return n},q=function(){this.keys=[],this.values={},this.m={}}
q.prototype.set=function(t,e,n){this.keys.push(t),n?this.m[":"+t]=e:this.values[":"+t]=e},q.prototype.get=function(t){return this.m.hasOwnProperty(":"+t)?this.m[":"+t]:this.values[":"+t]},q.prototype.map=function(t){for(var e=0;e<this.keys.length;e++){var n=this.keys[e],i=this.get(n)
i&&t(n,i)}}
var F,z,B,X,K=window,W=document,Y=function(t,e){return setTimeout(t,e)},J=window,Z=document,Q=function(t){var e=J._gaUserPrefs
if(e&&e.ioo&&e.ioo()||t&&!0===J["ga-disable-"+t])return!0
try{var n=J.external
if(n&&n._gaUserPrefs&&"oo"==n._gaUserPrefs)return!0}catch(t){}t=[],e=Z.cookie.split(";"),n=/^\s*AMP_TOKEN=\s*(.*?)\s*$/
for(var i=0;i<e.length;i++){var r=e[i].match(n)
r&&t.push(r[1])}for(e=0;e<t.length;e++)if("$OPT_OUT"==t[e])return!0
return!1},tt=function(t){var e=[],n=W.cookie.split(";")
t=new RegExp("^\\s*"+t+"=\\s*(.*?)\\s*$")
for(var i=0;i<n.length;i++){var r=n[i].match(t)
r&&e.push(r[1])}return e},et=function(e,n,i,r,a,o){if(!(a=!Q(a)&&!(rt.test(W.location.hostname)||"/"==i&&it.test(r))))return!1
if(n&&1200<n.length&&(n=n.substring(0,1200),t(24)),i=e+"="+n+"; path="+i+"; ",o&&(i+="expires="+new Date((new Date).getTime()+o).toGMTString()+"; "),r&&"none"!=r&&(i+="domain="+r+";"),r=W.cookie,W.cookie=i,!(r=r!=W.cookie))t:{for(e=tt(e),r=0;r<e.length;r++)if(n==e[r]){r=!0
break t}r=!1}return r},nt=function(t){return N(t).replace(/\(/g,"%28").replace(/\)/g,"%29")},it=/^(www\.)?google(\.com?)?(\.[a-z]{2})?$/,rt=/(^|\.)doubleclick\.net$/i,at=[],ot=function(){Li.D([L])},st=function(e,n){var i=tt("AMP_TOKEN")
return 1<i.length?(t(55),!1):"$OPT_OUT"==(i=i[0]||"")||"$ERROR"==i||Q(n)?(t(62),!1):void 0!==X?(t(56),Y(function(){e(X)},0),!0):F?(at.push(e),!0):"$RETRIEVING"==i?(t(57),Y(function(){st(e,n)},1e3),!0):(F=!0,i||(lt("$RETRIEVING",3e4),setTimeout(ut,3e4)),!!ct(i)&&(at.push(e),!0))},ct=function(e){if(!window.JSON)return t(58),!1
var n=K.XMLHttpRequest
if(!n)return t(59),!1
var i=new n
return"withCredentials"in i?(i.open("POST","https://ampcid.google.com/v1/publisher:getClientId?key=AIzaSyA65lEHUEizIsNtlbNo-l2K18dT680nsaM",!0),i.withCredentials=!0,i.setRequestHeader("Content-Type","text/plain"),i.onload=function(){if(F=!1,4==i.readyState){try{200!=i.status&&(t(61),ft("","$ERROR",3e4))
var e=JSON.parse(i.responseText)
e.optOut?(t(63),ft("","$OPT_OUT",31536e6)):e.clientId?ft(e.clientId,e.securityToken,31536e6):(t(64),ft("","$ERROR",864e5))}catch(e){t(65),ft("","$ERROR",864e5)}i=null}},n={originScope:"AMP_ECID_GOOGLE"},e&&(n.securityToken=e),i.send(JSON.stringify(n)),z=Y(function(){t(66),ft("","$ERROR",3e4)},1e3),!0):(t(60),!1)},ut=function(){F=!1},lt=function(t,e){if(void 0===B){B=""
for(var n=Hn(),i=0;i<n.length;i++){var r=n[i]
if(et("AMP_TOKEN",t,"/",r,"",e))return void(B=r)}}et("AMP_TOKEN",t,"/",B,"",e)},ft=function(t,e,n){for(z&&clearTimeout(z),e&&lt(e,n),X=t,e=at,at=[],n=0;n<e.length;n++)e[n](t)},ht=function(){return(Gt||M()?"https:":"http:")+"//www.google-analytics.com"},gt=function(t){this.name="len",this.message=t+"-8192"},dt=function(t,e,n){if(n=n||L,2036>=e.length)pt(t,e,n)
else{if(!(8192>=e.length))throw wt("len",e.length),new gt(e.length)
mt(t,e,n)||vt(t,e,n)||pt(t,e,n)}},pt=function(t,e,n){var i=R(t+"?"+e)
i.onload=i.onerror=function(){i.onload=null,i.onerror=null,n()}},vt=function(t,e,n){var i=K.XMLHttpRequest
if(!i)return!1
var r=new i
return"withCredentials"in r&&(t=t.replace(/^http:/,"https:"),r.open("POST",t,!0),r.withCredentials=!0,r.setRequestHeader("Content-Type","text/plain"),r.onreadystatechange=function(){4==r.readyState&&(n(),r=null)},r.send(e),!0)},mt=function(t,e,n){return!!K.navigator.sendBeacon&&(!!K.navigator.sendBeacon(t,e)&&(n(),!0))},wt=function(t,e,n){1<=100*Math.random()||Q("?")||(t=["t=error","_e="+t,"_v=j58","sr=1"],e&&t.push("_f="+e),n&&t.push("_m="+N(n.substring(0,100))),t.push("aip=1"),t.push("z="+_t()),pt(ht()+"/collect",t.join("&"),L))},yt=function(t){var e=K.gaData=K.gaData||{}
return e[t]=e[t]||{}},bt=function(){this.M=[]}
bt.prototype.add=function(t){this.M.push(t)},bt.prototype.D=function(t){try{for(var e=0;e<this.M.length;e++){var n=t.get(this.M[e])
n&&T(n)&&n.call(K,t)}}catch(t){}(e=t.get(Ft))!=L&&T(e)&&(t.set(Ft,L,!0),setTimeout(e,10))}
var _t=function(){return Math.round(2147483647*Math.random())},kt=function(){try{var t=new Uint32Array(1)
return K.crypto.getRandomValues(t),2147483647&t[0]}catch(t){return _t()}},Ot=function(){this.data=new q},xt=new q,St=[]
Ot.prototype.get=function(t){var e=It(t),n=this.data.get(t)
return e&&void 0==n&&(n=T(e.defaultValue)?e.defaultValue():e.defaultValue),e&&e.Z?e.Z(this,t,n):n}
var Tt=function(t,e){return t=t.get(e),void 0==t?"":""+t},Et=function(t,e){return t=t.get(e),void 0==t||""===t?0:1*t}
Ot.prototype.set=function(t,e,n){if(t)if("object"==typeof t)for(var i in t)t.hasOwnProperty(i)&&jt(this,i,t[i],n)
else jt(this,t,e,n)}
var jt=function(t,e,n,i){if(void 0!=n)switch(e){case rn:di.test(n)}var r=It(e)
r&&r.o?r.o(t,e,n,i):t.data.set(e,n,i)},Ct=function(t,e,n,i,r){this.name=t,this.F=e,this.Z=i,this.o=r,this.defaultValue=n},It=function(t){var e=xt.get(t)
if(!e)for(var n=0;n<St.length;n++){var i=St[n],r=i[0].exec(t)
if(r){e=i[1](r),xt.set(e.name,e)
break}}return e},At=function(t){var e
return xt.map(function(n,i){i.F==t&&(e=i)}),e&&e.name},Rt=function(t,e,n,i,r){return t=new Ct(t,e,n,i,r),xt.set(t.name,t),t.name},Lt=function(t,e){St.push([new RegExp("^"+t+"$"),e])},Nt=function(t,e,n){return Rt(t,e,n,void 0,Pt)},Pt=function(){},Dt=j(window.GoogleAnalyticsObject)&&I(window.GoogleAnalyticsObject)||"ga",$t=/^(?:utma\.)?\d+\.\d+$/,Mt=/^amp-[\w.-]{22,64}$/,Gt=!1,Vt=Nt("apiVersion","v"),Ut=Nt("clientVersion","_v")
Rt("anonymizeIp","aip")
var Ht=Rt("adSenseId","a"),qt=Rt("hitType","t"),Ft=Rt("hitCallback"),zt=Rt("hitPayload")
Rt("nonInteraction","ni"),Rt("currencyCode","cu"),Rt("dataSource","ds")
var Bt=Rt("useBeacon",void 0,!1),Xt=Rt("transport")
Rt("sessionControl","sc",""),Rt("sessionGroup","sg"),Rt("queueTime","qt")
var Kt=Rt("_s","_s")
Rt("screenName","cd")
var Wt=Rt("location","dl",""),Yt=Rt("referrer","dr"),Jt=Rt("page","dp","")
Rt("hostname","dh")
var Zt=Rt("language","ul"),Qt=Rt("encoding","de")
Rt("title","dt",function(){return W.title||void 0}),Lt("contentGroup([0-9]+)",function(t){return new Ct(t[0],"cg"+t[1])})
var te=Rt("screenColors","sd"),ee=Rt("screenResolution","sr"),ne=Rt("viewportSize","vp"),ie=Rt("javaEnabled","je"),re=Rt("flashVersion","fl")
Rt("campaignId","ci"),Rt("campaignName","cn"),Rt("campaignSource","cs"),Rt("campaignMedium","cm"),Rt("campaignKeyword","ck"),Rt("campaignContent","cc")
var ae=Rt("eventCategory","ec"),oe=Rt("eventAction","ea"),se=Rt("eventLabel","el"),ce=Rt("eventValue","ev"),ue=Rt("socialNetwork","sn"),le=Rt("socialAction","sa"),fe=Rt("socialTarget","st"),he=Rt("l1","plt"),ge=Rt("l2","pdt"),de=Rt("l3","dns"),pe=Rt("l4","rrt"),ve=Rt("l5","srt"),me=Rt("l6","tcp"),we=Rt("l7","dit"),ye=Rt("l8","clt"),be=Rt("timingCategory","utc"),_e=Rt("timingVar","utv"),ke=Rt("timingLabel","utl"),Oe=Rt("timingValue","utt")
Rt("appName","an"),Rt("appVersion","av",""),Rt("appId","aid",""),Rt("appInstallerId","aiid",""),Rt("exDescription","exd"),Rt("exFatal","exf")
var xe=Rt("expId","xid"),Se=Rt("expVar","xvar"),Te=Rt("exp","exp"),Ee=Rt("_utma","_utma"),je=Rt("_utmz","_utmz"),Ce=Rt("_utmht","_utmht"),Ie=Rt("_hc",void 0,0),Ae=Rt("_ti",void 0,0),Re=Rt("_to",void 0,20)
Lt("dimension([0-9]+)",function(t){return new Ct(t[0],"cd"+t[1])}),Lt("metric([0-9]+)",function(t){return new Ct(t[0],"cm"+t[1])}),Rt("linkerParam",void 0,void 0,p,Pt)
var Le=Rt("usage","_u"),Ne=Rt("_um")
Rt("forceSSL",void 0,void 0,function(){return Gt},function(e,n,i){t(34),Gt=!!i})
var Pe=Rt("_j1","jid"),De=Rt("_j2","gjid")
Lt("\\&(.*)",function(t){var e=new Ct(t[0],t[1]),n=At(t[0].substring(1))
return n&&(e.Z=function(t){return t.get(n)},e.o=function(t,e,i,r){t.set(n,i,r)},e.F=void 0),e})
var $e=Nt("_oot"),Me=Rt("previewTask"),Ge=Rt("checkProtocolTask"),Ve=Rt("validationTask"),Ue=Rt("checkStorageTask"),He=Rt("historyImportTask"),qe=Rt("samplerTask"),Fe=Rt("_rlt"),ze=Rt("buildHitTask"),Be=Rt("sendHitTask"),Xe=Rt("ceTask"),Ke=Rt("devIdTask"),We=Rt("timingTask"),Ye=Rt("displayFeaturesTask"),Je=Rt("customTask"),Ze=Nt("name"),Qe=Nt("clientId","cid"),tn=Nt("clientIdTime"),en=Nt("storedClientId"),nn=Rt("userId","uid"),rn=Nt("trackingId","tid"),an=Nt("cookieName",void 0,"_ga"),on=Nt("cookieDomain"),sn=Nt("cookiePath",void 0,"/"),cn=Nt("cookieExpires",void 0,63072e3),un=Nt("cookieUpdate",void 0,!0),ln=Nt("legacyCookieDomain"),fn=Nt("legacyHistoryImport",void 0,!0),hn=Nt("storage",void 0,"cookie"),gn=Nt("allowLinker",void 0,!1),dn=Nt("allowAnchor",void 0,!0),pn=Nt("sampleRate","sf",100),vn=Nt("siteSpeedSampleRate",void 0,1),mn=Nt("alwaysSendReferrer",void 0,!1),wn=Nt("_gid","_gid"),yn=Nt("_ge"),bn=Nt("_gcn"),_n=Nt("useAmpClientId"),kn=Rt("transportUrl"),On=Rt("_r","_r"),xn=function(t,e,n){this.V=t,this.fa=e,this.$=!1,this.oa=n,this.ea=1},Sn=function(t,e,n){if(t.fa&&t.$)return 0
if(t.$=!0,e){if(t.oa&&Et(e,t.oa))return Et(e,t.oa)
if(0==e.get(vn))return 0}return 0==t.V?0:(void 0===n&&(n=kt()),0==n%t.V?Math.floor(n/t.V)%t.ea+1:0)},Tn=function(t){var e=Math.min(Et(t,vn),100)
return!(b(Tt(t,Qe))%100>=e)},En=function(t){var e={}
if(jn(e)||Cn(e)){var n=e[he]
void 0==n||1/0==n||isNaN(n)||(0<n?(In(e,de),In(e,me),In(e,ve),In(e,ge),In(e,pe),In(e,we),In(e,ye),Y(function(){t(e)},10)):P(K,"load",function(){En(t)},!1))}},jn=function(t){var e=K.performance||K.webkitPerformance
if(!(e=e&&e.timing))return!1
var n=e.navigationStart
return 0!=n&&(t[he]=e.loadEventStart-n,t[de]=e.domainLookupEnd-e.domainLookupStart,t[me]=e.connectEnd-e.connectStart,t[ve]=e.responseStart-e.requestStart,t[ge]=e.responseEnd-e.responseStart,t[pe]=e.fetchStart-n,t[we]=e.domInteractive-n,t[ye]=e.domContentLoadedEventStart-n,!0)},Cn=function(t){if(K.top!=K)return!1
var e=K.external,n=e&&e.onloadT
return e&&!e.isValidLoadTime&&(n=void 0),2147483648<n&&(n=void 0),0<n&&e.setPageReadyTime(),void 0!=n&&(t[he]=n,!0)},In=function(t,e){var n=t[e];(isNaN(n)||1/0==n||0>n)&&(t[e]=void 0)},An=function(t){return function(e){if("pageview"==e.get(qt)&&!t.I){t.I=!0
var n=Tn(e),i=0<G(e.get(Wt),"gclid").length;(n||i)&&En(function(e){n&&t.send("timing",e),i&&t.send("adtiming",e)})}}},Rn=!1,Ln=function(t){if("cookie"==Tt(t,hn)){if(t.get(un)||Tt(t,en)!=Tt(t,Qe)){var e=1e3*Et(t,cn)
Nn(t,Qe,an,e)}t.get(yn)&&Nn(t,wn,bn,864e5)}},Nn=function(e,n,i,r){var a=$n(e,n)
if(a){i=Tt(e,i)
var o=qn(Tt(e,sn)),s=Un(Tt(e,on)),c=Tt(e,rn)
if("auto"!=s)et(i,a,o,s,c,r)&&(Rn=!0)
else{t(32)
for(var u=Hn(),l=0;l<u.length;l++)if(s=u[l],e.data.set(on,s),a=$n(e,n),et(i,a,o,s,c,r))return void(Rn=!0)
e.data.set(on,"auto")}}else e.get(yn)||t(54)},Pn=function(t){if("cookie"==Tt(t,hn)&&!Rn&&(Ln(t),!Rn))throw"abort"},Dn=function(e){if(e.get(fn)){var n=Tt(e,on),i=Tt(e,ln)||V(),r=g("__utma",i,n)
r&&(t(19),e.set(Ce,(new Date).getTime(),!0),e.set(Ee,r.R),(n=g("__utmz",i,n))&&r.hash==n.hash&&e.set(je,n.R))}},$n=function(t,e){e=nt(Tt(t,e))
var n=Un(Tt(t,on)).split(".").length
return t=Fn(Tt(t,sn)),1<t&&(n+="-"+t),e?["GA1",n,e].join("."):""},Mn=function(t,e){return Gn(e,Tt(t,on),Tt(t,sn))},Gn=function(e,n,i){if(!e||1>e.length)t(12)
else{for(var r=[],a=0;a<e.length;a++){var o=e[a],s=o.split("."),c=s.shift();("GA1"==c||"1"==c)&&1<s.length?(o=s.shift().split("-"),1==o.length&&(o[1]="1"),o[0]*=1,o[1]*=1,s={H:o,s:s.join(".")}):s=Mt.test(o)?{H:[0,0],s:o}:void 0,s&&r.push(s)}if(1==r.length)return t(13),r[0].s
if(0!=r.length)return t(14),r=Vn(r,Un(n).split(".").length,0),1==r.length?r[0].s:(r=Vn(r,Fn(i),1),1<r.length&&t(41),r[0]&&r[0].s)
t(12)}},Vn=function(t,e,n){for(var i,r=[],a=[],o=0;o<t.length;o++){var s=t[o]
s.H[n]==e?r.push(s):void 0==i||s.H[n]<i?(a=[s],i=s.H[n]):s.H[n]==i&&a.push(s)}return 0<r.length?r:a},Un=function(t){return 0==t.indexOf(".")?t.substr(1):t},Hn=function(){var t=[],e=V().split(".")
if(4==e.length){var n=e[e.length-1]
if(parseInt(n,10)==n)return["none"]}for(n=e.length-2;0<=n;n--)t.push(e.slice(n).join("."))
return t.push("none"),t},qn=function(t){return t?(1<t.length&&t.lastIndexOf("/")==t.length-1&&(t=t.substr(0,t.length-1)),0!=t.indexOf("/")&&(t="/"+t),t):"/"},Fn=function(t){return t=qn(t),"/"==t?1:t.split("/").length},zn=new RegExp(/^https?:\/\/([^\/:]+)/),Bn=/(.*)([?&#])(?:_ga=[^&#]*)(?:&?)(.*)/,Xn=function(e){t(48),this.target=e,this.T=!1}
Xn.prototype.ca=function(t,e){if(t.tagName){if("a"==t.tagName.toLowerCase())return void(t.href&&(t.href=Kn(this,t.href,e)))
if("form"==t.tagName.toLowerCase())return Wn(this,t)}if("string"==typeof t)return Kn(this,t,e)}
var Kn=function(t,e,n){var i=Bn.exec(e)
i&&3<=i.length&&(e=i[1]+(i[3]?i[2]+i[3]:"")),t=t.target.get("linkerParam")
var r=e.indexOf("?")
return i=e.indexOf("#"),n?e+=(-1==i?"#":"&")+t:(n=-1==r?"?":"&",e=-1==i?e+(n+t):e.substring(0,i)+n+t+e.substring(i)),e=e.replace(/&+_ga=/,"&_ga=")},Wn=function(t,e){if(e&&e.action)if("get"==e.method.toLowerCase()){t=t.target.get("linkerParam").split("=")[1]
for(var n=e.childNodes||[],i=0;i<n.length;i++)if("_ga"==n[i].name)return void n[i].setAttribute("value",t)
n=W.createElement("input"),n.setAttribute("type","hidden"),n.setAttribute("name","_ga"),n.setAttribute("value",t),e.appendChild(n)}else"post"==e.method.toLowerCase()&&(e.action=Kn(t,e.action))}
Xn.prototype.S=function(e,n,i){function r(i){try{i=i||K.event
t:{var r=i.target||i.srcElement
for(i=100;r&&0<i;){if(r.href&&r.nodeName.match(/^a(?:rea)?$/i)){var o=r
break t}r=r.parentNode,i--}o={}}("http:"==o.protocol||"https:"==o.protocol)&&w(e,o.hostname||"")&&o.href&&(o.href=Kn(a,o.href,n))}catch(e){t(26)}}var a=this
this.T||(this.T=!0,P(W,"mousedown",r,!1),P(W,"keyup",r,!1)),i&&P(W,"submit",function(t){if(t=t||K.event,(t=t.target||t.srcElement)&&t.action){var n=t.action.match(zn)
n&&w(e,n[1])&&Wn(a,t)}})}
var Yn,Jn=/^(GTM|OPT)-[A-Z0-9]+$/,Zn=/;_gaexp=[^;]*/g,Qn=/;((__utma=)|([^;=]+=GAX?\d+\.))[^;]*/g,ti=/^https?:\/\/[\w\-.]+\.google.com(:\d+)?\/optimize\/opt-launch\.html\?.*$/,ei=function(t){function e(t,e){e&&(n+="&"+t+"="+N(e))}var n="https://www.google-analytics.com/gtm/js?id="+N(t.id)
return"dataLayer"!=t.B&&e("l",t.B),e("t",t.target),e("cid",t.clientId),e("cidt",t.ka),e("gac",t.la),e("aip",t.ia),t.sync&&e("m","sync"),e("cycle",t.G),t.qa&&e("gclid",t.qa),ti.test(W.referrer)&&e("cb",String(_t())),n},ni=function(t,e,n){this.U=Pe,this.aa=e,(e=n)||(e=(e=Tt(t,Ze))&&"t0"!=e?si.test(e)?"_gat_"+nt(Tt(t,rn)):"_gat_"+nt(e):"_gat"),this.Y=e},ii=function(t,e){var n=e.get(ze)
e.set(ze,function(e){ri(t,e,t.U),ri(t,e,De)
var i=n(e)
return ai(t,e),i})
var i=e.get(Be)
e.set(Be,function(e){var n=i(e)
return oi(t,e),n})},ri=function(t,e,n){e.get(n)||("1"==tt(t.Y)[0]?e.set(n,"",!0):e.set(n,""+_t(),!0))},ai=function(t,e){e.get(t.U)&&et(t.Y,"1",e.get(sn),e.get(on),e.get(rn),6e4)},oi=function(t,e){if(e.get(t.U)){var n=new q,i=function(t){It(t).F&&n.set(It(t).F,e.get(t))}
i(Vt),i(Ut),i(rn),i(Qe),i(nn),i(t.U),i(De),i(wn),n.set(It(Le).F,x(e))
var r=t.aa
n.map(function(t,e){r+=N(t)+"=",r+=N(""+e)+"&"}),r+="z="+_t(),R(r),e.set(t.U,"",!0)}},si=/^gtm\d+$/,ci=function(t,e){if(t=t.b,!t.get("dcLoaded")){O(t,29),e=e||{}
var n
e[an]&&(n=nt(e[an])),e=new ni(t,"https://stats.g.doubleclick.net/r/collect?t=dc&aip=1&_r=3&",n),ii(e,t),t.set("dcLoaded",!0)}},ui=function(t){if(!t.get("dcLoaded")&&"cookie"==t.get(hn)){O(t,51)
var e=new ni(t)
ri(e,t,e.U),ri(e,t,De),ai(e,t),t.get(e.U)&&(t.set(On,1,!0),t.set(kn,ht()+"/r/collect",!0))}},li=function(){var t=K.gaGlobal=K.gaGlobal||{}
return t.hid=t.hid||_t()},fi=function(t,e,n){if(!Yn){var i=W.location.hash,r=K.name,a=/^#?gaso=([^&]*)/;(r=(i=(i=i&&i.match(a)||r&&r.match(a))?i[1]:tt("GASO")[0]||"")&&i.match(/^(?:!([-0-9a-z.]{1,40})!)?([-.\w]{10,1200})$/i))&&(et("GASO",""+i,n,e,t,0),window._udo||(window._udo=e),window._utcp||(window._utcp=n),t=r[1],$("https://www.google.com/analytics/web/inpage/pub/inpage.js?"+(t?"prefix="+t+"&":"")+_t(),"_gasojs")),Yn=!0}},hi=function(t){return t?(1*t).toFixed(3):"0"},gi=function(e){var n=K.performance
if(n&&n.getEntriesByName){t(35)
var i="https://www.google-analytics.com/analytics-7fbe8cb5fb5575a95b17bcd02bb45932.js?wpid="+e
$(i,void 0,void 0,function(){try{var r=1,a=n.getEntriesByName("https://www.google-analytics.com/analytics-7fbe8cb5fb5575a95b17bcd02bb45932.js")
a&&0!=a.length||(a=n.getEntriesByName("http://www.google-analytics.com/analytics-7fbe8cb5fb5575a95b17bcd02bb45932.js"),r=0)
var o=n.getEntriesByName(i)
if(a&&1==a.length&&o&&1==o.length){t(37)
var s=a[0],c=o[0],u={tid:e,ad:hi(s.duration),bd:hi(c.duration),ar:hi(s.responseEnd-s.requestStart),br:hi(c.responseEnd-c.requestStart),an:hi(s.domainLookupEnd-s.domainLookupStart),bn:hi(c.domainLookupEnd-c.domainLookupStart),ac:hi(s.connectEnd-s.connectStart),bc:hi(c.connectEnd-c.connectStart),as:r}
r=[],r.push("_v=j58"),r.push("id=10")
for(var l in u)u.hasOwnProperty(l)&&r.push(l+"="+N(u[l]))
r.push("z="+_t()),pt("https://www.google-analytics.com/u/d",r.join("&"),L)}}catch(t){}})}},di=/^(UA|YT|MO|GP)-(\d+)-(\d+)$/,pi=function(t){function f(t,e){g.b.data.set(t,e)}function h(t,e){f(t,e),g.filters.add(t)}var g=this
this.b=new Ot,this.filters=new bt,f(Ze,t[Ze]),f(rn,I(t[rn])),f(an,t[an]),f(on,t[on]||V()),f(sn,t[sn]),f(cn,t[cn]),f(un,t[un]),f(ln,t[ln]),f(fn,t[fn]),f(gn,t[gn]),f(dn,t[dn]),f(pn,t[pn]),f(vn,t[vn]),f(mn,t[mn]),f(hn,t[hn]),f(nn,t[nn]),f(tn,t[tn]),f(yn,t[yn]),f(Vt,1),f(Ut,"j58"),h($e,n),h(Je,L),h(Me,s),h(Ge,i),h(Ve,u),h(Ue,Pn),h(He,Dn),h(qe,e)
h(Fe,l),h(Xe,o),h(Ke,c),h(Ye,ui),h(ze,r),h(Be,a),h(We,An(this)),vi(this.b,t[Qe]),mi(this.b),this.b.set(Ht,li()),fi(this.b.get(rn),this.b.get(on),this.b.get(sn)),this.ra=new xn(1e4,!0,"gaexp10")},vi=function(e,n){if(e.data.set(yn,e.get(yn)||1==Sn(new xn(1,!0),void 0,b(e.get(Qe)))),e.get(yn)){var i=Tt(e,an)
e.data.set(bn,"_ga"==i?"_gid":i+"_gid")}if("cookie"==Tt(e,hn)){if(Rn=!1,i=tt(Tt(e,an)),!(i=Mn(e,i))){i=Tt(e,on)
var r=Tt(e,ln)||V()
i=g("__utma",r,i),void 0!=i?(t(10),i=i.O[1]+"."+i.O[2]):i=void 0}if(i&&(Rn=!0),r=i&&!e.get(un))if(r=i.split("."),2!=r.length)r=!1
else if(r=Number(r[1])){var a=Et(e,cn)
r=r+a<(new Date).getTime()/1e3}else r=!1
r&&(i=void 0),i&&(e.data.set(en,i),e.data.set(Qe,i),i=tt(Tt(e,bn)),(i=Mn(e,i))&&e.data.set(wn,i))}if(e.get(un))t:if(i=e.get(dn),r=G(W.location[i?"href":"search"],"_ga"))if(e.get(gn))if(-1==(i=r.indexOf(".")))t(22)
else{a=r.substring(0,i)
var o=r.substring(i+1)
if(i=o.indexOf("."),r=o.substring(0,i),o=o.substring(i+1),"1"==a){if(i=o,r!=v(i,0)&&r!=v(i,-1)&&r!=v(i,-2)&&r!=m(i,0)&&r!=m(i,-1)&&r!=m(i,-2)){t(23)
break t}}else{if("2"!=a){t(22)
break t}if(i=o.indexOf("-"),a=o.substring(0,i),i=o.substring(i+1),r!=v(a+i,0)&&r!=v(a+i,-1)&&r!=v(a+i,-2)&&r!=m(a+i,0)&&r!=m(a+i,-1)&&r!=m(a+i,-2)){t(53)
break t}t(2),e.data.set(wn,a)}t(11),e.data.set(Qe,i)}else t(21)
n&&(t(9),e.data.set(Qe,N(n))),e.get(Qe)||((n=(n=K.gaGlobal&&K.gaGlobal.vid)&&-1!=n.search($t)?n:void 0)?(t(17),e.data.set(Qe,n)):(t(8),e.data.set(Qe,A()))),e.get(yn)&&!e.get(wn)&&(t(3),e.data.set(wn,A())),Ln(e)},mi=function(e){var n=K.navigator,i=K.screen,r=W.location
if(e.set(Yt,U(e.get(mn))),r){var a=r.pathname||""
"/"!=a.charAt(0)&&(t(31),a="/"+a),e.set(Wt,r.protocol+"//"+r.hostname+a+r.search)}i&&e.set(ee,i.width+"x"+i.height),i&&e.set(te,i.colorDepth+"-bit"),i=W.documentElement
var o=(a=W.body)&&a.clientWidth&&a.clientHeight,s=[]
if(i&&i.clientWidth&&i.clientHeight&&("CSS1Compat"===W.compatMode||!o)?s=[i.clientWidth,i.clientHeight]:o&&(s=[a.clientWidth,a.clientHeight]),i=0>=s[0]||0>=s[1]?"":s.join("x"),e.set(ne,i),e.set(re,h()),e.set(Qt,W.characterSet||W.charset),e.set(ie,n&&"function"==typeof n.javaEnabled&&n.javaEnabled()||!1),e.set(Zt,(n&&(n.language||n.browserLanguage)||"").toLowerCase()),r&&e.get(dn)&&(n=W.location.hash)){for(n=n.split(/[?&#]+/),r=[],i=0;i<n.length;++i)(C(n[i],"utm_id")||C(n[i],"utm_campaign")||C(n[i],"utm_source")||C(n[i],"utm_medium")||C(n[i],"utm_term")||C(n[i],"utm_content")||C(n[i],"gclid")||C(n[i],"dclid")||C(n[i],"gclsrc"))&&r.push(n[i])
0<r.length&&(n="#"+r.join("&"),e.set(Wt,e.get(Wt)+n))}}
pi.prototype.get=function(t){return this.b.get(t)},pi.prototype.set=function(t,e){this.b.set(t,e)}
var wi={pageview:[Jt],event:[ae,oe,se,ce],social:[ue,le,fe],timing:[be,_e,Oe,ke]}
pi.prototype.send=function(t){if(!(1>arguments.length)){if("string"==typeof arguments[0])var e=arguments[0],n=[].slice.call(arguments,1)
else e=arguments[0]&&arguments[0][qt],n=arguments
e&&(n=H(wi[e]||[],n),n[qt]=e,this.b.set(n,void 0,!0),this.filters.D(this.b),this.b.data.m={},Sn(this.ra,this.b)&&gi(this.b.get(rn)))}},pi.prototype.ma=function(t,e){var n=this
Ei(t,n,e)||(Ci(t,function(){Ei(t,n,e)}),ji(String(n.get(Ze)),t,void 0,e,!0))}
var yi,bi,_i,ki,Oi=function(t){return"prerender"!=W.visibilityState&&(t(),!0)},xi=function(e){if(!Oi(e)){t(16)
var n=!1,i=function(){if(!n&&Oi(e)){n=!0
var t=i,r=W
r.removeEventListener?r.removeEventListener("visibilitychange",t,!1):r.detachEvent&&r.detachEvent("onvisibilitychange",t)}}
P(W,"visibilitychange",i)}},Si=/^(?:(\w+)\.)?(?:(\w+):)?(\w+)$/,Ti=function(t){if(T(t[0]))this.u=t[0]
else{var e=Si.exec(t[0])
if(null!=e&&4==e.length&&(this.c=e[1]||"t0",this.K=e[2]||"",this.C=e[3],this.a=[].slice.call(t,1),this.K||(this.A="create"==this.C,this.i="require"==this.C,this.g="provide"==this.C,this.ba="remove"==this.C),this.i&&(3<=this.a.length?(this.X=this.a[1],this.W=this.a[2]):this.a[1]&&(j(this.a[1])?this.X=this.a[1]:this.W=this.a[1]))),e=t[1],t=t[2],!this.C)throw"abort"
if(this.i&&(!j(e)||""==e))throw"abort"
if(this.g&&(!j(e)||""==e||!T(t)))throw"abort"
if(y(this.c)||y(this.K))throw"abort"
if(this.g&&"t0"!=this.c)throw"abort"}}
yi=new q,_i=new q,ki=new q,bi={ec:45,ecommerce:46,linkid:47}
var Ei=function(t,e,n){e==Ni||e.get(Ze)
var i=yi.get(t)
return!!T(i)&&(e.plugins_=e.plugins_||new q,!!e.plugins_.get(t)||(e.plugins_.set(t,new i(e,n||{})),!0))},ji=function(e,n,i,r,a){if(!T(yi.get(n))&&!_i.get(n)){if(bi.hasOwnProperty(n)&&t(bi[n]),Jn.test(n)){if(t(52),!(e=Ni.j(e)))return!0
i=r||{},r={id:n,B:i.dataLayer||"dataLayer",ia:!!e.get("anonymizeIp"),sync:a,G:!1},e.get("&gtm")==n&&(r.G=!0)
var o=String(e.get("name"))
"t0"!=o&&(r.target=o),Q(String(e.get("trackingId")))||(r.clientId=String(e.get(Qe)),r.ka=Number(e.get(tn)),i=i.palindrome?Qn:Zn,i=(i=W.cookie.replace(/^|(; +)/g,";").match(i))?i.sort().join("").substring(1):void 0,r.la=i,r.qa=G(e.b.get(Wt)||"","gclid")),e=r.B,i=(new Date).getTime(),K[e]=K[e]||[],i={"gtm.start":i},a||(i.event="gtm.js"),K[e].push(i),i=ei(r)}!i&&bi.hasOwnProperty(n)?(t(39),i=n+".js"):t(43),i&&(i&&0<=i.indexOf("/")||(i=(Gt||M()?"https:":"http:")+"//www.google-analytics.com/plugins/ua/"+i),r=Ri(i),e=r.protocol,i=W.location.protocol,("https:"==e||e==i||("http:"!=e?0:"http:"==i))&&Ai(r)&&($(r.url,void 0,a),_i.set(n,!0)))}},Ci=function(t,e){var n=ki.get(t)||[]
n.push(e),ki.set(t,n)},Ii=function(t,e){yi.set(t,e),e=ki.get(t)||[]
for(var n=0;n<e.length;n++)e[n]()
ki.set(t,[])},Ai=function(t){var e=Ri(W.location.href)
return!!C(t.url,"https://www.google-analytics.com/gtm/js?id=")||!(t.query||0<=t.url.indexOf("?")||0<=t.path.indexOf("://"))&&(t.host==e.host&&t.port==e.port||(e="http:"==t.protocol?80:443,!("www.google-analytics.com"!=t.host||(t.port||e)!=e||!C(t.path,"/plugins/"))))},Ri=function(t){function e(t){var e=(t.hostname||"").split(":")[0].toLowerCase(),n=(t.protocol||"").toLowerCase()
return n=1*t.port||("http:"==n?80:"https:"==n?443:""),t=t.pathname||"",C(t,"/")||(t="/"+t),[e,""+n,t]}var n=W.createElement("a")
n.href=W.location.href
var i=(n.protocol||"").toLowerCase(),r=e(n),a=n.search||"",o=i+"//"+r[0]+(r[1]?":"+r[1]:"")
return C(t,"//")?t=i+t:C(t,"/")?t=o+t:!t||C(t,"?")?t=o+r[2]+(t||a):0>t.split("/")[0].indexOf(":")&&(t=o+r[2].substring(0,r[2].lastIndexOf("/"))+"/"+t),n.href=t,i=e(n),{protocol:(n.protocol||"").toLowerCase(),host:i[0],port:i[1],path:i[2],query:n.search||"",url:t||""}},Li={ga:function(){Li.f=[]}}
Li.ga(),Li.D=function(t){var e=Li.J.apply(Li,arguments)
for(e=Li.f.concat(e),Li.f=[];0<e.length&&!Li.v(e[0])&&(e.shift(),!(0<Li.f.length)););Li.f=Li.f.concat(e)},Li.J=function(t){for(var e,n=[],i=0;i<arguments.length;i++)try{e=new Ti(arguments[i]),e.g?Ii(e.a[0],e.a[1]):(e.i&&(e.ha=ji(e.c,e.a[0],e.X,e.W)),n.push(e))}catch(t){}return n},Li.v=function(t){try{if(t.u)t.u.call(K,Ni.j("t0"))
else{var e=t.c==Dt?Ni:Ni.j(t.c)
if(t.A){if("t0"==t.c&&null===(e=Ni.create.apply(Ni,t.a)))return!0}else if(t.ba)Ni.remove(t.c)
else if(e)if(t.i){if(t.ha&&(t.ha=ji(t.c,t.a[0],t.X,t.W)),!Ei(t.a[0],e,t.W))return!0}else if(t.K){var n=t.C,i=t.a,r=e.plugins_.get(t.K)
r[n].apply(r,i)}else e[t.C].apply(e,t.a)}}catch(t){}}
var Ni=function(e){t(1),Li.D.apply(Li,[arguments])}
Ni.h={},Ni.P=[],Ni.L=0,Ni.answer=42
var Pi=[rn,on,Ze]
Ni.create=function(e){var n=H(Pi,[].slice.call(arguments))
n[Ze]||(n[Ze]="t0")
var i=""+n[Ze]
if(Ni.h[i])return Ni.h[i]
t:{if(n[_n])if(t(67),X)n[Qe]||(n[Qe]=X)
else{var r=String(n[on]||V()),a=String(n[sn]||"/"),o=tt(String(n[an]||"_ga"))
if((!(r=Gn(o,r,a))||$t.test(r)||Mt.test(r))&&st(ot,String(n[rn]))){r=!0
break t}}r=!1}return r?null:(n=new pi(n),Ni.h[i]=n,Ni.P.push(n),n)},Ni.remove=function(t){for(var e=0;e<Ni.P.length;e++)if(Ni.P[e].get(Ze)==t){Ni.P.splice(e,1),Ni.h[t]=null
break}},Ni.j=function(t){return Ni.h[t]},Ni.getAll=function(){return Ni.P.slice(0)},Ni.N=function(){"ga"!=Dt&&t(49)
var e=K[Dt]
if(!e||42!=e.answer){Ni.L=e&&e.l,Ni.loaded=!0
var n=K[Dt]=Ni
if(f("create",n,n.create),f("remove",n,n.remove),f("getByName",n,n.j,5),f("getAll",n,n.getAll,6),n=pi.prototype,f("get",n,n.get,7),f("set",n,n.set,4),f("send",n,n.send),f("requireSync",n,n.ma),n=Ot.prototype,f("get",n,n.get),f("set",n,n.set),!M()&&!Gt){t:{n=W.getElementsByTagName("script")
for(var i=0;i<n.length&&100>i;i++){var r=n[i].src
if(r&&0==r.indexOf("https://www.google-analytics.com/analytics")){t(33),n=!0
break t}}n=!1}n&&(Gt=!0)}M()||Gt||!Sn(new xn(1e4))||(t(36),Gt=!0),(K.gaplugins=K.gaplugins||{}).Linker=Xn,n=Xn.prototype,Ii("linker",Xn),f("decorate",n,n.ca,20),f("autoLink",n,n.S,25),Ii("displayfeatures",ci),Ii("adfeatures",ci),e=e&&e.q,E(e)?Li.D.apply(Ni,e):t(50)}},Ni.da=function(){for(var t=Ni.getAll(),e=0;e<t.length;e++)t[e].get(Ze)}
var Di=Ni.N,$i=K[Dt]
$i&&$i.r?Di():xi(Di),xi(function(){Li.D(["provide","render",L])})})(window)
