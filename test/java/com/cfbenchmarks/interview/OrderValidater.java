package com.cfbenchmarks.interview;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;

class OrderValidater {
  private OrderBookManager obManager;
  private String instrument;
  private Side side;
  private Map<String, Order> validatingOrders = new HashMap<String, Order>();
  
  OrderValidater( OrderBookManager obManager, String instrument, Side side) {
    this.obManager = obManager;
    this.instrument = instrument;
    this.side = side;
  }
  
  void addOrder( String orderId, long price, long qty ) {
    Order added = new Order.Builder().build( orderId, instrument, side, price, qty );
    obManager.addOrder( added );
    //keep it for later
    validatingOrders.put( orderId, added );
  }
  
  void modifyOrder( String orderId, long qty ) {
    obManager.modifyOrder( orderId, qty );
    
    // update the tracked order
    Order modified = new Order.Builder()
        .clone( validatingOrders.get( orderId ) )
        .setQuantity( qty )
        .get();
    //keep it for later
    validatingOrders.put( orderId, modified );
  }
  
  void deleteOrder( String orderId ) {
    obManager.deleteOrder( orderId );
    validatingOrders.remove( orderId );
  }
  
  void validate( long bestPrice, long level, long orderNumber, long qty, long vol ) {
    assertEquals( 
        String.format( "Best price is %s", bestPrice ),
        Optional.of( bestPrice ),
        obManager.getBestPrice( instrument, side ) );
    assertEquals( 
        String.format( "orders at %s is %s", level, orderNumber ),
        orderNumber,
        obManager.getOrderNumAtLevel( instrument, side, level ) );
    assertEquals(
        String.format( "Total qty at %s is %s", level, qty ),
        qty,
        obManager.getTotalQuantityAtLevel( instrument, side, level ) );
    assertEquals(
        String.format( "Total vol at %s is %s", level, vol ),
        vol,
        obManager.getTotalVolumeAtLevel( instrument, side, level ) );
  }
  
  void validateOrder( long priceLevel, String... orderIds ) {
    List<Order> levelOrders = obManager.getOrdersAtLevel( instrument, side, priceLevel );
    
    Assert.assertEquals( orderIds.length, levelOrders.size() );
    for ( int i = 0; i < orderIds.length; i++ ) {
      Assert.assertEquals( validatingOrders.get( orderIds[i] ), levelOrders.get( i ) );
    }
  }
}