(function(){
var A=Array.prototype,S=String.prototype,O=Object;
if(!A.includes)A.includes=function(x,i){return this.indexOf(x,i)>-1};
if(!A.find)A.find=function(f,t){for(var i=0;i<this.length;i++)if(f.call(t,this[i],i,this))return this[i]};
if(!A.findIndex)A.findIndex=function(f,t){for(var i=0;i<this.length;i++)if(f.call(t,this[i],i,this))return i;return -1};
if(!O.assign)O.assign=function(t){for(var i=1;i<arguments.length;i++){var s=arguments[i];if(s)for(var k in s)if(O.prototype.hasOwnProperty.call(s,k))t[k]=s[k]}return t};
if(!O.entries)O.entries=function(o){return Object.keys(o).map(function(k){return[k,o[k]]})};
if(!S.padStart)S.padStart=function(n,p){var s=String(this);p=p===undefined?' ':String(p);while(s.length<n)s=p+s;return s};
if(!S.includes)S.includes=function(x,i){return this.indexOf(x,i)>-1};
if(window.Element&&!Element.prototype.closest)Element.prototype.closest=function(s){var e=this;while(e&&e.nodeType===1){if((e.matches||e.msMatchesSelector||e.webkitMatchesSelector).call(e,s))return e;e=e.parentElement}return null};
if(window.NodeList&&!NodeList.prototype.forEach)NodeList.prototype.forEach=A.forEach;
window.addEventListener('error',function(e){var b=document.getElementById('oldbrowser');if(b&&!window.__appOk){b.style.display='block'}});
})();

(function(){try{var d=document.createElement('div');d.style.cssText='display:flex;flex-direction:column;row-gap:1px;position:absolute;visibility:hidden';d.innerHTML='<i></i><i></i>';d.firstChild.style.height='1px';d.lastChild.style.height='1px';(document.body||document.documentElement).appendChild(d);var ok=d.scrollHeight===3;d.parentNode.removeChild(d);if(!ok)document.documentElement.className+=' nogap';}catch(e){}})();
