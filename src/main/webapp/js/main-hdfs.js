/*
 * jQuery File Upload Plugin JS Example 6.7
 * https://github.com/blueimp/jQuery-File-Upload
 *
 * Copyright 2010, Sebastian Tschan
 * https://blueimp.net
 *
 * Licensed under the MIT license:
 * http://www.opensource.org/licenses/MIT
 */

/*jslint nomen: true, unparam: true, regexp: true */
/*global $, window, document */

var FILEMAN_HOME_URL = "fileman/hdfs/homedir";
var FILEMAN_LIST_URL = "fileman/hdfs/list";
var FILEMAN_DELETE_URL = "fileman/hdfs/delete?file=";
var FILEMAN_DOWNLOAD_URL = "fileman/hdfs/download?file=";

var HOME_DIR = "";

function fileStatuses2Metas(data) {
	var fileStatuses = $.parseJSON(data).FileStatuses;
	var len = fileStatuses["FileStatus"].length;
	var fileMetas = new Array();
	for(var i=0; i < len; i++) {
		var type = fileStatuses["FileStatus"][i]["type"]; // FILE, DIRECTORY;
		if(type=="FILE") {
			var name = fileStatuses["FileStatus"][i]["pathSuffix"] ;
			var fileMeta = {
				"name" 		: name,
				"size" 		: fileStatuses["FileStatus"][i]["length"],
				"url" 		: FILEMAN_DOWNLOAD_URL + encodeURIComponent(HOME_DIR + "/" + name),
				"delete_url": FILEMAN_DELETE_URL + encodeURIComponent(HOME_DIR + "/" + name)
			};
			fileMetas.push(fileMeta);
		
		}
	}
	//console.log(fileMetas);
	return fileMetas;
}

$(function () {
    'use strict';
    
$.getJSON(FILEMAN_HOME_URL,

function(ret) {
//console.log(ret);
if(ret.code=="200") {
	HOME_DIR = $.parseJSON(ret['data'])['Path'].substr(1);

	$('#working-directory').val( HOME_DIR );
	
    // Initialize the jQuery File Upload widget:
    $('#fileupload').fileupload({
    	//...
    });
    
    $('#fileupload').bind('fileuploadsubmit', function (e, data) {
        // The example input, doesn't have to be part of the upload form:
        var wd = $('#working-directory');
        data.formData = {"working-directory": wd.val()};
        if (!data.formData["working-directory"]) {
          wd.focus();
          return false;
        }
    });
    
    // Load existing files:
    $('#fileupload').each(function () {
        var that = this;
        $.getJSON(FILEMAN_LIST_URL, {"homedir": HOME_DIR }, function (ret) {
        	//console.log(ret);
        	var result = fileStatuses2Metas( ret['data'] );
            if (result && result.length) {
                $(that).fileupload('option', 'done')
                    .call(that, null, {result: result});
            }
        });
    });
}
}); // end fileman/homedir
});

