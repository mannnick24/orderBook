package com.cfbenchmarks.interview;

import static org.junit.Assert.*;

import java.util.Optional;
import org.junit.Test;

public class StatsTest {

  @Test
  public void noBook() {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();

    // check that stats return empty or 0
    assertEquals( "Empty book should return empty best price", Optional.empty(), orderBookManager.getBestPrice( "VOD.L", Side.BUY ) );
    assertEquals( "Empty book should return empty best price", Optional.empty(), orderBookManager.getBestPrice( "VOD.L", Side.SELL ) );
    
    assertEquals( "Zero orders at level", 0, orderBookManager.getOrderNumAtLevel( "VOD.L", Side.BUY, 1 ) );
    assertEquals( "Zero qty at level", 0, orderBookManager.getTotalQuantityAtLevel( "VOD.L", Side.BUY, 1 ) );
    assertEquals( "Zero vol at level", 0, orderBookManager.getTotalVolumeAtLevel( "VOD.L", Side.BUY, 1 ) );
    assertEquals( "Zero orderNum at level", 0, orderBookManager.getOrderNumAtLevel( "VOD.L", Side.BUY, 1 ) );
    
    //list orders
    assertEquals( "Zero orders at level", 0, orderBookManager.getOrdersAtLevel( "VOD.L", Side.BUY, 1 ).size() );
    
    assertEquals( "Zero orders at level", 0, orderBookManager.getOrderNumAtLevel( "VOD.L", Side.SELL, 1 ) );
    assertEquals( "Zero qty at level", 0, orderBookManager.getTotalQuantityAtLevel( "VOD.L", Side.SELL, 1 ) );
    assertEquals( "Zero vol at level", 0, orderBookManager.getTotalVolumeAtLevel( "VOD.L", Side.SELL, 1 ) );
    assertEquals( "Zero orderNum at level", 0, orderBookManager.getOrderNumAtLevel( "VOD.L", Side.SELL, 1 ) );
    
    //list orders
    assertEquals( "Zero orders at level", 0, orderBookManager.getOrdersAtLevel( "VOD.L", Side.SELL, 1 ).size() );
  }
  
  @Test
  public void missingOrderLevel() {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();
    // create order at 200
    Order buy = new Order.Builder()
        .setOrderId( "order1" )
        .setInstrument( "VOD.L" )
        .setSide( Side.BUY )
        .setPrice( 200 )
        .setQuantity( 10 )
        .get();

    // send order
    orderBookManager.addOrder(buy);
    
    assertEquals( "Zero orders at level 100", 0, orderBookManager.getOrderNumAtLevel( "VOD.L", Side.BUY, 100 ) );
    assertEquals( "Zero qty at level 100", 0, orderBookManager.getTotalQuantityAtLevel( "VOD.L", Side.BUY, 100 ) );
    assertEquals( "Zero vol at level 100", 0, orderBookManager.getTotalVolumeAtLevel( "VOD.L", Side.BUY, 100 ) );
    assertEquals( "Zero orderNum at level 100", 0, orderBookManager.getOrderNumAtLevel( "VOD.L", Side.BUY, 100 ) );
    
    //list orders
    assertEquals( "Zero orders at level 100", 0, orderBookManager.getOrdersAtLevel( "VOD.L", Side.BUY, 100 ).size() );
    
    //sanity check
    assertEquals( "Zero orders at level 100", 1, orderBookManager.getOrdersAtLevel( "VOD.L", Side.BUY, 200 ).size() );
  }
  
  @Test
  public void singleBuyBidPrice() {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();

    // create order
    Order buy = new Order.Builder()
        .setOrderId( "order1" )
        .setInstrument( "VOD.L" )
        .setSide( Side.BUY )
        .setPrice( 200 )
        .setQuantity( 10 )
        .get();

    // send order
    orderBookManager.addOrder( buy );

    // check that best price is 200
    Optional<Long> expectedPrice = Optional.of( 200L );
    Optional<Long> actualPrice = orderBookManager.getBestPrice( "VOD.L", Side.BUY );
    assertEquals( "Best bid price is 200", expectedPrice, actualPrice );
  }
  
  @Test
  public void bestPriceBid() {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();

    // send orders
    orderBookManager.addOrder( new Order.Builder().build( "order1", "microscope", Side.BUY, 300, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order2", "microscope", Side.BUY, 400, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order3", "microscope", Side.BUY, 600, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order4", "microscope", Side.BUY, 700, 5) );

    // check that best price is 700
    assertEquals( "Best bid price is 700", Optional.of( 700L ), orderBookManager.getBestPrice( "microscope", Side.BUY ) );
    
    orderBookManager.addOrder( new Order.Builder().build( "order5", "microscope", Side.BUY, 200, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order6", "microscope", Side.BUY, 400, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order7", "microscope", Side.BUY, 100, 5) );

    // check that best price is 700
    assertEquals( "Best bid price is 700", Optional.of( 700L ), orderBookManager.getBestPrice( "microscope", Side.BUY ) );
    
    orderBookManager.addOrder( new Order.Builder().build( "order8", "microscope", Side.BUY, 800, 5) );

    // check that best price is 800
    assertEquals( "Best bid price is 800", Optional.of( 800L ), orderBookManager.getBestPrice( "microscope", Side.BUY ) );
    
    // sanity
    assertEquals( "Best ask price is empty", Optional.empty(), orderBookManager.getBestPrice( "microscope", Side.SELL ) );
  }
  
  @Test
  public void bestPriceAsk() {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();

    // send orders
    orderBookManager.addOrder( new Order.Builder().build( "order1", "microscope", Side.SELL, 300, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order2", "microscope", Side.SELL, 400, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order3", "microscope", Side.SELL, 600, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order4", "microscope", Side.SELL, 700, 5) );

    // check SELL best price is correct
    assertEquals( "Best ask price is 300", Optional.of( 300L ), orderBookManager.getBestPrice( "microscope", Side.SELL ) );
    
    orderBookManager.addOrder( new Order.Builder().build( "order5", "microscope", Side.SELL, 200, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order6", "microscope", Side.SELL, 400, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order7", "microscope", Side.SELL, 100, 5) );

    assertEquals( "Best ask price is 100", Optional.of( 100L ), orderBookManager.getBestPrice( "microscope", Side.SELL ) );
    
    orderBookManager.addOrder( new Order.Builder().build( "order8", "microscope", Side.SELL, 800, 5) );

    assertEquals( "Best ask price is 100", Optional.of( 100L ), orderBookManager.getBestPrice( "microscope", Side.SELL ) );
    
    // sanity
    assertEquals( "Best bid price is empty", Optional.empty(), orderBookManager.getBestPrice( "microscope", Side.BUY ) );
  }
  
  @Test
  public void statsLifecycle() {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();

    // send orders
    OrderValidater validater = new OrderValidater( orderBookManager, "microscope", Side.SELL );
    validater.addOrder( "order1", 300, 5 );
    validater.addOrder( "order2", 400, 5 );

    // check stats
    validater.validate( 300, 300, 1, 5, 1500 );
    validater.validate( 300, 400, 1, 5, 2000 );
    
    // add and check
    validater.addOrder( "order3", 400, 5 );
    validater.addOrder( "order4", 300, 5 );
    validater.validate( 300, 300, 2, 10, 3000 );
    validater.validate( 300, 400, 2, 10, 4000 );
    
    // modify up and check
    validater.modifyOrder( "order4", 6 );
    // new quantity is 5 + 6
    // new volume is 11 * 300
    validater.validate( 300, 300, 2, 11, 3300 );
    
    // modify order 1 down and check
    validater.modifyOrder( "order1", 4 );
    validater.validate( 300, 300, 2, 10, 3000 );
    
    // modify same
    validater.modifyOrder( "order1", 4 );
    validater.validate( 300, 300, 2, 10, 3000 );
    
    // delete order and check
    validater.deleteOrder( "order1" );
    validater.validate( 300, 300, 1, 6, 1800 );
  }
  
  @Test
  public void deleteBestPriceSellAdjust() {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();

    // send orders
    OrderValidater sellValidator = new OrderValidater( orderBookManager, "microscope", Side.SELL );
    sellValidator.addOrder( "order1", 100, 5 );
    sellValidator.addOrder( "order2", 400, 5 );
    
    // check stats
    sellValidator.validate( 100, 100, 1, 5, 500 );
    
    // delete order and check
    sellValidator.deleteOrder( "order1" );
    sellValidator.validate( 400, 400, 1, 5, 2000 );
  }
  
  @Test
  public void deleteBestPriceBuyAdjust() {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();

    // send orders
    OrderValidater buyValidator = new OrderValidater( orderBookManager, "microscope", Side.BUY );
    buyValidator.addOrder( "order1", 100, 5 );
    buyValidator.addOrder( "order2", 400, 5 );
    
    // check stats
    buyValidator.validate( 400, 100, 1, 5, 500 );
    
    // delete order and check
    buyValidator.deleteOrder( "order2" );
    buyValidator.validate( 100, 400, 0, 0, 0 );
  }
  
  @Test
  public void getOrderNumAtLevel() {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();

    // send orders
    orderBookManager.addOrder( new Order.Builder().build( "order1", "microscope", Side.SELL, 300, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order2", "microscope", Side.SELL, 300, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order3", "microscope", Side.SELL, 600, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order4", "microscope", Side.SELL, 600, 5) );

    // check Order Number
    assertEquals( "Two orders at 300", 2, orderBookManager.getOrderNumAtLevel( "microscope", Side.SELL, 300 ) );
    assertEquals( "Two orders at 600", 2, orderBookManager.getOrderNumAtLevel( "microscope", Side.SELL, 600 ) );
    
    orderBookManager.addOrder( new Order.Builder().build( "order5", "microscope", Side.SELL, 300, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order6", "microscope", Side.SELL, 100, 5) );

    assertEquals( "3 orders at 300", 3, orderBookManager.getOrderNumAtLevel( "microscope", Side.SELL, 300 ) );
    assertEquals( "Two orders at 600", 2, orderBookManager.getOrderNumAtLevel( "microscope", Side.SELL, 600 ) );
    assertEquals( "1 orders at 100", 1, orderBookManager.getOrderNumAtLevel( "microscope", Side.SELL, 100 ) );
    
    // sanity
    assertEquals( "no Buy orders at 300", 0, orderBookManager.getOrderNumAtLevel( "microscope", Side.BUY, 300 ) );
  }
  
  @Test
  public void totalQuantityAtLevel() {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();

    // send orders
    orderBookManager.addOrder( new Order.Builder().build( "order1", "microscope", Side.SELL, 300, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order2", "microscope", Side.SELL, 300, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order3", "microscope", Side.SELL, 600, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order4", "microscope", Side.SELL, 600, 5) );

    // check tot qty
    assertEquals( "10 at 300", 10, orderBookManager.getTotalQuantityAtLevel( "microscope", Side.SELL, 300 ) );
    assertEquals( "10 at 600", 10, orderBookManager.getTotalQuantityAtLevel( "microscope", Side.SELL, 600 ) );
    
    orderBookManager.addOrder( new Order.Builder().build( "order5", "microscope", Side.SELL, 300, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order6", "microscope", Side.SELL, 100, 5) );

    assertEquals( "15 at 300", 15, orderBookManager.getTotalQuantityAtLevel( "microscope", Side.SELL, 300 ) );
    assertEquals( "10 at 600", 10, orderBookManager.getTotalQuantityAtLevel( "microscope", Side.SELL, 600 ) );
    assertEquals( "5 at 100", 5, orderBookManager.getTotalQuantityAtLevel( "microscope", Side.SELL, 100 ) );
    
    // sanity
    assertEquals( "nothing at 300", 0, orderBookManager.getTotalQuantityAtLevel( "microscope", Side.BUY, 300 ) );
  }
  
  @Test
  public void totalVolumeAtLevel() {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();

    // send orders
    orderBookManager.addOrder( new Order.Builder().build( "order1", "microscope", Side.SELL, 300, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order2", "microscope", Side.SELL, 300, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order3", "microscope", Side.SELL, 600, 5) );
    orderBookManager.addOrder( new Order.Builder().build( "order4", "microscope", Side.SELL, 600, 5) );

    // check tot vol
    assertEquals( "3000 at 300", 3000, orderBookManager.getTotalVolumeAtLevel( "microscope", Side.SELL, 300 ) );
    assertEquals( "6000 at 600", 6000, orderBookManager.getTotalVolumeAtLevel( "microscope", Side.SELL, 600 ) );
    
    orderBookManager.addOrder( new Order.Builder().build( "order5", "microscope", Side.SELL, 300, 1) );
    orderBookManager.addOrder( new Order.Builder().build( "order6", "microscope", Side.SELL, 600, 1) );
    orderBookManager.addOrder( new Order.Builder().build( "order7", "microscope", Side.SELL, 100, 5) );

    assertEquals( "3300 at 300", 3300, orderBookManager.getTotalVolumeAtLevel( "microscope", Side.SELL, 300 ) );
    assertEquals( "6600 at 600", 6600, orderBookManager.getTotalVolumeAtLevel( "microscope", Side.SELL, 600 ) );
    assertEquals( "500 at 100", 500, orderBookManager.getTotalVolumeAtLevel( "microscope", Side.SELL, 100 ) );
    
    // sanity
    assertEquals( "nothing at 300", 0, orderBookManager.getTotalVolumeAtLevel( "microscope", Side.BUY, 300 ) );
  }
}
