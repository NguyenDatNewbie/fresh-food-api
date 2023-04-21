package com.freshfood.API_FreshShop.Controller;

import com.freshfood.API_FreshShop.Entity.Inventory;
import com.freshfood.API_FreshShop.Entity.Product;
import com.freshfood.API_FreshShop.Entity.ResponseObject;
import com.freshfood.API_FreshShop.Repository.InventoryRepository;
import com.freshfood.API_FreshShop.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/fresh_shop/inventory")
public class InventoryController {
    @Autowired
    InventoryRepository repository;

    @Autowired
    ProductRepository productRepository;
    @GetMapping("/{id}")
    Inventory get(@PathVariable Long id){return repository.findOne(id);}

    @GetMapping("")
    List<Inventory> getAll(){
        return repository.findAll();
    }

    @PostMapping("/new")
    ResponseEntity<ResponseObject> insert(@RequestBody Inventory inventory){
        Date current = new Date(System.currentTimeMillis());
        if(current.compareTo(inventory.getExpirationDate())>0)
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed","Ngày hết hạn sản phẩm sớm hơn ngày hiện tại","")
            );
        // Ngay het han < Ngay San Xuat
        int checkDate = inventory.getExpirationDate().compareTo(inventory.getProductionDate());
        if(checkDate<0)
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed","Ngày hết hạn sản phẩm sớm hơn sản xuất","")
            );
        inventory.setCreatedAt(current);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("success","Đã thêm sản phẩm vào kho",repository.save(inventory))
        );
    }

    @PutMapping("/new")
    ResponseEntity<ResponseObject> update(@RequestBody Inventory inventory){
        Date current = new Date(System.currentTimeMillis());
        if(current.compareTo(inventory.getExpirationDate())>0)
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed","Ngày hết hạn sản phẩm sớm hơn ngày hiện tại","")
            );
        // Ngay het han < Ngay San Xuat
        int checkDate = inventory.getExpirationDate().compareTo(inventory.getProductionDate());
        if(checkDate<0)
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed","Ngày hết hạn sản phẩm sớm hơn sản xuất","")
            );
        inventory.setUpdatedAt(current);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("success","Đã cập nhật sản phẩm trong kho",repository.save(inventory))
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> delete(@PathVariable Long id){
        boolean exist = repository.exists(id);
        if(!exist)
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed","Xóa thành công","")
            );
        repository.delete(id);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new ResponseObject("failed","Xóa sản phẩm trong kho thành công","")
        );
    }

    @DeleteMapping("/product/{id}")
    void deleteByProductId(@PathVariable Long id){
        repository.deleteAllProductById(id);
    }
}
