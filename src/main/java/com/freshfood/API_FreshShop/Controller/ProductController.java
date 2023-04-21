package com.freshfood.API_FreshShop.Controller;

import com.freshfood.API_FreshShop.Entity.Category;
import com.freshfood.API_FreshShop.Entity.Product;
import com.freshfood.API_FreshShop.Entity.ResponseObject;
import com.freshfood.API_FreshShop.Repository.CategoryRepository;
import com.freshfood.API_FreshShop.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fresh_shop/product")
public class ProductController {

    @Autowired
    ProductRepository repository;

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping("/{id}")
    Product getById(@PathVariable Long id){return repository.findOne(id);}

    @GetMapping("")
    List<Product> getAll(){
        return repository.findAll();
    }

    @GetMapping("/promotion")
    List<Product> getAllProductPromotion(){
        return repository.getAllPromotion();
    }

    @GetMapping("/sort_price/desc")
    List<Product> getAllPriceDesc(){
        return repository.getPriceDesc();
    }

    @GetMapping("/sort_price/asc")
    List<Product> getAllPriceAsc(){
        return repository.getPriceAsc();
    }

    @GetMapping("/sold")
    List<Product> getSold(){
        return repository.getProductSold();
    }

    @GetMapping("/category/{category_id}")
    List<Product> getByCategory(@PathVariable Long category_id ){
        return repository.getProductByCategory(category_id);
    }


    @PostMapping(path = "/new")
    ResponseEntity<ResponseObject> insert(@RequestBody Product product){
        Product exist = repository.findByName(product.getName());
        if(exist!=null)
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObject("failed","Sản phẩm này đã tồn tại","")
            );
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("success","Thêm thành công",repository.save(product))
                );
    }

    @PutMapping(path="/new/{id}",consumes = {MediaType.ALL_VALUE} )
    ResponseEntity<ResponseObject> update(@PathVariable Long id,@RequestBody Product product){
        if(repository.exists(id))
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success","Cập nhật sản phẩm thành công",repository.save(product))
            );
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new ResponseObject("failed","Không tìm thấy sản phẩm","")
        );
    }

    // Cần xóa một vài bảng liên kết
    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> delete(@PathVariable Long id){
        if(repository.exists(id))
           return ResponseEntity.status(HttpStatus.OK).body(
                   new ResponseObject("success","Xóa thành công","")
           );
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new ResponseObject("failed","Không tìm thấy sản phẩm","")
        );
    }
}
