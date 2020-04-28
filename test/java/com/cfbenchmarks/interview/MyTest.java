package com.cfbenchmarks.interview;

import org.junit.Test;

import static org.junit.Assert.*;

public class MyTest {

    @Test
    public void testBestBidPrice() {
        // create order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        // create order
        Order buy = new Order("order1", "VOD.L", Side.buy, 200, 10 );

        // send order
        orderBookManager.addOrder( buy );

        // check that best price is 200
        long expectedPrice = 200;
        long actualPrice = orderBookManager.getBestPrice("VOD.L", Side.buy );
        assertEquals( "Best bid price is 200", expectedPrice, actualPrice );
    }
}
