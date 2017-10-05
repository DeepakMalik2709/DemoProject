<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Create Tutorial</title>
<script type="text/javascript" src="/js/lib/jquery.min.1.11.2.js" >  </script>

<script>
(function ($) {
    $.fn.serializeFormJSON = function () {

        var o = {};
        var a = this.serializeArray();
        $.each(a, function () {
            if (o[this.name]) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };
})(jQuery);
function save(){
	$.ajax({
		url : "/a/secure/tutorial/upsert",
		type: 'post',
		data : JSON.stringify($("#createForm").serializeFormJSON()),
		contentType : "application/json",
		 success : function(result){
			 console.log(result);
		 },
		 error:function(result){
			 console.log("error", result);
		 },
		})
}
</script>
</head>
<body>
<form id="createForm" onsubmit="return false;">
<table>

<tr>
<td>Title</td>
<td><input name="title" type="text" /></td>
</tr>

<tr>
<td>Description</td>
<td><input name="description" type="text"/></td>
</tr>

<tr>
<td>Youtube link</td>
<td><input name="url" type="text"/></td>
</tr>

<tr>
<td></td>
<td></td>
</tr>

<tr>
<td></td>
<td><button onclick="save()" >Save</button></td>
</tr>
</table>

</form>
</body>

</html>