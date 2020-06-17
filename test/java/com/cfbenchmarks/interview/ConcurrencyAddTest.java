package com.cfbenchmarks.interview;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.google.code.tempusfugit.concurrency.ConcurrentRule;
import com.google.code.tempusfugit.concurrency.RepeatingRule;
import com.google.code.tempusfugit.concurrency.annotations.Concurrent;
import com.google.code.tempusfugit.concurrency.annotations.Repeating;

public class ConcurrencyAddTest {

  private static OrderBookManager orderBookManager;
  private static AtomicInteger count = new AtomicInteger(0);

  @Rule
  public ConcurrentRule concurrently = new ConcurrentRule();
  @Rule
  public RepeatingRule repeatedly = new RepeatingRule();

  @BeforeClass
  public static void init() {
    orderBookManager = new OrderBookManagerImpl();
  }

  @Test
  @Concurrent(count = 10)
  @Repeating(repetition = 100)
  public void add() {

    // send sell orders
    addOrder(Side.SELL, 300, 5);
    addOrder(Side.SELL, 400, 5);
    addOrder(Side.SELL, 500, 50);
    // send buy orders
    addOrder(Side.BUY, 300, 5);
    addOrder(Side.BUY, 400, 5);
    addOrder(Side.BUY, 500, 50);
  }

  private void addOrder(Side side, long price, long qty) {
    Order toAdd = new Order.Builder().setOrderId(UUID.randomUUID().toString()).setInstrument("microscope").setSide(side)
        .setPrice(price).setQuantity(qty).get();
    count.incrementAndGet();
    orderBookManager.addOrder(toAdd);
  }

  @AfterClass
  public static void check() {
    // each test adds 3 orders total 3000 buy/sell orders
    // each test adds 60 qty total 180000 qty buy/sell
    // each test adds total 28.5k volume buy / sell
    Assert.assertEquals(Optional.of(300l), orderBookManager.getBestPrice("microscope", Side.SELL));
    Assert.assertEquals(Optional.of(500l), orderBookManager.getBestPrice("microscope", Side.BUY));
    Assert.assertEquals(6000, count.get());

    // order number
    Assert.assertEquals(1000, orderBookManager.getOrderNumAtLevel("microscope", Side.SELL, 300));
    Assert.assertEquals(1000, orderBookManager.getOrderNumAtLevel("microscope", Side.SELL, 400));
    Assert.assertEquals(1000, orderBookManager.getOrderNumAtLevel("microscope", Side.SELL, 500));

    // quantity
    Assert.assertEquals(5000, orderBookManager.getTotalQuantityAtLevel("microscope", Side.SELL, 300));
    Assert.assertEquals(5000, orderBookManager.getTotalQuantityAtLevel("microscope", Side.SELL, 400));
    Assert.assertEquals(50000, orderBookManager.getTotalQuantityAtLevel("microscope", Side.SELL, 500));

    // vol
    Assert.assertEquals(1500000, orderBookManager.getTotalVolumeAtLevel("microscope", Side.BUY, 300));
    Assert.assertEquals(2000000, orderBookManager.getTotalVolumeAtLevel("microscope", Side.BUY, 400));
    Assert.assertEquals(25000000, orderBookManager.getTotalVolumeAtLevel("microscope", Side.BUY, 500));

    Assert.assertEquals(1000, orderBookManager.getOrderNumAtLevel("microscope", Side.BUY, 300));
    Assert.assertEquals(1000, orderBookManager.getOrderNumAtLevel("microscope", Side.BUY, 400));
    Assert.assertEquals(1000, orderBookManager.getOrderNumAtLevel("microscope", Side.BUY, 500));

    // quantity
    Assert.assertEquals(5000, orderBookManager.getTotalQuantityAtLevel("microscope", Side.BUY, 300));
    Assert.assertEquals(5000, orderBookManager.getTotalQuantityAtLevel("microscope", Side.BUY, 400));
    Assert.assertEquals(50000, orderBookManager.getTotalQuantityAtLevel("microscope", Side.BUY, 500));

    // vol
    Assert.assertEquals(1500000, orderBookManager.getTotalVolumeAtLevel("microscope", Side.BUY, 300));
    Assert.assertEquals(2000000, orderBookManager.getTotalVolumeAtLevel("microscope", Side.BUY, 400));
    Assert.assertEquals(25000000, orderBookManager.getTotalVolumeAtLevel("microscope", Side.BUY, 500));
  }
}
