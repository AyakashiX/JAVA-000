package com.geektime.sharding.repository;

import com.geektime.sharding.pojo.OrderTable;

import java.util.List;

public interface OrderRepository {

    void addOrder(OrderTable order);

    int deleteOrder(Long orderId);

    List<OrderTable> findAllOrders();

    OrderTable selectOneOrder(Integer userId);

    void updateOrder(OrderTable order);
}
