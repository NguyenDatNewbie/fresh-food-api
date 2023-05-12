package com.freshfood.API_FreshShop.Controller;

import com.freshfood.API_FreshShop.Repository.*;
import com.freshfood.API_FreshShop.Service.IPaymentService;
import com.freshfood.API_FreshShop.Service.Impl.PaymentService;
import com.freshfood.API_FreshShop.Entity.*;
import com.mysql.cj.log.Log;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/fresh_shop/sell")
public class SellController {
    @Autowired
    OrderItemRepository repository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    InfoUserRepository infoUserRepository;

    @Autowired
    ProductRepository productRepository;

    @GetMapping("/cart/{user_id}")
    ResponseEntity<ResponseObject> getCart(@PathVariable Long user_id){
        Orders order = orderRepository.findByUser(user_id);
        if(order==null)
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed","Không tìm thấy user","")
            );
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("success","Xem giỏ hàng thành công",repository.getByOrder(order))
        );
    }

    @PostMapping("/checkout/{user_id}")
    ResponseEntity<ResponseObject> checkout(@PathVariable Long user_id,@RequestBody ProductQuantity productQuantity){
        Orders orders = orderRepository.findByUser(user_id);
        for(int i=0;i<productQuantity.getId().size();i++){
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(productQuantity.getQuantity().get(i));
            orderItem.setOrders(orders);
            orderItem.setInventory(inventoryRepository.getProductInInventory(productQuantity.getId().get(i),orderItem.getQuantity()).get(0));
            repository.save(orderItem);
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("success","Xem giỏ hàng thành công","")
        );
    }

    @PostMapping("check")
    ResponseEntity<ResponseObject> checkProductInInventory(@RequestBody ProductQuantity productQuantity){
        loadOpen();
        List<Long> idInventory = new ArrayList<>();
        List<Integer> quantityInventory = new ArrayList<>();

        List<Long> idProductFail = new ArrayList<>();
        List<Integer> quantityProduct  = new ArrayList<>();
        Boolean isTrue = true;
        for(int i = 0;i<productQuantity.getId().size();i++){
            List<Inventory> check = inventoryRepository.getProductInInventory(productQuantity.getId().get(i),productQuantity.getQuantity().get(i));
            if(check.size()>0)
            {
                idInventory.add(check.get(0).getId());
                quantityInventory.add(productQuantity.getQuantity().get(i));
            }
            else{
                isTrue=false;
                idProductFail.add(productQuantity.getId().get(i));
                quantityProduct.add(productQuantity.getQuantity().get(i));
            }
        }
        ProductQuantity result = new ProductQuantity();
        if(isTrue==true){
            result.setId(idInventory);
            result.setQuantity(quantityInventory);
            return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("success", "Sản phẩm và số lượng đủ trong kho", result)
            );
        }
        result.setId(idProductFail);
        result.setQuantity(quantityProduct);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("failed", "Sản phẩm và số lượng này kho không thể đáp ứng", result)
        );
    }


    @PutMapping("/cart")
    List<OrderItem> updateCartItem(@RequestBody OrderItem orderItem, @RequestBody Orders orders)
    {
        repository.save(orderItem);
        return repository.getByOrder(orders);
    }

//     Xử lý thanh toán
    @PostMapping("/payment/{user_id}/{phone}/{address}/{totalPrice}")
    Orders payment(@RequestBody ProductQuantity list,
                   @PathVariable Long user_id, @PathVariable String phone,@PathVariable String address,@PathVariable BigDecimal totalPrice){
        List<Long> inventories = list.getId();
        List<Integer> quantity = list.getQuantity();
        Orders orders = orderRepository.findByUser(user_id);
        addInventoryToOrderItem(orders,inventories,quantity);
        orders.setAddress(address);
        orders.setPhone(phone);
        orders.setComplete(true);
        orders.setTotal_price(totalPrice);
//        orders.setPaymentComplete();
        long millis=System.currentTimeMillis();   java.sql.Date date=new java.sql.Date(millis);
        orders.setPaymentComplete(date);

        Orders newOrder = new Orders();
        InfoUser user = infoUserRepository.findOne(user_id);
        newOrder.setUser(user);
        newOrder.setComplete(false);
        orderRepository.save(newOrder);
        return orderRepository.save(orders);
    }

    void addInventoryToOrderItem(Orders orders,List<Long> inventories,List<Integer> quantityList){
        for(int i =0;i<inventories.size();i++) {
            Inventory inventory = inventoryRepository.findOne(inventories.get(i));
            Integer quantity = quantityList.get(i);
            List<OrderItem> orderItems = repository.checkExits(orders, inventory.getProduct());
            OrderItem orderItem = new OrderItem();
            if(orderItems.size()>0){
                orderItem = orderItems.get(0);
                orderItem.setInventory(inventory);
                orderItem.setQuantity(quantity);
            }
            else {
                orderItem.setOrders(orders);
                orderItem.setInventory(inventory);
                orderItem.setQuantity(quantity);
            }
            repository.save(orderItem);
            int temp = inventory.getQuantity()-quantity;
            inventory.setQuantity(temp);
            inventoryRepository.save(inventory);

        }
    }

    @Autowired
    ExpiredProductRepository expiredProductRepository;
    Boolean checkNewDate = false;

    Date newDate=new Date(System.currentTimeMillis());
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
                for (int i = 0; i < inventoryList.size(); i++) {
                    Inventory inventory = inventoryList.get(i);
                    expiredProduct.setProduct(inventory.getProduct());
                    expiredProduct.setExpirationDate(inventory.getExpirationDate());
                    expiredProduct.setQuantity(inventory.getQuantity());
                    expiredProduct.setProductionDate(inventory.getProductionDate());
                    expiredProduct.setUpdatedAt(inventory.getUpdatedAt());
                    expiredProduct.setCreatedAt(inventory.getCreatedAt());
                    inventoryRepository.delete(inventory);
                    expiredProductRepository.save(expiredProduct);

                }
            }
            checkNewDate = true;
            newDate = currentDate;
        }
    }
}
