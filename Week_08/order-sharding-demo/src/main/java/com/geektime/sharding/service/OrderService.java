package com.geektime.sharding.service;

import com.geektime.sharding.pojo.OrderTable;
import com.geektime.sharding.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    //addOrder
    public void addOrder(){
        for(Long i=1L; i<=10000; i++){
            OrderTable order = new OrderTable();
            order.setOrderId(i);
            order.setProductId(i);
            order.setProductName("prodcut_" + i);
            order.setUserId(i.intValue());
            order.setUserName("user_" + i);
            orderRepository.addOrder(order);
        }
    }

    //select one
    public OrderTable selectOneOrder(Integer userId) {
        OrderTable order = orderRepository.selectOneOrder(userId);
        System.out.println(order.toString());
        System.out.println("order_id=" + order.getOrderId());
        return order;
    }

    //delete order
    public void deleteOrder(Integer userId) {
        OrderTable order = selectOneOrder(userId);
        orderRepository.deleteOrder(order.getOrderId());
    }

    //update order
    public void updateOrder(Integer userId) {
        OrderTable order = selectOneOrder(userId);
        order.setProductName("the product is deleted_" + order.getOrderId());
        order.setUserName("the user not available_" + order.getOrderId());
        orderRepository.updateOrder(order);
    }

    //find all
    public List<OrderTable> findAllOrders(){
        List<OrderTable> orderList = orderRepository.findAllOrders();
        return orderList;
    }

}





