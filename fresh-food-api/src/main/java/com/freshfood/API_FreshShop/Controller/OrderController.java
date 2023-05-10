package com.freshfood.API_FreshShop.Controller;

import com.freshfood.API_FreshShop.Entity.OrderItem;
import com.freshfood.API_FreshShop.Entity.Orders;
import com.freshfood.API_FreshShop.Repository.OrderItemRepository;
import com.freshfood.API_FreshShop.Repository.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Order;
import java.util.List;

@RestController
@RequestMapping("/api/fresh_shop/order")
public class OrderController {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @GetMapping("/{user_id}/{status}")
    List<Orders> getOrderByStatus(@PathVariable Long user_id,@PathVariable int status){
        List<Orders> orders = orderRepository.findByUserComplete(user_id,status);
        return orders;
    }

    @GetMapping("/{orderId}")
    List<OrderItem> getOrderByStatus(@PathVariable Long orderId){
        List<OrderItem> list = orderItemRepository.getByOrderId(orderId);
        return list;
    }
}
