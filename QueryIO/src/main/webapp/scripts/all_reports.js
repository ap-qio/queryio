Reports = {

	showReportParameters: function () {
		
	},
	ready: function () {
		$('#reports_table').dataTable( {
	        "bPaginate": false,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": true,
			"bInfo": false,
			"bDestroy": true,
			"bAutoWidth": true,
			"aaSorting": [[ 1, "desc" ]],
			"aaData": [
	           [ '<input type="checkbox" value="report1">', 'NameNode Status', 'NameNode status report'],
	           [ '<input type="checkbox" value="report1">', 'DataNode Status', 'DataNode status report'],
	           [ '<input type="checkbox" value="report1">', 'I/O report', 'IO report'],
	           [ '<input type="checkbox" value="report1">', 'CPU report', 'CPU report'],
	        ],
	        "aoColumnDefs": [{ "bSortable": false, "aTargets": [ 0 ] } ],
	        "aoColumns": [
	            { "sTitle": ""},
	            { "sTitle": "Report ID" },
	            { "sTitle": "Description" },
	        ]
	    } ); 

   	}
};
	