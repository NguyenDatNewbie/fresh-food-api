package com.freshfood.API_FreshShop.Repository;

import com.freshfood.API_FreshShop.Entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    @Override
    @Query("select i from Inventory i ORDER BY i.product.name ASC, i.expirationDate ASC")
    List<Inventory> findAll();

    @Query("delete from Inventory where product.id = ?1")
    void deleteAllProductById(Long id);

    @Query("SELECT i FROM Inventory i where curdate() > i.expirationDate")
    List<Inventory> getExpriedProduct();
}
