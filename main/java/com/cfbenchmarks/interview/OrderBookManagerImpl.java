package com.cfbenchmarks.interview;

import static com.cfbenchmarks.interview.Validation.validateArg;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
 * TODO use ReadWrite locks
 */
public class OrderBookManagerImpl implements OrderBookManager {

  private static OrderBook EMPTY_BUY_BOOK = new OrderBook(Side.BUY, Collections.emptyMap());
  private static OrderBook EMPTY_SELL_BOOK = new OrderBook(Side.SELL, Collections.emptyMap());

  private Map<String, Order> m_ordersById = Collections.synchronizedMap(new HashMap<String, Order>());
  private Map<String, OrderBook> m_ordersByInstrumentSide = Collections
      .synchronizedMap(new HashMap<String, OrderBook>());

  public void addOrder(Order order) {
    validateArg(order, "order cannot be null");
    // order objects are immutable - so can be safely added
    getOrCreateOrderBook(order).add(order);
  }

  public boolean modifyOrder(String orderId, long newQuantity) {
    validateArg(orderId, "orderId cannot be null");
    checkArgument(newQuantity > 0, "Quantity must be positive");

    Order order = m_ordersById.get(orderId);

    if (order == null) {
      throw new IllegalArgumentException(orderId + " does not exist");
    }
    if (order.getQuantity() == newQuantity) {
      return false;
    }
    return getOrderBook(order.getInstrument(), order.getSide()).modify(orderId, newQuantity);
  }

  public boolean deleteOrder(String orderId) {
    validateArg(orderId, "orderid cannot be null");

    Order order = m_ordersById.get(orderId);
    if (order == null) {
      throw new IllegalArgumentException(orderId + " does not exist");
    }
    Order deleted = getOrderBook(order.getInstrument(), order.getSide()).delete(orderId);
    return deleted != null;
  }

  public Optional<Long> getBestPrice(String instrument, Side side) {
    return getOrderBook(instrument, side).getBestPrice();
  }

  public long getOrderNumAtLevel(String instrument, Side side, long priceLevel) {
    return getOrderBook(instrument, side).getOrderNumAtLevel(priceLevel);
  }

  public long getTotalQuantityAtLevel(String instrument, Side side, long priceLevel) {
    return getOrderBook(instrument, side).getTotalQuantityAtLevel(priceLevel);
  }

  public long getTotalVolumeAtLevel(String instrument, Side side, long priceLevel) {
    return getOrderBook(instrument, side).getTotalVolumeAtpriceLevel(priceLevel);
  }

  public List<Order> getOrdersAtLevel(String instrument, Side side, long priceLevel) {
    return getOrderBook(instrument, side).getOrdersAtLevel(priceLevel);
  }

  /*
   * private methods
   */

  private OrderBook getOrCreateOrderBook(Order order) {
    return getOrderBook(order.getInstrument(), order.getSide(), true);
  }

  private OrderBook getOrderBook(String instrument, Side side) {
    return getOrderBook(instrument, side, false);
  }

  /**
   * find an order book using the key of instrument :: side
   * 
   * @param instrument
   * @param side
   * @param create     if true we will create an empty book
   * @return OrderBook
   */
  private OrderBook getOrderBook(String instrument, Side side, boolean create) {
    validateArg(side, "side cannot be null");
    validateArg(instrument, "instrument cannot be null");

    String orderBookKey = Order.sideInstrumentKey(instrument, side);

    if (m_ordersByInstrumentSide.containsKey(orderBookKey)) {
      return m_ordersByInstrumentSide.get(orderBookKey);
    }

    if (create) {
      synchronized (m_ordersByInstrumentSide) {
        if (m_ordersByInstrumentSide.containsKey(orderBookKey)) {
          return m_ordersByInstrumentSide.get(orderBookKey);
        }
        // don't want to fill up with empty books
        OrderBook book = new OrderBook(side, m_ordersById);
        m_ordersByInstrumentSide.put(orderBookKey, book);
        return book;
      }
    }
    return side == Side.BUY ? EMPTY_BUY_BOOK : EMPTY_SELL_BOOK;
  }
}
