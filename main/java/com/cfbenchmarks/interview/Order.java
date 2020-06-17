package com.cfbenchmarks.interview;

import static com.cfbenchmarks.interview.Validation.validateArg;
import static com.google.common.base.Preconditions.checkArgument;

/*
 * order is an immutable class, use the Builder to create modified instances
 */
public class Order {

  /** unique identifier for the order */
  private String orderId;

  /** identifier of an instrument */
  private String instrument;

  /** either buy or sell */
  private Side side;

  /** limit price for the order, always positive */
  private long price;

  /** required quantity, always positive */
  private long quantity;

  /** Hide the default ctor */
  private Order() {}

  /**
   * Copying ctor
   *
   * @param order an order to make copy from
   */
  private Order(Order order) {
    this(order.orderId, order.instrument, order.side, order.price, order.quantity);
  }

  /**
   * All-values ctor
   *
   * @param orderId unique identifier for the order
   * @param instrument identifier of an instrument
   * @param side either buy or sell
   * @param price limit price for the order, always positive
   * @param quantity required quantity, always positive
   */
  private Order(String orderId, String instrument, Side side, long price, long quantity) {
    this.orderId = orderId;
    this.instrument = instrument;
    this.side = side;
    this.price = price;
    this.quantity = quantity;
    validate();
  }

  public String getOrderId() {
    return orderId;
  }

  public String getInstrument() {
    return instrument;
  }

  public Side getSide() {
    return side;
  }

  public long getPrice() {
    return price;
  }

  public long getQuantity() {
    return quantity;
  }
  
  String getOrderBookKey() {
    return sideInstrumentKey( this.instrument, this.side );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Order order = (Order) o;

    if (price != order.price) return false;
    if (quantity != order.quantity) return false;
    if (orderId != null ? !orderId.equals(order.orderId) : order.orderId != null) return false;
    if (instrument != null ? !instrument.equals(order.instrument) : order.instrument != null)
      return false;
    return side == order.side;
  }

  @Override
  public int hashCode() {
    int result = orderId != null ? orderId.hashCode() : 0;
    result = 31 * result + (instrument != null ? instrument.hashCode() : 0);
    result = 31 * result + (side != null ? side.hashCode() : 0);
    result = 31 * result + (int) (price ^ (price >>> 32));
    result = 31 * result + (int) (quantity ^ (quantity >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "Order{"
        + "orderId='"
        + orderId
        + '\''
        + ", instrument='"
        + instrument
        + '\''
        + ", side="
        + side
        + ", price="
        + price
        + ", quantity="
        + quantity
        + '}';
  }
  
  private Order validate()
  {
    checkArgument( price > 0, "price must be positive");
    checkArgument( quantity > 0, "quantity must be positive");
    validateArg( instrument, "instrument cannot be null" );
    validateArg( orderId, "orderId cannot be null" );
    validateArg( side, "side cannot be null" );
    // check for special chars
    checkArgument(instrument.indexOf( "::" ) == -1, "instrument name cannot contain '::'");
    return this;
  }
  
  static String sideInstrumentKey( String instrument, Side side ) {
    return instrument + "::" + side.toString();
  }
  
  static class Builder {
    private Order order = new Order();
    
    public Builder clone( Order order )
    {
      validateArg( order, "cannot clone null" );
      this.order = new Order( order );
      return this;
    }
    
    public Builder setOrderId(String orderId) {
      this.order.orderId = orderId;
      return this;
    }

    public Builder setInstrument(String instrument) {
      this.order.instrument = instrument;
      return this;
    }

    public Builder setSide(Side side) {
      this.order.side = side;
      return this;
    }

    public Builder setPrice(long price) {
      this.order.price = price;
      return this;
    }

    public Builder setQuantity(long quantity) {
      this.order.quantity = quantity;
      return this;
    }
    
    public Order build(String orderId, String instrument, Side side, long price, long quantity) {
      return new Order( orderId, instrument, side, price, quantity);
    }
    
    public Order get() {
      // clone to prevent the builder affecting the order
      return new Order( order );
    }
  }
}
