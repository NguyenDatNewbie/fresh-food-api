package com.freshfood.API_FreshShop.Repository;

import com.freshfood.API_FreshShop.Entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface OrderRepository extends JpaRepository<Orders,Long> {
    @Query("select o from Orders o where o.user.id = ?1 and o.complete=false")
    Orders findByUser(Long user_id);

    @Query("select o from Orders o where o.user.id = ?1 and o.complete=true and o.status=?2")
    List<Orders> findByUserComplete(Long user_id, int status);

    @Query("select o from Orders o where o.complete=true and o.status=?2")
    List<Orders> findByStatus(int status);

    @Query("select o from Orders o where o.complete=true and o.status=?1")
    List<Orders> findByOrderFailed(int status);

    @Query("select o from Orders o where MONTH (o.paymentComplete)=?1")
    List<Orders> findByOrderByMonth(int month);

    @Query("select o from Orders o where MONTH (o.paymentComplete)=?1 and o.status=2")
    List<Orders> findByOrderByMonthPrice(int month);

    @Query("select o from Orders o where o.status=2")
    List<Orders> findByOrderPrice();

    @Query("select o from Orders o where MONTH (o.paymentComplete)=?1 and o.status!=3 and o.status!=1")
    List<Orders> findByOrderByMonthSold(int month);
}
