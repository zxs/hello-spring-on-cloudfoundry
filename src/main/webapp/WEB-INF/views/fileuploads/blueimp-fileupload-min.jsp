<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%--    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
 --%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>jQuery File Upload Example (minimal setup)</title>
</head>
<body>
	<input id="fileupload" type="file" name="files[]" data-url="fileman/upload.multi" multiple>
	<script	src="js/jquery/1.7.2/jquery.min.js"></script>
	<script src="js/jquery/fileupload/blueimp/vendor/jquery.ui.widget.js"></script>
	<script src="js/jquery/fileupload/blueimp/jquery.iframe-transport.js"></script>
	<script src="js/jquery/fileupload/blueimp/jquery.fileupload.js"></script>
	<script>
		$(function() {
			$('#fileupload').fileupload({
				dataType : 'json',
				done : function(e, data) {
					$.each(data.result, function(index, file) {
						$('<p/>').text(file.name).appendTo(document.body);
					});
				}
			});
		});
	</script>
</body>
</html>