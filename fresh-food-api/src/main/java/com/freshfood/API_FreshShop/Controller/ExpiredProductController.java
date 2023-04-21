package com.freshfood.API_FreshShop.Controller;

import com.freshfood.API_FreshShop.Entity.ExpiredProduct;
import com.freshfood.API_FreshShop.Entity.Inventory;
import com.freshfood.API_FreshShop.Repository.ExpiredProductRepository;
import com.freshfood.API_FreshShop.Repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/fresh_shop/expired")
public class ExpiredProductController {
    @Autowired
    ExpiredProductRepository repository;

    @Autowired
    InventoryRepository inventoryRepository;

    Boolean checkNewDate = false;

    Date newDate;
    @GetMapping("/load")
    public void loadOpen(){
        Date currentDate = new Date(System.currentTimeMillis());
        if(newDate.compareTo(currentDate)!=0)
            checkNewDate=false;
        if(checkNewDate && newDate.compareTo(currentDate)==0)
            return;
        else {
            List<Inventory> inventoryList = inventoryRepository.getExpriedProduct();
            if (inventoryList.size() > 0) {
                ExpiredProduct expiredProduct = new ExpiredProduct();
                Inventory inventory = null;
                for (int i = 0; i < inventoryList.size(); i++) {
                    inventory = inventoryList.get(i);
                    expiredProduct.setInventory(inventory);
                    repository.save(expiredProduct);
                    inventoryRepository.delete(inventory);
                }
            }
            checkNewDate = true;
            newDate = currentDate;
        }
    }

    @GetMapping("/product/{id}")
    List<ExpiredProduct> getByProduct(@PathVariable Long id){
        return repository.getAllByProductId(id);
    }
}
