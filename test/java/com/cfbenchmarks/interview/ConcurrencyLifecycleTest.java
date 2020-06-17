package com.cfbenchmarks.interview;

import java.util.Optional;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.google.code.tempusfugit.concurrency.ConcurrentRule;
import com.google.code.tempusfugit.concurrency.RepeatingRule;
import com.google.code.tempusfugit.concurrency.annotations.Concurrent;
import com.google.code.tempusfugit.concurrency.annotations.Repeating;

public class ConcurrencyLifecycleTest {

  private static OrderBookManager orderBookManager;
  
  @Rule public ConcurrentRule concurrently = new ConcurrentRule();
  @Rule public RepeatingRule repeatedly = new RepeatingRule();

  @BeforeClass
  public static void init()
  {
    orderBookManager = new OrderBookManagerImpl();
  }
  
  @Test
  @Concurrent (count = 10)
  @Repeating (repetition = 500)
  public void lifecycle() {
    
    // send sell orders
    Order order1 = addOrder( Side.SELL, 300, 5 );
    Order order2 = addOrder( Side.SELL, 400, 5 );
    Order order3 = addOrder( Side.SELL, 500, 5 );
    
    orderBookManager.modifyOrder( order1.getOrderId(), 1 );
    orderBookManager.modifyOrder( order2.getOrderId(), 1 );
    orderBookManager.modifyOrder( order3.getOrderId(), 6 );
    
    // send buy orders
    Order order4 = addOrder( Side.BUY, 300, 5 );
    Order order5 = addOrder( Side.BUY, 400, 5 );
    Order order6 = addOrder( Side.BUY, 500, 50 );
    
    orderBookManager.modifyOrder( order4.getOrderId(), 1 );
    orderBookManager.modifyOrder( order5.getOrderId(), 1 );
    orderBookManager.modifyOrder( order6.getOrderId(), 6 );
    
    orderBookManager.deleteOrder( order1.getOrderId() );
    orderBookManager.deleteOrder( order2.getOrderId() );
    orderBookManager.deleteOrder( order3.getOrderId() );
    
    orderBookManager.deleteOrder( order4.getOrderId() );
    orderBookManager.deleteOrder( order5.getOrderId() );
    orderBookManager.deleteOrder( order6.getOrderId() );
  }
  
  private Order addOrder( Side side, long price, long qty )
  {
    Order toAdd = new Order.Builder()
        .setOrderId( UUID.randomUUID().toString() )
        .setInstrument( "microscope" )
        .setSide( side )
        .setPrice( price )
        .setQuantity( qty )
        .get();
    orderBookManager.addOrder( toAdd );
    return toAdd;
  }
  
  @AfterClass
  public static void check() {
    Assert.assertEquals( Optional.empty(), orderBookManager.getBestPrice( "microscope", Side.SELL ) );
    Assert.assertEquals( Optional.empty(), orderBookManager.getBestPrice( "microscope", Side.BUY ) );
    
    // order number
    Assert.assertEquals( 0, orderBookManager.getOrderNumAtLevel( "microscope", Side.SELL, 300 ) );
    Assert.assertEquals( 0, orderBookManager.getOrderNumAtLevel( "microscope", Side.SELL, 400 ) );
    Assert.assertEquals( 0, orderBookManager.getOrderNumAtLevel( "microscope", Side.SELL, 500 ) );
    
    // quantity
    Assert.assertEquals( 0, orderBookManager.getTotalQuantityAtLevel( "microscope", Side.SELL, 300 ) );
    Assert.assertEquals( 0, orderBookManager.getTotalQuantityAtLevel( "microscope", Side.SELL, 400 ) );
    Assert.assertEquals( 0, orderBookManager.getTotalQuantityAtLevel( "microscope", Side.SELL, 500 ) );
    
    // vol
    Assert.assertEquals( 0, orderBookManager.getTotalVolumeAtLevel( "microscope", Side.BUY, 300 ) );
    Assert.assertEquals( 0, orderBookManager.getTotalVolumeAtLevel( "microscope", Side.BUY, 400 ) );
    Assert.assertEquals( 0, orderBookManager.getTotalVolumeAtLevel( "microscope", Side.BUY, 500 ) );
    
    Assert.assertEquals( 0, orderBookManager.getOrderNumAtLevel( "microscope", Side.BUY, 300 ) );
    Assert.assertEquals( 0, orderBookManager.getOrderNumAtLevel( "microscope", Side.BUY, 400 ) );
    Assert.assertEquals( 0, orderBookManager.getOrderNumAtLevel( "microscope", Side.BUY, 500 ) );
    
    // quantity
    Assert.assertEquals( 0, orderBookManager.getTotalQuantityAtLevel( "microscope", Side.BUY, 300 ) );
    Assert.assertEquals( 0, orderBookManager.getTotalQuantityAtLevel( "microscope", Side.BUY, 400 ) );
    Assert.assertEquals( 0, orderBookManager.getTotalQuantityAtLevel( "microscope", Side.BUY, 500 ) );
    
    // vol
    Assert.assertEquals( 0, orderBookManager.getTotalVolumeAtLevel( "microscope", Side.BUY, 300 ) );
    Assert.assertEquals( 0, orderBookManager.getTotalVolumeAtLevel( "microscope", Side.BUY, 400 ) );
    Assert.assertEquals( 0, orderBookManager.getTotalVolumeAtLevel( "microscope", Side.BUY, 500 ) );
  }
}
