package com.freshfood.API_FreshShop.Repository;

import com.freshfood.API_FreshShop.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product,Long> {
    Product findByName(String name);

    @Query("select p from Product p where p.promotion>0 order by p.promotion DESC")
    List<Product> getAllPromotion();

    @Query("select p from Product  p order by p.price DESC")
    List<Product> getPriceDesc();

    @Query("select p from Product  p order by p.price DESC")
    List<Product> getPriceAsc();

    @Query("select p from Product p where p.category.id = ?1")
    List<Product> getProductByCategory(Long id);

    @Query("select p from Product p order by p.sold DESC")
    List<Product> getProductSold();

    //product/sold   sản phẩm bán nhiều
}
