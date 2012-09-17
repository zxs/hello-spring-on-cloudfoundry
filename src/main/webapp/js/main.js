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
function Tojson() {
	
}

$(function () {
    'use strict';

    // Initialize the jQuery File Upload widget:
    $('#fileupload').fileupload({
    	/*
    	add : function(e, data) {
    		console.log("*** file add ***");
    		console.log(e);
    		console.log(data);
    		
    	},  	
    	done : function(e, data) {
    		console.log("*** file done ***");
    		console.log(e);
    		console.log(data);
    		
    	},  
    	destroy : function(e, data) {
    		console.log("*** file delete ***");
    		console.log(e);
    		console.log(data);
    		
    	}
    	*/
    });


    
    // Load existing files:
    $('#fileupload').each(function () {
        var that = this;
        $.getJSON("fileman/fs/list", function (result) {
        	//console.log(result);
            if (result && result.length) {
                $(that).fileupload('option', 'done')
                    .call(that, null, {result: result});
            }
        });
    });
});

