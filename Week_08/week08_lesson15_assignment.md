# Week08_lesson15_assignment

#### 作业要求: 将订单表分为2个库，每个库分别为16个表，使用ShardingSphere进行分库分片操作

## 1. 表结构
```sql
create table order_table
(
	order_id bigint auto_increment primary key,
	product_id bigint null,
	product_name varchar(255) null,
	user_id int null,
	user_name varchar(255) null
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
分别创建两个库orderdb0和orderdb1，每个库中创建16个order_table表，命名分别为
order_table0->order_table15。


## 2. Java代码结构

-- com.geektime.sharding
	-- pojo
		-- OrderTable
	-- repository
		-- OrderRepostitory
	-- service
		-- OrderService
	-- SpringApplication启动类
-- resources
	-- mappers/OrderMapper.xml
	-- application.properties
-- test
	-- com.geektime.sharding
		-- OrderShadingDemoApplicationTests测试类
-- pom.xml


### pom.xml - maven依赖配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.6.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.geektime.sharding</groupId>
    <artifactId>order-sharding-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>order-sharding-demo</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>

    <dependencies>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.3.0</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.16</version>
        </dependency>

        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
            <version>4.0.0-RC1</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>

```


### OrderTable - 订单类
```java
package com.geektime.sharding.pojo;

public class OrderTable {
    Long orderId;
    Long productId;
    String productName;
    Integer userId;
    String userName;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "OrderTable{" +
                "orderId=" + orderId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}

```


### OrderRepository - 订单数据访问类
```java
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
```


### OrderService - 订单业务类
```java
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
```


### SpringBootApplication - 启动类
```java
package com.geektime.sharding;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.geektime.sharding.repository")
public class OrderShardingDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderShardingDemoApplication.class, args);
    }
}

```


### OrderMapper.xml - Mybatis映射文件
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.geektime.sharding.repository.OrderRepository">

    <resultMap id="orderMap" type="OrderTable">
        <result column="order_id" property="orderId" jdbcType="INTEGER"/>
        <result column="product_id" property="productId" jdbcType="INTEGER"/>
        <result column="product_name" property="productName" jdbcType="VARCHAR"/>
        <result column="user_id" property="userId" jdbcType="INTEGER"/>
        <result column="user_name" property="userName" jdbcType="VARCHAR"/>
    </resultMap>

    <!--insert order-->
    <insert id="addOrder" parameterType="OrderTable">
        INSERT INTO order_table (order_id, product_id, product_name, user_id, user_name)
        VALUES (#{orderId}, #{productId}, #{productName}, #{userId}, #{userName})
    </insert>

    <!--delete order-->
    <delete id="deleteOrder" parameterType="long">
        DELETE FROM order_table WHERE order_id = #{orderId}
    </delete>

    <!--findAllOrders-->
    <select id="findAllOrders" resultMap="orderMap">
        SELECT * FROM order_table
    </select>

    <!--select one order-->
    <select id="selectOneOrder" parameterType="int" resultType="OrderTable">
        SELECT * FROM order_table WHERE user_id=#{userId}
    </select>

    <!--update order-->
    <update id="updateOrder" parameterType="OrderTable">
        UPDATE order_table SET product_name=#{productName}, user_name=#{userName} WHERE order_id=#{orderId}
    </update>
</mapper>
```


### application.properties - 数据库及分片配置信息
```bash
#mybatis
mybatis.type-aliases-package=com.geektime.sharding.pojo
mybatis.mapper-locations=classpath:mappers/*.xml
mybatis.configuration.map-underscore-to-camel-case=true
mybatis.configuration.cache-enabled=false

#sharding_database
spring.shardingsphere.datasource.names=orderdb0,orderdb1

#dataSource0
spring.shardingsphere.datasource.orderdb0.type=com.alibaba.druid.pool.DruidDataSource
spring.shardingsphere.datasource.orderdb0.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.orderdb0.url=jdbc:mysql://127.0.0.1:3306/orderdb0?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=UTF-8
spring.shardingsphere.datasource.orderdb0.username=root
spring.shardingsphere.datasource.orderdb0.password=123456

#dataSource1
spring.shardingsphere.datasource.orderdb1.type=com.alibaba.druid.pool.DruidDataSource
spring.shardingsphere.datasource.orderdb1.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.orderdb1.url=jdbc:mysql://127.0.0.1:3306/orderdb1?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=UTF-8
spring.shardingsphere.datasource.orderdb1.username=root
spring.shardingsphere.datasource.orderdb1.password=123456

#database_sharding_rules
spring.shardingsphere.sharding.default-database-strategy.inline.sharding-column=user_id
#use user_id be the rule to distribute data to 2 databases;
spring.shardingsphere.sharding.default-database-strategy.inline.algorithm-expression=orderdb$->{user_id % 2}
#no binding tables
#no broadcast tables

#table_sharding_rules
spring.shardingsphere.sharding.tables.order_table.actual-data-nodes=orderdb$->{0..1}.order_table$->{0..15}
spring.shardingsphere.sharding.tables.order_table.table-strategy.inline.sharding-column=order_id
spring.shardingsphere.sharding.tables.order_table.table-strategy.inline.algorithm-expression=order_table$->{order_id % 16}
spring.shardingsphere.sharding.tables.order_table.key-generator.column=order_id
spring.shardingsphere.sharding.tables.order_table.key-generator.type=SNOWFLAKE
spring.shardingsphere.sharding.tables.order_table.key-generator.props.worker.id=33
```


### OrderShardingDemoApplicationTests - 测试类
```java
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
```
