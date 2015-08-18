$(document).ready(
		function() {
			var welcome = function(msj) {
				$("#welcomeMsg").text(msj);
			};

			var isSelectDraw = false;

			var drawSelect = function(stock) {
				$("#frutasSelect").empty();

				var $select = $("<select>").attr("name", "frutasSelect");

				stock.frutas.forEach(function(entry) {
					var $op = $("<option>").attr("value", entry.fruta).text(
							entry.fruta);

					$select.append($op);

					$("#frutasSelect").append($select);
				});
				isSelectDraw = true;
			};

			var drawStock = function(stock) {
				$("#stock tbody").empty();

				var i = 1;
				stock.frutas.forEach(function(entry) {

					var $tr = $("<tr>");

					var $td = $("<td>").text(entry.fruta);
					$tr.append($td);

					$td = $("<td>").text(entry.cantidad);
					$tr.append($td);

					if (i % 2 == 0)
						$tr.addClass('alt');

					$("#stock tbody").append($tr);
					i++;
				});

			};
			
			var socket = null;
			
			var initWs = function() {
				
				socket = new WebSocket(
						"ws://localhost:8081/booking-java-websockets/socket/BookingGroceryWebSocketServlet");

				socket.onopen = function() {
					console.log('conexion establecida')
				}

				socket.onerror = function() {
					console.log('error')
				}

				socket.onclose = function() {
					console.log('close');
					reviewSocketConnection();
				}

				socket.onmessage = function(message) {

					var data = JSON.parse(message.data);
					var on = data.on;

					console.debug(on)

					if (on === "welcome")
						onWelcomeMessage(data);

					else if (on === "repaintStock")
						onRepaintStockMessage(data);
					
					else if (on === "repaintStock_updateFruitsSelect"){
						onRepaintStockMessage(data);
						onUpdateFruitsSelect(data);
					}
					
					

				};

				var onWelcomeMessage = function(data) {
					welcome(data.msj);

					drawStock(data.stock);

					drawSelect(data.stock);
				}

				var onRepaintStockMessage = function(data) {
					drawStock(data.stock);
				}
				
				var onUpdateFruitsSelect = function(data) {
					drawSelect(data.stock);
				}
			};
			
			var reviewSocketConnection = function(){
				 if(!socket || socket.readyState == 3)
					 initWs();
			}
			
			initWs();
			
			setInterval(reviewSocketConnection, 2000);

			/*var socket = new WebSocket(
					"ws://localhost:8081/websockets/socket/WsAppServlet");*/

			$("#buyBtn").on('click', function(event) {
				event.preventDefault();
				sendEvent('buy');
			});

			$("#sellBtn").on('click', function(event) {
				event.preventDefault();
				sendEvent('sell');
			});

			var sendEvent = function(event) {
				var value = $("#cantidadTxt").val().trim();

				if (value === "" || isNaN(value)) {
					alertify.alert("Please enter a integer number.");
					$("#cantidadTxt").val("")
				} else {
					var json = {
						event : event,
						fruta : $("#frutasSelect option:selected").text(),
						cantidad : value
					};
					if(socket!=null)
						socket.send(JSON.stringify(json));
				}
			};
			
			$("#addFruit").on("click", function(event){
				event.preventDefault();
				$("#addFruitNameTxt").val("");
				$("#addQuantityTxt").val("");
				$("#hiddenDiv").slideDown('slow');
			});
			
			$("#cancelAddBtn").on("click", function(event){
				event.preventDefault();
				$("#addFruitNameTxt").val("");
				$("#addQuantityTxt").val("");
				$("#hiddenDiv").slideUp('slow');
			});
			
			$("#addFruitBtn").on("click", function(event){
				event.preventDefault();
				
				var value = $("#addQuantityTxt").val().trim();

				if (value === "" || isNaN(value)) {
					alertify.alert("Please enter a integer number.");
					$("#addQuantityTxt").val("")
				} else {
					var json = {
						event : "add_fruit",
						fruta : $("#addFruitNameTxt").val(),
						cantidad : value
					};
					if(socket!=null)
						socket.send(JSON.stringify(json));
					
					$("#cancelAddBtn").click();
				}
				
			});
			
			
			
			/*
			socket.onopen = function() {
				console.log('conexion establecida')
			}

			socket.onerror = function() {
				console.log('error')
			}

			socket.onclose = function() {
				console.log('close')
			}

			socket.onmessage = function(message) {

				var data = JSON.parse(message.data);
				var on = data.on;

				console.debug(on)

				if (on === "welcome")
					onWelcomeMessage(data);

				else if (on === "repaintStock")
					onRepaintStockMessage(data);

			};

			var onWelcomeMessage = function(data) {
				welcome(data.msj);

				drawStock(data.stock);

				drawSelect(data.stock);
			}

			var onRepaintStockMessage = function(data) {
				drawStock(data.stock)
			}*/

		});