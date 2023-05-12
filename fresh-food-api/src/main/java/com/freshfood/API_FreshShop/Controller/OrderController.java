package com.freshfood.API_FreshShop.Controller;

import com.freshfood.API_FreshShop.Entity.*;
import com.freshfood.API_FreshShop.Repository.InventoryRepository;
import com.freshfood.API_FreshShop.Repository.OrderItemRepository;
import com.freshfood.API_FreshShop.Repository.OrderRepository;
import com.freshfood.API_FreshShop.Repository.ProductRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Order;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/fresh_shop/order")
public class OrderController {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    ProductRepository productRepository;
    @Autowired
    InventoryRepository inventoryRepository;

    @GetMapping("/{user_id}/{status}")
    List<Orders> getOrderByStatus(@PathVariable Long user_id,@PathVariable int status){
        List<Orders> orders = orderRepository.findByUserComplete(user_id,status);
        return orders;
    }

    @GetMapping("/admin/{status}")
    List<Orders> getOrderAdminByStatus(@PathVariable int status){
        List<Orders> orders = orderRepository.findByStatus(status);
        return orders;
    }

    @GetMapping("/{orderId}")
    List<OrderItem> getOrderByStatus(@PathVariable Long orderId){
        List<OrderItem> list = orderItemRepository.getByOrderId(orderId);
        return list;
    }

    @GetMapping("/revenue/{month}")
    Double getRevenueByMonth(@PathVariable int month){
        List<Orders> orders = orderRepository.findByOrderByMonthPrice(month);
        BigDecimal revenue = BigDecimal.valueOf(0);
        for(int i =0;i <orders.size();i++){
            BigDecimal price = orders.get(i).getTotal_price();
            revenue=revenue.add(price);
        }
        return revenue.doubleValue();
    }
    @GetMapping("/revenue")
    Double getRevenue(){
        List<Orders> orders = orderRepository.findByOrderPrice();
        BigDecimal revenue = BigDecimal.valueOf(0);
        for(int i =0;i <orders.size();i++){
            BigDecimal price = orders.get(i).getTotal_price();
            revenue=revenue.add(price);
        }
        return revenue.doubleValue();
    }

    @GetMapping("/revenue/order/{month}")
    List<Orders> getRevenueOrderByMonth(@PathVariable int month){
        List<Orders> orders = orderRepository.findByOrderByMonth(month);
        return orders;
    }

    @GetMapping("/revenue/product/{month}")
    List<Product> getProductSoldByMonth(@PathVariable int month){
        List<Orders> orders = orderRepository.findByOrderByMonthSold(month);
        return getProductSold(orders);
    }

    @GetMapping("/revenue/order/status/{status}")
    List<Orders> getOrderByStatus(@PathVariable int status){
        List<Orders> orders = orderRepository.findByOrderFailed(status);
        return orders;
    }


    @PutMapping("/new/{status}/{orderId}")
    Orders updateStatus(@PathVariable int status, @PathVariable Long orderId){
        Orders orders = orderRepository.findOne(orderId);
        orders.setStatus(status);
        handle(orders);
        return orderRepository.save(orders);
    }

    void handle(Orders orders){
        List<OrderItem> orderItems = orderItemRepository.getByOrder(orders);;
        // Nhận được hàng
        if(orders.getStatus()==2){
            for(int i=0;i<orderItems.size();i++) {
                OrderItem item = orderItems.get(i);
                Product product = item.getInventory().getProduct();
                product.setSold(product.getSold()+item.getQuantity());
                productRepository.save(product);
            }
        }
        //Hủy đơn
        else if(orders.getStatus()==3){
            for(int i=0;i<orderItems.size();i++) {
                OrderItem item = orderItems.get(i);
                Inventory inventory = item.getInventory();
                inventory.setQuantity(inventory.getQuantity()+item.getQuantity());
                inventoryRepository.save(inventory);
            }
        }
    }

    List<Long> productId;
    List<Integer> sold;

    List<Product> products;

    void addProducts(List<Long> id,List<Integer> quantity){
        for(int i =0;i<id.size();i++) {
            Product product = productRepository.findOne(id.get(i));
            product.setSold(quantity.get(i));
            products.add(product);
        }
    }

    public List<Product> getProductSold(List<Orders> ordersList)
    {
        productId = new ArrayList<>();
        sold = new ArrayList<>();
        products = new ArrayList<>();
        for(int i =0;i<ordersList.size();i++)
        {
            List<OrderItem> orderItems = orderItemRepository.getByOrder(ordersList.get(i));
            handle(orderItems);
        }

        addProducts(productId,sold);
        return products;
    }
    public void handle(List<OrderItem> list){
        for(int i=0;i<list.size();i++){
            OrderItem item = list.get(i);
            checkExits(item.getInventory().getProduct().getId(),item.getQuantity());
        }
    }

    void checkExits(Long id,Integer soldUpdate){
        for(int i=0;i<productId.size();i++)
        {
            if(productId.get(i)==id) {
                {
                    sold.set(i,sold.get(i)+soldUpdate);
                    return;
                }
            }
        }
        productId.add(id);
        sold.add(soldUpdate);
    }
}
