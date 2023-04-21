package com.freshfood.API_FreshShop.Repository;

import com.freshfood.API_FreshShop.Entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


public interface OrderRepository extends JpaRepository<Orders,Long> {
    @Query("select o from Orders o where o.user.id = ?1 and o.complete=false")
    Orders findByUser(Long user_id);
}
