package com.cfbenchmarks.interview;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class OrderBook {

  /** which side are we, sell or buy **/
  private Side side;
  /** group our orders by level( order price ) **/
  private Map<Long, Orders> ordersByLevel = Collections.synchronizedMap(new HashMap<Long, Orders>());
  /** a reference to the master map by id also used as a MUTEX **/
  private Map<String, Order> ordersById;
  private long minPrice = Long.MAX_VALUE;
  private long maxPrice = 0;

  OrderBook(Side side, Map<String, Order> ordersById) {
    requireNonNull(side);
    requireNonNull(ordersById);
    this.ordersById = ordersById;
    this.side = side;
  }

  void add(Order order) {
    requireNonNull(order);
    checkArgument(order.getSide() == side, "incorrect side " + order.getSide());

    synchronized (ordersById) {
      checkArgument(!ordersById.containsKey(order.getOrderId()), order.getOrderId() + " already exists");

      Orders orders = ordersByLevel.get(order.getPrice());

      if (orders == null) {
        ordersByLevel.put(order.getPrice(), new Orders(order));
      } else {
        orders.add(order);
      }
      ordersById.put(order.getOrderId(), order);

      minPrice = Math.min(minPrice, order.getPrice());
      maxPrice = Math.max(maxPrice, order.getPrice());
    }
  }

  Optional<Long> getBestPrice() {
    if (ordersByLevel.isEmpty()) {
      return Optional.empty();
    }
    if (side == Side.SELL) {
      return Optional.of(minPrice);
    } else {
      return Optional.of(maxPrice);
    }
  }

  long getOrderNumAtLevel(long priceLevel) {
    checkArgument(priceLevel > 0, "priceLevel must be positive");
    Orders orders = ordersByLevel.get(priceLevel);
    return orders == null ? 0 : orders.orders.size();
  }

  long getTotalQuantityAtLevel(long priceLevel) {
    checkArgument(priceLevel > 0, "priceLevel must be positive");
    Orders orders = ordersByLevel.get(priceLevel);
    return orders == null ? 0 : orders.stats.totalQuantity;
  }

  long getTotalVolumeAtpriceLevel(long priceLevel) {
    checkArgument(priceLevel > 0, "priceLevel must be positive");
    Orders orders = ordersByLevel.get(priceLevel);
    return orders == null ? 0 : orders.stats.totalVolume;
  }

  List<Order> getOrdersAtLevel(long priceLevel) {
    checkArgument(priceLevel > 0, "priceLevel must be positive");
    Orders orders = ordersByLevel.get(priceLevel);
    if (orders == null) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(ordersByLevel.get(priceLevel).orders);
  }

  /*
   * only call from synchronized blocks
   */
  private void removeAndResetMinMax(Order toDelete) {
    // take it out
    Orders orders = ordersByLevel.get(toDelete.getPrice());
    orders.remove(toDelete);
    if (orders.orders.size() == 0) {
      // clean up empty books
      ordersByLevel.remove(toDelete.getPrice());
    }

    if (side == Side.SELL) {
      minPrice = Long.MAX_VALUE;
    } else {
      maxPrice = 0;
    }
    ordersByLevel.keySet().forEach(key -> {
      if (side == Side.SELL) {
        minPrice = Math.min(minPrice, key);
      } else {
        maxPrice = Math.max(maxPrice, key);
      }
    });
  }

  Order delete(String orderId) {
    Validation.validateArg(orderId, "orderId cannot be null");

    synchronized (ordersById) {
      Order toDelete = ordersById.remove(orderId);

      if (toDelete == null) {
        throw new IllegalArgumentException(orderId + " does not exist");
      }

      removeAndResetMinMax(toDelete);
      return toDelete;
    }
  }

  boolean modify(String orderId, long newQuantity) {
    synchronized (ordersById) {
      Order order = ordersById.get(orderId);
      Order newOrder = new Order.Builder().clone(order).setQuantity(newQuantity).get();

      ordersById.put(newOrder.getOrderId(), newOrder);

      if (newQuantity > order.getQuantity()) {
        return modifyUp(order, newOrder);
      } else {
        return modifyDown(order, newOrder);
      }
    }
  }

  /*
   * in this version, we need to move to the end of the queue O(n) could be
   * optimized?
   */
  private boolean modifyUp(Order order, Order newOrder) {
    Orders orders = ordersByLevel.get(order.getPrice());
    orders.remove(order);
    orders.add(newOrder);
    return true;
  }

  /*
   * in this version we replace the order with the new order
   */
  private boolean modifyDown(Order order, Order newOrder) {
    Orders orders = ordersByLevel.get(order.getPrice());
    int index = orders.orders.indexOf(order);

    if (index != -1) {
      orders.orders.set(index, newOrder);
      orders.stats.remove(order);
      orders.stats.add(newOrder);
      return true;
    }
    return false;
  }

  class Orders {
    OrdersStats stats = new OrdersStats();
    LinkedList<Order> orders = new LinkedList<Order>();

    Orders(Order order) {
      add(order);
    }

    public void add(Order order) {
      orders.addLast(order);
      stats.add(order);
    }

    public void remove(Order order) {
      orders.removeIf(o -> o.getOrderId().equals(order.getOrderId()));
      stats.remove(order);
    }
  }

  class OrdersStats {
    long totalQuantity;
    long totalVolume;

    public void add(Order order) {
      totalQuantity += order.getQuantity();
      totalVolume += order.getQuantity() * order.getPrice();
    }

    public void remove(Order order) {
      totalQuantity -= order.getQuantity();
      totalVolume -= order.getQuantity() * order.getPrice();
    }
  }
}
