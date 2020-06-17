package com.cfbenchmarks.interview;

import org.junit.Test;

public class OrderOrderTest {

  @Test
  public void addOrder() {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();

    // send orders
    OrderValidater validater = new OrderValidater( orderBookManager, "microscope", Side.SELL );
    validater.addOrder( "order1", 300, 5 );
    validater.addOrder( "order2", 300, 5 );
    validater.addOrder( "order3", 300, 50 );

    validater.validateOrder( 300, "order1", "order2", "order3");
  }
  
  @Test
  public void deleteOrder() {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();

    // send orders
    OrderValidater validater = new OrderValidater( orderBookManager, "microscope", Side.SELL );
    validater.addOrder( "order1", 300, 5 );
    validater.addOrder( "order2", 300, 5 );
    validater.addOrder( "order3", 300, 50 );

    validater.validateOrder( 300, "order1", "order2", "order3");
    
    //delete one and check
    validater.deleteOrder( "order2" );
    validater.validateOrder( 300, "order1", "order3");
    
    //delete one and check
    validater.deleteOrder( "order1" );
    validater.validateOrder( 300, "order3");
    
    //delete one and check
    validater.deleteOrder( "order3" );
    validater.validateOrder( 300 );
  }
  
  @Test
  public void modifyDownOrder() {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();

    // send orders
    OrderValidater validater = new OrderValidater( orderBookManager, "microscope", Side.SELL );
    validater.addOrder( "order1", 300, 5 );
    validater.addOrder( "order2", 300, 5 );
    validater.addOrder( "order3", 300, 50 );

    // modify one down and check
    validater.modifyOrder( "order2", 4 );
    validater.validateOrder( 300, "order1", "order2", "order3");
    
    // modify one down and check
    validater.modifyOrder( "order1", 4 );
    validater.validateOrder( 300, "order1", "order2", "order3");
    
    // modify one down and check
    validater.modifyOrder( "order3", 4 );
    validater.validateOrder( 300, "order1", "order2", "order3");
  }
  
  @Test
  public void modifyUpOrder() {
    // create order book
    OrderBookManager orderBookManager = new OrderBookManagerImpl();

    // send orders
    OrderValidater validater = new OrderValidater( orderBookManager, "microscope", Side.SELL );
    validater.addOrder( "order1", 300, 5 );
    validater.addOrder( "order2", 300, 5 );
    validater.addOrder( "order3", 300, 50 );

    // modify one down and check
    validater.modifyOrder( "order2", 6 );
    validater.validateOrder( 300, "order1", "order3", "order2" );
    
    // modify one down and check
    validater.modifyOrder( "order1", 6 );
    validater.validateOrder( 300, "order3", "order2", "order1");
    
    // modify one down and check
    validater.modifyOrder( "order3", 511 );
    validater.validateOrder( 300, "order2", "order1", "order3");
  }
}
