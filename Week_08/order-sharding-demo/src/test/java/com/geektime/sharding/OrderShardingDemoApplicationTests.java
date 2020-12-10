package com.geektime.sharding;

import com.geektime.sharding.pojo.OrderTable;
import com.geektime.sharding.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderShardingDemoApplicationTests {

    @Autowired
    private OrderService orderService;

    @Test
    public void addOrderTest() {
        orderService.addOrder();
    }

    @Test
    public void deleteOrderTest(){
        orderService.deleteOrder(4);
    }

    @Test
    public void updateOrderTest(){
        orderService.updateOrder(2);
    }

    @Test
    public void findAllOrdersTest(){
        List<OrderTable> orderList = orderService.findAllOrders();
        for(OrderTable order : orderList){
            System.out.println(order.toString());
        }
    }

    @Test
    public void selectOneOrder(){
        OrderTable order = orderService.selectOneOrder(4);
        System.out.println(order);
    }

}










