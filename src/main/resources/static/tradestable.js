$(document).ready( function () {
	 var table = $('#tradesTable').DataTable({
			"sAjaxSource": "/getAllTrades?username=test",
			"sAjaxDataProp": "",
			"order": [[ 0, "asc" ]],
			"aoColumns": [
				  { "mData": "tradeId"},
			      { "mData": "itemId"},
		          { "mData": "quantity" },
				  { "mData": "price" },
				  { "mData": "buyerId" },
				  { "mData": "sellerId" }


			]
	 })
});