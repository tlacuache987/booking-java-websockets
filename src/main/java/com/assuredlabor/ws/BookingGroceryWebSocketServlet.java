package com.assuredlabor.ws;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

import com.assuredlabor.model.IncomingEvent;
import com.assuredlabor.model.Mensaje;
import com.assuredlabor.model.Stock;
import com.assuredlabor.model.StockItem;
import com.assuredlabor.model.enums.Event;
import com.google.gson.Gson;

@Slf4j
public class BookingGroceryWebSocketServlet extends WebSocketServlet {

	private static final long serialVersionUID = -5630167933431806651L;

	private static List<OrderMessageInbound> orderMessageInboundList = new ArrayList<OrderMessageInbound>();

	private static Gson gson = new Gson();
	private static List<StockItem> fruitList;

	static {
		fruitList = new ArrayList<StockItem>();
		fruitList.add(new StockItem("apple", 110));
		fruitList.add(new StockItem("pineapple", 210));
		fruitList.add(new StockItem("lemon", 310));
		fruitList.add(new StockItem("banana", 410));
		fruitList.add(new StockItem("melon", 510));
		fruitList.add(new StockItem("watermelon", 610));
		fruitList.add(new StockItem("pear", 710));
	}

	private class OrderMessageInbound extends MessageInbound {

		private WsOutbound outbound;

		@Override
		public void onOpen(WsOutbound outbound) {
			try {
				log.info("cliente conectado.");

				this.outbound = outbound;

				orderMessageInboundList.add(this);

				Stock stock = new Stock(fruitList);
				Mensaje msj = new Mensaje("welcome", "Welcome!", stock);

				String json = gson.toJson(msj);

				outbound.writeTextMessage(CharBuffer.wrap(json));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onClose(int status) {
			log.info("cliente desconectado.");

			orderMessageInboundList.remove(this);
		}

		@Override
		public void onTextMessage(CharBuffer charBuffer) throws IOException {
			String incomingMessage = charBuffer.toString();

			IncomingEvent incomingEvent = gson.fromJson(incomingMessage, IncomingEvent.class);

			log.info("incomingEvent : " + incomingEvent);

			String json = null;

			switch (incomingEvent.getEventEnum()) {
			case BUY:
			case SELL:
				recalculateStockToFruitList(incomingEvent.getEventEnum(), incomingEvent.getFruta(), incomingEvent.getCantidad());
				json = generateStockJsonMessage("repaintStock");
				break;
			case ADD_FRUIT:
				addNewFruitToStock(incomingEvent.getFruta(), incomingEvent.getCantidad());
				json = generateStockJsonMessage("repaintStock_updateFruitsSelect");
				break;
			}

			broadCast(json);

		}

		private void addNewFruitToStock(String fruta, Integer cantidad) {
			fruitList.add(new StockItem(fruta, cantidad));
		}

		private String generateStockJsonMessage(String clientSideEvent) {
			Stock stock = new Stock(fruitList);
			Mensaje msj = new Mensaje(clientSideEvent, null, stock);

			String json = gson.toJson(msj);
			return json;
		}

		private void recalculateStockToFruitList(Event event, String fruta, Integer cantidad) {
			for (StockItem stockItem : fruitList) {
				if (stockItem.getFruta().trim().equals(fruta.trim())) {

					if (event.equals(Event.BUY))
						stockItem.setCantidad(stockItem.getCantidad() + cantidad);
					else
						stockItem.setCantidad(stockItem.getCantidad() - cantidad);
					break;
				}
			}
		}

		private void broadCast(String json) throws IOException {
			for (OrderMessageInbound mmib : orderMessageInboundList) {

				CharBuffer buffer = CharBuffer.wrap(json);

				mmib.outbound.writeTextMessage(buffer);
				mmib.outbound.flush();

			}
		}

		@Override
		public void onBinaryMessage(ByteBuffer bb) throws IOException {
		}
	}

	@Override
	protected StreamInbound createWebSocketInbound(String arg0, HttpServletRequest arg1) {
		return new OrderMessageInbound();
	}
}