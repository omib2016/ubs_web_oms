$(document).ready( function () {
	 var table = $('#employeesTable').DataTable({
			"sAjaxSource": "/getAllOrders?username=test",
			"sAjaxDataProp": "",
			"order": [[ 0, "asc" ]],
			"aoColumns": [
			      { "mData": "orderId"},
		          { "mData": "itemId" },
				  { "mData": "side" },
				  { "mData": "quantity" },
				  { "mData": "price" }
			]
	 })
});