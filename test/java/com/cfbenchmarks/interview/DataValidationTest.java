package com.cfbenchmarks.interview;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.AssertionFailedError;

public class DataValidationTest {

  @Test
  public void orderBuilder()
  {
    Order order = new Order.Builder()
      .setInstrument( "test" )
      .setOrderId( "order1" )
      .setSide( Side.BUY )
      .setPrice( 200 )
      .setQuantity( 10 )
      .get();
    
    Assert.assertEquals( "test", order.getInstrument() );
    Assert.assertEquals( "order1", order.getOrderId() );
    Assert.assertEquals( Side.BUY, order.getSide() );
    Assert.assertEquals( 200, order.getPrice() );
    Assert.assertEquals( 10, order.getQuantity() );
    // check the orderBook key 
    Assert.assertEquals( "test::BUY", order.getOrderBookKey() );
  }
  
  @Test
  public void orderBuilderImmutable()
  {
    Order.Builder orderBuilder = new Order.Builder()
      .setInstrument( "test" )
      .setOrderId( "order1" )
      .setSide( Side.BUY )
      .setPrice( 200 )
      .setQuantity( 10 );
    
    Order firstOrder = orderBuilder.get();
    
    // now modify via the builder
    orderBuilder.setInstrument( "sddsfsdf" )
      .setOrderId( "sdfdsfsdf" )
      .setSide( Side.SELL )
      .setPrice( 2 )
      .setQuantity( 1 );
    
    Assert.assertEquals( "test", firstOrder.getInstrument() );
    Assert.assertEquals( "order1", firstOrder.getOrderId() );
    Assert.assertEquals( Side.BUY, firstOrder.getSide() );
    Assert.assertEquals( 200, firstOrder.getPrice() );
    Assert.assertEquals( 10, firstOrder.getQuantity() );
    // check the orderBook key 
    Assert.assertEquals( "test::BUY", firstOrder.getOrderBookKey() );
    
    // check the second order is correct
    Order secondOrder = orderBuilder.get();
    Assert.assertEquals( "sddsfsdf", secondOrder.getInstrument() );
    Assert.assertEquals( "sdfdsfsdf", secondOrder.getOrderId() );
    Assert.assertEquals( Side.SELL, secondOrder.getSide() );
    Assert.assertEquals( 2, secondOrder.getPrice() );
    Assert.assertEquals( 1, secondOrder.getQuantity() );
    // check the orderBook key 
    Assert.assertEquals( "sddsfsdf::SELL", secondOrder.getOrderBookKey() );
  }
  
  @Test
  public void cloneOrder()
  {
    Order firstOrder = new Order.Builder()
      .setInstrument( "test" )
      .setOrderId( "order1" )
      .setSide( Side.BUY )
      .setPrice( 200 )
      .setQuantity( 10 )
      .get();
    
    // clone and modify
    Order secondOrder = new Order.Builder()
      .clone( firstOrder )
      .setPrice( 2 )
      .setQuantity( 1 )
      .get();
    
    Assert.assertEquals( "test", secondOrder.getInstrument() );
    Assert.assertEquals( "order1", secondOrder.getOrderId() );
    Assert.assertEquals( Side.BUY, secondOrder.getSide() );
    Assert.assertEquals( 2, secondOrder.getPrice() );
    Assert.assertEquals( 1, secondOrder.getQuantity() );
  }
  
  @Test
  public void nullOrderArguments()
  {
    // instrument can't be null
    assertThrows( IllegalArgumentException.class, () ->{
      new Order.Builder()
        .setInstrument( null )
        .setOrderId( "order1" )
        .setSide( Side.BUY )
        .setPrice( 200 )
        .setQuantity( 10 )
        .get();
    } );
    
    // orderId can't be null
    assertThrows( IllegalArgumentException.class, () ->{
      new Order.Builder()
        .setInstrument( "guitar" )
        .setOrderId( null )
        .setSide( Side.BUY )
        .setPrice( 200 )
        .setQuantity( 10 )
        .get();
    } );
    
    // side can't be null
    assertThrows( IllegalArgumentException.class, () ->{
      new Order.Builder()
        .setInstrument( "guitar" )
        .setOrderId( "order1" )
        .setSide( null )
        .setPrice( 200 )
        .setQuantity( 10 )
        .get();
    } );    
  }
  
  @Test
  public void incorrectOrderArguments()
  {
    // instrument can't be blank
    assertThrows( IllegalArgumentException.class, () ->{
      new Order.Builder()
        .setInstrument( "" )
        .setOrderId( "order1" )
        .setSide( Side.BUY )
        .setPrice( 200 )
        .setQuantity( 10 )
        .get();
    } );
    
    // orderId can't be blank
    assertThrows( IllegalArgumentException.class, () ->{
      new Order.Builder()
        .setInstrument( "fake" )
        .setOrderId( "" )
        .setSide( Side.BUY )
        .setPrice( 200 )
        .setQuantity( 10 )
        .get();
    } );
    
    // price must be positive
    assertThrows( IllegalArgumentException.class, () ->{
      new Order.Builder()
        .setInstrument( "fake" )
        .setOrderId( "e" )
        .setSide( Side.BUY )
        .setPrice( -1 )
        .setQuantity( 10 )
        .get();
    } );
    
    // qty must be positive
    assertThrows( IllegalArgumentException.class, () ->{
      new Order.Builder()
        .setInstrument( "fake" )
        .setOrderId( "e" )
        .setSide( Side.BUY )
        .setPrice( 33 )
        .setQuantity( -1 )
        .get();
    } );   
    
    // instrument can't have ::
    assertThrows( IllegalArgumentException.class, () ->{
      new Order.Builder()
        .setInstrument( "fake::bad" )
        .setOrderId( "e" )
        .setSide( Side.BUY )
        .setPrice( 33 )
        .setQuantity( 10 )
        .get();
    } );   
  }
  
  @Test
  public void nullApiArguments()
  {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();
    
    /* add and delete */
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.addOrder( null );
    } );
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.deleteOrder( null );
    } );
    
    /* modify */
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.modifyOrder( null, 1 );
    } );
    
    /* best price */
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.getBestPrice( null, Side.BUY );
    } );
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.getBestPrice( "blast", null );
    } );
    
    // create order
    Order order = new Order.Builder()
        .setInstrument( "blast" )
        .setOrderId( "order1" )
        .setSide( Side.BUY )
        .setPrice( 200 )
        .setQuantity( 10 )
        .get();
    // send order
    orderBookManager.addOrder( order );
    
    /* getOrderNumAtLevel */
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.getOrderNumAtLevel( null, Side.BUY, 1 );
    } );
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.getOrderNumAtLevel( "blast", null, 1 );
    } );
    
    /* getOrdersAtLevel */
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.getOrdersAtLevel( null, Side.BUY, 1 );
    } );
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.getOrdersAtLevel( "blast", null, 1 );
    } );
    
    /* getTotalQuantityAtLevel */
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.getTotalQuantityAtLevel( null, Side.BUY, 1 );
    } );
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.getTotalQuantityAtLevel( "blast", null, 1 );
    } );
    
    /* getTotalVolumeAtLevel */
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.getTotalVolumeAtLevel( null, Side.BUY, 1 );
    } );
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.getTotalVolumeAtLevel( "blast", null, 1 );
    } );
  }
  
  @Test
  public void incorrectApiArguments()
  {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();
    
    // create order
    Order order = new Order.Builder()
        .setInstrument( "DXYP" )
        .setOrderId( "order1" )
        .setSide( Side.BUY )
        .setPrice( 200 )
        .setQuantity( 10 )
        .get();
    // send order
    orderBookManager.addOrder( order );
    
    /* modify */
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.modifyOrder( "DXYP", 0 );
    } );
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.modifyOrder( "DXYP", -1 );
    } );
    
    /* getOrderNumAtLevel */
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.getOrderNumAtLevel( "DXYP", Side.BUY, 0 );
    } );
    
    /* getOrdersAtLevel */
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.getOrdersAtLevel( "DXYP", Side.BUY, 0 );
    } );
    
    /* getTotalQuantityAtLevel */
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.getTotalQuantityAtLevel( "DXYP", Side.BUY, 0 );
    } );
    
    /* getTotalVolumeAtLevel */
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.getTotalVolumeAtLevel( "DXYP", Side.BUY, -1 );
    } );
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.getTotalVolumeAtLevel( "DXYP", null, -1 );
    } );
  }
  
  @Test
  public void existingAndMissing()
  {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();
    
    // create order
    Order order = new Order.Builder()
        .setInstrument( "DXYP" )
        .setOrderId( "order1" )
        .setSide( Side.BUY )
        .setPrice( 200 )
        .setQuantity( 10 )
        .get();
    // send order
    orderBookManager.addOrder( order );
    
    /* can't add twice */
    assertThrows( IllegalArgumentException.class, () ->{
      orderBookManager.addOrder( order );
    } );
    
    /* can't add existing */
    assertThrows( IllegalArgumentException.class, () ->{
      Order duplicateOrderId = new Order.Builder()
          .setInstrument( "DXYPY" )
          .setOrderId( "order1" )
          .setSide( Side.SELL )
          .setPrice( 44 )
          .setQuantity( 33 )
          .get();
      // send order
      orderBookManager.addOrder( duplicateOrderId );
    } );
    
    /* can't delete missing */
    assertThrows( IllegalArgumentException.class, () ->{
      // delete missing
      orderBookManager.deleteOrder( "not there" );
    } );
    
    /* can't modify missing */
    assertThrows( IllegalArgumentException.class, () ->{
      // delete missing
      orderBookManager.modifyOrder( "not there", 5 );
    } );
  }
  
  @SuppressWarnings("unchecked")
  public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable )
  {
    try {
      executable.execute();  
    }
    catch (Throwable actualException) {
      if (expectedType.isInstance(actualException)) {
        return (T) actualException;
      }      
    }
    throw new AssertionFailedError( "no exception was thrown of type " + expectedType );
  }
  
  @FunctionalInterface
  public interface Executable {
    void execute() throws Throwable;
  }
}
