package com.freshfood.API_FreshShop.Repository;

import com.freshfood.API_FreshShop.Entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;



public interface InventoryRepository extends JpaRepository<Inventory,Long> {


    @Override
    @Query("select i from Inventory i ORDER BY i.product.name ASC, i.expirationDate ASC")
    List<Inventory> findAll();

    @Query("delete from Inventory i where i.product.id = ?1")
    void deleteAllProductById(Long id);

    @Query("delete from Inventory i where i.quantity = 0")
    void deleteInventoryByQuantity();

    @Query("SELECT i FROM Inventory i where curdate() > i.expirationDate")
    List<Inventory> getExpriedProduct();

    @Query("SELECT i from Inventory i where i.product.id = ?1 and i.quantity>=?2 order by i.expirationDate ASC")
    List<Inventory> getProductInInventory(Long productId, int quantity);


}
