package com.freshfood.API_FreshShop.Repository;

import com.freshfood.API_FreshShop.Entity.ExpiredProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface ExpiredProductRepository extends JpaRepository<ExpiredProduct,Long> {
    @Query("select e from ExpiredProduct e where e.inventory.product.id = ?1")
    List<ExpiredProduct> getAllByProductId(Long productId);
}
