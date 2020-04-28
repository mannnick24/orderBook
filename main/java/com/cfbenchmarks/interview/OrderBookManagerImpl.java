package com.cfbenchmarks.interview;

import java.util.List;

public class OrderBookManagerImpl implements OrderBookManager {

    public void addOrder(Order order) {

    }

    public void modifyOrder(String orderId, long newQuantity) {

    }

    public void deleteOrder(String orderId) {

    }

    public long getBestPrice(String instrument, Side side) {
        return 0;
    }

    public long getOrderNumAtLevel(String instrument, Side side, long price) {
        return 0;
    }

    public long getTotalQuantityAtLevel(String instrument, Side side, long price) {
        return 0;
    }

    public long getTotalVolumeAtLevel(String instrument, Side side, long price) {
        return 0;
    }

    public List<Order> getOrdersAtLevel(String instrument, Side side, long price) {
        return null;
    }
}
