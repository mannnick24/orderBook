package com.cfbenchmarks.interview;

import java.util.ArrayList;
import java.util.List;
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

public class ConcurrencyUsageTest {

  private static OrderBookManager orderBookManager;
  
  @Rule public ConcurrentRule concurrently = new ConcurrentRule();
  @Rule public RepeatingRule repeatedly = new RepeatingRule();
  
  private static List<Order> orders = new ArrayList<Order>();

  @BeforeClass
  public static void init()
  {
    orderBookManager = new OrderBookManagerImpl();
    
    // send sell orders
    addOrder( Side.SELL, 300, 5 );
    addOrder( Side.SELL, 300, 5 );
    addOrder( Side.SELL, 300, 5 );
    
    // send buy orders
    addOrder( Side.BUY, 300, 5 );
    addOrder( Side.BUY, 300, 5 );
    addOrder( Side.BUY, 300, 5 );
  }
  
  @Test
  @Concurrent (count = 10)
  @Repeating (repetition = 500)
  public void usage() {
     orders.forEach( order -> {
       orderBookManager.modifyOrder(order.getOrderId(), 4 );
       
       orderBookManager.getOrderNumAtLevel( "microscope", Side.SELL, 300 );
       orderBookManager.getTotalQuantityAtLevel( "microscope", Side.SELL, 300 );
       orderBookManager.getTotalVolumeAtLevel( "microscope", Side.SELL, 300 );
       orderBookManager.getOrderNumAtLevel( "microscope", Side.BUY, 300 );
       orderBookManager.getTotalQuantityAtLevel( "microscope", Side.BUY, 300 );
       orderBookManager.getTotalVolumeAtLevel( "microscope", Side.BUY, 300 );
       
       orderBookManager.modifyOrder(order.getOrderId(), 6 );
     } );
  }
  
  private static void addOrder( Side side, long price, long qty )
  {
    Order toAdd = new Order.Builder()
        .setOrderId( UUID.randomUUID().toString() )
        .setInstrument( "microscope" )
        .setSide( side )
        .setPrice( price )
        .setQuantity( qty )
        .get();
    orderBookManager.addOrder( toAdd );
    orders.add( toAdd );
  }
  
  @AfterClass
  public static void check()
  {
    Assert.assertEquals( Optional.of( 300l ), orderBookManager.getBestPrice( "microscope", Side.SELL ) );
    Assert.assertEquals( Optional.of( 300l ), orderBookManager.getBestPrice( "microscope", Side.BUY ) );
    
    // order number
    Assert.assertEquals( 3, orderBookManager.getOrderNumAtLevel( "microscope", Side.SELL, 300 ) );
    Assert.assertEquals( 3, orderBookManager.getOrderNumAtLevel( "microscope", Side.BUY, 300 ) );
    
    // quantity
    Assert.assertEquals( 18, orderBookManager.getTotalQuantityAtLevel( "microscope", Side.SELL, 300 ) );
    Assert.assertEquals( 18, orderBookManager.getTotalQuantityAtLevel( "microscope", Side.BUY, 300 ) );
    
    // vol
    Assert.assertEquals( 18 * 300, orderBookManager.getTotalVolumeAtLevel( "microscope", Side.SELL, 300 ) );
    Assert.assertEquals( 18 * 300, orderBookManager.getTotalVolumeAtLevel( "microscope", Side.BUY, 300 ) );
  }
}
