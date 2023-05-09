package com.freshfood.API_FreshShop.Repository;

import com.freshfood.API_FreshShop.Entity.Inventory;
import com.freshfood.API_FreshShop.Entity.OrderItem;
import com.freshfood.API_FreshShop.Entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    @Query("select o from OrderItem o where o.orders=?1")
    List<OrderItem> getByOrder(Orders orders);

    @Query("select o from OrderItem o where o.orders=?1 and o.inventory=?2")
    List<OrderItem> checkExits(Orders orders, Inventory inventory);
}
